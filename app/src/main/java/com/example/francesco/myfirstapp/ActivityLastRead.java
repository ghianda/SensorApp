package com.example.francesco.myfirstapp;

import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by francesco on 31/12/2016.
 */

public class ActivityLastRead extends ActivityAbstractReading {


    @Override
    public int myView() {
        return R.layout.activity_last_reading;
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


    @Override
    //display in a textview the result value and the time of the reading
    public void displayResult(Netsens response, Meter chosenMeter, Sensor chosenSensor) {

        //VALUE RESULT (set textView)
        TextView tvValue = (TextView) findViewById(R.id.tvDisplayValueResult);

        DecimalFormat frmt = new DecimalFormat(SensorProjectApp.valueFormat);

        String fixedValue = fixUnit(((SensorProjectApp) this.getApplication()).getLastValueFromMeterAndSensor(chosenMeter, chosenSensor)
                , chosenSensor.getUnitOfMeasure()
                , frmt);
        tvValue.setText(fixedValue);


        //TIME OF READING (set textView)
        boolean shortVersion = false;
        SensorProjectApp.fromMillisToDateOnTextView(
                ((SensorProjectApp) this.getApplication()).getLastTimestampFromMeterAndSensor(chosenMeter, chosenSensor),
                (TextView) findViewById(R.id.tvDisplayTimeResult),
                shortVersion);
    }


    @Override
    //Clear the textview of the result value and the time of the reading
    public void clearValueTextView() {
        ((TextView) findViewById(R.id.tvDisplayValueResult)).setText("");
        ((TextView) findViewById(R.id.tvDisplayTimeResult)).setText("");
    }


    private String fixUnit(double value, String unit, DecimalFormat frmt) {
        String fixedValue;
        String prefix = "";

        if (value < 1) {
            value = value * 1000;
            prefix = "m";
        }

        if (value > 1000) {
            value = value / 1000;
            prefix = "K";
        }

        fixedValue = frmt.format(value) + " " + prefix + unit;
        return fixedValue;
    }




}
