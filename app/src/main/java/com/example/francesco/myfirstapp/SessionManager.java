package com.example.francesco.myfirstapp;

/**
 * Created by francesco on 20/02/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.util.HashMap;

import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_isLoginPref;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_namePref;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_passwordPref;
import static com.example.francesco.myfirstapp.SensorProjectApp.KEY_stationPref;

public class SessionManager {
    // Shared Preferences
    SharedPreferences _pref;

    // Editor for Shared preferences
    Editor _editor;

    // Context
    Context _context;



    // Constructor
    public SessionManager(Context context){

        this._context = context;
        _pref = PreferenceManager.getDefaultSharedPreferences(context);
        _editor = _pref.edit();
    }


    /**
     * Create login session
     * */
    public void createLoginSession(String name, String station, String password){
        // Storing login value as TRUE
        _editor.putBoolean(KEY_isLoginPref, true);

        // Storing name in _pref
        _editor.putString(KEY_namePref, name);

        // Storing station in _pref
        _editor.putString(KEY_stationPref, station);

        // Storing password in _pref
        _editor.putString(KEY_passwordPref, password);

        // commit changes
        _editor.commit();
    }




    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_namePref, _pref.getString(KEY_namePref, null));

        // station name
        user.put(KEY_stationPref, _pref.getString(KEY_stationPref, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        _editor.clear();
        _editor.commit();

        //TODO non cancella i settings ma solo i dati utente

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, ActivityLogin.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Starting Login Activity
        _context.startActivity(i);
    }

    // Get Login State
    public boolean isLoggedIn(){
        return _pref.getBoolean(KEY_isLoginPref, false);
    }
}

