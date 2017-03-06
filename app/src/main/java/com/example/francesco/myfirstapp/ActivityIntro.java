package com.example.francesco.myfirstapp;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ActivityIntro extends AppCompatActivity {

    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Clear the Notification Bar after you've clicked on the message in the Notification Bar
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        // Session Manager
        session = new SessionManager(getApplicationContext());

        NetworkManager.getInstance(this);

        if (session.isLoggedIn()){
            startGaia();
        }
        else{
            startLogin();

        }

    }



    private void startGaia(){
        //Intent i = new Intent(getApplicationContext(), ActivityHome.class);
        Intent i = new Intent(getApplicationContext(), ActivityReader.class);
        startActivity(i);
        finish();
    }

    private void startLogin(){
        Intent i = new Intent(getApplicationContext(), ActivityLogin.class);
        startActivity(i);
        finish();
    }

}
