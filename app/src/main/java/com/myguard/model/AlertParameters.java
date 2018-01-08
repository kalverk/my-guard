package com.myguard.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.myguard.alerts.AlertType;

import java.io.Serializable;

/**
 * Created by user on 06.01.2018.
 */

public class AlertParameters implements Serializable {

    public final boolean soundAlertEnabled;
    public final String soundAlertAlarm;

    public final boolean smsAlertEnabled;
    public final boolean callAlertEnabled;
    public final String alertNumber;
    public AlertType alertType;

    public AlertParameters(Context context, SharedPreferences sharedPreferences) {
        this.soundAlertEnabled = sharedPreferences.getBoolean("sound_alert_enabled", false);
        //TODO default alarm?
        this.soundAlertAlarm = sharedPreferences.getString("sound_alert_alarm", null);
        this.smsAlertEnabled = sharedPreferences.getBoolean("sms_alert_enabled", false);
        this.callAlertEnabled = sharedPreferences.getBoolean("call_alert_enabled", false);
        this.alertNumber = sharedPreferences.getString("alert_number", null);
    }

    @Override
    public String toString() {
        return "AlertParameters{" +
                "soundAlertEnabled=" + soundAlertEnabled +
                ", soundAlertAlarm=" + soundAlertAlarm +
                ", smsAlertEnabled=" + smsAlertEnabled +
                ", callAlertEnabled=" + callAlertEnabled +
                ", alertNumber='" + alertNumber + '\'' +
                '}';
    }
}
