package com.myguard.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.myguard.util.Debugger;

/**
 * Created by kalver on 24/01/18.
 */

public class BatteryLevelReceiver extends BroadcastReceiver {

    private long smsDiff = 3600000;
    private long lastSMS = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Debugger.writeToOutputStream("DEBUG", new Object[]{"Battery Level Receiver onReceive"});

        int rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 1);

        Debugger.writeToOutputStream(this.getClass().getSimpleName(), new Object[]{rawLevel, scale, true});
    }
}