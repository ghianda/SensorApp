package com.example.francesco.myfirstapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import static com.example.francesco.myfirstapp.SensorProjectApp.serviceRepeatPeriodInMillis;
import static com.example.francesco.myfirstapp.SessionManager.KEY_NAME;
import static com.example.francesco.myfirstapp.SessionManager.KEY_STATION;

/**
 * Created by francesco on 22/12/2016.
 */

public class ActivityHome extends AppCompatActivity {

    SessionManager session;
    HashMap<String, String> userCredentials;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        session = new SessionManager(getApplicationContext());

        setListenerBottomMenu();

        //load the user credentials and display it
        loadUserCredentials();

        //set the listener on Service switch and load the user last state
        setServiceSwitch();

    }



    private void loadUserCredentials(){
        userCredentials =  session.getUserDetails();

        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        TextView tvStationName = (TextView) findViewById(R.id.tvStationName);

        tvUserName.setText(userCredentials.get(KEY_NAME));
        tvStationName.setText(userCredentials.get(KEY_STATION));

    }



    /*  Start Last_Reading button method    */
    public void startLastReadActivity(View view) {

        Intent intent = new Intent(this, ActivityLastRead.class);
        startActivity(intent);
    }



    /*  Start Time_Get button activity*/
    public void startTimeActivity(View view) {

        Intent intent = new Intent(this, ActivityTimeRead.class);
        startActivity(intent);
    }



    /*  Start Time_Get button activity*/
    public void startCompareActivity(View view) {

        //Intent intent = new Intent(this, ActivityCompare.class);
        Intent intent = new Intent(this, ActivityCompare.class);
        startActivity(intent);
    }



    /* set the switch listener */
    public void setServiceSwitch(){

        loadThePreferenceState();
        setServiceSwitchListener();

    }



    /* Load the preference and set the switch state */
    public  void loadThePreferenceState(){

        //load preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Boolean isChecked = sharedPref.getBoolean("pref_service", true); //default: true

        System.out.println(" PREF SWITCH LOADED IS : " + isChecked);

        //set the pref on switch
        SwitchCompat switchCompat = (SwitchCompat)findViewById(R.id.switchService);
        switchCompat.setChecked(isChecked);
    }



    /* Set Switch Service listener */
    public void setServiceSwitchListener(){
        final SwitchCompat serviceSwitch = (SwitchCompat) findViewById(R.id.switchService);

        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be true if the switch is in the On position

                if (isChecked) {
                    //save the choice...
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(getApplicationContext()).edit();
                    editor.putBoolean("pref_service", true);
                    editor.apply();

                    //... and start the service
                    startSchedulerAlarm(buttonView);
                } else {
                    //save the choice...
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(getApplicationContext()).edit();
                    editor.putBoolean("pref_service", false);
                    editor.apply();

                    //...and stop the service
                    stopSchedulerAlarm(buttonView);
                }
            }
        });
    }


    public void startSchedulerAlarm(View view){
        //restart the alarm in background that repeat the task

        Intent i = new Intent(this, BackgroundTask.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Repeat the notification every 15 seconds (15000)
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                serviceRepeatPeriodInMillis, pi);

        Toast.makeText(this, "My Service RE-started", Toast.LENGTH_LONG).show();
    }


    public void stopSchedulerAlarm(View view){
        // stop the alarm in background
        //here i recreate the pendingIntent that start the alarm and cancel it with alarm manager

        Intent i = new Intent(getApplicationContext(), BackgroundTask.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent piToStop = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(piToStop);


        Toast.makeText(this, "My Service is stopped", Toast.LENGTH_LONG).show();

    }




    // Overrided method for menu title bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SessionManager session = new SessionManager(getApplicationContext());

        switch (item.getItemId()) {

            case R.id.action_logout:
                session.logoutUser();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gaia_intro_menu, menu);
        return true;
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
