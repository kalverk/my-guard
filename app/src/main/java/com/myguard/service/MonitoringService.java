package com.myguard.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.myguard.Constants;
import com.myguard.MainActivity;
import com.myguard.NotificationID;
import com.myguard.R;
import com.myguard.model.LocationParameters;
import com.myguard.model.MovementParameters;
import com.myguard.util.Debugger;

public class MonitoringService extends Service {

    private Intent acceleratorService;
    private Intent locationService;
    private BatteryLevelReceiver batteryLevelReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Debugger.writeToOutputStream("DEBUG", new Object[]{"Monitoring Service onStartCommand"});
        runInForeground();
        registerMonitoring(intent);

        batteryLevelReceiver = new BatteryLevelReceiver();
        registerReceiver(batteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));

        exeptionLogger();

        return START_STICKY;
    }

    private void exeptionLogger() {
        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                        Debugger.writeToOutputStream("DEBUG", new Object[]{paramThrowable.getMessage(), paramThrowable.getCause().getMessage(), paramThrowable.toString()});

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
                .setVibrate(new long[]{0L})
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

    private void registerMonitoring(Intent intent) {
        if (intent == null) {
            Debugger.writeToOutputStream("DEBUG", new Object[]{"registerMonitoring: Intent is null"});
        }

        MovementParameters movementParameters = (MovementParameters) intent.getSerializableExtra(Constants.MOVEMENT_PARAMETERS);
        LocationParameters locationParameters = (LocationParameters) intent.getSerializableExtra(Constants.LOCATION_PARAMETERS);

        if (movementParameters.enabled) {
            startMovementMonitoring(intent);
        }

        if (locationParameters.enabled) {
            startLocationMonitoring(intent);
        }
    }

    private void startLocationMonitoring(Intent intent) {
        if (intent == null) {
            Debugger.writeToOutputStream("DEBUG", new Object[]{"startLocationMonitoring: Intent is null"});
        }

        locationService = new Intent(this, LocationService.class);
        locationService.putExtra(Constants.LOCATION_PARAMETERS, intent.getSerializableExtra(Constants.LOCATION_PARAMETERS));
        locationService.putExtra(Constants.ALERT_PARAMETERS, intent.getSerializableExtra(Constants.ALERT_PARAMETERS));
        startService(locationService);
    }

    private void startMovementMonitoring(Intent intent) {
        if (intent == null) {
            Debugger.writeToOutputStream("DEBUG", new Object[]{"startMovementMonitoring: Intent is null"});
        }

        acceleratorService = new Intent(this, AcceleratorService.class);
        acceleratorService.putExtra(Constants.MOVEMENT_PARAMETERS, intent.getSerializableExtra(Constants.MOVEMENT_PARAMETERS));
        acceleratorService.putExtra(Constants.ALERT_PARAMETERS, intent.getSerializableExtra(Constants.ALERT_PARAMETERS));
        startService(acceleratorService);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Debugger.writeToOutputStream("DEBUG", new Object[]{"Monitoring Service onDestroy"});

        if (acceleratorService != null) {
            stopService(acceleratorService);
        }
        if (locationService != null) {
            stopService(locationService);
        }
        if (batteryLevelReceiver != null) {
            unregisterReceiver(batteryLevelReceiver);
        }

        Debugger.closeStreams();

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NotificationID.MONITORING.value);
        super.onDestroy();
    }
}
