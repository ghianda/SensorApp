package com.example.francesco.myfirstapp;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import static com.example.francesco.myfirstapp.SensorProjectApp.fixUnit;


public class FragmentLastRead extends Fragment
{
    private final String TAG = "com.example.app.FragmentLastRead";
    private Activity mActivity;


    TextView tvValue , tvTimestamp;
    Spinner meterSpinner , sensorSpinner;


    protected NetworkManager networkManager;

    protected final static SensorList allSensors = new SensorList(); //lista di coppie (meter -> elenco sensori)
    protected static ArrayAdapter<String> spinMeterAdapter;
    protected static ArrayAdapter<String> spinSensorAdapter;

    ImageButton lastReadButton;

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
        View view = inflater.inflate(R.layout.fragment_last_read, container, false);

        return view;
    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        findAndSaveInputOutputResource(view);

        // create NetworkManager
        networkManager = new NetworkManager(getActivity().getApplicationContext());

        //set input listener
        setSensorsSpinner();
        setLastReadButtonListener();





    }


    // -------------------------------------- not overrided method ----------------------------


    private void findAndSaveInputOutputResource(View view){
        //find and save layout variables
        tvValue = (TextView) view.findViewById(R.id.tvDisplayValueResult);
        tvTimestamp = (TextView) view.findViewById(R.id.tvDisplayTimeResult);

        meterSpinner = (Spinner) getView().findViewById(R.id.MeterSpinner);
        sensorSpinner = (Spinner) getView().findViewById(R.id.SensorSpinner);

        lastReadButton = (ImageButton) view.findViewById(R.id.btLastRead);

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
        displayResult(response, chosenSensor);
    }


    //display in a textview the result value and the time of the reading
    public void displayResult(Netsens response, Sensor chosenSensor) {


        //create the sensor object and put data in it
        Sensor ss = new Sensor(chosenSensor.getUrlString(), chosenSensor.getName());
        //put data into object
        ss.addValue(response.getMeasuresList().get(0).getValue()
                , response.getMeasuresList().get(0).getTimeStamp());
        ss.setConversionFactorByUrlCode();

        //display data on textview
        setValueInTv(ss.getDatas().get(0).getValue() / ss.getConversionFactor(), ss.getUnitOfMeasure(), tvValue);
        setTimeInTv(ss.getDatas().get(0), tvTimestamp);

    }



    private void setLastReadButtonListener(){
        lastReadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                url = networkManager.createLastReadUrl(chosenMeter, chosenSensor);
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







    private void setTimeInTv(Data data, TextView tv) {
        boolean shortVersion = false;
        SensorProjectApp.fromMillisToDateOnTextView(data.getTimestamp(), tv, shortVersion);
    }



    //set the value in the textview with correct format and unit of measure
    private void setValueInTv(double v, String unit, TextView tv) {

        String stringValue = fixUnit(v, unit);
        tv.setText(stringValue);
    }
}