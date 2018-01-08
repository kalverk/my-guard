package com.myguard.alerts;

/**
 * Created by kalver on 8/01/18.
 */

public enum AlertType {
    MOVEMENT("Movement"),
    LOCATION("Location");

    public final String label;

    AlertType(String label) {
        this.label = label;
    }
}
