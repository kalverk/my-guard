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

import com.myguard.MainActivity;
import com.myguard.NotificationID;
import com.myguard.PreferenceKey;
import com.myguard.R;
import com.myguard.alerts.AlertHandler;
import com.myguard.model.AlertParameters;
import com.myguard.model.LocationParameters;
import com.myguard.model.MovementParameters;
import com.myguard.util.Debugger;

public class MonitoringService extends Service {

    private LocationListener locationListener;
    private MovementMonitoring.MovementListener movementListener;
    private BatteryLevelReceiver batteryLevelReceiver;
    private SMSListener smsListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Debugger.log(new Object[]{this.getClass().getSimpleName(), "onStartCommand"});
        runInForeground();
        registerMonitoring();

        exeptionLogger();

        batteryLevelReceiver = new BatteryLevelReceiver();
        registerReceiver(batteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));

        smsListener = new SMSListener();
        registerReceiver(smsListener, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));

        return START_STICKY;
    }

    private void exeptionLogger() {
        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                        Debugger.log(new Object[]{MonitoringService.class.getSimpleName(), paramThrowable.getMessage(), paramThrowable.getCause().getMessage(), paramThrowable.toString()});

                        if (oldHandler != null) {
                            oldHandler.uncaughtException(
                                    paramThread,
                                    paramThrowable
                            );
                        } else {
                            System.exit(2);
                        }
                    }
                });
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
        Debugger.log(new Object[]{MonitoringService.class.getSimpleName(), "onDestroy"});

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.getBoolean(PreferenceKey.user_initiated_shutdown.name(), false)) {
            Debugger.log(new Object[]{MonitoringService.class.getSimpleName(), "Android initiated shutdown"});
        } else {
            Debugger.log(new Object[]{MonitoringService.class.getSimpleName(), "User initiated shutdown"});
            sharedPreferences.edit().putBoolean(PreferenceKey.user_initiated_shutdown.name(), false).commit();
        }

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

        Debugger.finish();

        super.onDestroy();
    }
}
