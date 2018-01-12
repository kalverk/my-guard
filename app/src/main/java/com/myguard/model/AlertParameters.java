package com.myguard.model;

import android.content.SharedPreferences;

import com.myguard.PreferenceKey;
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

    public AlertParameters(SharedPreferences sharedPreferences) {
        this.soundAlertEnabled = sharedPreferences.getBoolean(PreferenceKey.sound_alert_enabled.name(), false);
        this.soundAlertAlarm = sharedPreferences.getString(PreferenceKey.sound_alert_alarm.name(), null);
        this.smsAlertEnabled = sharedPreferences.getBoolean(PreferenceKey.sms_alert_enabled.name(), false);
        this.callAlertEnabled = sharedPreferences.getBoolean(PreferenceKey.call_alert_enabled.name(), false);
        this.alertNumber = sharedPreferences.getString(PreferenceKey.alert_number.name(), null);
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
