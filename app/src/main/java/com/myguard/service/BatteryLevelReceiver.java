package com.myguard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.myguard.util.Debugger;

/**
 * Created by kalver on 24/01/18.
 */

public class BatteryLevelReceiver extends BroadcastReceiver {

    private long smsDiff = 3600000;
    private long lastSMS = 0;

    //TODO send SMS when level is low

    @Override
    public void onReceive(Context context, Intent intent) {
        Debugger.log(new Object[]{BatteryLevelReceiver.class.getSimpleName(), "onReceive"});

        int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);

        Debugger.log(new Object[]{BatteryLevelReceiver.class.getSimpleName(), rawLevel, scale, true});

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int rawLevel2 = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale2 = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        Debugger.log(new Object[]{BatteryLevelReceiver.class.getSimpleName(), rawLevel2, scale2, true});
    }
}