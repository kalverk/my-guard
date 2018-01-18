package com.myguard.model;

import android.content.SharedPreferences;

import com.myguard.PreferenceKey;

import java.io.Serializable;

/**
 * Created by user on 06.01.2018.
 */

public class MovementParameters implements Serializable {

    public final boolean enabled;
    public final long sensitivity;
    public final double scaledSensitivity;

    public MovementParameters(SharedPreferences sharedPreferences) {
        this.enabled = sharedPreferences.getBoolean(PreferenceKey.movement_enabled.name(), Boolean.parseBoolean(PreferenceKey.movement_enabled.defaultValue));
        this.sensitivity = Math.abs(Long.parseLong(sharedPreferences.getString(PreferenceKey.movement_sensitivity.name(), PreferenceKey.movement_sensitivity.defaultValue)));
        this.scaledSensitivity = this.sensitivity / 100.0;
    }

    @Override
    public String toString() {
        return "MovementParameters{" +
                "enabled=" + enabled +
                ", sensitivity=" + sensitivity +
                '}';
    }
}
