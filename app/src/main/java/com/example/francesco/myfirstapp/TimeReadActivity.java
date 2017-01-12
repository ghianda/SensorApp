package com.example.francesco.myfirstapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

public class TimeReadActivity extends AbstractReadingActivity {

    //Attribute_------------------------------------------------------------------
    //TODO in futuro da togliere______
    //TODO indispensabili se uso il Dialog per i picker, ma dovrò sostituirlo con i FRAGMENT
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    //TODO _________________

    private TextView tvDisplayFromDate;  // text view per la visualizzazione della data selezionata
    private TextView tvDisplayToDate;
    private TextView tvDisplayFromHour;
    private TextView tvDisplayToHour;

    private Button btFromDate; //bottoni per l'avvio dei picker dialog di selezione data/ora
    private Button btFromHour;
    private Button btToDate;
    private Button btToHour;

    //oggetti Calendario inizializzati a oggi
    private static Calendar fromDate = Calendar.getInstance();
    private static Calendar toDate = Calendar.getInstance();

    //key of input form
    public final static String EXTRA_MESSAGE = "com.example.francesco.MESSAGE";
    public final static String EXTRA_SENSOR_LR = "com.example.francesco.SENSOR_LR";

    //key for Picker Dialog
    static final int DATE_FROM_DIALOG_ID = 911;
    static final int HOUR_FROM_DIALOG_ID = 611;
    static final int DATE_TO_DIALOG_ID = 999;
    static final int HOUR_TO_DIALOG_ID = 666;


    //Overrided Method -------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(myView());

        //preparazione degli spinner dei Sensori
        setSensorsSpinner();

        //set the current date and hour on view
        setCurrentDateOnBtText();
        setCurrentHourOnBtText();

