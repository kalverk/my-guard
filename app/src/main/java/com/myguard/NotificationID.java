package com.myguard;

/**
 * Created by kalver on 28/12/17.
 */

public enum NotificationID {
    MONITORING(1),
    ACCELERATOR(2),
    LOCATION(3);

    public final int value;

    NotificationID(int value) {
        this.value = value;
    }
}
