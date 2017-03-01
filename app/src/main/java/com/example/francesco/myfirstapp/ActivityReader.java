package com.example.francesco.myfirstapp;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import static com.example.francesco.myfirstapp.SensorProjectApp.COMPARE_FRAG_TAG;
import static com.example.francesco.myfirstapp.SensorProjectApp.HOME_FRAG_TAG;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_FRAGMENT_SAVED;
import static com.example.francesco.myfirstapp.SensorProjectApp.LASTREAD_FRAG_TAG;
import static com.example.francesco.myfirstapp.SensorProjectApp.TIMEREAD_FRAG_TAG;

public class ActivityReader extends AppCompatActivity {

    private String fragmentDisplayedTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);


        //set bottom menu
        setListenerBottomMenu();

        //set Home layout
        FragmentHome fragmentHome = new FragmentHome();
        loadFragment(fragmentHome,HOME_FRAG_TAG);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("MyString", "Welcome back to Android");
        outState.putString(KEY_FRAGMENT_SAVED, fragmentDisplayedTag);
        System.out.println("ActivityReader -> onSaveInstanceState -> frag_saved: " + fragmentDisplayedTag);

    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String fragmentTag = savedInstanceState.getString(KEY_FRAGMENT_SAVED);
        System.out.println("ActivityReader -> onRestoreInstanceState-> frag_saved: " + fragmentTag);
        restoreFragmentByTag(fragmentTag);
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
                //FragmentCompare fragmentCompare= new FragmentCompare();
                //loadFragment(fragmentCompare, COMPARE_FRAG_TAG);
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
                                break;

                            case R.id.action_bar_last_read:

                                FragmentLastRead fragmentLastRead = new FragmentLastRead();
                                loadFragment(fragmentLastRead, LASTREAD_FRAG_TAG);
                                break;

                            case R.id.action_bar_time_read:
                                FragmentTimeRead fragmentTimeRead= new FragmentTimeRead();
                                loadFragment(fragmentTimeRead, TIMEREAD_FRAG_TAG);
                                break;

                            case R.id.action_bar_consume:
                                //FragmentCompare fragmentCompare= new FragmentCompare();
                                //loadFragment(fragmentCompare, COMPARE_FRAG_TAG);
                                break;

                        }
                        return true;
                    }
                });
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





    // Overrided method for logout button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        SessionManager session = new SessionManager(getApplicationContext());

        switch (item.getItemId()) {

            case R.id.action_logout:
                session.logoutUser();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gaia_intro_menu, menu);
        return true;
    }
}
