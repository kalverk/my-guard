package com.myguard.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.myguard.R;
import com.myguard.alerts.AlertHandler;
import com.myguard.alerts.AlertType;
import com.myguard.alerts.UIAlert;
import com.myguard.model.AlertParameters;
import com.myguard.model.LocationParameters;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by kalver on 24/01/18.
 */

public class LocationMonitoring {

    public static LocationListener register(final Context context, final LocationParameters locationParameters, final AlertParameters alertParameters) {
        alertParameters.alertType = AlertType.LOCATION;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            UIAlert.showAlert(context, R.string.title_location_unavailable, R.string.description_location_unavailable);
            return null;
        }

        final LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            final LocationListener locationListener = new LocationListener() {
                Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                @Override
                public void onLocationChanged(final Location location) {
                    if (lastLocation != null && location.distanceTo(lastLocation) >= locationParameters.distance) {
                        alertParameters.alertMessage = String.format("Alert! www.google.com/maps/place/%s,%s", location.getLatitude(), location.getLongitude());
                        AlertHandler.handle(context, alertParameters);
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

            return locationListener;
        } else {
            UIAlert.showAlert(context, R.string.title_location_manager_unavailable, R.string.description_location_manager_unavailable);
        }
        return null;
    }

    public static void unregister(final Context context, final LocationListener locationListener) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        locationManager.removeUpdates(locationListener);
    }

}
