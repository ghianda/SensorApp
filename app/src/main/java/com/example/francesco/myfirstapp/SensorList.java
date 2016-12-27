package com.example.francesco.myfirstapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by francesco on 22/12/2016.
 */

public class SensorList {

    private HashMap<String, ArrayList<String>> sensors; // sensor -> list of parameters

    public SensorList() {

        sensors = new HashMap<String, ArrayList<String>>();
        ArrayList<String> parameters = new ArrayList<String>();

        //prepare list of 1 sensor
        parameters.add("par a1");
        parameters.add("par a2");
        parameters.add("par a3");
        sensors.put("primo sensore", parameters);

        //pulisco la lista dei parametri
        parameters = new ArrayList<String>();

        //prepare list of 2 sensor
        parameters.add("par b1");
        parameters.add("par b2");
        parameters.add("par b3");
        sensors.put("secondo sensore", parameters);

        //pulisco la lista dei parametri
        parameters = new ArrayList<String>();

        //TODO FARE PER TUTTI I SENSORI...


    }

    //restituisce la lista dei sensori (chiavi della lista)
    public Collection<String> getSensors() {
        return sensors.keySet();
    }


    public Collection<String> getParametersBySensor(String s) {
        return sensors.get(s);

    }


}
