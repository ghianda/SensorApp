package com.example.francesco.myfirstapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_CAKE;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_FROM_TIME;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_CONVERSION_FACTOR;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_NAME;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_UNIT;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_TO_TIME;
import static java.lang.Math.abs;

public class ActivityCompare extends ActivityAbstractReading {

    //Attribute_------------------------------------------------------------------
    private final String powerTag = "POWER";
    private final String energyTag = "ENERGY";
    private final long diffWindowInMillis = 900000; //15 minuti - finestra per letture dei consumi (pre e post)


    private HashMap<String, Netsens> powerResponse;
    private HashMap<String, List<Netsens>> consumeResponse;

    private ArrayList<Sensor> sensors;
    private SensorList metersToControl;
    private Sensor selectedSensor;
    private String typeOfLecture = ""; //is "power" or "consume"
    private int countResponse;


    //bottoni per l'avvio dei picker dialog di selezione data/ora
    private Button btFromDate;
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

        // create NetworkManager
        networkManager = new NetworkManager(this);

        //allocate memory
        powerResponse = new HashMap<>();
        consumeResponse = new HashMap<>();
        countResponse = 0;

        //set bottom menu
        setListenerBottomMenu();

        //create list of object to control
        createSensorsList();

        //create list of meters with sensors list for eac meters
        metersToControl = new SensorList(sensors);

        //preparazione degli spinner dei Sensori
        setSensorSpinner();

        //set the current date and hour on view
        setCurrentDateOnBtText();
        setCurrentHourOnBtText();

