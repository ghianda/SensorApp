package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 28/12/2016.
 */

public class Meter {


    private String urlCode;
    private String name;

    public Meter(String urlCode, String name) {
        this.urlCode = urlCode;
        this.name = name;
    }


    public String getUrlString() {
        return urlCode;
    }

    public String getName() {
        return name;
    }
}
