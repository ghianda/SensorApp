package com.example.francesco.myfirstapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_CAKE;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_FROM_TIME;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_CONVERSION_FACTOR;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_NAME;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_UNIT;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_TO_TIME;

public class ActivityCompare extends AppCompatActivity {

    //Attribute_------------------------------------------------------------------
    private HashMap<String, Netsens> savedResponse;

    ArrayList<Sensor> sensors = new ArrayList<Sensor>();
    ArrayList<Meter> metersToControl = new ArrayList<>();
    Sensor selectedSensor;
    private final  SensorList allSensors = new SensorList();


    private Button btFromDate; //bottoni per l'avvio dei picker dialog di selezione data/ora
    private Button btFromHour;
    private Button btToDate;
    private Button btToHour;

    //oggetti Calendario inizializzati a oggi
    private static Calendar fromDate = Calendar.getInstance();
    private static Calendar toDate = Calendar.getInstance();

    //key for Picker Dialog
    static final int DATE_FROM_DIALOG_ID = 911;
    static final int HOUR_FROM_DIALOG_ID = 611;
    static final int DATE_TO_DIALOG_ID = 999;
    static final int HOUR_TO_DIALOG_ID = 666;


    //Overrided Method -------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        //allocate memory
        savedResponse = new HashMap<>();

        //create list of object to control
        createSensorsList();
        createMeterToControlList();

        //preparazione degli spinner dei Sensori
        setSensorSpinner();

        //set the current date and hour on view
        setCurrentDateOnBtText();
        setCurrentHourOnBtText();

