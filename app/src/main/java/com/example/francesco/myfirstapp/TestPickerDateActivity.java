package com.example.francesco.myfirstapp;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

/**
 * Created by francesco on 15/12/2016.
 */


public class TestPickerDateActivity extends AppCompatActivity {

    private TextView tvDisplayDate;
    private Button btnChangeDate;

    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 999;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_main_date);

        setCurrentDateOnView();
        addListenerOnButton();

    }

    // display current date in textView
    public void setCurrentDateOnView() {

        tvDisplayDate = (TextView) findViewById(R.id.tvDate);
        //dpResult = (DatePicker) findViewById(R.id.dpResult); //TODO

        //estraggo la data odierna
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // set current date into textview
        tvDisplayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

    }


    //dichiaro un Listener sul bottone "change date"
    public void addListenerOnButton() {

        btnChangeDate = (Button) findViewById(R.id.btnChangeDate);

        btnChangeDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        //controllo che sia stato creato il Dialog con codice 999
        switch (id) {
            case DATE_DIALOG_ID: //se si
                //  esegue il datePicker e ripesca la data selezionata
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }


    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into textview
            tvDisplayDate.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

            // set selected date into datepicker also
            //dpResult.init(year, month, day, null); //TODO

        }
    };

}
