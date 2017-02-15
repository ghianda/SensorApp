package com.example.francesco.myfirstapp;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**Open when click on alarm notification*/
public class ActivityDisplayAlarm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_alarm);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Clear the Notification Bar after you've clicked on the message in the Notification Bar
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
