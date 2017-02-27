package com.example.francesco.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 * Created by francesco on 31/12/2016.
 */



public abstract class ActivityAbstractReading extends AppCompatActivity {
    //Attribute --------------------------------
    protected NetworkManager networkManager;

    protected final static SensorList allSensors = new SensorList(); //lista di coppie (meter -> elenco sensori)
    protected static ArrayAdapter<String> spinMeterAdapter;
    protected static ArrayAdapter<String> spinSensorAdapter;

    protected static Meter chosenMeter; //meter selezionato
    protected static Sensor chosenSensor; //sensore scelto

    protected String url;


    //Method -------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(myView());

        // create NetworkManager
        networkManager = new NetworkManager(this);

        //setListenerBottomMenu();

        //preparazione degli spinner dei Sensori
        setSensorsSpinner();



    }


    protected abstract int myView();

    protected abstract int getIdMeterSpinner();

    protected abstract int getIdSensorSpinner();

    protected abstract void createUrl();

    protected abstract void displayResult(Netsens response, Meter chosenMeter, Sensor chosenSensor);






    //Not Abstract Method - button read method
    public void read(View view) {
        createUrl();

        ParseUrl(url);
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

        storeResult(response);  //extract and save the measures

        //abstract method defined in child activities
        displayResult(response, chosenMeter, chosenSensor);
    }


    private void storeResult(Netsens response) {
        //store the response data in Global Sensor Attribute
        ((SensorProjectApp) this.getApplication()).setGlobalData(
                response, chosenMeter, chosenSensor);
    }



    /* Not Abstract Method -
     * Set the value in the meter Spinner and set update method for Sensor Spinner
     */
    protected void setSensorsSpinner() {

        //spinner object
        final Spinner meterSpinner = (Spinner) findViewById(getIdMeterSpinner());
        final Spinner sensorSpinner = (Spinner) findViewById(getIdSensorSpinner());

        //dichiarazione Spinner adapters
        spinMeterAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);
        spinSensorAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item);

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


    //listener for bottom menu bar
    private void setListenerBottomMenu() {
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

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

}
