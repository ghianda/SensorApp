package com.example.francesco.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class ActivityIntro extends AppCompatActivity {

    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()){
            startGaia();
        }
        else{
            startLogin();

        }

    }


    private void startGaia(){
        Intent i = new Intent(getApplicationContext(), ActivityHome.class);
        startActivity(i);
        finish();
    }

    private void startLogin(){
        Intent i = new Intent(getApplicationContext(), ActivityLogin.class);
        startActivity(i);
        finish();
    }

}
