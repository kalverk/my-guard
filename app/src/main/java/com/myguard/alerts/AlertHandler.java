package com.myguard.alerts;

import android.content.Context;

import com.myguard.model.AlertParameters;

/**
 * Created by user on 07.01.2018.
 */

public class AlertHandler {

    private AlertHandler() {
    }

    //TODO shave to check alarm type here otherwise movement alarm and location alarm will trigger the same
    public static void handle(Context context, AlertParameters alertParameters) {
        if (alertParameters.soundAlertEnabled) {
            AlarmPlayer.start(context, alertParameters);
        }

        if (alertParameters.smsAlertEnabled) {
            SMS.send(alertParameters);
        }

        if (alertParameters.callAlertEnabled) {
            Call.call(context, alertParameters);
        }
    }

    public static void stop(Context context, AlertParameters alertParameters) {
        AlarmPlayer.stop(context, alertParameters);
    }

}
