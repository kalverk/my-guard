package com.myguard.model;

import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by user on 06.01.2018.
 */

public class MovementParameters implements Serializable {

    public final boolean enabled;
    public final long sensitivity;
    public final double scaledSensitivity;

    public MovementParameters(SharedPreferences sharedPreferences) {
        this.enabled = sharedPreferences.getBoolean("movement_enabled", false);
        this.sensitivity = Long.parseLong(sharedPreferences.getString("movement_sensitivity", String.valueOf(50)));
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
