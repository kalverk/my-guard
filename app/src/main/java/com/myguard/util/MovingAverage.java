package com.myguard.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by kalver on 13/02/18.
 */

public class MovingAverage {
    private final int defaultPeriod = 100;

    private Queue<Double> window = new LinkedList<>();
    private int period = 0;
    private double sum = 0.0;

    public MovingAverage(int period) {
        this.period = period < 1 ? defaultPeriod : period;
    }

    public void add(double value) {
        sum = sum + value;
        window.add(value);
        if (window.size() > period) {
            sum = sum - window.remove();
        }
    }

    public double get() {
        if (window.isEmpty()) {
            return 0.0;
        }
        return sum / window.size();
    }
}
