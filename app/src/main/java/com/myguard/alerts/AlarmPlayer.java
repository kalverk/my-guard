package com.myguard.alerts;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.myguard.model.AlertParameters;

/**
 * Created by user on 07.01.2018.
 */

public class AlarmPlayer {

    private AlarmPlayer() {
    }

    private static Ringtone ringtone;

    public static void start(Context context, AlertParameters alertParameters) {
        if (alertParameters.soundAlertAlarm != null) {
            Ringtone ringtone = getRingtone(context, alertParameters);
            if (ringtone != null && !ringtone.isPlaying()) {
                ringtone.play();
            }
        }
    }

    public static void stop(Context context) {
        try {
            RingtoneManager ringtoneManager = new RingtoneManager(context);
            ringtoneManager.stopPreviousRingtone();
        } catch (Exception e) {
            Log.e("ERROR", "Unable to stop ringtone", e);
        }
    }

    private static Ringtone getRingtone(Context context, AlertParameters alertParameters) {
        if (ringtone == null) {
            ringtone = RingtoneManager.getRingtone(context, Uri.parse(alertParameters.soundAlertAlarm));
        }
        return ringtone;
    }

}
