package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 11/01/2017.
 */

public class Data {
    private long value;
    private long timestamp;

    //costructor
    public Data(long value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public long getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
