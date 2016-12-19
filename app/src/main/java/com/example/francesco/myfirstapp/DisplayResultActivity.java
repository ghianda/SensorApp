package com.example.francesco.myfirstapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by francesco on 15/12/2016.
 */




public class DisplayResultActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);

        System.out.println("DISPLAY-RESULT-ACTIVITY CREATED");


        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_SENSOR);
        System.out.println("value estracted");

        //aggiungere una textbox al volo
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        ViewGroup layout = (ViewGroup) findViewById(R.id.activity_display_message);
        layout.addView(textView);
    }
}