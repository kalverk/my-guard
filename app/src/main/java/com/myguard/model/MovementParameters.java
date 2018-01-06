package com.myguard.model;

import android.content.SharedPreferences;

/**
 * Created by user on 06.01.2018.
 */

public class MovementParameters {

    public final boolean enabled;
    public final long sensitivity;

    public MovementParameters(SharedPreferences sharedPreferences) {
        this.enabled = sharedPreferences.getBoolean("movement_enabled", false);
        this.sensitivity = Long.parseLong(sharedPreferences.getString("movement_sensitivity", String.valueOf(50)));
    }

    @Override
    public String toString() {
        return "MovementParameters{" +
                "enabled=" + enabled +
                ", sensitivity=" + sensitivity +
                '}';
    }
}