        // Associo i picker dialog (Time e Date) ai bottoni
        addListenerOnButton();
    }


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
        // never used in this class
    }

    @Override
    protected void displayResult(Netsens response, Meter chosenMeter, Sensor chosenSensor) {
        //never used in this class
    }

    /**
     * Button compare method
     * here i create url, get request and store response for each meter
     * @param view
     */
    public void compareConsume(View view) {
        switch (typeOfLecture) {
            case powerTag : {
                for (Meter meter : metersToControl.getMeters()) {

                    String url = networkManager.createTimeReadUrl(meter, selectedSensor, fromDate, toDate);
                    ParseUrl(url);

                }
                break;
            }
            case energyTag : {
                for (Meter meter : metersToControl.getMeters()) {

                    String urlFrom = networkManager.createWindowUrl(meter, selectedSensor, fromDate, diffWindowInMillis);
                    String urlTo = networkManager.createWindowUrl(meter, selectedSensor, toDate, diffWindowInMillis);

                    ParseUrl(urlFrom);
                    ParseUrl(urlTo);
                }
                break;
            }
            default:
                Toast.makeText(this, "DEFAULT !!", Toast.LENGTH_SHORT);
        }
    }





    public void ParseUrl(String url)
    {
        NetworkManager.getInstance().getNetsensRequest(url, new SomeCustomListener<Netsens>()
        {
            @Override
            public void getResult(Netsens response)
            {
                //TODO DO WORK!
                saveResponse(response);

            }
        });

    }








    private void saveResponse(Netsens newResponse) {

        switch (typeOfLecture) {
            case powerTag : {
                System.out.println(" >>>>>...... saveResponse: case = power");

                if (powerResponse.size() < metersToControl.getMeters().size() - 1) {
                    //insert the response
                    powerResponse.put(newResponse.getMeasuresList().get(0).getMeter(), newResponse);

                } else {
                    if (powerResponse.size() == metersToControl.getMeters().size() - 1) {

                        //insert the last response
                        powerResponse.put(newResponse.getMeasuresList().get(0).getMeter(), newResponse);

                        prepareDataAndSendIt();
                    }
                }
            }
            break;



            case energyTag: {
                System.out.println("count: " + countResponse);

                if (consumeResponse.size() < metersToControl.getMeters().size() - 1){
                    //insert the response

                    String keyMeter = newResponse.getMeasuresList().get(0).getMeter();
                    System.out.println("keyMeter : " + keyMeter);
                    //check if there is some response of that meter
                    if(!consumeResponse.containsKey(keyMeter)){
                        // this newResponse is the first of that measure
                        ArrayList<Netsens> list = new ArrayList<>();
                        list.add(newResponse);
                        System.out.println("[A] inserted: " + newResponse.toString());
                        consumeResponse.put(keyMeter, list);
                        countResponse ++;
                    }
                    else{
                        // consumeResponse already contains the key
                        System.out.println("[B] inserted: " + newResponse.toString());
                        consumeResponse.get(keyMeter).add(newResponse);
                        countResponse ++;
                    }


                } else {

                    //insert the last meter

                    String keyMeter = newResponse.getMeasuresList().get(0).getMeter();
                    //check if there is some response of that meter
                    if (!consumeResponse.containsKey(keyMeter)) {
                        // this newResponse is the first of that measure
                        ArrayList<Netsens> list = new ArrayList<>();
                        list.add(newResponse);
                        System.out.println("[C] inserted: " + newResponse.toString());
                        consumeResponse.put(keyMeter, list);
                        countResponse ++;
                    } else {
                        // consumeResponse already contains the key
                        System.out.println("[D] inserted: " + newResponse.toString());
                        consumeResponse.get(keyMeter).add(newResponse);
                        countResponse ++;
                    }
                }

                //control if all response is inserted
                if (countResponse == (metersToControl.getMeters().size() * sensors.size())){

                    System.out.println("   -  -  - finish saveResponse -  -  -  ");
                    prepareDataAndSendIt();
                }

            }
        }
    }





    private void prepareDataAndSendIt(){

        //data to send
        HashMap<String, Float> data;

        //prepare data
        switch (typeOfLecture){
            case powerTag  : {
                System.out.println(" power case ");
                //calculate the Average
                data = doAverage();
                break;
            }
            case energyTag : {
                System.out.println(" consume case ");
                //calculate the Difference
                data = doDifference();
                break;
            }
            default: System.out.println(" default case "); data = null;
        }


        //Prepare to start cakeActivity to display results
        Intent intent = new Intent(this, ActivityCakeGraph.class);

        //put info in the intent
        selectedSensor.setConversionFactorByUrlCode();
        intent.putExtra(EXTRA_SENSOR_NAME, selectedSensor.getName());
        intent.putExtra(EXTRA_SENSOR_UNIT, selectedSensor.getUnitOfMeasure());
        intent.putExtra(EXTRA_SENSOR_CONVERSION_FACTOR, selectedSensor.getConversionFactor());
        intent.putExtra(EXTRA_FROM_TIME, fromDate.getTimeInMillis());
        intent.putExtra(EXTRA_TO_TIME, toDate.getTimeInMillis());

        //put data in the intent
        intent.putExtra(EXTRA_CAKE, data);

        //start activity
        startActivity(intent);

    }













    private HashMap<String, Float> doAverage(){

        HashMap<String, Float> averageMeasure = new HashMap<>();
        Double avg;
        double sum = 0;

        for ( String key : powerResponse.keySet()){
            for ( Measure m: powerResponse.get(key).getMeasuresList()){

                //todo l'IF è un controllo interno sui valori
                if (m.getValue() > 0)
                    sum += m.getValue();
            }

            avg = sum / powerResponse.get(key).getMeasuresList().size();
            averageMeasure.put(key, Float.valueOf(avg.toString()));
            sum = 0;
        }

        return averageMeasure;
    }


    private HashMap<String, Float> doDifference(){

        HashMap<String, Float> differences = new HashMap<>();

        for ( String key : consumeResponse.keySet()){
            float diff = abs ( consumeResponse.get(key).get(1).getMeasuresList().get(0).getValue()
                    - consumeResponse.get(key).get(0).getMeasuresList().get(0).getValue());
            differences.put(key, diff);
        }

        return differences;
    }




    protected void setSensorSpinner() {

        ArrayAdapter<String> spinSensorAdapter;

        //spinner object
        final Spinner sensorSpinner = (Spinner) findViewById(R.id.compareSensorSpinner);

        //dichiarazione Spinner adapters
        spinSensorAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);

        //add data
        spinSensorAdapter.addAll(getSensorsNames());
        sensorSpinner.setAdapter(spinSensorAdapter);

        //definizione del setOnItemSelectedListener per SensorSpinner
        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //salvataggio Oggetto Sensor scelto
                selectedSensor = sensors.get((int)id);
                //todo togliere
                System.out.println(" ++++++++++ ACTIVITY COMPARE -> " + selectedSensor.getName());

                //set type of parameter (power or consume):
                switch (selectedSensor.getUrlString()){
                    case "/actpw" : typeOfLecture = powerTag ; break;
                    case "/con"   : typeOfLecture = energyTag; break;
                    default:    break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }





    private void createSensorsList(){
        /*
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
        */
        sensors = new ArrayList<>();

        sensors.add(new Sensor("/actpw", "Active Power"));
        sensors.add(new Sensor("/con", "Active Energy"));
    }



    //for spinner dataset
    private ArrayList<String> getSensorsNames(){
        ArrayList<String> names = new ArrayList<>();

        for (Sensor sensor : sensors){
            names.add(sensor.getName());
        }

        return names;

    }




















    //listener for bottom menu bar
    private void setListenerBottomMenu() {
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        //TODO TRY
        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.getItem(3);
        item.setChecked(true);
        //TODO


        //set listener
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_bar_home:
                                start(ActivityHome.class);
                                break;

                            case R.id.action_bar_last_read:
                                start(ActivityLastRead.class);
                                break;

                            case R.id.action_bar_time_read:
                                start(ActivityTimeRead.class);
                                break;

                            case R.id.action_bar_consume:
                                start(ActivityCompare.class);
                                break;

                        }
                        return true;
                    }
                });
    }

    private void start(Class clazz){
        Intent intent = new Intent(getApplicationContext(), clazz);
        startActivity(intent);
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

}
