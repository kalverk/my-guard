package com.myguard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

import com.myguard.Constants;
import com.myguard.PreferenceKey;
import com.myguard.alerts.SMS;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by kalver on 26/01/18.
 */

public class SMSListener extends BroadcastReceiver {

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
                        if (smsMessage.getMessageBody().trim().equalsIgnoreCase(sharedPreferences.getString(PreferenceKey.location_keyword.name(), PreferenceKey.location_keyword.defaultValue))) {
                            final LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                            if (locationManager != null) {
                                SMS.send(
                                        smsMessage.getOriginatingAddress(),
                                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                );
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
