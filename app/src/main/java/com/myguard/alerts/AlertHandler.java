package com.myguard.alerts;

import android.content.Context;

import com.myguard.model.AlertParameters;

/**
 * Created by user on 07.01.2018.
 */

public class AlertHandler {

    public static void handle(Context context, AlertParameters alertParameters) {
        if (alertParameters.soundAlertEnabled) {
            AlarmPlayer.start(context, alertParameters);
        }

        if (alertParameters.smsAlertEnabled) {
            System.out.println("TODO");
        }

        if (alertParameters.callAlertEnabled) {
            System.out.println("TODO");
        }
    }

    public static void stop(Context context, AlertParameters alertParameters) {
        AlarmPlayer.stop(context, alertParameters);
    }

}
