package com.example.francesco.myfirstapp;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_DATA_CAKE;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_FROM_TIME;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_CONVERSION_FACTOR;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_NAME;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_UNIT;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_TO_TIME;
import static java.lang.Math.abs;


public class FragmentCompare extends Fragment {

    //Attribute_------------------------------------------------------------------
    private final String powerTag = "POWER";
    private final String energyTag = "ENERGY";
    private final long diffWindowInMillis = 900000; //15 minuti - finestra per letture dei consumi (pre e post)

    protected NetworkManager networkManager;

    //sturtture dati che conterranno le risposte della query
    private HashMap<String, Netsens> powerResponse;
    private HashMap<String, List<Netsens>> consumeResponse;

    //oggetti Calendario inizializzati a oggi
    private static Calendar fromDate = Calendar.getInstance();
    private static Calendar toDate = Calendar.getInstance();
    private Boolean timeRangeIsOk = false;

    private Spinner sensorSpinner;
    private ImageButton compareButton;
    private ProgressBar progressBar;

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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_compare, container, false);

        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        findAndSaveInputOutputResource(view);

        // create NetworkManager
        networkManager = new NetworkManager(getActivity().getApplicationContext());

        //allocate memory
        powerResponse = new HashMap<>();
        consumeResponse = new HashMap<>();
        countResponse = 0;

        //create list of object to control
        sensors = createSensorsList();

        //create list of meters with sensors list for each meters
        metersToControl = new SensorList(sensors, getActivity());

        //preparazione degli spinner dei Sensori
        setSensorsSpinner();

        //set the current date and hour on view
        setCurrentDateOnBtText();
        setCurrentHourOnBtText();
        checkIfSelectionIsValid();

        // Associo i picker dialog (Time e Date) ai bottoni
        addListenerOnPickerButton();

        //associo il metodo listener al bottone Compare
        setCompareButtonListener();

    }


    // -------------------------------------- not overrided method ----------------------------


    private void findAndSaveInputOutputResource(View view){
        //find and save layout variables

        sensorSpinner = (Spinner) getView().findViewById(R.id.SensorSpinner);

        compareButton = (ImageButton) view.findViewById(R.id.btCompare);

        btFromDate = (Button) view.findViewById(R.id.btFromDate);
        btToDate = (Button) view.findViewById(R.id.btToDate);
        btFromHour = (Button) view.findViewById(R.id.btFromHour);
        btToHour = (Button) view.findViewById(R.id.btToHour);

        progressBar = (ProgressBar)view.findViewById(R.id.progCompare);
        progressBar.setVisibility(View.GONE); //make invisible

    }



    private void setCompareButtonListener(){
        compareButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(timeRangeIsOk) {

                    //display the progress bar
                    progressBar.setVisibility(View.VISIBLE);

                    switch (typeOfLecture) {
                        case powerTag : {
                            for (Meter meter : metersToControl.getMeters()) {

                                String url = networkManager.createTimeReadUrl(meter, selectedSensor, fromDate, toDate);
                                System.out.println(url);
                                ParseUrl(url);

                            }
                            break;
                        }
                        case energyTag : {
                            for (Meter meter : metersToControl.getMeters()) {

                                String urlFrom = networkManager.createWindowUrl(meter, selectedSensor, fromDate, diffWindowInMillis);
                                String urlTo = networkManager.createWindowUrl(meter, selectedSensor, toDate, diffWindowInMillis);

                                System.out.println(urlTo);
                                ParseUrl(urlFrom);
                                ParseUrl(urlTo);
                            }
                            break;
                        }
                        default: break;
                    }
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.badTimeSelection), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void ParseUrl(String url)
    {
        NetworkManager.getInstance().getNetsensRequest(url, new CustomListener<Netsens>()
        {
            @Override
            public void getResult(Netsens response)
            {

                saveResponse(response);

            }
        });

    }


    private void saveResponse(Netsens newResponse) {

        switch (typeOfLecture) {
            case powerTag : {

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

                if (consumeResponse.size() < metersToControl.getMeters().size() - 1){

                    //insert the response
                    String keyMeter = newResponse.getMeasuresList().get(0).getMeter();

                    //check if there is some response of that meter
                    if(!consumeResponse.containsKey(keyMeter)){
                        // this newResponse is the first of that measure
                        ArrayList<Netsens> list = new ArrayList<>();
                        list.add(newResponse);

                        consumeResponse.put(keyMeter, list);
                        countResponse ++;
                    }
                    else{
                        // consumeResponse already contains the key

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

                        consumeResponse.put(keyMeter, list);
                        countResponse ++;
                    } else {

                        // consumeResponse already contains the key
                        consumeResponse.get(keyMeter).add(newResponse);
                        countResponse ++;
                    }
                }

                //control if all response is inserted
                if (countResponse == (metersToControl.getMeters().size() * sensors.size())){

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

                //calculate the Average
                data = doAverage();
                break;
            }
            case energyTag : {

                //calculate the Difference
                data = doDifference();
                break;
            }
            default: data = null;
        }

        HashMap<String, Float> dataForCake = parseMeterName(data);


        //Prepare to start cakeActivity to display results
        Intent intent = new Intent(getActivity(), ActivityCakeGraph.class);

        //put info in the intent
        selectedSensor.setConversionFactorByUrlCode();
        intent.putExtra(EXTRA_SENSOR_NAME, selectedSensor.getName());
        intent.putExtra(EXTRA_SENSOR_UNIT, selectedSensor.getUnitOfMeasure());
        intent.putExtra(EXTRA_SENSOR_CONVERSION_FACTOR, selectedSensor.getConversionFactor());
        intent.putExtra(EXTRA_FROM_TIME, fromDate.getTimeInMillis());
        intent.putExtra(EXTRA_TO_TIME, toDate.getTimeInMillis());

        //put data in the intent
        intent.putExtra(EXTRA_DATA_CAKE, data);

        //hide the progress bar
        progressBar.setVisibility(View.GONE);

        //start activity
        startActivity(intent);

    }

    private HashMap<String, Float> parseMeterName(HashMap<String, Float> data) {
        //change the netsens meter key with right user key word

        Resources Res = getActivity().getApplicationContext().getResources();
        HashMap<String, Float> modifiedData = new HashMap<>();

        for (String key : data.keySet()) {
            if (key.equals("Lighting - Active Energy") || key.equals("Lighting - Active power")) {
                modifiedData.put(Res.getString(R.string.mm_gf_labslighting), data.get(key));
                System.out.println(key);
            }
            if (key.equals("Active Energy") || key.equals("Active power")) {
                modifiedData.put(Res.getString(R.string.mm_1f_roomslighting), data.get(key));
                System.out.println(key);
            }
            if (key.equals("Geometri-1F-ActiveEnergy") || key.equals("Geometri-1F-ActivePower")) {
                modifiedData.put(Res.getString(R.string.mm_geom_1f), data.get(key));
                System.out.println(key);
            }
            if (key.equals("Geometri-GF-ActiveEnergy") || key.equals("Geometri-GF-ActivePower")) {
                modifiedData.put(Res.getString(R.string.mm_geom_gf), data.get(key));
                System.out.println(key);
            }
            if (key.equals("Hall-Lighting-ActiveEnergy") || key.equals("Hall-Lighting-ActivePower")) {
                modifiedData.put(Res.getString(R.string.mm_qg_hall_lighting), data.get(key));
                System.out.println(key);
            }
            if (key.equals("QS - Active Energy") || key.equals("QS - ActivePower")) {
                modifiedData.put(Res.getString(R.string.mm_qs), data.get(key));
                System.out.println(key);
            }
            if (key.equals("QG - Active Energy") || key.equals("QG - ActivePower")) {
                modifiedData.put(Res.getString(R.string.mm_qg), data.get(key));
                System.out.println(key);
            }
            if (key.equals("FM - Active Energy") || key.equals("FM - ActivePower")) {
                modifiedData.put(Res.getString(R.string.mm_geom_gf_labsmotionpower), data.get(key));
                System.out.println(key);
            }
        }

        return modifiedData;
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












    protected void setSensorsSpinner() {

        ArrayAdapter<String> spinSensorAdapter;

        //dichiarazione Spinner adapters
        spinSensorAdapter = new ArrayAdapter<>(getActivity().getApplication(), R.layout.spinner_item);

        //add data
        spinSensorAdapter.addAll(getSensorsNames());
        sensorSpinner.setAdapter(spinSensorAdapter);

        //definizione del setOnItemSelectedListener per SensorSpinner
        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //salvataggio Oggetto Sensor scelto
                selectedSensor = sensors.get((int)id);

                //set type of parameter (power or consume):
                switch (selectedSensor.getUrlString()){
                    case "/actpw" : typeOfLecture = powerTag ; updateTimeInterval(typeOfLecture); break;
                    case "/con"   : typeOfLecture = energyTag; updateTimeInterval(typeOfLecture); break;
                    default:    break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void updateTimeInterval(String typeOfLecture){

        System.out.println("updateTimeInterval +++++");
        //restore toDate Calendar at today
        toDate = Calendar.getInstance();
        fromDate = Calendar.getInstance();

        switch (typeOfLecture) {
            case powerTag: {
                System.out.println("typeOfLecture +++++" + powerTag);
                // dafault : 5 minute of range
                fromDate.add(Calendar.MINUTE, -6); break;
            }
            case energyTag: {
                System.out.println("typeOfLecture +++++" + powerTag);
                // dafault : 1 day of range
                fromDate.add(Calendar.HOUR_OF_DAY, -24); break;
            }
            default: break;
        }

        //rstore textview on button
        setCurrentDateOnBtText();
        System.out.println(" date");
        setCurrentHourOnBtText();
        System.out.println(" hour");
        checkIfSelectionIsValid();
        System.out.println(" check");

    }


    //for spinner dataset
    private ArrayList<String> getSensorsNames(){
        ArrayList<String> names = new ArrayList<>();

        for (Sensor sensor : sensors){
            names.add(sensor.getName());
        }

        return names;

    }




    private ArrayList<Sensor> createSensorsList(){
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

        sensors.add(new Sensor("/con", getString(R.string.sscon)));
        sensors.add(new Sensor("/actpw", getString(R.string.ssactpw)));

        return sensors;
    }



    // display current date in Date button
    public void setCurrentDateOnBtText() {

        setDisplayDate(btFromDate, fromDate);
        setDisplayDate(btToDate, toDate);

    }

    // display current hour in textView
    public void setCurrentHourOnBtText() {

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
    public void addListenerOnPickerButton() {

        btFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(fromDate, dateFromPickerListener);

            }


        });

        btToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(toDate, dateToPickerListener);
            }
        });

        btFromHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(fromDate, timeFromPickerListener);
            }
        });

        btToHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(toDate, timeToPickerListener);
            }
        });
    }

    /* control if FROM - TO interval is valid for query
    and, if not, color the button with bad selection
     */
    private void checkIfSelectionIsValid() {
        setCorrectLayoutOnDateButton();
        timeRangeIsOk = true;
        long oneDayInMillis = 86400000;
        long fiveMinutesInMillis = 300000;

        //check if the selected day if before now
        Calendar now = Calendar.getInstance();

        //check from date to now
        if (fromDate.getTimeInMillis() >= now.getTimeInMillis()) {
            //if ((fromDate.getTimeInMillis() - now.getTimeInMillis()) > oneDayInMillis) {
            if ((fromDate.get(Calendar.DAY_OF_YEAR) > now.get(Calendar.DAY_OF_YEAR))) {
                // from is before now more than a day
                invalidButton(btFromDate);
                timeRangeIsOk = false;
            } else {
                // from is before now less than a day
                invalidButton(btFromHour);
                timeRangeIsOk = false;
            }
        }

        //check to date to now
        if (toDate.getTimeInMillis() >= now.getTimeInMillis()) {
            if ((toDate.getTimeInMillis() - now.getTimeInMillis()) > oneDayInMillis) {
                // from is before now more than a day
                invalidButton(btToDate);
                timeRangeIsOk = false;
            } else {
                // from is before now less than a day
                invalidButton(btToHour);
                timeRangeIsOk = false;
            }
        }

        //FROM and TO are before now, so control if FROM is before TO for more the 5 minutes
        if (fromDate.getTimeInMillis() < now.getTimeInMillis() ||
                toDate.getTimeInMillis() < now.getTimeInMillis()) {

            if (fromDate.getTimeInMillis() >= (toDate.getTimeInMillis() - fiveMinutesInMillis)) {
                //not valid time range

                if ((fromDate.getTimeInMillis() - toDate.getTimeInMillis()) > oneDayInMillis) {

                    //the difference is bigger than a day
                    invalidButton(btFromDate);
                    timeRangeIsOk = false;
                } else {

                    //the difference is smaller than a day
                    invalidButton(btFromHour);
                    timeRangeIsOk = false;

                }
            }
        }

        //set the search button color
        checkSearchButtonColor(timeRangeIsOk);


    }

    private void checkSearchButtonColor(Boolean timeRangeIsOk){
        if (timeRangeIsOk)
            compareButton.setBackground(getResources().getDrawable(R.drawable.bt_search_ok_selector));
        else
            compareButton.setBackground(getResources().getDrawable(R.drawable.bt_search_not_ok_selector));
    }


    private void setCorrectLayoutOnDateButton(){
        btToDate.setBackground(getResources().getDrawable(R.drawable.time_button_selector));
        btToHour.setBackground(getResources().getDrawable(R.drawable.time_button_selector));
        btFromDate.setBackground(getResources().getDrawable(R.drawable.time_button_selector));
        btFromHour.setBackground(getResources().getDrawable(R.drawable.time_button_selector));
    }

    private void invalidButton(Button b){
        b.setBackground(getResources().getDrawable(R.drawable.bad_date_button));
    }


    /** _______________________________ PICKER SHOW METHOD ________________________________ **/

    private void showDatePicker(Calendar date, DatePickerDialog.OnDateSetListener listener) {
        DatePickerFragment datePicker = new DatePickerFragment();

        //Set Up saved Date Into dialog
        setUpSavedDate(datePicker, date);

        //Set Call back to capture selected date
        datePicker.setCallBack(listener);
        datePicker.show(getFragmentManager(), "Date Picker");
    }

    private void showTimePicker(Calendar date, TimePickerDialog.OnTimeSetListener listener) {
        TimePickerFragment timePicker = new TimePickerFragment();

        //Set Up saved Date Into dialog
        setUpSavedTime(timePicker, date);

        //Set Call back to capture selected date
        timePicker.setCallBack(listener);
        timePicker.show(getFragmentManager(), "Time Picker");
    }

    //set up in datePicker the saved date
    private void setUpSavedDate(DatePickerFragment datePicker, Calendar date){
        Bundle args = new Bundle();
        args.putInt("year", date.get(Calendar.YEAR));
        args.putInt("month", date.get(Calendar.MONTH));
        args.putInt("day", date.get(Calendar.DAY_OF_MONTH));
        datePicker.setArguments(args);
    }

    //set up in datePicker the saved date
    private void setUpSavedTime(TimePickerFragment timePicker, Calendar date){
        Bundle args = new Bundle();
        args.putInt("hour", date.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", date.get(Calendar.MINUTE));
        timePicker.setArguments(args);
    }

    /** __________________________ End of Picker Show Method _________________________________ **/







    /**   -  -  -  -  -  - -  -  - == PICKER LISTENER METHOD == -  -  -  -  -  - -  -    -  -  -  ***/

    //Dichiarazione picker DATE FROM
    private DatePickerDialog.OnDateSetListener dateFromPickerListener = (new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            //update fromDate object
            fromDate.set(selectedYear, selectedMonth, selectedDay);
            setDisplayDate(btFromDate, fromDate); // update selected date on button text
            checkIfSelectionIsValid();
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
            setDisplayHour(btFromHour, fromDate); // update selected hour on button text
            checkIfSelectionIsValid();
        }
    });

    //Dichiarazione de Picker DATE TO
    private DatePickerDialog.OnDateSetListener dateToPickerListener = (new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            //update fromDate object
            toDate.set(selectedYear, selectedMonth, selectedDay);
            setDisplayDate(btToDate, toDate); // update selected date on button text
            checkIfSelectionIsValid();
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
            setDisplayHour(btToHour, toDate); // update selected hour on button text
            checkIfSelectionIsValid();
        }
    });
    /**-  -  -  -  -  -    - == End Of Picker Listener method ==-  -  -  -  -  -  -  -  -  -  -  **/





}