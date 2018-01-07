package com.myguard;

import com.myguard.model.AlertParameters;

/**
 * Created by user on 07.01.2018.
 */

public class AlarmPlayer {

    public static void start(AlertParameters alertParameters) {
        if (alertParameters.soundAlertAlarm != null && !alertParameters.soundAlertAlarm.isPlaying()) {
            alertParameters.soundAlertAlarm.play();
        }
    }

    public static void stop(AlertParameters alertParameters) {
        if (alertParameters.soundAlertAlarm != null) {
            alertParameters.soundAlertAlarm.stop();
        }
    }

}
