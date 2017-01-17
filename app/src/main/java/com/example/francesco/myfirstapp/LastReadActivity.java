package com.example.francesco.myfirstapp;

import android.widget.TextView;

import java.text.DecimalFormat;

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


    @Override
    //display in a textview the result value and the time of the reading
    public void displayResult(Netsens response, Meter chosenMeter, Sensor chosenSensor) {

        //VALUE RESULT (set textView)
        TextView tvValue = (TextView) findViewById(R.id.tvDisplayValueResult);

        DecimalFormat frmt = new DecimalFormat(SensorProjectApp.valueFormat);
        String value = frmt.format(((SensorProjectApp) this.getApplication()).getLastValueFromMeterAndSensor(
                chosenMeter, chosenSensor));
        tvValue.setText(value + " " + chosenSensor.getUnitOfMeasure());


        //TIME OF READING (set textView)
        boolean shortVersion = false;
        SensorProjectApp.fromMillisToDateOnTextView(
                ((SensorProjectApp) this.getApplication()).getLastTimestampFromMeterAndSensor(chosenMeter, chosenSensor),
                (TextView) findViewById(R.id.tvDisplayTimeResult),
                shortVersion);


    }
}
