package com.example.francesco.myfirstapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

import java.util.Calendar;
import java.util.Locale;

public class FragmentTimeRead extends Fragment
{
    private final String TAG = "com.example.app.FragmentTimeRead";
    private Activity mActivity;

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

    //key for Picker Dialog
    static final int DATE_FROM_DIALOG_ID = 911;
    static final int HOUR_FROM_DIALOG_ID = 611;
    static final int DATE_TO_DIALOG_ID = 999;
    static final int HOUR_TO_DIALOG_ID = 666;

    protected static Meter chosenMeter; //meter selezionato
    protected static Sensor chosenSensor; //sensore scelto

    protected String url;



    public void onAttach(Activity act)
    {
        super.onAttach(act);

        this.mActivity = act;
    }

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

        // Associo i picker dialog (Time e Date) ai bottoni
        addListenerOnButton();

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
        NetworkManager.getInstance().getNetsensRequest(url, new SomeCustomListener<Netsens>()
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
        //intent to new activiy ( line graph)
        //creating a intent
        Intent intent = new Intent(getActivity(), ActivityLinearGraph.class);
        //put data in the intent
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
                url = networkManager.createTimeReadUrl(chosenMeter, chosenSensor, fromDate, toDate);
                ParseUrl(url);
            }
        });
    }







    /*
     * Set the value in the meter Spinner and set update method for Sensor Spinner
     */
    protected void setSensorsSpinner() {


        //dichiarazione Spinner adapters
        spinMeterAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.support_simple_spinner_dropdown_item);
        spinSensorAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.support_simple_spinner_dropdown_item);

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
    public void addListenerOnButton() {

        // definizione dei 4 OnClickListener per i bottoni
        View.OnClickListener dateFromListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().showDialog(DATE_FROM_DIALOG_ID);
            }
        });
        View.OnClickListener dateToListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().showDialog(DATE_TO_DIALOG_ID);
            }
        });
        View.OnClickListener hourFromListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().showDialog(HOUR_FROM_DIALOG_ID);
            }
        });
        View.OnClickListener hourToListener = (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().showDialog(HOUR_TO_DIALOG_ID);
            }
        });

        //associazione listener ai bottoni
        btFromDate.setOnClickListener(dateFromListener);
        btToDate.setOnClickListener(dateToListener);
        btFromHour.setOnClickListener(hourFromListener);
        btToHour.setOnClickListener(hourToListener);
    }


    //selezione del picker da eseguire
    protected Dialog onCreateDialog(int id) {


        //seleziono il picker in base al dialog creato (date o hour)
        switch (id) {
            case DATE_FROM_DIALOG_ID:
                //  esegue il datePicker e ripesca la data selezionata per il FROM
                return new DatePickerDialog(getActivity().getApplicationContext(), dateFromPickerListener, fromDate.get(Calendar.YEAR),
                        fromDate.get(Calendar.MONTH), fromDate.get(Calendar.DAY_OF_MONTH));
            case HOUR_FROM_DIALOG_ID:
                //  esegue il timePicker e ripesca l'ora selezionata per il FROM
                return new TimePickerDialog(getActivity().getApplicationContext(), timeFromPickerListener, fromDate.get(Calendar.HOUR_OF_DAY),
                        fromDate.get(Calendar.MINUTE), true);
            case DATE_TO_DIALOG_ID:
                //  esegue il datePicker e ripesca la data selezionata per il TO
                return new DatePickerDialog(getActivity().getApplicationContext(), dateToPickerListener, toDate.get(Calendar.YEAR),
                        toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH));
            case HOUR_TO_DIALOG_ID:
                //  esegue il hourPicker e ripesca la data selezionata per il TO
                return new TimePickerDialog(getActivity().getApplicationContext(), timeToPickerListener, toDate.get(Calendar.HOUR_OF_DAY),
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
            setDisplayDate(btFromDate, fromDate);
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
            setDisplayHour(btFromHour, fromDate);
        }
    });


    //Dichiarazione de Picker DATE TO
    private DatePickerDialog.OnDateSetListener dateToPickerListener = (new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

            //update fromDate object
            toDate.set(selectedYear, selectedMonth, selectedDay);
            // update selected date on button text
            setDisplayDate(btToDate, toDate);

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
            setDisplayHour(btToHour, toDate);
        }
    });
}