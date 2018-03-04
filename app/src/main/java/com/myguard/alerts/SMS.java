package com.myguard.alerts;

import android.location.Location;
import android.telephony.SmsManager;

import com.myguard.model.AlertParameters;

/**
 * Created by kalver on 8/01/18.
 */

public class SMS {

    private SMS() {
    }

    private static long smsDiff = 20000;
    private static long lastAlertSMS = 0;
    private static long lastBatterySMS = 0;
    private static long lastLocationSMS = 0;

    public static void send(AlertParameters alertParameters) {
        long current = System.currentTimeMillis();
        if (lastAlertSMS == 0 || current - lastAlertSMS > smsDiff) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(alertParameters.managementNumber, null, getMessage(alertParameters), null, null);

            lastAlertSMS = current;
        }
    }

    private static String getMessage(AlertParameters alertParameters) {
        return alertParameters.alertMessage == null ? String.format("Type %s alert has been triggered!", alertParameters.alertType.label) : alertParameters.alertMessage;
    }

    public static void send(String number, String message) {
        long current = System.currentTimeMillis();
        if (lastBatterySMS == 0 || current - lastBatterySMS > smsDiff) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, message, null, null);

            lastBatterySMS = current;
        }
    }

    public static void send(String number, Location location) {
        long current = System.currentTimeMillis();
        if (lastLocationSMS == 0 || current - lastLocationSMS > smsDiff) {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, String.format("www.google.com/maps/place/%s,%s", location.getLatitude(), location.getLongitude()), null, null);

            lastLocationSMS = current;
        }
    }

}
