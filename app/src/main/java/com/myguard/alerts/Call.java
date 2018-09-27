package com.myguard.alerts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.myguard.model.AlertParameters;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by kalver on 8/01/18.
 */

public class Call {

    private Call() {
    }

    private static long callDiff = 15000;
    private static long lastCall = 0;

    public static void call(final Context context, AlertParameters alertParameters) {
        long current = System.currentTimeMillis();
        if (lastCall == 0 || current - lastCall > callDiff) {
            Intent callIntent = new Intent(Intent.ACTION_CALL)
                    .setData(Uri.parse(String.format("tel:%s", alertParameters.managementNumber)))
                    .setFlags(FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(callIntent);
            lastCall = current;
        }
    }

}
