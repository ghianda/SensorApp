package com.example.francesco.myfirstapp;

import android.widget.TextView;

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
    public void displayResult(Netsens response) {

        //value result (set textView)
        TextView tvValue = (TextView) findViewById(R.id.tvDisplayValueResult);

        //controllo sull'unicità del valore restituito
        if (response.getMeasuresList().size() == 1) {
            //TODO fare prima conversione!!!
            tvValue.setText(Float.toString(response.getMeasuresList().get(0).getValue()));
        }


        //time of reading
        //todo devo fare il metodo get dell'istante di lettura nella classe netsens


    }
}
