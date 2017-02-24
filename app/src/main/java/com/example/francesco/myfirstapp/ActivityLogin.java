package com.example.francesco.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityLogin extends AppCompatActivity {

    Button btLogin, btCancel;
    EditText etUser, etStation, etPassword;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Session Manager Class
    SessionManager session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        session = new SessionManager(getApplicationContext());

        btLogin = (Button)findViewById(R.id.btLogin);
        btCancel = (Button)findViewById(R.id.btCancelLogin);

        //user name, station and password imput text
        etUser = (EditText)findViewById(R.id.EtUser);
        etStation = (EditText)findViewById(R.id.EtStation);
        etPassword= (EditText)findViewById(R.id.Etpassword);

        //precompile Station
        etStation.setText("723", TextView.BufferType.EDITABLE);



        //cancel login listener
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //erase the EditTextViews
                etUser.getText().clear();
                etPassword.getText().clear();
            }
        });

        //login button click event
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get data from EditText
                String username = etUser.getText().toString();
                String password = etPassword.getText().toString();
                String station  = etStation.getText().toString();

                // Check if username, password is filled
                if(username.trim().length() > 0 && password.trim().length() > 0 && station.trim().length() > 0) {

                    Toast.makeText(getApplicationContext(),
                            getString(R.string.loginRedirect), Toast.LENGTH_SHORT).show();
                    session.createLoginSession(username, station, password);


                    // Starting MainActivity
                    Intent i = new Intent(getApplicationContext(), ActivityHome.class);
                    startActivity(i);
                    finish();
                }
                else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alert.showAlertDialog(ActivityLogin.this, getString(R.string.failed), getString(R.string.noDataInsert), false);
                }
                }
            });
    }
}
