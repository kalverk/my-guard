package com.myguard.alerts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.myguard.model.AlertParameters;

/**
 * Created by kalver on 8/01/18.
 */

public class Call {

    private Call() {
    }
    
    private static long callDiff = 5000;
    private static long lastCall = 0;

    public static void call(Context context, AlertParameters alertParameters) {
        long current = System.currentTimeMillis();
        if (lastCall == 0 || current - lastCall >= callDiff) {
            context.startActivity(
                    new Intent(Intent.ACTION_CALL)
                            .setData(Uri.parse(String.format("tel:%s", alertParameters.alertNumber)))
            );

            lastCall = current;
        }
    }

}
