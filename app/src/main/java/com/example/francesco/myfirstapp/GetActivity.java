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

import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by francesco on 18/12/2016.
 */

public class GetActivity extends AppCompatActivity {

    //ATTRIBUTE ========= >>>>>>
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private TextView tvDisplayFromDate;  // text view per la visualizzazione della data selezionata
    private TextView tvDisplayToDate;
    private TextView tvDisplayFromHour;
    private TextView tvDisplayToHour;

    private Button btFromDate; //bottoni per l'avvio dei picker dialog di selezione data/ora
    private Button btFromHour;
    private Button btToDate;
    private Button btToHour;


    private String url; //url completa per esecuzione get

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


    /* "INTERVAL TIME READING" button onclick methos: */
    public void IntervalReading(View view){
        System.out.println("Interval Time reading-------------->>");

        //TODO *_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_       <<<<<<----------
        url = createUrl(false);
        //TODO *_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_*_

        ParseXmlUrl(); //TODO per ora stampa a video, poi restituirà i valori
    }




    /*  "LAST READING" button onclick method:    */
    public void lastReading(View view){
        System.out.println("Last reading-------------->>");

        url = createUrl(true);

        ParseXmlUrl(); //TODO per ora stampa a video, poi restituirà i valori
    }




    //Create the url - if LastReading = false take the Date and Hour for FROM and TO in milliseconds
    public String createUrl(boolean LastReading){
        //read from the sensor from spinner
        Spinner spinner = (Spinner)findViewById(R.id.spinnerSensor);
        String chosenSensor = spinner.getSelectedItem().toString();

        //TODO da togliere
        System.out.println(chosenSensor);
        System.out.println("value getted V");

        //costruisco la url
        //String url = "http://live.netsens.it/export/xml_export_2A.php?user=temp&password=5lkz1d&station=723&meter=QG/Lighting&from=1480503600000&to=1480507200000" ;

        if(LastReading) {
            String url = getString(R.string.urlDomain) +
                    getString(R.string.m) + chosenSensor +
                    getString(R.string.lr) + //last_reading
                    getString(R.string.f) + "0000000000000" +
                    getString(R.string.t) + "0000000000000";
            /*
            String url = new String(getString(R.string.urlDomain)
                    .concat(getString(R.string.m)).concat(chosenSensor)
                    .concat(getString(R.string.lr))
                    .concat(getString(R.string.f)).concat("0000000000000")
                    .concat(getString(R.string.t)).concat("0000000000000"));
                    */

            //TODO da togliere
            System.out.println("url appena creata:");
            System.out.println(url);

            return url;
        } else //interval time request:
        {
            //estraggo l'istante  FROM e l'istante TO (date + time)
            // Results example: "2-5-2012 20:43"
            String stringFrom = tvDisplayFromDate.toString()+" "+tvDisplayFromHour.toString();
            String stringTo = tvDisplayToDate.toString()+" "+tvDisplayToHour.toString();

            //TODO da controllare il formato del formatter:   ---|
            SimpleDateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm"); // I assume d-M, you may refer to M-d for month-day instead.

            //converto in oggetti Date
            try{
                Date dateFrom = formatter.parse(stringFrom);
                Date dateTo = formatter.parse(stringTo);

                //converto in millisecondi
                long fromMillis = dateFrom.getTime();
                long toMillis   = dateTo.getTime();

                //Costruisco l'url
                String url = getString(R.string.urlDomain) +
                        getString(R.string.m) + chosenSensor +
                        getString(R.string.f) + fromMillis +
                        getString(R.string.t) + toMillis;

                //TODO da togliere
                System.out.println("url appena creata:");
                System.out.println(url);

                return url;


            }
            catch(ParseException e){
                //System.out.println(" ****** ECCEZIONE CONVERSIONE STRING -> DATE nella 'createUrl()' ****");
                }

        }
        return "FAILED";
    }






    // ====== metodo di rihiesta get XML (NETSENS)======
    public void ParseXmlUrl() {
        //TODO da togliee
        System.out.println("PArseXMLURL -------->");

        //connecting
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // avviso di connessione effettuata
            Toast.makeText(this,R.string.text_toast_net_ok,Toast.LENGTH_SHORT).show();

            // FETCH DATA_________________________________________________________________
            final TextView mTextView = (TextView) findViewById(R.id.text_response);

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
                            // error Object
                            mTextView.setText("onErrorResponse()");
                            System.out.println("onErrorResponse()");
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




    //Dichiarazione de Picker DATE FROM
    private DatePickerDialog.OnDateSetListener dateFromPickerListener
            = new DatePickerDialog.OnDateSetListener() {

                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
                    Date d = new Date();
                    Calendar cal = Calendar.getInstance();

                    year = selectedYear;
                    month = selectedMonth;
                    day = selectedDay;

                    // set selected date into textview
                    setDisplayDate(tvDisplayFromDate,day,month,year);
        }
    };


    //Dichiarazione de Picker TIME FROM
    private TimePickerDialog.OnTimeSetListener timeFromPickerListener =
            new TimePickerDialog.OnTimeSetListener(){

                // when dialog box is closed, below method will be called.
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute){
                    hour    = selectedHour;
                    minute  = selectedMinute;

                    //set selected date into textview
                    setDisplayHour(tvDisplayFromHour,hour,minute);
                }

            };


    //Dichiarazione de Picker DATE TO
    private DatePickerDialog.OnDateSetListener dateToPickerListener
            = new DatePickerDialog.OnDateSetListener() {

                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    year = selectedYear;
                    month = selectedMonth;
                    day = selectedDay;

                    // set selected date into textview
                    setDisplayDate(tvDisplayToDate,day,month,year);
        }
    };


    //Dichiarazione de Picker TIME TO
    private TimePickerDialog.OnTimeSetListener timeToPickerListener =
            new TimePickerDialog.OnTimeSetListener(){

                // when dialog box is closed, below method will be called.
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute){
                    hour    = selectedHour;
                    minute  = selectedMinute;

                    //set selected date into textview
                    setDisplayHour(tvDisplayToHour,hour,minute);
                }

            };



    // Inserimento della data nelle textView in input
    protected void setDisplayDate(TextView tv, int d, int m, int y){
        tv.setText(new StringBuilder()
                .append(d).append("-").append(m+1).append("-").append(y).append(" "));
    }

    // Inserimento dell'ora nelle textView in input
    protected void setDisplayHour(TextView tv, int h, int m){
        tv.setText(new StringBuilder()
                .append(String.format(Locale.getDefault(),"%02d", h)).append(":").append(m).append(" "));
                //.append(String.format("%02d", h)).append(":").append(m).append(" "));
    }








    // display current date in textView
    public void setCurrentDateOnView() {

        //aggancio le textview alle risorse xml
        tvDisplayFromDate   = (TextView) findViewById(R.id.selectedFromDate);
        tvDisplayToDate     = (TextView) findViewById(R.id.selectedToDate);

        //estraggo la data odierna
        final Calendar c = Calendar.getInstance();
        year    = c.get(Calendar.YEAR);
        month   = c.get(Calendar.MONTH);
        day     = c.get(Calendar.DAY_OF_MONTH);

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
        final Calendar c = Calendar.getInstance();
        hour    = c.get(Calendar.HOUR);
        minute  = c.get(Calendar.MINUTE);

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
