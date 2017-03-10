package com.example.francesco.myfirstapp;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.example.francesco.myfirstapp.SensorProjectApp.COMPARE_FRAG_TAG;
import static com.example.francesco.myfirstapp.SensorProjectApp.HOME_FRAG_TAG;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_FRAGMENT_SAVED;
import static com.example.francesco.myfirstapp.SensorProjectApp.LASTREAD_FRAG_TAG;
import static com.example.francesco.myfirstapp.SensorProjectApp.TIMEREAD_FRAG_TAG;

public class ActivityReader extends AppCompatActivity {

    private String fragmentDisplayedTag;
    private View customView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        setActionBarLayoutAndButton();
        setTitleBar(getString(R.string.app_name));

        //set bottom menu
        setListenerBottomMenu();

        //set Home layout
        FragmentHome fragmentHome = new FragmentHome();
        loadFragment(fragmentHome,HOME_FRAG_TAG);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_FRAGMENT_SAVED, fragmentDisplayedTag);

    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String fragmentTag = savedInstanceState.getString(KEY_FRAGMENT_SAVED);
        restoreFragmentByTag(fragmentTag);
    }




    private void setActionBarLayoutAndButton(){

        final Activity thisActivity = this;
        ActionBar mActionBar = getSupportActionBar();

        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater li = LayoutInflater.from(this);

        View customView = li.inflate(R.layout.gaia_action_bar, null);
        this.customView = customView;

        mActionBar.setCustomView(customView);
        mActionBar.setDisplayShowCustomEnabled(true);

        ImageButton btLogout = (ImageButton)    customView.findViewById(R.id.logout);
        btLogout .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SessionManager(getApplicationContext()).logoutUser();
            }
        });

        ImageButton btSettings = (ImageButton)    customView.findViewById(R.id.settings);
        btSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(thisActivity , ActivitySettings.class);
                startActivity(i);
            }
        });

    }




    private void restoreFragmentByTag(String fragmentToRestoreTag){
        switch (fragmentToRestoreTag){
            case HOME_FRAG_TAG:
                FragmentHome fragmentHome = new FragmentHome();
                loadFragment(fragmentHome, HOME_FRAG_TAG);
                break;

            case LASTREAD_FRAG_TAG:
                FragmentLastRead fragmentLastRead = new FragmentLastRead();
                loadFragment(fragmentLastRead, LASTREAD_FRAG_TAG);
                break;

            case TIMEREAD_FRAG_TAG:
                FragmentTimeRead fragmentTimeRead= new FragmentTimeRead();
                loadFragment(fragmentTimeRead, TIMEREAD_FRAG_TAG);
                break;

            case COMPARE_FRAG_TAG:
                FragmentCompare fragmentCompare= new FragmentCompare();
                loadFragment(fragmentCompare, COMPARE_FRAG_TAG);
                break;
        }
    }


    //listener for bottom menu bar
    private void setListenerBottomMenu() {
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_bar_home:

                                FragmentHome fragmentHome = new FragmentHome();
                                loadFragment(fragmentHome, HOME_FRAG_TAG);
                                setTitleBar(getString(R.string.app_name));
                                break;

                            case R.id.action_bar_last_read:

                                FragmentLastRead fragmentLastRead = new FragmentLastRead();
                                loadFragment(fragmentLastRead, LASTREAD_FRAG_TAG);
                                setTitleBar(getString(R.string.lastReadActivityName));
                                break;

                            case R.id.action_bar_time_read:
                                FragmentTimeRead fragmentTimeRead= new FragmentTimeRead();
                                loadFragment(fragmentTimeRead, TIMEREAD_FRAG_TAG);
                                setTitleBar(getString(R.string.timeReadActivityName));
                                break;

                            case R.id.action_bar_consume:
                                FragmentCompare fragmentCompare= new FragmentCompare();
                                loadFragment(fragmentCompare, COMPARE_FRAG_TAG);
                                setTitleBar(getString(R.string.compareActivityName));
                                break;

                        }
                        return true;
                    }
                });
    }


    private void setTitleBar(String title){
        TextView tvTitle = (TextView) customView.findViewById(R.id.ActionBarTitle);
        tvTitle.setText(title);
    }




    public void loadFragment(Fragment frag, String tag)
    {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        if(fragment == null)
        {
            ft.add(R.id.fragment_container, frag, tag);
            fragmentDisplayedTag = tag;
        } else
        {
            ft.replace(R.id.fragment_container, frag, tag);
            fragmentDisplayedTag = tag;
        }
        ft.addToBackStack(null);

        ft.commit();
    }



}
