package com.example.francesco.myfirstapp;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_ACTPOWER;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_LIGHT;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_PROBLEM_DETECTED;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SUGGESTED_ACTION;
import static com.example.francesco.myfirstapp.SensorProjectApp.fixUnit;

/**Open when click on alarm notification*/
public class ActivityDisplayAlarm extends AppCompatActivity {

    //Attribute
    Double avgLight;
    Double avgActPower;
    String problemDetected;
    String suggestedAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_alarm);

        getDataFromIntent();

        setValueOnTextValue();
    }




    @Override
    protected void onResume() {
        super.onResume();

        // Clear the Notification Bar after you've clicked on the message in the Notification Bar
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }




    private void setValueOnTextValue(){

        TextView tvLightValue       = (TextView) findViewById(R.id.tvLightValue);
        TextView tvActPowerValue    = (TextView) findViewById(R.id.tvActPowerValue);
        TextView tvProblemDetected  = (TextView) findViewById(R.id.tvDispErrActProblemDetected);
        TextView tvSuggestedAction  = (TextView) findViewById(R.id.tvDispErrActSuggestedAction);


        //Power is in centiWatt
        avgActPower = avgActPower/100;

        //display the value received
        tvLightValue.setText(fixUnit(avgLight, "Lux"));
        tvActPowerValue.setText(fixUnit(avgActPower, "W"));

        //display the message received
        tvProblemDetected.setText(problemDetected);
        tvSuggestedAction.setText(suggestedAction);

    }



    private void getDataFromIntent(){
        //get the data from extra
        avgLight        = getIntent().getDoubleExtra(EXTRA_LIGHT, 0.0);    //default value: 0.0
        avgActPower     = getIntent().getDoubleExtra(EXTRA_ACTPOWER, 0.0);
        problemDetected = getIntent().getStringExtra(EXTRA_PROBLEM_DETECTED);
        suggestedAction = getIntent().getStringExtra(EXTRA_SUGGESTED_ACTION);
    }
}
