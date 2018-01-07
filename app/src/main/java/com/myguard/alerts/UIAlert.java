package com.myguard.alerts;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by user on 07.01.2018.
 */

public class UIAlert {

    public static void showAlert(Context context, int title, int message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .show();
    }

}
