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
    private NotificationManager notificationManager;
    private BatteryLevelReceiver batteryLevelReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runInForeground();
        registerMonitoring(intent);

        batteryLevelReceiver = new BatteryLevelReceiver();
        registerReceiver(batteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));

        return START_STICKY;
    }

    private void runInForeground() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent monitoringServiceIntent = new Intent(this, MonitoringService.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                monitoringServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(this.getClass().getSimpleName(), getResources().getString(R.string.title_alerts_enabled), NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), this.getClass().getSimpleName())
                .setDefaults(Notification.DEFAULT_LIGHTS)
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
        locationService = new Intent(this, LocationService.class);
        locationService.putExtra(Constants.LOCATION_PARAMETERS, intent.getSerializableExtra(Constants.LOCATION_PARAMETERS));
        locationService.putExtra(Constants.ALERT_PARAMETERS, intent.getSerializableExtra(Constants.ALERT_PARAMETERS));
        startService(locationService);
    }

    private void startMovementMonitoring(Intent intent) {
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

        notificationManager.cancel(NotificationID.MONITORING.value);
        super.onDestroy();
    }
}
