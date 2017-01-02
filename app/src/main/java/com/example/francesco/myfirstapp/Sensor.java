package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 28/12/2016.
 */

public class Sensor {

    private String urlCode;
    private String name;
    private long value;
    private String unit;


    //sensore vuoto
    public Sensor(String urlCode, String name) {
        this.urlCode = urlCode;
        this.name = name;
    }

    //sensore con valore già acquisito
    public Sensor(String urlCode, String unit, long value, String name) {
        this.urlCode = urlCode;
        this.unit = unit;
        this.value = value;
        this.name = name;
    }

    //insert the value
    public void setValue(long value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public String getUrlString() {
        return urlCode;
    }

    public String getName() {
        return name;
    }

    public long getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }
}
