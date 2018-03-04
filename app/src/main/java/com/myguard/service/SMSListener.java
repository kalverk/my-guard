package com.myguard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

import com.myguard.Constants;
import com.myguard.MainActivity;
import com.myguard.PreferenceKey;
import com.myguard.alerts.SMS;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by kalver on 26/01/18.
 */

public class SMSListener extends BroadcastReceiver {

    private class SMSResponse {
        public final Context context;
        public final String originatingAddress;
        public final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                SMS.send(originatingAddress, location);
                final LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                if (locationManager != null) {
                    locationManager.removeUpdates(locationListener);
                }
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

        private SMSResponse(Context context, String originatingAddress) {
            this.context = context;
            this.originatingAddress = originatingAddress;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPreferences.getBoolean(PreferenceKey.location_via_sms.name(), Boolean.parseBoolean(PreferenceKey.location_via_sms.defaultValue)) && intent.getAction().equals(Constants.SMS_RECEIVED)) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                try {
                    final Object[] pdus = (Object[]) bundle.get(Constants.PDUS);
                    final SmsMessage[] smsMessages = new SmsMessage[pdus.length];
                    for (int i = 0; i < smsMessages.length; i++) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        if (getNumber(smsMessage.getOriginatingAddress()).equals(getNumber(sharedPreferences.getString(PreferenceKey.management_number.name(), PreferenceKey.management_number.defaultValue)))) {
                            handleLocationRequest(context, sharedPreferences, smsMessage);
                            handleUnlock(sharedPreferences, smsMessage);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleUnlock(SharedPreferences sharedPreferences, SmsMessage smsMessage) {
        if (smsMessage.getMessageBody().trim().equalsIgnoreCase(sharedPreferences.getString(PreferenceKey.unlock_keyword.name(), PreferenceKey.unlock_keyword.defaultValue).trim())) {
            boolean unlockSMS = MainActivity.unlockSMS();
            if (!unlockSMS) {
                MonitoringService.unlockSMS();
            }
        }
    }

    private void handleLocationRequest(Context context, SharedPreferences sharedPreferences, SmsMessage smsMessage) {
        if (smsMessage.getMessageBody().trim().equalsIgnoreCase(sharedPreferences.getString(PreferenceKey.location_keyword.name(), PreferenceKey.location_keyword.defaultValue).trim())) {
            final LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new SMSResponse(context, smsMessage.getOriginatingAddress()).locationListener);
            }
        }
    }

    private String getNumber(String string) {
        return string.replaceAll("[^\\d.]", "");
    }

}
