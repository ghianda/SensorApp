package com.example.francesco.myfirstapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by francesco on 16/12/2016.
 */

public class TestPickerHourActivity extends AppCompatActivity {

    private TextView tvDisplayHour;
    private Button btChangeHour;

    private int hour;
    private int minute;

    static final int HOUR_DIALOG_ID = 666;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_main_hour);

        setCurrentHourOnView();
        addListenerOnButton();
    }


    // display current hour in textView
    public void setCurrentHourOnView() {
        tvDisplayHour = (TextView) findViewById(R.id.tvHour);
        //dpResult = (DatePicker) findViewById(R.id.dpResult); //TODO

        //estraggo la data odierna
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);

        // set current time into textview
        tvDisplayHour.setText(new StringBuilder()
                .append(hour).append("-").append(minute).append(" "));
    }


    //dichiaro un Listener sul bottone "change hour"
    public void addListenerOnButton() {

        btChangeHour = (Button) findViewById(R.id.btnChangeHour);

        btChangeHour.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(HOUR_DIALOG_ID);
            }

        });

    }


    //metodo che fa apparire il timePicker se attivato il Dialog dal Listener
    @Override
    protected Dialog onCreateDialog(int id) {
        //controllo che sia stato creato il Dialog con codice 666
        switch (id) {

            case HOUR_DIALOG_ID: //se si
                //  esegue il TimePicker e ripesca l'ora selezionata
                return new TimePickerDialog(this, timePickerListener, hour, minute, true);
        }
        return null;
    }


    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener(){

                // when dialog box is closed, below method will be called.
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute){
                    hour    = selectedHour;
                    minute  = selectedMinute;

                    //set selected date into textview
                    tvDisplayHour.setText(new StringBuilder().append(hour)
                            .append("-").append(minute).append(" "));
                }

            };






}
