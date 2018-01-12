package com.myguard.model;

import android.content.SharedPreferences;

import com.myguard.PreferenceKey;

import java.io.Serializable;

/**
 * Created by user on 06.01.2018.
 */

public class LocationParameters implements Serializable {

    public final boolean enabled;
    public final long interval;
    public final long distance;

    public LocationParameters(SharedPreferences sharedPreferences) {
        this.enabled = sharedPreferences.getBoolean(PreferenceKey.location_enabled.name(), false);
        this.interval = Long.parseLong(sharedPreferences.getString(PreferenceKey.location_interval.name(), String.valueOf(60)));
        this.distance = Long.parseLong(sharedPreferences.getString(PreferenceKey.location_distance.name(), String.valueOf(5)));
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
