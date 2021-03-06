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
    public final String managementNumber;

    public AlertType alertType;
    public String alertMessage;

    public final long initTime = 30000;

    public AlertParameters(SharedPreferences sharedPreferences) {
        this.soundAlertEnabled = sharedPreferences.getBoolean(PreferenceKey.sound_alert_enabled.name(), Boolean.parseBoolean(PreferenceKey.sound_alert_enabled.defaultValue));
        this.soundAlertAlarm = sharedPreferences.getString(PreferenceKey.sound_alert_alarm.name(), PreferenceKey.sound_alert_alarm.defaultValue);
        this.smsAlertEnabled = sharedPreferences.getBoolean(PreferenceKey.sms_alert_enabled.name(), Boolean.parseBoolean(PreferenceKey.sms_alert_enabled.defaultValue));
        this.callAlertEnabled = sharedPreferences.getBoolean(PreferenceKey.call_alert_enabled.name(), Boolean.parseBoolean(PreferenceKey.call_alert_enabled.defaultValue));
        this.managementNumber = sharedPreferences.getString(PreferenceKey.management_number.name(), PreferenceKey.management_number.defaultValue);
    }

    @Override
    public String toString() {
        return "AlertParameters{" +
                "soundAlertEnabled=" + soundAlertEnabled +
                ", soundAlertAlarm=" + soundAlertAlarm +
                ", smsAlertEnabled=" + smsAlertEnabled +
                ", callAlertEnabled=" + callAlertEnabled +
                ", managementNumber='" + managementNumber + '\'' +
                '}';
    }
}
