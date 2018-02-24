package com.myguard.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.myguard.alerts.AlertHandler;
import com.myguard.alerts.AlertType;
import com.myguard.model.AlertParameters;
import com.myguard.model.MovementParameters;
import com.myguard.util.Debugger;
import com.myguard.util.MovingAverage;

import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

/**
 * Created by kalver on 24/01/18.
 */

public class MovementMonitoring {

    private static final int AVERAGE_PERIOD = 10;
    private static final int SAMPLING_PERIOD = 1000000;

    public static MovementListener register(final Context context, final MovementParameters movementParameters, final AlertParameters alertParameters) {
        Debugger.log(new Object[]{MovementMonitoring.class.getSimpleName(), "register"});

        alertParameters.alertType = AlertType.MOVEMENT;

        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        MovementListener movementListener = new MovementListener(context, movementParameters, alertParameters);
        sensorManager.registerListener(movementListener, sensor, SENSOR_DELAY_NORMAL);
        return movementListener;
    }

    public static void unregister(final Context context, final MovementListener movementListener) {
        Debugger.log(new Object[]{MovementMonitoring.class.getSimpleName(), "unregister"});

        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(movementListener);
    }

    public static class MovementListener implements SensorEventListener {
        private MovingAverage movingAverageOfX = new MovingAverage(AVERAGE_PERIOD);
        private MovingAverage movingAverageOfY = new MovingAverage(AVERAGE_PERIOD);
        private MovingAverage movingAverageOfZ = new MovingAverage(AVERAGE_PERIOD);

        private final Context context;
        private final MovementParameters movementParameters;
        private final AlertParameters alertParameters;
        private final long startTime;

        private MovementListener(Context context, MovementParameters movementParameters, AlertParameters alertParameters) {
            this.context = context;
            this.movementParameters = movementParameters;
            this.alertParameters = alertParameters;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float currentX = event.values[0];
            float currentY = event.values[1];
            float currentZ = event.values[2];

            if (System.currentTimeMillis() - startTime > alertParameters.initTime &&
                    (movingAverageOfX.get() - Math.abs(currentX) > movementParameters.scaledSensitivity ||
                            movingAverageOfY.get() - Math.abs(currentY) > movementParameters.scaledSensitivity ||
                            movingAverageOfZ.get() - Math.abs(currentZ) > movementParameters.scaledSensitivity)) {
                AlertHandler.handle(context, alertParameters);
                Debugger.log(new Object[]{
                        this.getClass().getSimpleName(),
                        movingAverageOfX.get(),
                        currentX,
                        movingAverageOfY.get(),
                        currentY,
                        movingAverageOfZ.get(),
                        currentZ,
                        true});

                throw new RuntimeException("WOLOLOLO");

//                return; //Do not calculate alarms into averages
            } else {
                Debugger.log(new Object[]{
                        this.getClass().getSimpleName(),
                        movingAverageOfX.get(),
                        currentX,
                        movingAverageOfY.get(),
                        currentY,
                        movingAverageOfZ.get(),
                        currentZ,
                        false});
            }

            movingAverageOfX.add(currentX);
            movingAverageOfY.add(currentY);
            movingAverageOfZ.add(currentZ);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }


}
