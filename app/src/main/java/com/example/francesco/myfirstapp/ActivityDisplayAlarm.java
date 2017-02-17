package com.example.francesco.myfirstapp;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_ACTPOWER;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_LIGHT;
import static com.example.francesco.myfirstapp.SensorProjectApp.fixUnit;

/**Open when click on alarm notification*/
public class ActivityDisplayAlarm extends AppCompatActivity {

    //Attribute
    Double avgLight;
    Double avgActPower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_alarm);

        //get the data from extra
        avgLight = getIntent().getDoubleExtra(EXTRA_LIGHT, 0.0);
        avgActPower = getIntent().getDoubleExtra(EXTRA_ACTPOWER, 0.0);

        System.out.println(" ........................ intent received:");
        System.out.println(avgLight + " and " + avgActPower);


        //TODO DA MODIFICARE NELLA VISUALIZZAZIONE
        //TODO AGGIUNGERE DI PASSARGLI IL TIMESTAMP!!
        setValueOnTextValue();
    }



    private void setValueOnTextValue(){

        //Power is in centiWatt
        avgActPower = avgActPower/100;

        String avgLightFixed = fixUnit(avgLight, "Lux");
        String avkActPowerFixed = fixUnit(avgActPower, "W");


        TextView tvLightValue = (TextView) findViewById(R.id.tvLightValue);
        TextView tvActPowerValue= (TextView) findViewById(R.id.tvActPowerValue);

        tvLightValue.setText(avgLightFixed);
        tvActPowerValue.setText(avkActPowerFixed);

    }



    @Override
    protected void onResume() {
        super.onResume();

        // Clear the Notification Bar after you've clicked on the message in the Notification Bar
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }
}
