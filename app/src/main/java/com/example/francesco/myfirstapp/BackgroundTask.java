package com.example.francesco.myfirstapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;

import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_ACTPOWER;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_LIGHT;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_PROBLEM_DETECTED;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SUGGESTED_ACTION;
import static com.example.francesco.myfirstapp.SensorProjectApp.fixUnit;
import static com.example.francesco.myfirstapp.SensorProjectApp.windowInMillis;

/**
 * This receiver is drive by AlarmManager every 5 minutes
 *
 * It get data from Netsens, check the power/lumen control and (if true)
 * notify the state on notification bar of android.
 * */

public class BackgroundTask extends BroadcastReceiver {


    //Attributes
    private final String keyLight = "Radiazione solare";
    private final String keyActPower = "Hall-Lighting-ActivePower";
    private Calendar nowCalendar;
    private HashMap<String, Netsens> savedResponse;
    private Context contextMaster;  //context of service (StarterService) that fire intent to this Receiver




    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println( "............ new cycle:");
        Log.d("BackgroundTask", "onReceive");

        //allocate memory
        savedResponse = new HashMap<>();

        //save the context in a local attribute
        contextMaster = context;
        nowCalendar = Calendar.getInstance();

        doTask();

    }





    private void doTask(){

        ParseXmlUrl(createUrl("/light"));
        ParseXmlUrl(createUrl("/actpw"));
        //now in responseReceived thee is Data response of light and ActPower
    }





    private void ParseXmlUrl(String url){

        //connecting
        ConnectivityManager connMgr = (ConnectivityManager) contextMaster.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(contextMaster.getApplicationContext());

            //Request a XML response from the provided URL
            SimpleXmlRequest<Netsens> simpleRequest_netsens = new SimpleXmlRequest<Netsens>(
                    Request.Method.GET, url, Netsens.class,
                    new Response.Listener<Netsens>() {
                        @Override
                        public void onResponse(Netsens response) {
                            // override onResponse method
                            if (response.getMeasuresList() != null) {
                                //TODO da togliere
                                System.out.println(" >>>>>  Inside Volley.onResponse >>>>>>>");

                                extractResponse(response);

                                //TODO da togliere
                                System.out.println(" >>>>>  Exit Volley.onResponse >>>>>>>");

                            } else {

                                //TODO verificare che contextMaster.getApplicationContext() vada bene
                                Toast.makeText(contextMaster.getApplicationContext(),
                                        R.string.text_toast_no_data, Toast.LENGTH_SHORT).show();
                            }

                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //override ErrorListener method
                            Toast toast = Toast.makeText(contextMaster,
                                    R.string.text_toast_net_error, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
            );

            // Add the request to the RequestQueue.
            queue.add(simpleRequest_netsens);   //agg richiesta
            // fine get___________________________________________________________________

        } else {
            //no connessione disponibile: avviso
            Toast.makeText(contextMaster.getApplicationContext(),
                    R.string.text_toast_net_error, Toast.LENGTH_SHORT).show();
        }
    }





    private void extractResponse(Netsens newResponse){

        if (savedResponse.size() == 0){
            //insert the first response
            savedResponse.put(newResponse.getMeasuresList().get(0).getMeter(), newResponse);
        }
        else {
            if (savedResponse.size() == 1) {
                //insert the second response
                savedResponse.put(newResponse.getMeasuresList().get(0).getMeter(), newResponse);

                /** CHECK ALARM: */
                checkResponse();
            }
        }
    }




    private void checkResponse(){
        // here i check the threshold and notify if there is some problem

        //calculate the doAverage of value
        HashMap<String, Double> averageMeasure = doAverage();

        //read the data and control if there is a problem
        checkIfNotifyAlarm(contextMaster, averageMeasure);
    }





    private HashMap<String, Double> doAverage(){

        HashMap<String, Double> averageMeasure = new HashMap<>();
        Double avg = 0.0;

        for ( String key : savedResponse.keySet()){
            for ( Measure m: savedResponse.get(key).getMeasuresList()){
                avg += m.getValue();
            }
            avg /= savedResponse.get(key).getMeasuresList().size();
            averageMeasure.put(key, avg);
            avg = 0.0;
        }

        //Todo da togliere
        System.out.println(" ++++++   AVERAGE VALUE:");
        for ( String key : averageMeasure.keySet()){
            System.out.println(key + " -> " +averageMeasure.get(key));
        }

        return averageMeasure;
    }





    private String createUrl(String par){

        //create the date of now and 15 minutes before
        final long nowInMillis = nowCalendar.getTimeInMillis();
        final long beforeInMillis = nowInMillis - windowInMillis; // now - 15 minutes

        //create  url for get the last three measure
        String url = contextMaster.getResources().getString(R.string.urlDomain)
                + contextMaster.getResources().getString(R.string.m)
                + "QG/Lighting"
                + par
                + contextMaster.getResources().getString(R.string.f) + beforeInMillis
                + contextMaster.getResources().getString(R.string.t) + nowInMillis;

        return url;
    }





    private void checkIfNotifyAlarm(Context context, HashMap<String, Double> averageMeasure) {

        //TODO XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx


        if (true) {
            // todo  - here i control if there is a problem with value in averageMeasure
            // todo - [ MOVE HERE PART OF THE CODE WRITE IN createNotify ]

            //create the right notification
            Notification myNotification = createNotify(context, averageMeasure);

            //notify on Notification bar
            fireNotification(context, myNotification);
        }

        //TODO XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXx
    }






    private Notification createNotify(Context context, HashMap<String, Double> averageMeasure){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Intent notificationIntent = new Intent(context, ActivityDisplayAlarm.class);

        // ActivityDisplayAlarm will be started when the user clicks the notification
        notificationIntent.putExtra(EXTRA_ACTPOWER, averageMeasure.get(keyActPower));
        notificationIntent.putExtra(EXTRA_LIGHT, averageMeasure.get(keyLight));


        /** here i costruct the message to write in notify and to pass to intent */

        String problemDetected;
        String contentTextValues;
        String suggestedAction;

        //here ActPower is in WATT and Light is in Lumen
        if (averageMeasure.get(keyLight) < 100){
            // LOW LIGHT CASE
            problemDetected = contextMaster.getResources().getString(R.string.errorLowLight);
            suggestedAction = contextMaster.getResources().getString(R.string.suggSwitchOnLight);
        }
        else{
            // HIGH LIGHT CASE
            problemDetected = contextMaster.getResources().getString(R.string.errorHighLight);
            suggestedAction = contextMaster.getResources().getString(R.string.suggSwitchOffLight);
        }


        //costruct the content text with formatted values

        DecimalFormat frmt = new DecimalFormat(SensorProjectApp.notifyValueFormat);
        contentTextValues = (
                "Light: " + fixUnit(averageMeasure.get(keyLight),"Lux",frmt) +
                " / "  +
                "ActivePower: " + fixUnit(averageMeasure.get(keyActPower)/100, "W"));


        //set the message content
        builder.setContentTitle(suggestedAction); //here display suggeseted action
        builder.setContentText(contentTextValues);   //here display the values
        builder.setSubText(problemDetected);   //here display type of error

        //set the notification property
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.drawable.marker2);
        builder.setTicker(contextMaster.getResources().getString(R.string.tickerTextAlarm)); //è il testo che appare in alto solo appena arriva la notifica
        builder.setOngoing(true);


        //add message to intent for show in DisplayActivityAlarm
        notificationIntent.putExtra(EXTRA_PROBLEM_DETECTED, problemDetected);
        notificationIntent.putExtra(EXTRA_SUGGESTED_ACTION, suggestedAction);


        //Costruct the pending Intent and insert it in the notification
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);


        //create the notification object and returned it
        // [[REMEMBER THAT -> builder.build() returned a Notification object]]
        return (builder.build());
    }






    private void fireNotification(Context context,  Notification myNotif){

        //get the NotificationManager from System
        NotificationManager notifyManager;
        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //notify my Notification:
        notifyManager.notify(1, myNotif);

    }


}