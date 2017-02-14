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

public class StarterService extends Service {

    private static PendingIntent piForStop = null;
    private static final String TAG = "StarterService";


    /**
     * The started service starts the AlarmManager.
     */


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //create the intent for start NotificationBarAlarm
        Intent i = new Intent(this, NotificationBarAlarm.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // Repeat the task every 15 seconds (15000)
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 15000, pi);

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


    public static void stopAlarm(){

    }
}