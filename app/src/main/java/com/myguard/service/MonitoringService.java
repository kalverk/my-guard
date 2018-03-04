package com.myguard.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationListener;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.myguard.Constants;
import com.myguard.CustomExceptionHandler;
import com.myguard.MainActivity;
import com.myguard.NotificationID;
import com.myguard.R;
import com.myguard.alerts.AlertHandler;
import com.myguard.model.AlertParameters;
import com.myguard.model.LocationParameters;
import com.myguard.model.MovementParameters;

public class MonitoringService extends Service {

    private static MonitoringService monitoringService;

    private LocationListener locationListener;
    private MovementMonitoring.MovementListener movementListener;
    private BatteryLevelReceiver batteryLevelReceiver;
    private SMSListener smsListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        monitoringService = this;

        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(getApplicationContext()));

        runInForeground();
        registerMonitoring();

        batteryLevelReceiver = new BatteryLevelReceiver();
        registerReceiver(batteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));

        smsListener = new SMSListener();
        registerReceiver(smsListener, new IntentFilter(Constants.SMS_RECEIVED));

        return START_STICKY;
    }

    private void runInForeground() {
        Intent monitoringServiceIntent = new Intent(this, MonitoringService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                monitoringServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(this.getClass().getSimpleName(), getResources().getString(R.string.title_alerts_enabled), NotificationManager.IMPORTANCE_HIGH);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), this.getClass().getSimpleName())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(new long[]{0L, 0L, 0L})
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.title_alerts_enabled))
                .setWhen(System.currentTimeMillis())
                .addAction(R.drawable.ic_lock_open_black_24dp, getResources().getString(R.string.title_alerts_disable), getMainActivityIntent())
                .setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.ic_lock_outline_black_24dp);
        }

        startForeground(NotificationID.MONITORING.value, builder.build());
    }

    private PendingIntent getMainActivityIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void registerMonitoring() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        final MovementParameters movementParameters = new MovementParameters(sharedPreferences);
        final LocationParameters locationParameters = new LocationParameters(sharedPreferences);
        final AlertParameters alertParameters = new AlertParameters(sharedPreferences);

        if (movementParameters.enabled) {
            movementListener = MovementMonitoring.register(this, movementParameters, alertParameters);
        }

        if (locationParameters.enabled) {
            locationListener = LocationMonitoring.register(this, locationParameters, alertParameters);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (movementListener != null) {
            MovementMonitoring.unregister(this, movementListener);
        }

        if (locationListener != null) {
            LocationMonitoring.unregister(this, locationListener);
        }

        if (batteryLevelReceiver != null) {
            unregisterReceiver(batteryLevelReceiver);
        }

        if (smsListener != null) {
            unregisterReceiver(smsListener);
        }

        AlertHandler.stop(this, new AlertParameters(sharedPreferences));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NotificationID.MONITORING.value);

        super.onDestroy();
    }

    public static void unlockSMS() {
        if (monitoringService != null) {
            monitoringService.stopSelf();
        }
    }
}
