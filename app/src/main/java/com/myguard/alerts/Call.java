package com.myguard.alerts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.myguard.model.AlertParameters;

/**
 * Created by kalver on 8/01/18.
 */

public class Call {

    private Call() {}

    public static void call(Context context, AlertParameters alertParameters) {
        context.startActivity(
                new Intent(Intent.ACTION_CALL)
                        .setData(Uri.parse(String.format("tel:%s", alertParameters.alertNumber)))
        );
    }

}
