package com.example.francesco.myfirstapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashMap;

import static com.example.francesco.myfirstapp.ServiceManager.loadThePreferenceState;
import static com.example.francesco.myfirstapp.ServiceManager.startSchedulerAlarm;
import static com.example.francesco.myfirstapp.ServiceManager.stopSchedulerAlarm;
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

        findLayoutVariables(view);

        //load the user credentials and display it
        loadUserCredentials();

        //set the listener on Service switch and load the user last state
        setServiceSwitch();

        //make get http for hme value
        makeGet();

    }


    private void makeGet(){
        lastReadPower();
        yesterdayConsume();

    }

    private void lastReadPower(){

    }





    private void yesterdayConsume(){
        //TODO
        //getYesterdayConsume();
        //displayCO2();
        //displayEuro();

    }






        //find and save layout variables
    private void findLayoutVariables(View view){
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvStationName = (TextView) view.findViewById(R.id.tvStationName);
        serviceSwitch = (SwitchCompat) view.findViewById(R.id.switchService);
    }




    private void loadUserCredentials(){
        userCredentials =  session.getUserDetails();

        tvUserName.setText(userCredentials.get(KEY_NAME));
        tvStationName.setText(userCredentials.get(KEY_STATION));

    }



































    /* set the switch listener */
    public void setServiceSwitch(){

        Context context = getActivity().getApplicationContext();
        loadThePreferenceState(serviceSwitch, context);
        setServiceSwitchListener();

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
                    startSchedulerAlarm(buttonView, getActivity());
                } else {
                    //save the choice...
                    SharedPreferences.Editor editor = PreferenceManager.
                            getDefaultSharedPreferences(getActivity().getApplicationContext()).edit();
                    editor.putBoolean("pref_service", false);
                    editor.apply();

                    //...and stop the service
                    stopSchedulerAlarm(buttonView, getActivity());
                }
            }
        });
    }



}
