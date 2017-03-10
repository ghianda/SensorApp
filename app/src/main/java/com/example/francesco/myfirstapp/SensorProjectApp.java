package com.example.francesco.myfirstapp;

import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by francesco on 11/01/2017.
 */


public class SensorProjectApp extends Application {


    //key string for intent object
    public final static String EXTRA_PARCDATARESPONSE = "com.example.francesco.PARCDATARESPONSE";
    public final static String EXTRA_SENSOR = "com.example.francesco.SENSOR";
    public final static String EXTRA_METER = "com.example.francesco.METER";
    public final static String EXTRA_LIGHT = "com.example.francesco.AVERAGELIGHT";
    public final static String EXTRA_ACTPOWER = "com.example.francesco.AVERAGEACTPOWER";
    public final static String EXTRA_PROBLEM_DETECTED = "com.example.francesco.PROBLEM_DETECTED";
    public final static String EXTRA_SUGGESTED_ACTION = "com.example.francesco.SUGGESTED_ACTION";
    public final static String EXTRA_DATA_CAKE = "com.example.francesco.CAKE";
    public final static String EXTRA_SENSOR_NAME = "com.example.francesco.EXTRA_SENSOR_NAME";
    public final static String EXTRA_SENSOR_UNIT = "com.example.francesco.EXTRA_SENSOR_UNIT";
    public final static String EXTRA_SENSOR_CONVERSION_FACTOR = "com.example.francesco.EXTRA_SENSOR_CONVERSION_FACTOR";
    public final static String EXTRA_FROM_TIME = "com.example.francesco.EXTRA_FROM_TIME";
    public final static String EXTRA_TO_TIME = "com.example.francesco.EXTRA_TO_TIME";

    //Key for savedInstanceState
    public final static String KEY_FRAGMENT_SAVED = "com.example.francesco.KEY_FRAGMENT_SAVED";
    public final static String HOME_FRAG_TAG = "com.example.francesco.HOME_FRAG_TAG";
    public final static String LASTREAD_FRAG_TAG= "com.example.francesco.LASTREAD_FRAG_TAG";
    public final static String TIMEREAD_FRAG_TAG= "com.example.francesco.TIMEREAD_FRAG_TAG";
    public final static String COMPARE_FRAG_TAG= "com.example.francesco.COMPARE_FRAG_TAG";

    //KEY FO PREFERENCE VALUES
    public static final String co2PrefTag = "CO2";
    public static final String euroPrefTag = "EURO";
    public static final String clockPositionPrefTag = "POSITION";
    public static final String clockPMillisPrefTag = "MILLIS";
    public static final String serviceOnOffPfreTag = "PREF_SERVICE_ON_OFF";

    //formatter
    static final public String valueFormat = "###.##";
    static final public String notifyValueFormat = "###.#";

    //parametri di default
    static final public long windowInMillis = 900000; //15 minuti - finestra per ultime letture del servizio per poi farne la media
    static final public long windowYesterdayConsumeRequest = 1800000; //30 minuti - finestra per le letture dei consumi di inizio e fine giornata
    static final public int defaultServiceRepeatPeriodPosition = 3; //30 minuti
    static final public Boolean defaultNotifyActivated = true;
    static final public float defaultEuroForWattHour = (float)0.166646;
    static final public float defaultCO2ForWattHour = (float)0.14;
    static public ArrayList<Long> clockChoice;




    //Global data (here we store the sensor value(s)
    static private SensorList globalSensorData; //data with only name parameter (like the spinner menu)





    //METHOD________________________________________________________________________________


    public static Sensor createParceableDataResponse(Netsens response, Sensor chosenSensor) {
        //crea un oggetto Sensor (quindi Parceable) dai dati in response

        Sensor ss = new Sensor(chosenSensor.getUrlString(), chosenSensor.getName());
        ss.setConversionFactorByUrlCode();
        //put data into object
        for (Measure m : response.getMeasuresList()) {
            ss.addValue(m.getValue() / ss.getConversionFactor(), m.getTimeStamp());
        }

        return ss;

    }

    public static SensorList getGlobalSensorData(Context context) {

        globalSensorData = new SensorList(context.getApplicationContext());
        return globalSensorData;
    }


    public static void fromMillisToDateOnTextView(Long millis, TextView tv, boolean shortVersion) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);

        int second = cal.get(Calendar.SECOND);
        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);


        if (shortVersion) {

            String timeStamp = String.format(Locale.getDefault(),
                    "%02d:%02d:%02d - %02d/%02d/%04d", hour, minute, second, day, month, year);
            tv.setText(timeStamp);
        } else {

            String timeStamp = String.format(Locale.getDefault(),
                    "Read at:  %02d:%02d:%02d  of  %02d/%02d/%04d", hour, minute, second, day, month, year);
            tv.setText(timeStamp);
        }

    }


    public static String fixUnit(double value, String unit) {
        String fixedValueString;
        String prefix = "";

        DecimalFormat frmt = new DecimalFormat(SensorProjectApp.valueFormat);

        if (!unit.equals(" ")) {
            //parameter is not Power Factor, then i fix the unit:
            if (value < 1) {
                value = value * 1000;
                prefix = "m";
            }

            if (value > 1000) {
                value = value / 1000;
                prefix = "k";
            }
        }

        fixedValueString = frmt.format(value) + " " + prefix + unit;
        return fixedValueString;
    }



    public static String fixEuro(double value, String unit) {
        String fixedValueString;
        String prefix = "";

        DecimalFormat frmt = new DecimalFormat(SensorProjectApp.valueFormat);

        fixedValueString = frmt.format(value) + " " + prefix + unit;
        return fixedValueString;
    }



    public static String findPrefixOfMeasure(Sensor ss) {
        String prefix = "";

        double exampleValue = ss.getDatas().get(0).getValue();
        String unit = ss.getUnitOfMeasure();

        if (!unit.equals(" ")) {
            //parameter is not Power Factor, then i fix the unit:
            if (exampleValue  < 1) {
                prefix = "m";
            }

            if (exampleValue  > 1000) {
                prefix = "k";
            }
        }

        return prefix;
    }


    public static String fixUnit(double value, String unit, DecimalFormat frmt) {
        String fixedValue;
        String prefix = "";

        if (!unit.equals(" ")) {
            //parameter is not Power Factor, then i fix the unit:
            if (value < 1) {
                value = value * 1000;
                prefix = "m";
            }

            if (value > 1000) {
                value = value / 1000;
                prefix = "k";
            }
        }

        fixedValue = frmt.format(value) + " " + prefix + unit;
        return fixedValue;
    }



    public static void createClockChoiceArray() {

        clockChoice = new ArrayList<>();

        clockChoice.add((long)30000);
        clockChoice.add((long)300000);
        clockChoice.add((long)600000);
        clockChoice.add((long)1800000);
        clockChoice.add((long)3600000);

    }

}
