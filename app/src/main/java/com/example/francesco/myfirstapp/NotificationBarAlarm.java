package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 14/02/2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotificationBarAlarm extends BroadcastReceiver {

    NotificationManager notifyManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("NotificationAlarm", "onReceive");

        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // ActivityDisplayAlarm will be started when the user clicks the notification
        // in the notification bar

        //Intent notificationIntent = new Intent(context, ActivityDisplayAlarm.class);
        Intent notificationIntent = new Intent(context, ActivityIntro.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);


        //todo fare funzione che mi crea la notifica per benino
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setAutoCancel(false);
        builder.setTicker("this is ticker text");
        builder.setContentTitle("Bar Notification");
        builder.setContentText("You have a new message");
        builder.setSmallIcon(R.drawable.marker2);
        builder.setContentIntent(contentIntent);
        builder.setOngoing(true);
        builder.setSubText("This is subtext...");   //API level 16

        //create the notification object
        Notification myNotification = builder.build();

        //notify:
        notifyManager.notify(1, myNotification);
    }
}