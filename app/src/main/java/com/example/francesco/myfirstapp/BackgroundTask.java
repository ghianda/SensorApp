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

import java.util.Calendar;
import java.util.HashMap;

import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_ACTPOWER;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_LIGHT;
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

        //TODO :
        //1) make get online and save the response in
        getDataFromNetsens();

        /** Occhio - usare questo */
        //notifyAlarm(context);   //funziona!!


    }





    private void getDataFromNetsens(){

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

        /*TODO da togliere:
        System.out.println("extractResponse -> extracted this:");
        for (Measure measure: newResponse.getMeasuresList()) {
            System.out.println(measure.toString());
        }
        */

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

        //calulate the doAverage of value
        HashMap<String, Double> averageMeasure = doAverage();


        /** IF.... varie soglie da ontrollare..
        /** notify, and if click opern ActivityDisplayAlarm*/
        notifyAlarm(contextMaster, averageMeasure);
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





    private void notifyAlarm(Context context, HashMap<String, Double> averageMeasure){
        //create the right notification
        Notification myNotification = createNotify(context, averageMeasure);

        //notify on Notification bar
        fireNotification(context, myNotification);
    }





    private Notification createNotify(Context context, HashMap<String, Double> averageMeasure){

        // ActivityDisplayAlarm will be started when the user clicks the notification
        // in the notification bar
        Intent notificationIntent = new Intent(context, ActivityDisplayAlarm.class);
        //todo - potrei mettere degli extra nell'intent con i dati da visualizzare
        //todo -  e il messaggio da visualizzare in ActivityDisplayAlarm:
        notificationIntent.putExtra(EXTRA_ACTPOWER, averageMeasure.get(keyActPower));
        notificationIntent.putExtra(EXTRA_LIGHT, averageMeasure.get(keyLight));

        System.out.println(" +++++++++ Intent spediti:");
        System.out.println(averageMeasure.get(keyLight));
        System.out.println(" e poi ");
        System.out.println(averageMeasure.get(keyActPower));


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //todo la notifica per benino
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setAutoCancel(false);
        builder.setTicker("this is ticker text");
        builder.setContentTitle("Gaia Notification");
        builder.setContentText("You have a new message");
        builder.setSmallIcon(R.drawable.marker2);
        builder.setContentIntent(contentIntent);
        builder.setOngoing(true);
        builder.setSubText("This is subtext...");   //API level 16

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
