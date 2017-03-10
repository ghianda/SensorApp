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

import static com.example.francesco.myfirstapp.SensorProjectApp.clockChoice;
import static com.example.francesco.myfirstapp.SensorProjectApp.clockPMillisPrefTag;
import static com.example.francesco.myfirstapp.SensorProjectApp.clockPositionPrefTag;
import static com.example.francesco.myfirstapp.SensorProjectApp.co2PrefTag;
import static com.example.francesco.myfirstapp.SensorProjectApp.createClockChoiceArray;
import static com.example.francesco.myfirstapp.SensorProjectApp.defaultCO2ForWattHour;
import static com.example.francesco.myfirstapp.SensorProjectApp.defaultEuroForWattHour;
import static com.example.francesco.myfirstapp.SensorProjectApp.defaultNotifyActivated;
import static com.example.francesco.myfirstapp.SensorProjectApp.defaultServiceRepeatPeriodPosition;
import static com.example.francesco.myfirstapp.SensorProjectApp.euroPrefTag;
import static com.example.francesco.myfirstapp.SensorProjectApp.serviceOnOffPfreTag;
import static com.example.francesco.myfirstapp.ServiceManager.loadThePreferenceState;
import static com.example.francesco.myfirstapp.ServiceManager.startSchedulerAlarm;
import static com.example.francesco.myfirstapp.ServiceManager.stopSchedulerAlarm;
import static com.example.francesco.myfirstapp.SessionManager.KEY_NAME;
import static com.example.francesco.myfirstapp.SessionManager.KEY_STATION;

public class ActivitySettings extends AppCompatActivity {

    //user settings
    static public long serviceRepeatPeriodInMillis;
    static public int serviceRepeatPeriodPosition;
    static public Boolean notifyActivated;
    static public float euroForWattHour;
    static public float CO2ForWattHour;

    //Layout resources
    private Button btSave, btRestore;
    private Spinner clockSpinner;
    private SwitchCompat serviceSwitch;
    private EditText editTextCo2, editTextEuro;
    private TextView tvUserName , tvStationName;

    //Activity attributes
    private HashMap<String, String> userCredentials;
    private SessionManager session;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        session = new SessionManager(getApplicationContext());
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
                serviceRepeatPeriodInMillis = clockChoice.get(defaultServiceRepeatPeriodPosition);
                notifyActivated = defaultNotifyActivated;
                euroForWattHour = defaultEuroForWattHour;
                CO2ForWattHour = defaultCO2ForWattHour;
                serviceRepeatPeriodPosition = defaultServiceRepeatPeriodPosition;

                saveAllInPreferences();
                updateLayoutResources();

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
                    CO2ForWattHour  = Float.valueOf(editTextCo2.getText().toString());
                else{
                    valueIsOk = false;
                }

                if (!editTextEuro.getText().toString().matches(""))
                    euroForWattHour = Float.valueOf(editTextEuro.getText().toString());
                else{
                    valueIsOk = false;
                }

                notifyActivated = serviceSwitch.isChecked();
                serviceRepeatPeriodInMillis = clockChoice.get(clockSpinner.getSelectedItemPosition());
                serviceRepeatPeriodPosition = clockSpinner.getSelectedItemPosition();

                if(valueIsOk) {

                    saveAllInPreferences();
                    updateLayoutResources();

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

        editor.putFloat(co2PrefTag, CO2ForWattHour);
        editor.putFloat(euroPrefTag, euroForWattHour);
        editor.putInt(clockPositionPrefTag, serviceRepeatPeriodPosition);
        editor.putLong(clockPMillisPrefTag, serviceRepeatPeriodInMillis);
        editor.putBoolean(serviceOnOffPfreTag, notifyActivated);

        editor.apply();

        //if notify is actived, update the background service with ne repeating value in Millis
        if (notifyActivated)
            updateAlarmInterval();

    }






    private void setEditTextOnCurrentChoice(){
        //load preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        euroForWattHour = sharedPref.getFloat(euroPrefTag, (float)0.166646);
        editTextEuro.setText(new StringBuilder().append(euroForWattHour));

        CO2ForWattHour = sharedPref.getFloat(co2PrefTag, (float)0.14);
        editTextCo2.setText(new StringBuilder().append(CO2ForWattHour));
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
        int position = sharedPref.getInt(clockPositionPrefTag, 1); //default: true

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
        startSchedulerAlarm(null, this, serviceRepeatPeriodInMillis);
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
        userCredentials =  session.getUserDetails();

        tvUserName.setText(userCredentials.get(KEY_NAME));
        tvStationName.setText(userCredentials.get(KEY_STATION));

    }


    /* set the switch listener */
    public void setServiceSwitch(){

        notifyActivated = loadThePreferenceState(serviceSwitch, this);
        serviceSwitch.setChecked(notifyActivated);
    }





}
