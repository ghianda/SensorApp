package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 14/02/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutostartOnBoot extends BroadcastReceiver {

    /**
     * Listens for Android's BOOT_COMPLETED broadcast and then executes
     * the onReceive() method.
     */
    @Override
    public void onReceive(Context context, Intent arg1) {

        Intent intent = new Intent(context, StarterService.class);
        context.startService(intent);
    }
}