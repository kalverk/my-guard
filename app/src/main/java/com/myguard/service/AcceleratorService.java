package com.myguard.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.myguard.Constants;
import com.myguard.NotificationID;
import com.myguard.alerts.AlertHandler;
import com.myguard.alerts.AlertType;
import com.myguard.model.AlertParameters;
import com.myguard.model.MovementParameters;

public class AcceleratorService extends Service implements SensorEventListener {

    private static final int SAMPLING_PERIOD = 1000000;

    private boolean firstEvent = true;

    private double averageOfX = 0;
    private long countOfX = 0L;

    private double averageOfY = 0;
    private long countOfY = 0L;

    private double averageOfZ = 0;
    private long countOfZ = 0L;

    private SensorManager sensorManager;
    private MovementParameters movementParameters;
    private AlertParameters alertParameters;

    @Override
    public void onCreate() {
        super.onCreate();
        runInForeground();
    }

    private void runInForeground() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), this.getClass().getSimpleName())
                .setDefaults(Notification.DEFAULT_ALL);
        startForeground(NotificationID.ACCELERATOR.value, notificationBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        movementParameters = (MovementParameters) intent.getSerializableExtra(Constants.MOVEMENT_PARAMETERS);

        alertParameters = (AlertParameters) intent.getSerializableExtra(Constants.ALERT_PARAMETERS);
        alertParameters.alertType = AlertType.MOVEMENT;

        sensorManager.registerListener(this, sensor, SAMPLING_PERIOD);
        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float currentX = event.values[0];
        float currentY = event.values[1];
        float currentZ = event.values[2];

        if (!firstEvent &&
                (Math.abs(averageOfX) - Math.abs(currentX) > movementParameters.scaledSensitivity ||
                        Math.abs(averageOfY) - Math.abs(currentY) > movementParameters.scaledSensitivity ||
                        Math.abs(averageOfZ) - Math.abs(currentZ) > movementParameters.scaledSensitivity)) {
            AlertHandler.handle(this, alertParameters);
            return; //Do not calculate alarms into averages
        }

        averageOfX = getAverage(averageOfX, countOfX, currentX);
        countOfX++;

        averageOfY = getAverage(averageOfY, countOfY, currentY);
        countOfY++;

        averageOfZ = getAverage(averageOfZ, countOfZ, currentZ);
        countOfZ++;

        firstEvent = false;
    }

    private double getAverage(double currentAverage, long count, float newElement) {
        return ((currentAverage * count + newElement) / (count + 1));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //No need to act onAccuracyChanged
    }

    @Override
    public void onDestroy() {
        AlertHandler.stop(this, alertParameters);
        sensorManager.unregisterListener(this);
        super.onDestroy();
    }
}
