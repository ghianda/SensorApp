package com.example.francesco.myfirstapp;

import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;

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
        tvValue.setText(value + " " +
                ((SensorProjectApp) this.getApplication()).getUnitOfMeasureFromSensor(chosenSensor));


        //TIME OF READING (set textView)
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(
                ((SensorProjectApp) this.getApplication()).
                        getLastTimestampFromMeterAndSensor(chosenMeter, chosenSensor));

        //TODO VERSIONE INGLESE - capire come settare (se si può) il formato mm/dd/yyyy di %tD in versione dd/mm/yyyy
        String timestamp = String.format(Locale.getDefault(),
                "Read at:  %tl:%tM %tp  of  %tD", cal, cal, cal, cal);

        //TODO VERSIONE ITALIANA (bruttino così :) )
        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        String timeStamp = String.format(Locale.getDefault(),
                "Read at:  %02d:%02d:%02d  of  %02d/%02d/%04d", hour, minute, second, day, month, year);


        TextView tvTimestamp = (TextView) findViewById(R.id.tvDisplayTimeResult);
        tvTimestamp.setText(timeStamp);

    }
}
