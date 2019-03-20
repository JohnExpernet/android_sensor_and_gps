package com.example.mysensorapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.BatteryManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Locale;

// References: https://stuff.mit.edu/afs/sipb/project/android/docs/training/basics/location/locationmanager.html
public class LocationActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private LocationProvider locationProvider;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 1);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, listener);
        geocoder = new Geocoder(this, Locale.getDefault());
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onStart() {
        super.onStart();

        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            enableLocationSettings();
        }
    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }

    private final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();

            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                Log.d("LOCATION_PLACE ADDRESS", address);
                Log.d("LOCATION_PLACE CITY", city);
                Log.d("LOCATION_PLACE STATE", state);
                Log.d("LOCATION_PLACE COUNTRY", country);
                Log.d("LOCATION_PLACE POSTAL CODE", postalCode);

                if (knownName != null) {
                    Log.d("LOCATION_PLACE KNOWN NAME", knownName);
                }
            }

            catch (Exception ex) {
                Log.d("LOCATION_PLACE", "Exception: " + ex.getMessage());
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("LOCATION_PLACE", "STATUS CHANGED");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("LOCATION_PLACE", "PROVIDER ENABLED");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("LOCATION_PLACE", "PROVIDER DISABLED: " + provider);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctx, Intent intent) {
            int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            Log.d("LOCATION_PLACE BATTERY", Integer.toString(batteryLevel));
        }
    };
}
