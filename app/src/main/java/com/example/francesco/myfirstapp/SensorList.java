package com.example.francesco.myfirstapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static com.example.francesco.myfirstapp.SensorProjectApp.sensorComparatorByName;
import static com.example.francesco.myfirstapp.SensorProjectApp.sortByKeysName;

/**
 * Created by francesco on 22/12/2016.
 */

/* TODO sostituire le stringhe scritte a mano con delle risorse string, così posso abilitare ita/eng
    es: sostituire
            list.put(new Meter("Geom/GF", "Piano Terra"))
    con
            list.put(new Meter(R.string_array.bla, R.string_array.blo))
*/



public class SensorList{

    private HashMap<Meter, ArrayList<Sensor>> list; // meter -> list of sensors


    public SensorList(Context context) {
        //costruttore con liste complete e valori vuoti

        list = new HashMap<Meter, ArrayList<Sensor>>();

        ArrayList<Sensor> sensors = new ArrayList<Sensor>();


        //make list for only meter Geom/GF/Labs/Lighting
        sensors.add(new Sensor("/con", context.getString(R.string.sscon)));
        sensors.add(new Sensor("/actpw", context.getString(R.string.ssactpw)));
        sensors.add(new Sensor("/pwf", context.getString(R.string.sspwf)));
        sensors.add(new Sensor("/cur/1", context.getString(R.string.sscur1)));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("Geom/GF/Labs/Lighting", context.getString(R.string.mm_gf_labslighting)), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();



        //make list for only meter Geom/1F/Rooms/Lighting
        sensors.add(new Sensor("/con", context.getString(R.string.sscon)));
        sensors.add(new Sensor("/actpw", context.getString(R.string.ssactpw)));
        sensors.add(new Sensor("/pwf", context.getString(R.string.sspwf)));
        sensors.add(new Sensor("/cur/1", context.getString(R.string.sscur1)));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("Geom/1F/Rooms/Lighting", context.getString(R.string.mm_1f_roomslighting)), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //make list for only meter Geom/1F/Rooms/53
        sensors.add(new Sensor("/temp", context.getString(R.string.sstemp)));
        sensors.add(new Sensor("/light", context.getString(R.string.sslight)));
        sensors.add(new Sensor("/humid", context.getString(R.string.sshumid)));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("Geom/1F/Rooms/53", "Aula 53"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //make list for only meter Geom/1F/Rooms/54
        sensors.add(new Sensor("/temp", context.getString(R.string.sstemp)));
        sensors.add(new Sensor("/light", context.getString(R.string.sslight)));
        sensors.add(new Sensor("/humid", context.getString(R.string.sshumid)));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("Geom/1F/Rooms/54", "Aula 54"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //make list for only meter Geom/1F/Rooms/55
        sensors.add(new Sensor("/temp", context.getString(R.string.sstemp)));
        sensors.add(new Sensor("/light", context.getString(R.string.sslight)));
        sensors.add(new Sensor("/humid", context.getString(R.string.sshumid)));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("Geom/1F/Rooms/55", "Aula 55"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter QG/Lighting
        sensors.add(new Sensor("/con", context.getString(R.string.sscon)));
        sensors.add(new Sensor("/actpw", context.getString(R.string.ssactpw)));
        sensors.add(new Sensor("/pwf", context.getString(R.string.sspwf)));
        sensors.add(new Sensor("/cur/1", context.getString(R.string.sscur1)));
        sensors.add(new Sensor("/cur/2", context.getString(R.string.sscur2)));
        sensors.add(new Sensor("/cur/3", context.getString(R.string.sscur3)));
        sensors.add(new Sensor("/light", "Light"));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("QG/Lighting", context.getString(R.string.mm_qg_hall_lighting)), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter QS
        sensors.add(new Sensor("/reactcon", context.getString(R.string.ssreactcon)));
        sensors.add(new Sensor("/apcon", context.getString(R.string.ssapcon)));
        sensors.add(new Sensor("/con", context.getString(R.string.sscon)));
        sensors.add(new Sensor("/actpw", context.getString(R.string.ssactpw)));
        sensors.add(new Sensor("/pwf", context.getString(R.string.sspwf)));
        sensors.add(new Sensor("/cur/1", context.getString(R.string.sscur1)));
        sensors.add(new Sensor("/cur/2", context.getString(R.string.sscur2)));
        sensors.add(new Sensor("/cur/3", context.getString(R.string.sscur3)));
        sensors.add(new Sensor("/appw", context.getString(R.string.ssappw)));
        sensors.add(new Sensor("/reactpw", context.getString(R.string.ssreactpw)));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("QS", context.getString(R.string.mm_qs)), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter QG
        sensors.add(new Sensor("/reactcon", context.getString(R.string.ssreactcon)));
        sensors.add(new Sensor("/apcon", context.getString(R.string.ssapcon)));
        sensors.add(new Sensor("/con", context.getString(R.string.sscon)));
        sensors.add(new Sensor("/actpw", context.getString(R.string.ssactpw)));
        sensors.add(new Sensor("/pwf", context.getString(R.string.sspwf)));
        sensors.add(new Sensor("/cur/1", context.getString(R.string.sscur1)));
        sensors.add(new Sensor("/cur/2", context.getString(R.string.sscur2)));
        sensors.add(new Sensor("/cur/3", context.getString(R.string.sscur3)));
        sensors.add(new Sensor("/appw", context.getString(R.string.ssappw)));
        sensors.add(new Sensor("/reactpw", context.getString(R.string.ssreactpw)));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("QG", context.getString(R.string.mm_qg)), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter Geom/GF
        sensors.add(new Sensor("/reactcon", context.getString(R.string.ssreactcon)));
        sensors.add(new Sensor("/apcon", context.getString(R.string.ssapcon)));
        sensors.add(new Sensor("/con", context.getString(R.string.sscon)));
        sensors.add(new Sensor("/actpw", context.getString(R.string.ssactpw)));
        sensors.add(new Sensor("/pwf", context.getString(R.string.sspwf)));
        sensors.add(new Sensor("/cur/1", context.getString(R.string.sscur1)));
        sensors.add(new Sensor("/cur/2", context.getString(R.string.sscur2)));
        sensors.add(new Sensor("/cur/3", context.getString(R.string.sscur3)));
        sensors.add(new Sensor("/appw", context.getString(R.string.ssappw)));
        sensors.add(new Sensor("/reactpw", context.getString(R.string.ssreactpw)));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("Geom/GF", context.getString(R.string.mm_geom_gf)), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter Geom/1F
        sensors.add(new Sensor("/reactcon", context.getString(R.string.ssreactcon)));
        sensors.add(new Sensor("/apcon", context.getString(R.string.ssapcon)));
        sensors.add(new Sensor("/con", context.getString(R.string.sscon)));
        sensors.add(new Sensor("/actpw", context.getString(R.string.ssactpw)));
        sensors.add(new Sensor("/pwf", context.getString(R.string.sspwf)));
        sensors.add(new Sensor("/cur/1", context.getString(R.string.sscur1)));
        sensors.add(new Sensor("/cur/2", context.getString(R.string.sscur2)));
        sensors.add(new Sensor("/cur/3", context.getString(R.string.sscur3)));
        sensors.add(new Sensor("/appw", context.getString(R.string.ssappw)));
        sensors.add(new Sensor("/reactpw", context.getString(R.string.ssreactpw)));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("Geom/1F", context.getString(R.string.mm_geom_1f)), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter Geom/GF/Labs/MP
        sensors.add(new Sensor("/reactcon", context.getString(R.string.ssreactcon)));
        sensors.add(new Sensor("/apcon", context.getString(R.string.ssapcon)));
        sensors.add(new Sensor("/con", context.getString(R.string.sscon)));
        sensors.add(new Sensor("/actpw", context.getString(R.string.ssactpw)));
        sensors.add(new Sensor("/pwf", context.getString(R.string.sspwf)));
        sensors.add(new Sensor("/cur/1", context.getString(R.string.sscur1)));
        sensors.add(new Sensor("/cur/2", context.getString(R.string.sscur2)));
        sensors.add(new Sensor("/cur/3", context.getString(R.string.sscur3)));
        sensors.add(new Sensor("/appw", context.getString(R.string.ssappw)));
        sensors.add(new Sensor("/reactpw", context.getString(R.string.ssreactpw)));
        //TODO TRY
        Collections.sort(sensors, sensorComparatorByName);
        list.put(new Meter("Geom/GF/Labs/MP", context.getString(R.string.mm_geom_gf_labsmotionpower)), sensors);

        //TODO TRY TO SORT THE MAP
        list = sortByKeysName(list);

    }



    /** COTRUTTORE CHE COMPILA LE METER INSERENDO IL PARAMETRO SENSORS */
    public SensorList( ArrayList<Sensor> sensors , Context context) {

        list = new HashMap<Meter, ArrayList<Sensor>>();

        list.put(new Meter("QS", context.getString(R.string.mm_qs)), sensors);
        list.put(new Meter("QG", context.getString(R.string.mm_qg)), sensors);

        list.put(new Meter("Geom/1F", context.getString(R.string.mm_geom_1f)), sensors);
        list.put(new Meter("Geom/GF/Labs/Lighting", context.getString(R.string.mm_gf_labslighting)), sensors);
        list.put(new Meter("Geom/1F/Rooms/Lighting", context.getString(R.string.mm_1f_roomslighting)), sensors);
        list.put(new Meter("QG/Lighting", context.getString(R.string.mm_qg_hall_lighting)), sensors);
        list.put(new Meter("Geom/GF", context.getString(R.string.mm_geom_gf)), sensors);
        list.put(new Meter("Geom/GF/Labs/MP", context.getString(R.string.mm_geom_gf_labsmotionpower)), sensors);
    }



    //restituisce la lista dei meter (chiavi della lista)
    public Collection<Meter> getMeters() {
        return list.keySet();
    }


    //restituisce una lista dei nomi delle meter
    public ArrayList<String> getMetersName() {

        ArrayList<String> names = new ArrayList<>();

        for (Meter meter : list.keySet()) {
            names.add(meter.getName());
        }

        return names;
    }



    //restituisce l'oggetto Meter dall'id
    public Meter getMeterById(int id) {

        return list.keySet().toArray(new Meter[0])[id];
    }



    //nome del Meter dal suo urlString
    public String getMeterNameByUrl(String urlCode) {

        String meterName = null;

        for (Meter m : list.keySet()) {
            if (m.getUrlString().equals(urlCode)) {
                meterName = m.getName();
            }
        }

        return meterName;
    }


    //restituisce la lista dei sensori dalla chiave m
    public Collection<Sensor> getSensorsByMeter(Meter m) {

        return list.get(m);
    }




    //restituisce l'oggetto sensore di indice i nella lista associata al Meter m
    public Sensor getSensor(Meter m, int i) {

        return list.get(m).get(i);
    }


    //restituisce la lista dei nomi dei sensori dalla chiave Meter m
    public ArrayList<String> getSensorsNamesByMeter(Meter m) {

        ArrayList<String> names = new ArrayList<>();

        for (Sensor sensor : list.get(m)) {
            names.add(sensor.getName());
        }

        return names;
    }




    //restituisce la lista dei sensori dal codice url della chiave
    public Collection<Sensor> getSensorsByUrlMeter(String urlMeterCode) {

        Collection<Sensor> sensors = null;

        for (Meter m : list.keySet()) {
            if (m.getUrlString().equals(urlMeterCode)) {
                sensors = getSensorsByMeter(m);
            }
        }

        return sensors;
    }


}
