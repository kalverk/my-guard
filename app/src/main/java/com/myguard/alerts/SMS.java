package com.myguard.alerts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.myguard.model.AlertParameters;

/**
 * Created by kalver on 8/01/18.
 */

public class SMS {

    public static void send(Context context, AlertParameters alertParameters) {
        context.startActivity(
                new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + alertParameters.alertNumber))
                        .putExtra("sms_body", getMessage(alertParameters.alertType.label))
        );
    }

    private static String getMessage(String alertType) {
        return String.format("Type %s alert has been triggered!", alertType);
    }

}
