package com.example.francesco.myfirstapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_clockPMillisPref;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_clockPositionPref;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_co2Pref;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_euroPref;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_namePref;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_serviceOnOffPref;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_stationPref;
import static com.example.francesco.myfirstapp.SensorProjectApp.clockChoice;
import static com.example.francesco.myfirstapp.SensorProjectApp.createClockChoiceArray;
import static com.example.francesco.myfirstapp.SensorProjectApp.defaultCO2ForKiloWattHour;
import static com.example.francesco.myfirstapp.SensorProjectApp.defaultEuroForKiloWattHour;
import static com.example.francesco.myfirstapp.SensorProjectApp.defaultNotifyActivated;
import static com.example.francesco.myfirstapp.SensorProjectApp.defaultServiceRepeatPeriodPosition;
import static com.example.francesco.myfirstapp.ServiceManager.loadThePreferenceSwitchState;
import static com.example.francesco.myfirstapp.ServiceManager.startSchedulerAlarm;
import static com.example.francesco.myfirstapp.ServiceManager.stopSchedulerAlarm;

public class ActivitySettings extends AppCompatActivity {

    //user settings
    static public long _serviceRepeatPeriodInMillis;
    static public int _serviceRepeatPeriodPosition;
    static public Boolean _notifyActivated;
    static public float _euroForKiloWattHour;
    static public float _CO2ForKiloWattHour;

    //Layout resources
    private Button btSave, btRestore;
    private Spinner clockSpinner;
    private SwitchCompat serviceSwitch;
    private EditText editTextCo2, editTextEuro;
    private TextView tvUserName , tvStationName;

    //Activity attributes
    private HashMap<String, String> _userCredentials;
    private SessionManager _session;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        _session = new SessionManager(getApplicationContext());
        createClockChoiceArray();

        loadLayoutResources();

        //load the user credentials and display it
        loadUserCredentials();

        //Prepare the Notify clock selection Spinner
        populateClockSpinner();

        //parameter edittext
        setEditTextListener();

        //set listener on button
        setListenerOnButton();

        //set resources at current Preferences values
        updateLayoutResources();


    }





    private void updateLayoutResources(){

        //set resources at current Preferences values
        setServiceSwitch();
        setClockSpinnerAtCurrentChoice();
        setEditTextOnCurrentChoice();
    }




    private void setListenerOnButton(){
        btRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Restore default settings
                _serviceRepeatPeriodInMillis = clockChoice.get(defaultServiceRepeatPeriodPosition);
                _notifyActivated = defaultNotifyActivated;
                _euroForKiloWattHour = defaultEuroForKiloWattHour;
                _CO2ForKiloWattHour = defaultCO2ForKiloWattHour;
                _serviceRepeatPeriodPosition = defaultServiceRepeatPeriodPosition;

                saveAllInPreferences();
                updateLayoutResources();

                if (_notifyActivated) {
                    //update the background service with ne repeating value in Millis
                    updateAlarmInterval();
                }
                else{
                    stopSchedulerAlarm(null, ActivitySettings.this);
                }

                Toast.makeText(getApplicationContext() , getString(R.string.settingsRestoreComplete)
                        , Toast.LENGTH_LONG).show();
            }
        });


        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean valueIsOk = true;

                //Save current settings
                if (!editTextCo2.getText().toString().matches(""))
                    _CO2ForKiloWattHour = Float.valueOf(editTextCo2.getText().toString());
                else{
                    valueIsOk = false;
                }

                if (!editTextEuro.getText().toString().matches(""))
                    _euroForKiloWattHour = Float.valueOf(editTextEuro.getText().toString());
                else{
                    valueIsOk = false;
                }

                _notifyActivated = serviceSwitch.isChecked();
                _serviceRepeatPeriodInMillis = clockChoice.get(clockSpinner.getSelectedItemPosition());
                _serviceRepeatPeriodPosition = clockSpinner.getSelectedItemPosition();

                if(valueIsOk) {

                    saveAllInPreferences();
                    updateLayoutResources();


                    if (_notifyActivated) {
                        //update the background service with ne repeating value in Millis
                        updateAlarmInterval();
                    }
                    else{
                        stopSchedulerAlarm(null, ActivitySettings.this);
                    }

                    Toast.makeText(getApplicationContext(), getString(R.string.settingsSaveComplete)
                            , Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext() , getString(R.string.settingsSaveError) ,
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    private void saveAllInPreferences(){
        //save all settings in shared Preferences

        SharedPreferences.Editor editor = PreferenceManager.
                getDefaultSharedPreferences(getApplicationContext()).edit();

        editor.putFloat(KEY_co2Pref, _CO2ForKiloWattHour);
        editor.putFloat(KEY_euroPref, _euroForKiloWattHour);
        editor.putInt(KEY_clockPositionPref, _serviceRepeatPeriodPosition);
        editor.putLong(KEY_clockPMillisPref, _serviceRepeatPeriodInMillis);
        editor.putBoolean(KEY_serviceOnOffPref, _notifyActivated);

        editor.apply();

    }






    private void setEditTextOnCurrentChoice(){
        //load preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        _euroForKiloWattHour = sharedPref.getFloat(KEY_euroPref, (float)0.166646);
        editTextEuro.setText(new StringBuilder().append(_euroForKiloWattHour));

        _CO2ForKiloWattHour = sharedPref.getFloat(KEY_co2Pref, (float)0.14);
        editTextCo2.setText(new StringBuilder().append(_CO2ForKiloWattHour));
    }




    public void setEditTextListener(){

        //add the "DONE" button
        editTextEuro.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editTextCo2.setImeOptions(EditorInfo.IME_ACTION_DONE);

        //add the listener
        editTextCo2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    //hide the keyboard and exit
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextCo2.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        //add the listener
        editTextEuro.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    //hide the keyboard and exit
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editTextEuro.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }





    private void setClockSpinnerAtCurrentChoice(){
        //load preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int position = sharedPref.getInt(KEY_clockPositionPref, 3); //default: true

        clockSpinner.setSelection(position);
    }

    private void populateClockSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.selectClockNotify, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clockSpinner.setAdapter(adapter);
    }





    private void updateAlarmInterval(){
        //stop the service and restart it with the new repeat timing
        stopSchedulerAlarm(null, this);
        startSchedulerAlarm(null, this, _serviceRepeatPeriodInMillis);
    }






    private void loadLayoutResources(){

        btSave        = (Button) findViewById(R.id.btSave);
        btRestore     = (Button) findViewById(R.id.btRestore);
        editTextCo2   = (EditText) findViewById(R.id.editTextCo2);
        editTextEuro  = (EditText) findViewById(R.id.editTextEuro);
        clockSpinner  = (Spinner) findViewById(R.id.spinnerClock);
        tvUserName    = (TextView) findViewById(R.id.tvUserName);
        tvStationName = (TextView) findViewById(R.id.tvStationName);
        serviceSwitch = (SwitchCompat) findViewById(R.id.switchService);
    }



    private void loadUserCredentials(){
        _userCredentials =  _session.getUserDetails();

        tvUserName.setText(_userCredentials.get(KEY_namePref));
        tvStationName.setText(_userCredentials.get(KEY_stationPref));

    }


    /* set the switch listener */
    public void setServiceSwitch(){

        _notifyActivated = loadThePreferenceSwitchState(serviceSwitch, this);
        serviceSwitch.setChecked(_notifyActivated);
    }







}
