package com.example.francesco.myfirstapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import static com.example.francesco.myfirstapp.SensorProjectApp.serviceRepeatPeriodInMillis;
import static com.example.francesco.myfirstapp.SessionManager.KEY_NAME;
import static com.example.francesco.myfirstapp.SessionManager.KEY_STATION;


public class FragmentHome extends Fragment
{
    private final String TAG = "com.example.app.FragmentHome";
    private Activity mActivity;


    TextView tvUserName , tvStationName;
    SwitchCompat serviceSwitch;


    SessionManager session;
    HashMap<String, String> userCredentials;






    public void onAttach(Activity act)
    {
        super.onAttach(act);

        this.mActivity = act;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        session = new SessionManager(getActivity().getApplicationContext());


        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //find and save layout variables
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvStationName = (TextView) view.findViewById(R.id.tvStationName);
        serviceSwitch = (SwitchCompat) view.findViewById(R.id.switchService);


        //load the user credentials and display it
        loadUserCredentials();

        //set the listener on Service switch and load the user last state
        setServiceSwitch();

    }







    private void loadUserCredentials(){
        userCredentials =  session.getUserDetails();

        tvUserName.setText(userCredentials.get(KEY_NAME));
        tvStationName.setText(userCredentials.get(KEY_STATION));

    }


    /* set the switch listener */
    public void setServiceSwitch(){

        loadThePreferenceState();
        setServiceSwitchListener();

    }



    /* Load the preference and set the switch state */
    public  void loadThePreferenceState(){

        //load preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        Boolean isChecked = sharedPref.getBoolean("pref_service", true); //default: true

        System.out.println(" PREF SWITCH LOADED IS : " + isChecked);

        //set the pref on switch

        serviceSwitch.setChecked(isChecked);
    }



    /* Set Switch Service listener */
    public void setServiceSwitchListener(){

        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be true if the switch is in the On position

                if (isChecked) {
                    //save the choice...
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
                    editor.putBoolean("pref_service", true);
                    editor.apply();

                    //... and start the service
                    startSchedulerAlarm(buttonView);
                } else {
                    //save the choice...
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
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

        Intent i = new Intent(getActivity(), BackgroundTask.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Repeat the notification every 15 seconds (15000)
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                serviceRepeatPeriodInMillis, pi);

        Toast.makeText(getActivity(), "My Service RE-started", Toast.LENGTH_LONG).show();
    }


    public void stopSchedulerAlarm(View view){
        // stop the alarm in background
        //here i recreate the pendingIntent that start the alarm and cancel it with alarm manager

        Intent i = new Intent(getActivity().getApplicationContext(), BackgroundTask.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        final PendingIntent piToStop = PendingIntent.getBroadcast(getActivity(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(piToStop);


        Toast.makeText(getActivity(), "My Service is stopped", Toast.LENGTH_LONG).show();

    }
}
