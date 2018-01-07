package com.myguard;

import com.myguard.model.AlertParameters;

/**
 * Created by user on 07.01.2018.
 */

public class AlertHandler {

    public static void handle(AlertParameters alertParameters) {
        if (alertParameters.soundAlertEnabled) {
            AlarmPlayer.start(alertParameters);
        }

        if (alertParameters.smsAlertEnabled) {
            throw new RuntimeException("TODO");
        }

        if (alertParameters.callAlertEnabled) {
            throw new RuntimeException("TODO");
        }
    }

    public static void stop(AlertParameters alertParameters) {
        AlarmPlayer.stop(alertParameters);
        //TODO stop all no matter if they were running or not
    }

}
