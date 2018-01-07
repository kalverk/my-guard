package com.myguard.model;

import android.content.SharedPreferences;

import java.io.Serializable;

/**
 * Created by user on 06.01.2018.
 */

public class LocationParameters implements Serializable {

    public final boolean enabled;
    public final long interval;
    public final long distance;

    public LocationParameters(SharedPreferences sharedPreferences) {
        this.enabled = sharedPreferences.getBoolean("location_enabled", false);
        this.interval = Long.parseLong(sharedPreferences.getString("location_interval", String.valueOf(60)));
        this.distance = Long.parseLong(sharedPreferences.getString("location_distance", String.valueOf(5)));
    }

    @Override
    public String toString() {
        return "LocationParameters{" +
                "enabled=" + enabled +
                ", interval=" + interval +
                ", distance=" + distance +
                '}';
    }
}