        // Associo i picker dialog (Time e Date) ai bottoni
        addListenerOnButton();
    }




    //Button compare method
    public void compareConsume(View view){
        String url;

        /** here i create url, get request and store response for each meter */
        for (Meter m : metersToControl){

            url = createUrl(m.getUrlString());
            ParseXmlUrl(url);
        }

    }




    public String createUrl(String meterUrl) {

        //read date and hour from Date and Hour Picker
        long fromMillis = fromDate.getTimeInMillis();
        long toMillis = toDate.getTimeInMillis();

        //costruisco l'url
        return getString(R.string.urlDomain)
                + getString(R.string.m) + meterUrl + selectedSensor.getUrlString()
                + getString(R.string.f) + fromMillis
                + getString(R.string.t) + toMillis;
    }




    private void extractResponse(Netsens newResponse){

        if (savedResponse.size() < metersToControl.size() - 1){
            //insert the response
            savedResponse.put(newResponse.getMeasuresList().get(0).getMeter(), newResponse);
        }
        else {
            if (savedResponse.size() == metersToControl.size() - 1) {

                //insert the last response
                savedResponse.put(newResponse.getMeasuresList().get(0).getMeter(), newResponse);

                workOnSavedResponse();
            }
        }
    }


    private void workOnSavedResponse(){

        //calculate the doAverage of value
        HashMap<String, Double> averageMeasure = doAverage();

        //TODO remove
        System.out.println("ActivityCOMPARE PRE INTENT: averageMeasure->");
        for( String meter: averageMeasure.keySet()){
            System.out.println(meter + "->  " + averageMeasure.get(meter));
        }
        //todo ************+


        //Prepare to start cakeActivity to display results
        Intent intent = new Intent(this, ActivityCakeGraph.class);

        //put data in the intent
        selectedSensor.setConversionFactorByUrlCode();


        System.out.println("ActivityCOMPARE PRE INTENT: unit-> " + selectedSensor.getUnitOfMeasure());
        intent.putExtra(EXTRA_CAKE, averageMeasure);
        intent.putExtra(EXTRA_SENSOR_NAME, selectedSensor.getName());
        intent.putExtra(EXTRA_SENSOR_UNIT, selectedSensor.getUnitOfMeasure());
        intent.putExtra(EXTRA_SENSOR_CONVERSION_FACTOR, selectedSensor.getConversionFactor());
        intent.putExtra(EXTRA_FROM_TIME, fromDate.getTimeInMillis());
        intent.putExtra(EXTRA_TO_TIME, toDate.getTimeInMillis());

        startActivity(intent);

    }




    private void ParseXmlUrl(String url){

        //connecting
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            //Request a XML response from the provided URL
            SimpleXmlRequest<Netsens> simpleRequest_netsens = new SimpleXmlRequest<Netsens>(
                    Request.Method.GET, url, Netsens.class,
                    new Response.Listener<Netsens>() {
                        @Override
                        public void onResponse(Netsens response) {
                            // override onResponse method
                            if (response.getMeasuresList() != null) {

                                extractResponse(response);

                            } else {

                                Toast.makeText(getApplicationContext(),
                                        R.string.text_toast_no_data, Toast.LENGTH_SHORT).show();
                            }

                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //override ErrorListener method
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    R.string.text_toast_net_error, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
            );

            // Add the request to the RequestQueue.
            queue.add(simpleRequest_netsens);   //agg richiesta
            // fine get___________________________________________________________________

        } else {
            //no connessione disponibile: avviso
            Toast.makeText(getApplicationContext(),
                    R.string.text_toast_net_error, Toast.LENGTH_SHORT).show();
        }
    }






    protected void setSensorSpinner() {

        ArrayAdapter<String> spinSensorAdapter;

        //spinner object
        final Spinner sensorSpinner = (Spinner) findViewById(R.id.compareSensorSpinner);

        //dichiarazione Spinner adapters
        spinSensorAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);

        //add data
        spinSensorAdapter.addAll(sensorsNames());
        sensorSpinner.setAdapter(spinSensorAdapter);

        //definizione del setOnItemSelectedListener per SensorSpinner
        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //salvataggio Oggetto Sensor scelto
                selectedSensor = sensors.get((int)id);
                //todo togliere
                System.out.println(" ++++++++++ ACTIVE COMPARE -> " + selectedSensor.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }





    private void createSensorsList(){
        sensors.add(new Sensor("/reactcon", "Reactive Energy"));
        sensors.add(new Sensor("/apcon", "Apparent Energy"));
        sensors.add(new Sensor("/con", "Active Energy"));
        sensors.add(new Sensor("/actpw", "Active Power"));
        sensors.add(new Sensor("/pwf", "Power Factor"));
        sensors.add(new Sensor("/cur/1", "Current - 1' phase"));
        sensors.add(new Sensor("/cur/2", "Current - 2' phase"));
        sensors.add(new Sensor("/cur/3", "Current - 3' phase"));
        sensors.add(new Sensor("/appw", "Apparent Power"));
        sensors.add(new Sensor("/reactpw", "Reactive Power"));
    }


    private void createMeterToControlList(){

        //TODO qui potrei fare una funzione che selezione le meter da esplorare in base al sensore scelto
        //todo - (ovvero, cerco le meter che hanno quel sensore a disosizione e le aggiungo a metersToControl
        metersToControl.add(new Meter("QG", "Blocco didattico"));
        metersToControl.add(new Meter("QS", "Blocco Sportivo"));
    }



    private ArrayList<String> sensorsNames(){
        ArrayList<String> names = new ArrayList<>();

        for (Sensor sensor : sensors){
            names.add(sensor.getName());
        }

        return names;

    }




    // display current date in Date button
    public void setCurrentDateOnBtText() {

        //aggancio i bottoni alla data ordierna
        btFromDate = (Button) findViewById(R.id.btFromDate);
        btToDate = (Button) findViewById(R.id.btToDate);

        setDisplayDate(btFromDate, fromDate);
        setDisplayDate(btToDate, toDate);

    }


    // display current hour in textView
    public void setCurrentHourOnBtText() {

        //aggancio le textview alle risorse xml
        btFromHour = (Button) findViewById(R.id.btFromHour);
        btToHour = (Button) findViewById(R.id.btToHour);

        setDisplayHour(btFromHour, fromDate);
        setDisplayHour(btToHour, toDate);

    }


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




    //dichiaro i Listener sui bottoni "date" e "time" di FROM e TO
    public void addListenerOnButton() {

        btFromDate = (Button) findViewById(R.id.btFromDate);
        btFromHour = (Button) findViewById(R.id.btFromHour);
        btToDate = (Button) findViewById(R.id.btToDate);
        btToHour = (Button) findViewById(R.id.btToHour);

        // definizione dei 4 OnClickListener per i bottoni
        View.OnClickListener dateFromListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompare.this.showDialog(DATE_FROM_DIALOG_ID);
            }
        });
        View.OnClickListener dateToListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompare.this.showDialog(DATE_TO_DIALOG_ID);
            }
        });
        View.OnClickListener hourFromListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompare.this.showDialog(HOUR_FROM_DIALOG_ID);
            }
        });
        View.OnClickListener hourToListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompare.this.showDialog(HOUR_TO_DIALOG_ID);
            }
        });

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
                return new DatePickerDialog(this, dateFromPickerListener, fromDate.get(Calendar.YEAR),
                        fromDate.get(Calendar.MONTH), fromDate.get(Calendar.DAY_OF_MONTH));
            case HOUR_FROM_DIALOG_ID:
                //  esegue il timePicker e ripesca l'ora selezionata per il FROM
                return new TimePickerDialog(this, timeFromPickerListener, fromDate.get(Calendar.HOUR_OF_DAY),
                        fromDate.get(Calendar.MINUTE), true);
            case DATE_TO_DIALOG_ID:
                //  esegue il datePicker e ripesca la data selezionata per il TO
                return new DatePickerDialog(this, dateToPickerListener, toDate.get(Calendar.YEAR),
                        toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH));
            case HOUR_TO_DIALOG_ID:
                //  esegue il hourPicker e ripesca la data selezionata per il TO
                return new TimePickerDialog(this, timeToPickerListener, toDate.get(Calendar.HOUR_OF_DAY),
                        toDate.get(Calendar.MINUTE), true);
        }
        return null;
    }


    //METODI PICKER

    //Dichiarazione picker DATE FROM
    private DatePickerDialog.OnDateSetListener dateFromPickerListener = (new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            //update fromDate object
            fromDate.set(selectedYear, selectedMonth, selectedDay);
            // update selected date on button text
            ActivityCompare.this.setDisplayDate(btFromDate, fromDate);
        }
    });


    //Dichiarazione  Picker TIME FROM
    private TimePickerDialog.OnTimeSetListener timeFromPickerListener = (new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            //update fromDate object
            fromDate.set(Calendar.HOUR_OF_DAY, selectedHour);
            fromDate.set(Calendar.MINUTE, selectedMinute);
            fromDate.set(Calendar.SECOND, 0);
            // update selected hour on button text
            ActivityCompare.this.setDisplayHour(btFromHour, fromDate);
        }
    });


    //Dichiarazione de Picker DATE TO
    private DatePickerDialog.OnDateSetListener dateToPickerListener = (new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            //update fromDate object
            toDate.set(selectedYear, selectedMonth, selectedDay);
            // update selected date on button text
            ActivityCompare.this.setDisplayDate(btToDate, toDate);

        }
    });


    //Dichiarazione de Picker TIME TO
    private TimePickerDialog.OnTimeSetListener timeToPickerListener = (new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {

            //update fromDate object
            toDate.set(Calendar.HOUR_OF_DAY, selectedHour);
            toDate.set(Calendar.MINUTE, selectedMinute);
            toDate.set(Calendar.SECOND, 0);
            // update selected hour on button text
            ActivityCompare.this.setDisplayHour(btToHour, toDate);
        }
    });




    private HashMap<String, Double> doAverage(){

        HashMap<String, Double> averageMeasure = new HashMap<>();
        Double avg = 0.0;

        for ( String key : savedResponse.keySet()){
            for ( Measure m: savedResponse.get(key).getMeasuresList()){
                avg += m.getValue();
            }
            avg /= savedResponse.get(key).getMeasuresList().size();
            averageMeasure.put(key, avg);
            avg = 0.0;
        }

        return averageMeasure;
    }

}
