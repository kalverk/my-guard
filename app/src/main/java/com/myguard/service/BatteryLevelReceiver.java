package com.myguard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;

import com.myguard.PreferenceKey;
import com.myguard.util.Debugger;

/**
 * Created by kalver on 11/01/18.
 */

public class BatteryLevelReceiver extends BroadcastReceiver {

    private long smsDiff = 3600000;
    private long lastSMS = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);
        notifyBySMS(context, (rawLevel * 100) / scale);
    }

    private void notifyBySMS(Context context, int level) {
        String alertNumber = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKey.alert_number.name(), null);
        long current = System.currentTimeMillis();
        if (alertNumber != null && level > 0 && (lastSMS == 0 || current - lastSMS >= smsDiff)) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(alertNumber, null, String.format("Battery level is %s", level), null, null);

            lastSMS = current;

            Debugger.writeToOutputStream(this.getClass().getSimpleName(), new Object[]{level, System.currentTimeMillis(), true});
        } else {
            Debugger.writeToOutputStream(this.getClass().getSimpleName(), new Object[]{level, System.currentTimeMillis(), false});
        }
    }
}
