package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 14/02/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutostartOnBoot extends BroadcastReceiver {

    /**
     * Listens for Android's BOOT_COMPLETED broadcast and then executes
     * the onReceive() method.
     */
    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.d("AutostartOnBoot", "BOOT_COMPLETED broadcast received. Executing starter service.");

        Intent intent = new Intent(context, StarterService.class);
        context.startService(intent);
    }
}