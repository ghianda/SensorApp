package com.example.francesco.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ActivityIntro extends AppCompatActivity {

    Button btGoToLogin;
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
            //set login button listener
            setBtLoginListener();
        }

    }



    private void startGaia(){
        Intent i = new Intent(getApplicationContext(), ActivityHome.class);
        startActivity(i);
        finish();
    }



    private void setBtLoginListener(){
        btGoToLogin = (Button)findViewById(R.id.btToLogin);


        btGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(i);
                finish();
            }
        });

    }
}
