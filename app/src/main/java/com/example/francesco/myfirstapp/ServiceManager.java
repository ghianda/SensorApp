package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 02/03/2017.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Toast;

import static com.example.francesco.myfirstapp.SensorProjectApp.serviceRepeatPeriodInMillis;

/** there is static method for service  management */
public class ServiceManager {


    /* Load the preference and set the switch state */
    public static void loadThePreferenceState(SwitchCompat serviceSwitch, Context context){

        //load preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean isChecked = sharedPref.getBoolean("pref_service", false); //default: true

        serviceSwitch.setChecked(isChecked);
    }






    public static void startSchedulerAlarm(View view, Activity activity){
        //restart the alarm in background that repeat the task

        Intent i = new Intent(activity, BackgroundTask.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getBroadcast(activity, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Repeat the notification every 15 seconds (15000)
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                serviceRepeatPeriodInMillis, pi);

        Toast.makeText(activity, "My Service RE-started", Toast.LENGTH_LONG).show();
    }


    public static  void stopSchedulerAlarm(View view, Activity activity){
        // stop the alarm in background
        //here i recreate the pendingIntent that start the alarm and cancel it with alarm manager

        Intent i = new Intent(activity.getApplicationContext(), BackgroundTask.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent piToStop = PendingIntent.getBroadcast(activity, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(piToStop);


        Toast.makeText(activity, "My Service is stopped", Toast.LENGTH_LONG).show();

    }
}
