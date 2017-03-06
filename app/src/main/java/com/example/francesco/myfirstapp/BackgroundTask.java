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
        //now in responseReceived there is Data response of light and ActPower
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

                                extractResponse(response);

                            } else {

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

        DecimalFormat frmt = new DecimalFormat(SensorProjectApp.notifyValueFormat);

        Boolean sendNotify = false;
        String problemDetected = "";
        String contentTextValues = "";
        String suggestedAction = "";
        Double avgLight     = averageMeasure.get(keyLight);
        Double avgActPower  = averageMeasure.get(keyActPower);

        //exract the hour of now
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        // ActivityDisplayAlarm will be started when the user clicks the notification
        Intent notificationIntent = new Intent(context, ActivityIntro.class);
        notificationIntent.putExtra(EXTRA_ACTPOWER, avgActPower);
        notificationIntent.putExtra(EXTRA_LIGHT, avgLight);

        //Active Power is in centiWatt, so i convert in Watt
        avgActPower /= 100;

        if (avgActPower > 7500) {
            // TOO POWER
            problemDetected = contextMaster.getResources().getString(R.string.errorTooConsume);
            suggestedAction = contextMaster.getResources().getString(R.string.suggTooConsume);
            contentTextValues = ("ActPower : " + fixUnit(avgActPower,"W",frmt));
            sendNotify = true;
        }

        if (avgLight < 100 && (hour > 6 || hour < 19)) { //100
            // TOO LOW LUX
            problemDetected = contextMaster.getResources().getString(R.string.errorTooLowLight);
            suggestedAction = contextMaster.getResources().getString(R.string.suggTooLowLight);
            contentTextValues = ("Light : " + fixUnit(avgLight,"Lux",frmt));
            sendNotify = true;
        }

        if (avgActPower > 1000 && (hour < 6 || hour > 19)){
            //INTRUDER ALARM
            problemDetected = contextMaster.getResources().getString(R.string.errorIntruderAlarm);
            suggestedAction = contextMaster.getResources().getString(R.string.suggIntruderAlarm);
            contentTextValues = formatBothInString(avgLight, avgActPower, frmt);
            sendNotify = true;
        }

        if (avgLight > 400 && avgActPower > 5500){
            //TOO WASTEFUL
            problemDetected = contextMaster.getResources().getString(R.string.errorTooWasteful);
            suggestedAction = contextMaster.getResources().getString(R.string.suggTooWasteful);
            contentTextValues = formatBothInString(avgLight, avgActPower, frmt);
            sendNotify = true;
        }

        /*TODO DA TOGLIERE_________*/
        if(true){
            problemDetected = "problem test";
            suggestedAction = "action test";
            contentTextValues = "example values";
            sendNotify = true;
        }
        //TODO  _______________ */


        if (sendNotify) {
            //costruct the notification
            Notification myNotification = makeNotification(
                    context, notificationIntent, suggestedAction, contentTextValues, problemDetected);

            //notify on Notification bar
            fireNotification(context, myNotification);
        }

    }






    private Notification makeNotification(Context context, Intent i, String action, String text, String problem){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //set the message content
        builder.setContentTitle(action); //here display suggeseted action
        builder.setContentText(text);   //here display the values
        builder.setSubText(problem);   //here display type of error

        //set the notification property
        builder.setAutoCancel(false);
        builder.setSmallIcon(R.drawable.ic_notify);
        builder.setTicker(contextMaster.getResources().getString(R.string.tickerTextAlarm)); //è il testo che appare in alto solo appena arriva la notifica
        builder.setOngoing(true);


        //add message to intent for show in DisplayActivityAlarm
        i.putExtra(EXTRA_PROBLEM_DETECTED, problem);
        i.putExtra(EXTRA_SUGGESTED_ACTION, action);


        //Costruct the pending Intent and insert it in the notification
        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(contentIntent);


        //create and return the notification object
        return builder.build();
    }







    private void fireNotification(Context context,  Notification myNotif){

        //get the NotificationManager from System
        NotificationManager notifyManager;
        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //notify my Notification:
        notifyManager.notify(1, myNotif);

    }






    private String formatBothInString(Double light, Double power, DecimalFormat frmt) {

        return ("Light: " + fixUnit(light,"Lux",frmt)
                + " / "
                + "ActivePower: " + fixUnit(power, "W"));
    }
}
