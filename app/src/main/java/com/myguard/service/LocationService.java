package com.myguard.service;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.myguard.Constants;
import com.myguard.NotificationID;
import com.myguard.model.LocationParameters;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        runInForeground();
    }

    private void runInForeground() {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), this.getClass().getSimpleName())
                .setDefaults(Notification.DEFAULT_ALL);
        startForeground(NotificationID.LOCATION.value, notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerLocationListener((LocationParameters) intent.getSerializableExtra(Constants.LOCATION_PARAMETERS));
        return START_STICKY;
    }

    private void registerLocationListener(LocationParameters locationParameters) {
        final Context context = this;
        final LocationListener locationListener = new LocationListener() {
            Location lastLocation = null;

            @Override
            public void onLocationChanged(final Location location) {
                if (lastLocation != null && location.distanceTo(lastLocation) >= locationDistance) {
//                    AlarmPlayer.start(context);
                    Log.d(this.getClass().getSimpleName(), "ALARM");
                    Debugger.writeToOutputStream("Location", new double[]{location.getLongitude(), location.getLatitude(), System.currentTimeMillis(), 1});
                } else {
                    Debugger.writeToOutputStream("Location", new double[]{location.getLongitude(), location.getLatitude(), System.currentTimeMillis(), 0});
                }
                Log.d(this.getClass().getSimpleName(), String.format("Location change %s %s", location.getLongitude(), location.getLatitude()));
                lastLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(this.getClass().getSimpleName(), "Missing required permissions");
            return;
        }
        ((LocationManager) getSystemService(LOCATION_SERVICE)).requestLocationUpdates(LocationManager.GPS_PROVIDER, locationParameters.interval, locationParameters.distance, locationListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
