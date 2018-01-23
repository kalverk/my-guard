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
        notifyBySMS(context, rawLevel, scale);

        exeptionLogger();
    }

    private void exeptionLogger() {
        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                        Debugger.writeToOutputStream("DEBUG", new Object[]{paramThrowable.getMessage(), paramThrowable.getCause().getMessage(), paramThrowable.toString()});

                        if (oldHandler != null) {
                            oldHandler.uncaughtException(
                                    paramThread,
                                    paramThrowable
                            );
                        } else {
                            System.exit(2);
                        }
                    }
                });
    }

    private void notifyBySMS(Context context, int rawLevel, int scale) {
        int level = rawLevel / scale;
        String alertNumber = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferenceKey.alert_number.name(), null);
        long current = System.currentTimeMillis();
        if (alertNumber != null && level > 0 && (lastSMS == 0 || current - lastSMS >= smsDiff)) {
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(alertNumber, null, String.format("Battery level is %s", level), null, null);

            lastSMS = current;

            Debugger.writeToOutputStream(this.getClass().getSimpleName(), new Object[]{rawLevel, scale, level, System.currentTimeMillis(), true});
        } else {
            Debugger.writeToOutputStream(this.getClass().getSimpleName(), new Object[]{rawLevel, scale, level, System.currentTimeMillis(), false});
        }
    }
}
