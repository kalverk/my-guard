package com.myguard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

/**
 * Created by kalver on 11/01/18.
 */

public class BatteryLevelReceiver extends BroadcastReceiver {

    private static long smsDiff = 3600000;
    private static long lastSMS = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
        notifyBySMS(context, (rawLevel * 100) / scale);
    }

    private void notifyBySMS(Context context, int level) {
        String alertNumber = PreferenceManager.getDefaultSharedPreferences(context).getString("alert_number", null);
        long current = System.currentTimeMillis();
        if (alertNumber != null && level > 0 && (lastSMS == 0 || current - lastSMS >= smsDiff)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(alertNumber, null, String.format("Battery level is %s", level), null, null);

            lastSMS = current;
        }
    }
}
