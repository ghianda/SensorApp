package com.example.francesco.myfirstapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * This receiver is drive by AlarmManager every 5 minutes
 *
 * It get data from Netsens, check the power/lumen control and (if true)
 * notify the state on notification bar of android.
 * */

public class BackgroundTask extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("BackgroundTask", "onReceive");

        //TODO :

        //1) make get online and returned the value

        //2) check value power/lumen
        if(true){
            // POSSIBILE: notifyAlarm(context, int value, String message);
            notifyAlarm(context);
        }
        else{
            //createNotify(case2);
        }
        //... ecc (switch - case)? or if? booo



    }


    private void notifyAlarm(Context context){
        //create the right notification
        Notification myNotification = createNotify(context);

        //notify on Notification bar
        fireNotification(context, myNotification);
    }


    private Notification createNotify(Context context){

        // ActivityDisplayAlarm will be started when the user clicks the notification
        // in the notification bar
        Intent notificationIntent = new Intent(context, ActivityDisplayAlarm.class);
        //todo - potrei mettere degli extra nell'intent con i dati da visualizzare
        //todo -  e il messaggio da visualizzare in ActivityDisplayAlarm


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

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
        // [[REMEMBER THAT  builder.build() returned a Notification object]]

        return builder.build();
    }


    private void fireNotification(Context context,  Notification myNotification){

        //get the NotificationManager from System
        NotificationManager notifyManager;
        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //notify my Notification:
        notifyManager.notify(1, myNotification);

    }


}
