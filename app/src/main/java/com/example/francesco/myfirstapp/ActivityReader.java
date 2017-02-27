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

public class ActivityReader extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);





        //set bottom menu
        setListenerBottomMenu();

        //set Home layout
        FragmentHome fragmentHome = new FragmentHome();
        loadFragment(fragmentHome, "fragmentHome");

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
                                loadFragment(fragmentHome, "fragmentHome");
                                break;

                            case R.id.action_bar_last_read:

                                FragmentLastRead fragmentLastRead = new FragmentLastRead();
                                loadFragment(fragmentLastRead, "fragmentLastRead");
                                break;

                            case R.id.action_bar_time_read:
                                FragmentTimeRead fragmentTimeRead= new FragmentTimeRead();
                                loadFragment(fragmentTimeRead, "fragmentTimeRead");
                                break;

                            case R.id.action_bar_consume:
                                //FragmentCompare fragmentCompare= new FragmentCompare();
                                //loadFragment(fragmentCompare, "fragmentCompare");
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
        } else
        {
            ft.replace(R.id.fragment_container, frag, tag);
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
