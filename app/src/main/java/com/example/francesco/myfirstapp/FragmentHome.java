package com.example.francesco.myfirstapp;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import static com.example.francesco.myfirstapp.SensorProjectApp.CO2ForWattHour;
import static com.example.francesco.myfirstapp.SensorProjectApp.euroForKiloWattHour;
import static com.example.francesco.myfirstapp.SensorProjectApp.fixUnit;
import static com.example.francesco.myfirstapp.ServiceManager.loadThePreferenceState;
import static com.example.francesco.myfirstapp.ServiceManager.startSchedulerAlarm;
import static com.example.francesco.myfirstapp.ServiceManager.stopSchedulerAlarm;
import static com.example.francesco.myfirstapp.SessionManager.KEY_NAME;
import static com.example.francesco.myfirstapp.SessionManager.KEY_STATION;


public class FragmentHome extends Fragment
{

    private static final String difference_TAG = "com.example.francesco.DIFFERENCE";
    private static final String sum_TAG = "com.example.francesco.SUM";

    private ArrayList<Netsens> consumeResponse;
    private ArrayList<Netsens> powerResponse;
    private float yesterdayCO2, yesterdayEuro;

    private TextView tvUserName , tvStationName, tvTodayValue, tvYesterdayConsume,
            tvYesterdayCO2, tvYesterdayEuro;
    private SwitchCompat serviceSwitch;
    private Calendar nowCalendar;
    private ProgressBar progBarToday, progBarYestCons, progBarYestCO2, progBarYestEuro;


    private NetworkManager networkManager;
    private SessionManager session;
    private HashMap<String, String> userCredentials;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //create session and network manager, and get an instance of calendar
        session = new SessionManager(getActivity().getApplicationContext());
        networkManager = new NetworkManager(getActivity().getApplicationContext());
        nowCalendar = Calendar.getInstance();

        //allocate the memory
        consumeResponse = new ArrayList<>();
        powerResponse = new ArrayList<>();
        yesterdayCO2 = 0;
        yesterdayEuro = 0;

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

    /*_____________________________________________________________________________________ */


    private void makeGet(){
        lastReadPower();
        yesterdayConsume();
    }


    private void lastReadPower(){

        //start progress bar
        progBarToday.setVisibility(View.VISIBLE);

        String urlQG = networkManager.createLastReadUrl("QG", "/actpw");
        String urlQS = networkManager.createLastReadUrl("QS", "/actpw");

        ParseUrl(urlQG, sum_TAG, 2);
        ParseUrl(urlQS, sum_TAG, 2);

    }


    private void yesterdayConsume(){

        //start progress bar
        progBarYestCO2.setVisibility(View.VISIBLE);
        progBarYestEuro.setVisibility(View.VISIBLE);
        progBarYestCons.setVisibility(View.VISIBLE);

        //calculate begin and end time of yesterday
        HashMap<String, Long> fromAndTo = findStartAndStopOfYesterday(nowCalendar);

        String urlQGfrom = networkManager.createWindowUrl("QG", "/con", fromAndTo.get("00.01"));
        String urlQGto = networkManager.createWindowUrl("QG", "/con", fromAndTo.get("23.59"));
        String urlQSfrom = networkManager.createWindowUrl("QS", "/con", fromAndTo.get("00.01"));
        String urlQSto = networkManager.createWindowUrl("QS", "/con", fromAndTo.get("23.59"));

        ParseUrl(urlQGfrom, difference_TAG, 4);
        ParseUrl(urlQGto, difference_TAG, 4);
        ParseUrl(urlQSfrom, difference_TAG, 4);
        ParseUrl(urlQSto, difference_TAG, 4);

    }




    public void ParseUrl(String url, final String operation_TAG, final int expectedResponses)
    {
        NetworkManager.getInstance().getNetsensRequest(url, new CustomListener<Netsens>()
        {
            @Override
            public void getResult(Netsens response)
            {
                switch (operation_TAG){
                    case sum_TAG : extractResponse(response, powerResponse, expectedResponses); break;
                    case difference_TAG : extractResponse(response, consumeResponse, expectedResponses); break;
                    default: break;
            }
        }});
    }





    private void extractResponse(Netsens newResponse, ArrayList<Netsens> savedResponses,
                                 int expectedResponses) {

        //add new response
        savedResponses.add(newResponse);

        //check if all response are saved
        if (savedResponses.size() == expectedResponses) {
            /** DO WORK */
            doWorkOnResponses(savedResponses);
        }
    }


