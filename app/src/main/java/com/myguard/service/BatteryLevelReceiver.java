package com.myguard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.myguard.PreferenceKey;
import com.myguard.R;
import com.myguard.alerts.SMS;

/**
 * Created by kalver on 24/01/18.
 */

public class BatteryLevelReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String managementNumber = sharedPreferences.getString(PreferenceKey.management_number.name(), PreferenceKey.management_number.defaultValue);
        if (managementNumber.length() > 0) {
            SMS.send(managementNumber, context.getString(R.string.battery_low));
        }
    }
}