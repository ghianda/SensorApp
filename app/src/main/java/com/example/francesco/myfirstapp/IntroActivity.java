package com.example.francesco.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by francesco on 22/12/2016.
 */

public class IntroActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity);
    }


    /*  Start Last_Reading button method    */
    public void startLastReadActivity(View view) {

        Intent intent = new Intent(this, LastReadActivity.class);
        startActivity(intent);
    }

    /*  Start Time_Get button activity*/
    public void startTimeActivity(View view) {

        //Intent intent = new Intent(this, TimeRequestActivity.class);
        //startActivity(intent);
    }
}
