package com.example.francesco.myfirstapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by francesco on 18/12/2016.
 */

public class DisplayGetResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display_message); //TODO sost con displayGR_main (xml da fare)

        System.out.println("DisplayGetResultActivity CREATED");
    }
}
