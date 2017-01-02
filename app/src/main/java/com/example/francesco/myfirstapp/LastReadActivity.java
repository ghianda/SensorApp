package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 31/12/2016.
 */

public class LastReadActivity extends AbstractReadingActivity {

    @Override
    public int myView() {
        return R.layout.last_reading_activity;
    }


    @Override
    public int getIdMeterSpinner() {
        return R.id.MeterSpinner;
    }

    @Override
    public int getIdSensorSpinner() {
        return R.id.SensorSpinner;
    }

    @Override
    public void createUrl() {

        url = getString(R.string.urlDomain)
                + getString(R.string.m)
                + chosenMeter.getUrlString()
                + chosenSensor.getUrlString()
                + getString(R.string.lr);
        System.out.println(" LAST READ URL CREATED:" + url);
    }
}
