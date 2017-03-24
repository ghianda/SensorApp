package com.example.francesco.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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

        // get layout resorche
        findAndSaveInputOutputResource();
        preCompileTextView();


        //cancel login listener
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //restore the EditTextViews
                etUser.getText().clear();
                etPassword.getText().clear();
                etStation.getText().clear();
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
                    Intent i = new Intent(getApplicationContext(), ActivityReader.class);
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


    private void preCompileTextView(){

        etUser.setText(getString(R.string.urlUserTemp), TextView.BufferType.EDITABLE);
        etPassword.setText(getString(R.string.urlPasswordTemp), TextView.BufferType.EDITABLE);
        etStation.setText(getString(R.string.urlStationTemp), TextView.BufferType.EDITABLE);
    }


    private void findAndSaveInputOutputResource(){

        btLogin = (Button)findViewById(R.id.btLogin);
        btCancel = (Button)findViewById(R.id.btCancelLogin);

        //user name, station and password imput text
        etUser = (EditText)findViewById(R.id.EtUser);
        etStation = (EditText)findViewById(R.id.EtStation);
        etPassword= (EditText)findViewById(R.id.Etpassword);

    }



    @Override
    public void onBackPressed(){
        Toast.makeText(this, getString(R.string.settingsbackPressed), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                Toast.makeText(this, getString(R.string.settingsbackPressed), Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
