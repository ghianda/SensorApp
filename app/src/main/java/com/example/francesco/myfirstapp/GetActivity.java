package com.example.francesco.myfirstapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;
import java.util.Locale;

//import org.joda.time.DateTime;

/**
 * Created by francesco on 18/12/2016.
 */

public class GetActivity extends AppCompatActivity {

    //ATTRIBUTE ========= >>>>>>


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

    private String url;                //url completa per esecuzione get

    //key of input form
    public final static String EXTRA_MESSAGE = "com.example.francesco.MESSAGE";
    public final static String EXTRA_SENSOR_LR = "com.example.francesco.SENSOR_LR";

    //key for Picker Dialog
    static final int DATE_FROM_DIALOG_ID = 911;
    static final int HOUR_FROM_DIALOG_ID = 611;
    static final int DATE_TO_DIALOG_ID = 999;
    static final int HOUR_TO_DIALOG_ID = 666;



    //METHOD ========== >>>>>>>
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //assegnazione layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_activity);

        //set sensor spinner
        setSensorsSpinner();

        //set the current date and hour on view
        setCurrentDateOnView();
        setCurrentHourOnView();

        // Associo i picker dialog (Time e Date) ai bottoni "change"
        addListenerOnButton();

    }//fine onCreate()


    /* "INTERVAL TIME READING" button onclick method: */
    public void IntervalReading(View view){
        System.out.println("Interval Time reading-------------->>");

        createTimeUrl();

        ParseXmlUrl(); //TODO per ora stampa a video, poi restituirà i valori
    }





    /*  "LAST READING" button onclick method:    */
    public void lastReading(View view){
        System.out.println("Last reading-------------->>");

        createLastReadingUrl();

        ParseXmlUrl(); //TODO per ora stampa a video, poi restituirà i valori
    }


    public void createTimeUrl() {
        //read from the sensor from spinner
        Spinner spinner = (Spinner)findViewById(R.id.spinnerSensor);
        String chosenSensor = spinner.getSelectedItem().toString();

        //estraggo le date in millisecondi
        long fromMillis = fromDate.getTimeInMillis();
        long toMillis = toDate.getTimeInMillis();

        //TODO da togliere
        System.out.println(fromMillis);
        System.out.println(toMillis);


        //Costruisco l'url
        url = getString(R.string.urlDomain) +
                getString(R.string.m) + chosenSensor +
                getString(R.string.f) + fromMillis +
                getString(R.string.t) + toMillis;

        //TODO da togliere
        System.err.println("url appena creata da createTimeUrl:");
        System.out.println(url);

    }


    public void createLastReadingUrl() {
        //read from the sensor from spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinnerSensor);
        String chosenSensor = spinner.getSelectedItem().toString();

        //String url = getString(R.string.urlDomain) +
        url = getString(R.string.urlDomain)
                + getString(R.string.m) + chosenSensor
                + getString(R.string.lr) //last_reading
                + getString(R.string.f) + "0000000000000"
                + getString(R.string.t) + "0000000000000";

        //TODO da togliere
        System.out.println("url appena creata:");
        System.out.println(url);

    }





    // ====== metodo di rihiesta get XML (NETSENS)======
    public void ParseXmlUrl() {
        //TODO da togliee
        System.out.println("ParseXML_URL -------->");
        System.err.println(url);

        //connecting
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // avviso di connessione effettuata
            Toast.makeText(this,R.string.text_toast_net_ok,Toast.LENGTH_SHORT).show();


            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);

            //Request a XML response from the provided URL
            SimpleXmlRequest<Netsens> simpleRequest_netsens = new SimpleXmlRequest<Netsens>(
                    Request.Method.GET, url, Netsens.class,
                    new Response.Listener<Netsens>()
                    {
                        @Override
                        public void onResponse(Netsens response) {
                            //TODO da togliere
                            System.out.println("Netsens_onResponse()");

                            //TODO RESTITUIRO' I RISULTATI PER ELAB. GRAFICA SUCCESSIVA

                            System.out.println("Netsens_ getMeasures().getClass(): ");
                            for(Measure m : response.getMeasuresList()){
                                System.out.println(m.getMeter()+"  "+m.getValue());
                            }

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.err.println("onErrorResponse() - errore libreria Volley");
                            System.err.println(error.getMessage());
                            //TODO gestire un messaggio di erore (toast?) senz far crashare il prog
                        }
                    }
            );


            // Add the request to the RequestQueue.
            queue.add(simpleRequest_netsens);   //agg richiesta con nodi di tipo netsens per url vera
            // fine get___________________________________________________________________

        } else {
            //no connessione disponibile: avviso
            Toast.makeText(this,R.string.text_toast_net_error,Toast.LENGTH_SHORT).show();
        }
    }




    //dichiaro i Listener sui bottoni "date" e "time" di FROM e TO
    public void addListenerOnButton() {

        btFromDate = (Button) findViewById(R.id.btFromDate);
        btFromHour = (Button) findViewById(R.id.btFromHour);
        btToDate = (Button) findViewById(R.id.btToDate);
        btToHour = (Button) findViewById(R.id.btToHour);

        // definizione dei 4 OnClickListenere per i bottoni
        View.OnClickListener dateFromListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_FROM_DIALOG_ID);
            }
        };

        View.OnClickListener dateToListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_TO_DIALOG_ID);
            }
        };

        View.OnClickListener hourFromListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(HOUR_FROM_DIALOG_ID);
            }
        };

        View.OnClickListener hourToListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(HOUR_TO_DIALOG_ID);
            }
        };


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
                return new DatePickerDialog(this, dateFromPickerListener,year, month,day);
            case HOUR_FROM_DIALOG_ID:
                //esegue il timePicker e ripesca l'ora selezionata per il FROM
                return new TimePickerDialog(this, timeFromPickerListener,hour, minute, true);
            case DATE_TO_DIALOG_ID:
                //  esegue il datePicker e ripesca la data selezionata per il TO
                return new DatePickerDialog(this, dateToPickerListener,year, month,day);
            case HOUR_TO_DIALOG_ID:
                //  esegue il hourPicker e ripesca la data selezionata per il TO
                return new TimePickerDialog(this, timeToPickerListener,year, month, true);
        }
        return null;
    }


    //METODI PICKER
    private DatePickerDialog.OnDateSetListener dateFromPickerListener
            = new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

                    //update fromDate object
                    fromDate.set(selectedYear, selectedMonth, selectedDay);

                    //TODO d tolgie
                    System.out.println("fromDate modificato:  ");
                    System.out.println(fromDate.getTime());
                    System.out.println(fromDate.get(Calendar.YEAR) + "/" +
                            (fromDate.get(Calendar.MONTH) + 1) + "/" +
                            fromDate.get(Calendar.DAY_OF_MONTH) + " ");

                    // set selected date into textview
                    setDisplayDate(tvDisplayFromDate, fromDate);
        }
    };


    //Dichiarazione de Picker TIME FROM
    private TimePickerDialog.OnTimeSetListener timeFromPickerListener =
            new TimePickerDialog.OnTimeSetListener(){

                // when dialog box is closed, below method will be called.
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute){

                    //update fromDate object
                    fromDate.set(Calendar.HOUR_OF_DAY, selectedHour);
                    fromDate.set(Calendar.MINUTE, selectedMinute);
                    fromDate.set(Calendar.SECOND, 0);

                    //TODO d tolgie
                    System.out.println("fromTime modificato:  ");
                    System.out.println(fromDate.getTime());
                    System.out.println(fromDate.get(Calendar.HOUR_OF_DAY) + ":" +
                            fromDate.get(Calendar.MINUTE) + ":" +
                            fromDate.get(Calendar.SECOND) + " ");

                    //set selected date into textview
                    setDisplayHour(tvDisplayFromHour, fromDate);
                }

            };


    //Dichiarazione de Picker DATE TO
    private DatePickerDialog.OnDateSetListener dateToPickerListener
            = new DatePickerDialog.OnDateSetListener() {

                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {

                    //update fromDate object
                    toDate.set(selectedYear, selectedMonth, selectedDay);

                    //TODO d tolgie
                    System.out.println("toDate modificato:  ");
                    System.out.println(toDate.getTime());
                    System.out.println(toDate.get(Calendar.YEAR) + "/" +
                            (toDate.get(Calendar.MONTH) + 1) + "/" +
                            toDate.get(Calendar.DAY_OF_MONTH) + " ");


                    // set selected date into textview
                    setDisplayDate(tvDisplayToDate, toDate);
        }
    };


    //Dichiarazione de Picker TIME TO
    private TimePickerDialog.OnTimeSetListener timeToPickerListener =
            new TimePickerDialog.OnTimeSetListener(){

                // when dialog box is closed, below method will be called.
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute){

                    //update fromDate object
                    toDate.set(Calendar.HOUR_OF_DAY, selectedHour);
                    toDate.set(Calendar.MINUTE, selectedMinute);
                    toDate.set(Calendar.SECOND, 0);

                    //TODO d tolgie
                    System.out.println("fromTime modificato:  ");
                    System.out.println(toDate.getTime());
                    System.out.println(toDate.get(Calendar.HOUR_OF_DAY) + ":" +
                            toDate.get(Calendar.MINUTE) + ":" +
                            toDate.get(Calendar.SECOND) + " ");


                    //set selected date into textview
                    setDisplayHour(tvDisplayToHour, toDate);
                }

            };



    // Inserimento della data nelle textView in input
    protected void setDisplayDate(TextView tv, Calendar cal) {
        tv.setText(new StringBuilder()
                .append(cal.get(Calendar.DAY_OF_MONTH)).append("-")
                .append(cal.get(Calendar.MONTH) + 1).append("-")
                .append(cal.get(Calendar.YEAR)).append(" "));
    }

    // Inserimento dell'ora nelle textView in input
    protected void setDisplayHour(TextView tv, Calendar cal) {
        tv.setText(new StringBuilder()
                .append(String.format(Locale.getDefault(), "%02d", cal.get(Calendar.HOUR_OF_DAY)))
                .append(":")
                .append(String.format(Locale.getDefault(), "%02d", cal.get(Calendar.MINUTE)))
                .append(" "));
    }






    // display current date in textView
    public void setCurrentDateOnView() {


        //aggancio le textview alle risorse xml
        tvDisplayFromDate   = (TextView) findViewById(R.id.selectedFromDate);
        tvDisplayToDate     = (TextView) findViewById(R.id.selectedToDate);


        year = fromDate.get(Calendar.YEAR);
        month = fromDate.get(Calendar.MONTH);
        day = fromDate.get(Calendar.DAY_OF_MONTH);

        // set current date into textview FROM
        tvDisplayFromDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        // set current date into textview TO
        tvDisplayToDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

    }




    // display current hour in textView
    public void setCurrentHourOnView() {

        //aggancio le textview alle risorse xml
        tvDisplayFromHour   = (TextView) findViewById(R.id.selectedFromHour);
        tvDisplayToHour     = (TextView) findViewById(R.id.selectedToHour);

        //estraggo l'ora odierna
        //final Calendar c = Calendar.getInstance();
        hour = fromDate.get(Calendar.HOUR_OF_DAY);
        minute = fromDate.get(Calendar.MINUTE);

        // set current hour into textview FROM
        tvDisplayFromHour.setText(new StringBuilder()
                .append(hour).append("-").append(minute).append(" "));

        // set current hour into textview TO
        tvDisplayToHour.setText(new StringBuilder()
                .append(hour).append("-").append(minute).append(" "));

    }





    /* Set the content of sensor spinner menu */
    public void setSensorsSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerSensor);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sensorName_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }



















}
