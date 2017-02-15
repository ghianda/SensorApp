package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 14/02/2017.
 */
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class OnBootCompletedExampleActivity extends AppCompatActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boot_completed_example);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Clear the Notification Bar after you've clicked on the message in the Notification Bar
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

}
