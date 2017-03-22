package com.example.francesco.myfirstapp;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_co2Pref;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_euroPref;
import static com.example.francesco.myfirstapp.SensorProjectApp.fix;
import static java.lang.Math.abs;


public class FragmentHome extends Fragment
{

    private static final String difference_TAG = "com.example.francesco.DIFFERENCE";
    private static final String sum_TAG = "com.example.francesco.SUM";

    private ArrayList<Netsens> consumeResponse;
    private ArrayList<Netsens> powerResponse;
    private float yesterdayCO2, yesterdayEuro;


    private TextView tvTodayValue, tvYesterdayConsume,
            tvYesterdayCO2, tvYesterdayEuro;

    private Calendar nowCalendar;
    private ProgressBar progBarToday, progBarYestCons, progBarYestCO2, progBarYestEuro;


    private NetworkManager networkManager;
    private SharedPreferences sharedPref;






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //create network manager, and get an instance of calendar
        networkManager = new NetworkManager(getActivity().getApplicationContext());
        nowCalendar = Calendar.getInstance();

        //load preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

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

        //make get http for today and yesterday value
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


    private void calculateOtherStatistics(float WattHour){

        float euroForKiloWattHour = sharedPref.getFloat(KEY_euroPref, (float)0.153);
        float  CO2ForKiloHour = sharedPref.getFloat(KEY_co2Pref, (float)0.72);

        yesterdayEuro = (WattHour/1000) * (euroForKiloWattHour);
        yesterdayCO2  = (WattHour/1000) * (CO2ForKiloHour);

        setValueInTv(yesterdayEuro, "€", tvYesterdayEuro);
        setValueInTv(yesterdayCO2, "Kg", tvYesterdayCO2);

        progBarYestEuro.setVisibility(View.GONE);
        progBarYestCO2.setVisibility(View.GONE);


    }


    private  ArrayList<Float> calculateConsumesFromLecture(ArrayList<Netsens> response){

        ArrayList<Float> QgLectures = new ArrayList<>();
        ArrayList<Float> QsLectures = new ArrayList<>();
        ArrayList<Float> consumes = new ArrayList<>();

        //extract the lecture
        for (Netsens resp : response){
            if (resp.getMeasuresList().get(0).getMeter().equals("QS - Active Energy"))
                QsLectures.add(resp.getMeasuresList().get(resp.getMeasuresList().size() - 1).getValue());

            if (resp.getMeasuresList().get(0).getMeter().equals("QG - Active Energy"))
                QgLectures.add(resp.getMeasuresList().get(resp.getMeasuresList().size() - 1).getValue());

        }

        //extract consumes
        consumes.add( abs(QgLectures.get(1) - QgLectures.get(0)));
        consumes.add( abs(QsLectures.get(1) - QsLectures.get(0)));

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










    //set the value in the textview with correct format and unit of measure
    private void setValueInTv(float v, String unit, TextView tv) {

        String stringValue;
        stringValue = fix(v, unit);
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




    //find and save layout variables
    private void findLayoutVariables(View view){

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
