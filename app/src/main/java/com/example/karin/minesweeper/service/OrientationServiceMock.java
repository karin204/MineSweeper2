package com.example.karin.minesweeper.Service;

import android.content.Intent;
import android.os.IBinder;

import java.util.Random;

/**
 * Created by Karin on 07/01/2017.
 */

public class OrientationServiceMock extends OrientationService
{
    private static final long TIME_TO_SLEEP = 1000;
    private static final String _TAG = OrientationServiceMock.class.getSimpleName();

    private boolean shouldRun = false;
    private Random random = new Random();

    @Override
    public IBinder onBind(Intent intent) {
        shouldRun = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (shouldRun) {
                    try {
                        Thread.sleep(TIME_TO_SLEEP);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    notifyEvaluation(evaluate());
                }
            }
        }).start();

        return orientationServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        shouldRun = false;

        return super.onUnbind(intent);
    }

    @Override
    public OrientationService getOrientation() {
        return this;
    }

    @Override
    public String getTag() {
        return _TAG;
    }

    @Override
    public float[] evaluate() {
        return new float[]{random.nextFloat(),random.nextFloat(),random.nextFloat()};
    }

}
