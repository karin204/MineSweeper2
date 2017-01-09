package com.example.karin.minesweeper.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * Created by Karin on 07/01/2017.
 */

public class OrientationService extends Service implements SensorEventListener
{
    private static final String _TAG = OrientationService.class.getSimpleName();
    private MyServiceListener listener;
    private SensorManager sensorManager;
    ServiceBinder serviceBinder;

    public interface MyServiceListener
    {
        void onSensorEvent(float[] values);
    }

    public class ServiceBinder extends Binder
    {
        public OrientationService getService()
        {
            return OrientationService.this;
        }
    }

    public void setListener(MyServiceListener listener)
    {
        this.listener = listener;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        serviceBinder = new ServiceBinder();
        return serviceBinder;
    }

    public void startListening()
    {
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopListening()
    {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(listener != null)
            listener.onSensorEvent(event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
