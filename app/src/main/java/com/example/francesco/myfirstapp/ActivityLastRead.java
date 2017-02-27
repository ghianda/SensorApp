package com.example.francesco.myfirstapp;

import android.widget.TextView;

import static com.example.francesco.myfirstapp.SensorProjectApp.fixUnit;

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

        url = networkManager.createLastReadUrl(chosenMeter, chosenSensor);
    }






    @Override
    //display in a textview the result value and the time of the reading
    public void displayResult(Netsens response, Meter chosenMeter, Sensor chosenSensor) {

        //VALUE RESULT (set textView)
        TextView tvValue = (TextView) findViewById(R.id.tvDisplayValueResult);
        TextView tvTimestamp = (TextView) findViewById(R.id.tvDisplayTimeResult);

        //create the sensor object and put data in it
        Sensor ss = new Sensor(chosenSensor.getUrlString(), chosenSensor.getName());
        //put data into object
        ss.addValue(response.getMeasuresList().get(0).getValue()
                 , response.getMeasuresList().get(0).getTimeStamp());
        ss.setConversionFactorByUrlCode();

        //display data on textview
        setValueInTv(ss.getDatas().get(0).getValue() / ss.getConversionFactor(), ss.getUnitOfMeasure(), tvValue);
        setTimeInTv(ss.getDatas().get(0), tvTimestamp);

    }



    private void setTimeInTv(Data data, TextView tv) {
        boolean shortVersion = true;
        SensorProjectApp.fromMillisToDateOnTextView(data.getTimestamp(), tv, shortVersion);
    }



    //set the value in the textview with correct format and unit of measure
    private void setValueInTv(double v, String unit, TextView tv) {

        String stringValue = fixUnit(v, unit);
        tv.setText(stringValue);
    }











}