        // Associo i picker dialog (Time e Date) ai bottoni
        addListenerOnButton();


    }


    @Override
    public void displayResult(Netsens response) {
        //TODO intent to new activiy (mean, count and graph]
    }

    ;

    @Override
    public int myView() {
        return R.layout.time_reading_activity;
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

        //read date and hour from Date and Hour Picker
        long fromMillis = fromDate.getTimeInMillis();
        long toMillis = toDate.getTimeInMillis();

        //TODO da togliere
        System.out.println(fromMillis);
        System.out.println(toMillis);

        //costruisco l'url
        url = getString(R.string.urlDomain)
                + getString(R.string.m) + chosenMeter + chosenSensor
                + getString(R.string.f) + fromMillis
                + getString(R.string.t) + toMillis;


    }


    // Method_----------------------------------------------------------


    // display current date in Date button
    public void setCurrentDateOnBtText() {

        //aggancio i bottoni alla data ordierna
        btFromDate = (Button) findViewById(R.id.btFromDate);
        btToDate = (Button) findViewById(R.id.btToDate);


        year = fromDate.get(Calendar.YEAR);
        month = fromDate.get(Calendar.MONTH);
        day = fromDate.get(Calendar.DAY_OF_MONTH);

        // set current date into buttons FROM
        btFromDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // set current date into button TO
        btToDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

    }


    // display current hour in textView
    public void setCurrentHourOnBtText() {

        //aggancio le textview alle risorse xml
        btFromHour = (Button) findViewById(R.id.btFromHour);
        btToHour = (Button) findViewById(R.id.btToHour);

        //estraggo l'ora odierna
        //final Calendar c = Calendar.getInstance();
        hour = fromDate.get(Calendar.HOUR_OF_DAY);
        minute = fromDate.get(Calendar.MINUTE);

        // set current hour into buttons from
        btFromHour.setText(new StringBuilder()
                .append(hour).append(":").append(minute).append(" "));

        // set current hour into button TO
        btToHour.setText(new StringBuilder()
                .append(hour).append(":").append(minute).append(" "));

    }


    //dichiaro i Listener sui bottoni "date" e "time" di FROM e TO
    public void addListenerOnButton() {

        btFromDate = (Button) findViewById(R.id.btFromDate);
        btFromHour = (Button) findViewById(R.id.btFromHour);
        btToDate = (Button) findViewById(R.id.btToDate);
        btToHour = (Button) findViewById(R.id.btToHour);

        // definizione dei 4 OnClickListener per i bottoni
        View.OnClickListener dateFromListener = ((View v) -> showDialog(DATE_FROM_DIALOG_ID));
        View.OnClickListener dateToListener = ((View v) -> showDialog(DATE_TO_DIALOG_ID));
        View.OnClickListener hourFromListener = ((View v) -> showDialog(HOUR_FROM_DIALOG_ID));
        View.OnClickListener hourToListener = ((View v) -> showDialog(HOUR_TO_DIALOG_ID));

        //associazione listener ai bottoni
        btFromDate.setOnClickListener(dateFromListener);
        btToDate.setOnClickListener(dateToListener);
        btFromHour.setOnClickListener(hourFromListener);
        btToHour.setOnClickListener(hourToListener);
    }


    //selezione del picker da eseguire
    @Override
    protected Dialog onCreateDialog(int id) {
        //seleziono il picker in base al dialog creato (date o hour)
        switch (id) {
            case DATE_FROM_DIALOG_ID:
                //  esegue il datePicker e ripesca la data selezionata per il FROM
                return new DatePickerDialog(this, dateFromPickerListener, year, month, day);
            case HOUR_FROM_DIALOG_ID:
                //esegue il timePicker e ripesca l'ora selezionata per il FROM
                return new TimePickerDialog(this, timeFromPickerListener, hour, minute, true);
            case DATE_TO_DIALOG_ID:
                //  esegue il datePicker e ripesca la data selezionata per il TO
                return new DatePickerDialog(this, dateToPickerListener, year, month, day);
            case HOUR_TO_DIALOG_ID:
                //  esegue il hourPicker e ripesca la data selezionata per il TO
                return new TimePickerDialog(this, timeToPickerListener, year, month, true);
        }
        return null;
    }


    //METODI PICKER

    //Dichiarazione picker DATE FROM
    private DatePickerDialog.OnDateSetListener dateFromPickerListener = ((DatePicker view, int selectedYear,
                                                                          int selectedMonth, int selectedDay) -> {
        //update fromDate object
        fromDate.set(selectedYear, selectedMonth, selectedDay);
        // update selected date on button text
        setDisplayDate(btFromDate, fromDate);
    });


    //Dichiarazione  Picker TIME FROM
    private TimePickerDialog.OnTimeSetListener timeFromPickerListener = ((TimePicker view, int selectedHour,
                                                                          int selectedMinute) -> {
        //update fromDate object
        fromDate.set(Calendar.HOUR_OF_DAY, selectedHour);
        fromDate.set(Calendar.MINUTE, selectedMinute);
        fromDate.set(Calendar.SECOND, 0);
        //set selected date into textview
        setDisplayHour(btFromHour, fromDate);
    });


    //Dichiarazione de Picker DATE TO
    private DatePickerDialog.OnDateSetListener dateToPickerListener = ((DatePicker view, int selectedYear,
                                                                        int selectedMonth, int selectedDay) -> {

        //update fromDate object
        toDate.set(selectedYear, selectedMonth, selectedDay);
        // set selected date into textview
        setDisplayDate(btToDate, toDate);

    });


    //Dichiarazione de Picker TIME TO
    private TimePickerDialog.OnTimeSetListener timeToPickerListener = ((TimePicker view, int selectedHour,
                                                                        int selectedMinute) -> {

        //update fromDate object
        toDate.set(Calendar.HOUR_OF_DAY, selectedHour);
        toDate.set(Calendar.MINUTE, selectedMinute);
        toDate.set(Calendar.SECOND, 0);
        //set selected date into textview
        setDisplayHour(btToHour, toDate);
    });


    // Setting selected Date on button text
    protected void setDisplayDate(Button bt, Calendar cal) {
        bt.setText(new StringBuilder()
                .append(cal.get(Calendar.DAY_OF_MONTH)).append("-")
                .append(cal.get(Calendar.MONTH) + 1).append("-")
                .append(cal.get(Calendar.YEAR)).append(" "));
    }

    // Setting selected Hour on button text
    protected void setDisplayHour(Button bt, Calendar cal) {
        bt.setText(new StringBuilder()
                .append(String.format(Locale.getDefault(), "%02d", cal.get(Calendar.HOUR_OF_DAY)))
                .append(":")
                .append(String.format(Locale.getDefault(), "%02d", cal.get(Calendar.MINUTE)))
                .append(" "));
    }


}
