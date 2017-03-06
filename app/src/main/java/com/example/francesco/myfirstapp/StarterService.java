package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 14/02/2017.
 */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import static com.example.francesco.myfirstapp.SensorProjectApp.serviceRepeatPeriodInMillis;


/**The started service starts the AlarmManager that repeat the Background task. */
public class StarterService extends Service {

    private static final String TAG = "StarterService";


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //create the intent for start BackgroundTask
        Intent i = new Intent(this, BackgroundTask.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Repeat the BackgroundTask every 10 seconds (10000)
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), serviceRepeatPeriodInMillis, pi);

        Toast.makeText(this, "My Service started", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Received start id " + startId + ": " + intent);


        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service stopped", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onDestroy");
    }
}