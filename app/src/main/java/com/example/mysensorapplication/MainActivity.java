package com.example.mysensorapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private SmsManager smsManager;
    private Sensor mLight;
    private Sensor mTemperature;
    Intent lightIntent;
    float previousTemp = -10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.SEND_SMS }, 1);
        }
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        Log.d("SENSOR TEST IN onAccuracyChanged", Integer.toString(accuracy));
        Log.d("SENSOR TEST IN onAccuracyChanged", sensor.toString());
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        if (event.sensor == mLight) {
            float lux = event.values[0];

            if (lux == 0.0) {
                lightIntent = new Intent(this, LightActivity.class);
                startActivity(lightIntent);
            } else {
                lightIntent = null;
            }
        } else if (event.sensor == mTemperature) {
            float temp = event.values[0];

            if (previousTemp == -10000) {
                previousTemp = temp;
                showToastMessage();
            }

            if (previousTemp != temp) {
                previousTemp = temp;
                showToastMessage();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mTemperature, SensorManager.SENSOR_DELAY_NORMAL);

        Intent i = new Intent(this, LocationActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void showToastMessage() {
        if (previousTemp < 13.0) {
            Toast.makeText(this, "We advise you to cover yourself", Toast.LENGTH_SHORT).show();
        } else if (previousTemp >= 13.0 && previousTemp <= 23) {
            Toast.makeText(this, "We advise you to take a pull", Toast.LENGTH_SHORT).show();
        } else if (previousTemp >= 23.0 && previousTemp <= 30) {
            Toast.makeText(this, "We advise you to wear a tee-shirt", Toast.LENGTH_SHORT).show();
        } else if (previousTemp >= 30) {
            Toast.makeText(this, "We advise you to wear a cat", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("+262692113574", null, "Hello world", null, null);
            }
        }
    }
}
