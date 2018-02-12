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

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by kalver on 24/01/18.
 */

public class MovementMonitoring {

    private static final int SAMPLING_PERIOD = 1000000;

    public static MovementListener register(final Context context, final MovementParameters movementParameters, final AlertParameters alertParameters) {
        Debugger.log(new Object[]{MovementMonitoring.class.getSimpleName(), "register"});

        alertParameters.alertType = AlertType.MOVEMENT;

        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        MovementListener movementListener = new MovementListener(context, movementParameters, alertParameters);
        sensorManager.registerListener(movementListener, sensor, SAMPLING_PERIOD);
        return movementListener;
    }

    public static void unregister(final Context context, final MovementListener movementListener) {
        Debugger.log(new Object[]{MovementMonitoring.class.getSimpleName(), "unregister"});

        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(movementListener);
    }

    public static class MovementListener implements SensorEventListener {
        private double averageOfX = 0;
        private long countOfX = 0L;

        private double averageOfY = 0;
        private long countOfY = 0L;

        private double averageOfZ = 0;
        private long countOfZ = 0L;

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
                    (Math.abs(averageOfX) - Math.abs(currentX) > movementParameters.scaledSensitivity ||
                            Math.abs(averageOfY) - Math.abs(currentY) > movementParameters.scaledSensitivity ||
                            Math.abs(averageOfZ) - Math.abs(currentZ) > movementParameters.scaledSensitivity)) {
                AlertHandler.handle(context, alertParameters);
                Debugger.log(new Object[]{
                        this.getClass().getSimpleName(),
                        averageOfX,
                        currentX,
                        averageOfY,
                        currentY,
                        averageOfZ,
                        currentZ,
                        true});
//                return; //Do not calculate alarms into averages
            } else {
                Debugger.log(new Object[]{
                        this.getClass().getSimpleName(),
                        averageOfX,
                        currentX,
                        averageOfY,
                        currentY,
                        averageOfZ,
                        currentZ,
                        false});
            }

            averageOfX = getAverage(averageOfX, countOfX, currentX);
            countOfX++;

            averageOfY = getAverage(averageOfY, countOfY, currentY);
            countOfY++;

            averageOfZ = getAverage(averageOfZ, countOfZ, currentZ);
            countOfZ++;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        private double getAverage(double currentAverage, long count, float newElement) {
            return ((currentAverage * count + newElement) / (count + 1));
        }
    }


}
