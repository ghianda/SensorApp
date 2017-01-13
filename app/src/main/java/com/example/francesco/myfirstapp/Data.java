package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 11/01/2017.
 */

public class Data {
    private double value;
    private long timestamp;

    //costructor
    public Data(double value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "value " + "/ " + timestamp;
    }
}