    /** sum of QG and QS last reading power */
    private void doWorkOnResponses(ArrayList<Netsens> response) {

        switch (response.size()) {
            case 2: {

                // response is power lecture
                float sum = doSumAndConvertFromCentiUnit(response);
                setValueInTv(sum, "W", tvTodayValue);
                progBarToday.setVisibility(View.GONE);
                break;
            }
            case 4: {

                // response is consume lecture
                ArrayList <Float> consumes = calculateConsumesFromLecture(response);
                float sum = (consumes.get(0) + consumes.get(1)) * (1000/100); //are centiKiloWattHour

                setValueInTv(sum, "Wh", tvYesterdayConsume);
                progBarYestCons.setVisibility(View.GONE);

                calculateOtherStatistics(sum);
                break;
            }
            default:
                Toast.makeText(getActivity() ,
                        getString(R.string.text_toast_net_error) , Toast.LENGTH_LONG).show();
                break;
        }
    }


    private void calculateOtherStatistics(float kiloWattHour){
        yesterdayEuro = kiloWattHour * euroForKiloWattHour;
        yesterdayCO2  = kiloWattHour * CO2ForWattHour;

        setValueInTv(yesterdayEuro, "€", tvYesterdayEuro);
        setValueInTv(yesterdayCO2, "Kg", tvYesterdayCO2);

        progBarYestEuro.setVisibility(View.GONE);
        progBarYestCO2.setVisibility(View.GONE);


    }


    private  ArrayList<Float> calculateConsumesFromLecture(ArrayList<Netsens> response){

        ArrayList<Float> lectures = new ArrayList<>();
        ArrayList<Float> consumes = new ArrayList<>();

        //extract the lecture
        for (Netsens resp : response){
            lectures.add(resp.getMeasuresList().get(0).getValue());
        }

        //sort the lecture;
        Collections.sort(lectures); //ascending order

        //extract consumes
        consumes.add( lectures.get(2) - lectures.get(0));
        consumes.add( lectures.get(3) - lectures.get(1));

        return consumes;
    }




    private float doSumAndConvertFromCentiUnit(ArrayList<Netsens> response){

        float sum = 0;

        for ( Netsens resp : response){
            sum = sum + resp.getMeasuresList().get(0).getValue();
        }

        //lecture is in centiUnit
        return sum / 100;
    }







    private void loadUserCredentials(){
        userCredentials =  session.getUserDetails();

        tvUserName.setText(userCredentials.get(KEY_NAME));
        tvStationName.setText(userCredentials.get(KEY_STATION));

    }


    //set the value in the textview with correct format and unit of measure
    private void setValueInTv(float v, String unit, TextView tv) {

        String stringValue = fixUnit(v, unit);
        tv.setText(stringValue);
    }


    private HashMap<String, Long> findStartAndStopOfYesterday(Calendar cal){

        HashMap<String, Long> fromAndTo = new HashMap<>();

        //remove one day
        cal.add(Calendar.DAY_OF_MONTH, -1);

        // reset hour, minutes, seconds and millis
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 1);
        cal.set(Calendar.MILLISECOND, 0);
        fromAndTo.put("00.01" , cal.getTimeInMillis());

        // next day
        cal.add(Calendar.DAY_OF_MONTH, 1);  //00.01
        cal.add(Calendar.MINUTE, -2);       //23.59
        fromAndTo.put("23.59" , cal.getTimeInMillis());

        return fromAndTo;
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



    //find and save layout variables
    private void findLayoutVariables(View view){
        tvUserName         = (TextView) view.findViewById(R.id.tvUserName);
        tvStationName      = (TextView) view.findViewById(R.id.tvStationName);
        serviceSwitch      = (SwitchCompat) view.findViewById(R.id.switchService);

        tvTodayValue       = (TextView) view.findViewById(R.id.tvHomeTodayParValue);
        tvYesterdayConsume = (TextView) view.findViewById(R.id.tvHomeYesterdayParValue);
        tvYesterdayCO2     = (TextView) view.findViewById(R.id.tvHomeYesterdayCO2);
        tvYesterdayEuro    = (TextView) view.findViewById(R.id.tvHomeYesterdayEuro);

        progBarToday    = (ProgressBar)view.findViewById(R.id.progHomeToday);
        progBarYestCons = (ProgressBar)view.findViewById(R.id.progHomeYestConsume);
        progBarYestCO2  = (ProgressBar)view.findViewById(R.id.progHomeYestCO2);
        progBarYestEuro = (ProgressBar)view.findViewById(R.id.progHomeYestEuro);

        //make invisible
        progBarToday.setVisibility(View.GONE);
        progBarYestCons.setVisibility(View.GONE);
        progBarYestCO2.setVisibility(View.GONE);
        progBarYestEuro.setVisibility(View.GONE);


    }



}
