package com.myguard;

/**
 * Created by user on 07.01.2018.
 */

public enum PreferenceKey {
    sound_alert_enabled("true"),
    sound_alert_alarm("content://settings/system/alarm_alert"),
    sms_alert_enabled("false"),
    call_alert_enabled("false"),
    alert_number(""),

    location_enabled("false"),
    location_interval("60"),
    location_distance("5"),

    movement_enabled("true"),
    movement_sensitivity("75"),

    locked("false"),

    user_initiated_shutdown("false");

    public final String defaultValue;

    PreferenceKey(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
