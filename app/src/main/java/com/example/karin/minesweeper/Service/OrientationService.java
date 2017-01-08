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
    protected final IBinder orientationServiceBinder = new OrientationServiceBinder();
    protected float values;
    private SensorManager sensorManager;
    public static final String SENSOR_SERVICE_BROADCAST_ACTION = "SENSOR_SERVICE_BROADCAST_ACTION";
    public static final String SENSOR_SERVICE_VALUES_KEY = "SENSOR_SERVICE_VALUES_KEY";


    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        Log.v(getTag(),"Available seneors:"+ sensorList);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor == null && sensorList.size() > 0)
        {
            sensor = sensorList.get(0);
        }
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
        return orientationServiceBinder;

    }

    public boolean onUnbind(Intent intent)
    {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
        }

        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        float[] values = new float[event.values.length];
        for (int i =0; i < event.values.length; i++) {
            values[i] = event.values[i];
        }
        notifyEvaluation(values);
    }


    //not implemented
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public OrientationService getOrientation()
    {
        return this;
    }

    public String getTag() {
        return _TAG;
    }

    public float[] evaluate()
    {
        return new float[]{0.1f,0.1f,0.1f};
    }

    protected void notifyEvaluation(float[] values)
    {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(SENSOR_SERVICE_BROADCAST_ACTION);
        broadcastIntent.putExtra(SENSOR_SERVICE_VALUES_KEY, values);
        sendBroadcast(broadcastIntent);
    }

    public float getValues()
    {
        return values;
    }



    public class OrientationServiceBinder extends Binder {
        public OrientationService getService() {
            return OrientationService.this.getOrientation();
        }

        public void notifyService(String message) {
            Log.v(getTag(), OrientationService.class.getSimpleName() + "new message = " + message);
        }
    }

}
