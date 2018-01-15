package com.myguard.alerts;

import android.telephony.SmsManager;

import com.myguard.model.AlertParameters;

/**
 * Created by kalver on 8/01/18.
 */

public class SMS {

    private SMS() {
    }

    private static long smsDiff = 20000;
    private static long lastSMS = 0;

    public static void send(AlertParameters alertParameters) {
        long current = System.currentTimeMillis();
        if (lastSMS == 0 || current - lastSMS > smsDiff) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(alertParameters.alertNumber, null, getMessage(alertParameters), null, null);

            lastSMS = current;
        }
    }

    private static String getMessage(AlertParameters alertParameters) {
        return alertParameters.alertMessage == null ? String.format("Type %s alert has been triggered!", alertParameters.alertType.label) : alertParameters.alertMessage;
    }

}
