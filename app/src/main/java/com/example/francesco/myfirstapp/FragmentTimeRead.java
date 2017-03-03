package com.example.francesco.myfirstapp;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class FragmentTimeRead extends Fragment
{


    protected NetworkManager networkManager;

    protected final static SensorList allSensors = new SensorList(); //lista di coppie (meter -> elenco sensori)
    protected static ArrayAdapter<String> spinMeterAdapter;
    protected static ArrayAdapter<String> spinSensorAdapter;

    private Spinner meterSpinner , sensorSpinner;

    private ImageButton timeReadButton;

    private Button btFromDate; //bottoni per l'avvio dei picker dialog di selezione data/ora
    private Button btFromHour;
    private Button btToDate;
    private Button btToHour;

    //oggetti Calendario inizializzati a oggi
    private static Calendar fromDate = Calendar.getInstance();
    private static Calendar toDate = Calendar.getInstance();
    private Boolean timeRangeIsOk = false;

    protected static Meter chosenMeter; //meter selezionato
    protected static Sensor chosenSensor; //sensore scelto

    protected String url;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {


        View view = inflater.inflate(R.layout.fragment_time_read, container, false);
        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        findAndSaveInputOutputResource(view);

        // create NetworkManager
        networkManager = new NetworkManager(getActivity().getApplicationContext());

        //set input listener
        setSensorsSpinner();
        setTimeReadButtonListener();

        //set the current date and hour on view
        setCurrentDateOnBtText();
        setCurrentHourOnBtText();
        checkIfSelectionIsValid();

        // Associo i picker dialog (Time e Date) ai bottoni
        addListenerOnPickerButton();

    }


    // -------------------------------------- not overrided method ----------------------------


    private void findAndSaveInputOutputResource(View view){
        //find and save layout variables

        meterSpinner = (Spinner) getView().findViewById(R.id.MeterSpinner);
        sensorSpinner = (Spinner) getView().findViewById(R.id.SensorSpinner);

        timeReadButton = (ImageButton) view.findViewById(R.id.btTimeRead);

        btFromDate = (Button) view.findViewById(R.id.btFragFromDate);
        btToDate = (Button) view.findViewById(R.id.btToDate);
        btFromHour = (Button) view.findViewById(R.id.btFromHour);
        btToHour = (Button) view.findViewById(R.id.btToHour);

    }




    public void ParseUrl(String url)
    {
        NetworkManager.getInstance().getNetsensRequest(url, new CustomListener<Netsens>()
        {
            @Override
            public void getResult(Netsens response)
            {
                //do some work with response
                workOnResponse(response);

            }
        });

    }


    private void workOnResponse(Netsens response) {

        //abstract method defined in child activities
        displayResult(response, chosenMeter, chosenSensor);
    }


    public void displayResult(Netsens response, Meter chosenMeter, Sensor chosenSensor) {

        //creating a intent
        Intent intent = new Intent(getActivity(), ActivityLinearGraph.class);

        //put data in the intent (NOTE: here value is  divide from ConversionFactor)
        Sensor parcObj = SensorProjectApp.createParceableDataResponse(response, chosenSensor);
        intent.putExtra(SensorProjectApp.EXTRA_PARCDATARESPONSE, parcObj);
        intent.putExtra(SensorProjectApp.EXTRA_METER, chosenMeter.getUrlString());

        startActivity(intent);
    }



    private void setTimeReadButtonListener(){
        timeReadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(timeRangeIsOk) {

                    url = networkManager.createTimeReadUrl(chosenMeter, chosenSensor, fromDate, toDate);
                    ParseUrl(url);
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.badTimeSelection), Toast.LENGTH_LONG).show();
                }
            }
        });
    }







    /*
     * Set the value in the meter Spinner and set update method for Sensor Spinner
     */
    protected void setSensorsSpinner() {


        //dichiarazione Spinner adapters
        spinMeterAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item);
        spinSensorAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item);

        // setting MeterAdapter e applicazione allo spinner
        spinMeterAdapter.addAll(allSensors.getMetersName());
        meterSpinner.setAdapter(spinMeterAdapter);

        //definizione del setOnItemSelectedListener per MeterSpinner
        meterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //salvataggio Oggetto Meter scelto
                chosenMeter = allSensors.getMeterById((int) id);

                //aggiornamento sensorSpinner in base al Meter scelto
                spinSensorAdapter.clear();
                spinSensorAdapter.addAll(allSensors.getSensorsNamesByMeter(chosenMeter));

                sensorSpinner.setAdapter(spinSensorAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //definizione del setOnItemSelectedListener per SensorSpinner
        sensorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //salvataggio Oggetto Sensor scelto
                chosenSensor = allSensors.getSensor(chosenMeter, (int) id);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
            System.out.println(" A ");

            if (fromDate.getTimeInMillis() >= (toDate.getTimeInMillis() - fiveMinutesInMillis)) {
                System.out.println(" B ");
                //not valid time range
                if ((fromDate.getTimeInMillis() - toDate.getTimeInMillis()) > oneDayInMillis) {
                    System.out.println(" C ");
                    //the difference is bigger than a day
                    invalidButton(btFromDate);
                    timeRangeIsOk = false;
                } else {
                    System.out.println(" D ");
                    //the difference is smaller than a day
                    System.out.println(" E ");
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
            timeReadButton.setBackground(getResources().getDrawable(R.drawable.bt_search_ok_selector));
        else
            timeReadButton.setBackground(getResources().getDrawable(R.drawable.bt_search_not_ok_selector));
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