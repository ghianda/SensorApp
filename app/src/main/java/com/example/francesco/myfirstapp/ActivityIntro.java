package com.example.francesco.myfirstapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

/**
 * Created by francesco on 22/12/2016.
 */

public class ActivityIntro extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //set the listener on Service switch and load the user last state
        setServiceSwitch();

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



    /* Load the preference state and set the switch listener */
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
                    //SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
                    editor.putBoolean("pref_service", true);
                    editor.apply();

                    //... and start the service
                    startSchedulerAlarm(buttonView);
                } else {
                    //save the choice...
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(getApplicationContext()).edit();
                    //SharedPreferences.Editor editor = getSharedPreferences("com.example.xyz", MODE_PRIVATE).edit();
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

        Intent i = new Intent(this, NotificationBarAlarm.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Repeat the notification every 15 seconds (15000)
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 15000, pi);

        Toast.makeText(this, "My Service RE-started", Toast.LENGTH_LONG).show();
    }


    public void stopSchedulerAlarm(View view){
        // stop the alarm in background
        //here i recreate the pendingIntent that start the alarm and cancel it with alarm manager

        Intent i = new Intent(getApplicationContext(), NotificationBarAlarm.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent piToStop = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(piToStop);


        Toast.makeText(this, "My Service is stopped", Toast.LENGTH_LONG).show();

    }

}
