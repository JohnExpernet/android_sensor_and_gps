package com.example.mysensorapplication;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class LightActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor mLight;
    Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        Log.d("SENSOR TEST IN onAccuracyChanged", Integer.toString(accuracy));
        Log.d("SENSOR TEST IN onAccuracyChanged", sensor.toString());
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        float lux = event.values[0];
        // Do something with this sensor value.

        if (lux > 0.0) {
            mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        } else {
            mainIntent = null;
        }

        Log.d("SENSOR TEST IN onSensorChanged", Float.toString(lux));
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        pm.reboot(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
