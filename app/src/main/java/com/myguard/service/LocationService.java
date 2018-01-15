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

import com.myguard.Constants;
import com.myguard.NotificationID;
import com.myguard.R;
import com.myguard.alerts.AlertHandler;
import com.myguard.alerts.AlertType;
import com.myguard.alerts.UIAlert;
import com.myguard.model.AlertParameters;
import com.myguard.model.LocationParameters;
import com.myguard.util.Debugger;

public class LocationService extends Service {

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
        registerLocationListener((LocationParameters) intent.getSerializableExtra(Constants.LOCATION_PARAMETERS), (AlertParameters) intent.getSerializableExtra(Constants.ALERT_PARAMETERS));
        return START_STICKY;
    }

    private void registerLocationListener(final LocationParameters locationParameters, final AlertParameters alertParameters) {
        alertParameters.alertType = AlertType.LOCATION;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            UIAlert.showAlert(this, R.string.title_location_unavailable, R.string.description_location_unavailable);
            return;
        }

        final Context context = this;
        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            final LocationListener locationListener = new LocationListener() {
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                @Override
                public void onLocationChanged(final Location location) {
                    if (lastLocation != null && location.distanceTo(lastLocation) >= locationParameters.distance) {
                        alertParameters.alertMessage = String.format("Alert! www.google.com/maps/place/%s,%s", Math.round(location.getLatitude() * 1000000) / 1000000, Math.round(location.getLongitude() * 1000000) / 1000000);
                        Debugger.writeToOutputStream(this.getClass().getSimpleName(), new Object[]{location.getLatitude(), location.getLongitude(), System.currentTimeMillis(), true});
                        AlertHandler.handle(context, alertParameters);
                    } else {
                        Debugger.writeToOutputStream(this.getClass().getSimpleName(), new Object[]{location.getLatitude(), location.getLongitude(), System.currentTimeMillis(), false});
                    }
                    lastLocation = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    //Nothing to do here
                }

                @Override
                public void onProviderEnabled(String provider) {
                    //Nothing to do here
                }

                @Override
                public void onProviderDisabled(String provider) {
                    //Nothing to do here
                }
            };

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, locationParameters.interval, locationParameters.distance, locationListener);
        } else {
            UIAlert.showAlert(this, R.string.title_location_manager_unavailable, R.string.description_location_manager_unavailable);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
