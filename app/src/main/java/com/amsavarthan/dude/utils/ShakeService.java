package com.amsavarthan.dude.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.amsavarthan.dude.AccidentDetected;

/**
 * Created by anask on 16-04-2016.
 */
public class ShakeService extends Service implements ShakeListener.OnShakeListener {
    private ShakeListener mShaker;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    public int check;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onCreate() {

        super.onCreate();
        this.mSensorManager = ((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        this.mAccelerometer = this.mSensorManager.getDefaultSensor(1);
        mShaker = new ShakeListener(this);
        mShaker.setOnShakeListener(this);
        //Toast.makeText(ShakeService.this, "Service is created!", Toast.LENGTH_LONG).show();
        Log.d(getPackageName(), "Created the Service!");

    }

    @Override
    public void onShake() {
            Toast.makeText(ShakeService.this, "Oh no!", Toast.LENGTH_LONG).show();
            final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vib.vibrate(500);
            Intent i = new Intent();
            i.setClass(this, AccidentDetected.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    public void onDestroy(){
        super.onDestroy();
        Log.d(getPackageName(),"Service Destroyed.");
    }
}
