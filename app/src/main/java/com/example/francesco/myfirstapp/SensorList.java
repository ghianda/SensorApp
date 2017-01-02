package com.example.francesco.myfirstapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by francesco on 22/12/2016.
 */

/* TODO sostituire le stringhe scritte a mano con delle risorse string, così posso abilitare ita/eng
    es: sostituire
            list.put(new Meter("Geom/GF", "Piano Terra"))
    con
            list.put(new Meter(R.string_array.bla, R.string_array.blo))
*/


public class SensorList {

    private HashMap<Meter, ArrayList<Sensor>> list; // meter -> list of sensors

    private Sensor reactpw = new Sensor("/reactpw", "Reactive Power");
    private Sensor reactcon = new Sensor("/reactcon", "Reactive Consumption");
    private Sensor pwf = new Sensor("/pwf", "Power Factor");
    private Sensor cur3 = new Sensor("cur/3", "Current - 3' phase");
    private Sensor cur2 = new Sensor("cur/2", "Current - 2' phase");
    private Sensor cur1 = new Sensor("cur/1", "Current - 1' phase");
    private Sensor con = new Sensor("/con", "Consumption");
    private Sensor appw = new Sensor("/appw", "Apparent Power");
    private Sensor apcon = new Sensor("/apcon", "Apparent consumption");
    private Sensor actpw = new Sensor("/actpw", "Active Power");
    //TODO manca la sigla url per l'Active Energy
    //TODO manca la sigla url per Reactive Energy
    //TODO manca la sigla url per Apparent Energy


    public SensorList() {

        list = new HashMap<Meter, ArrayList<Sensor>>();

        ArrayList<Sensor> sensors = new ArrayList<Sensor>();


        //make list for only meter Geom/GF/Labs/Lighting and Geom/1F/Rooms/Lighting
        // TODO sensors.add(ACTIVE ENERGY);
        sensors.add(actpw);
        sensors.add(pwf);
        sensors.add(cur1);
        list.put(new Meter("Geom/GF/Labs/Lighting", "Illuminazione Laboratori"), sensors);
        list.put(new Meter("Geom/1F/Rooms/Lighting", "Illuminazione Aule 1 Piano Geometri"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter QG/Lighting
        // TODO sensors.add(ACTIVE ENERGY);
        sensors.add(actpw);
        sensors.add(pwf);
        sensors.add(cur1);
        sensors.add(cur3);
        sensors.add(cur2);
        list.put(new Meter("QG/Lighting", "Illuminazione Hall e Aree Comuni"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter QS, QG, Geom/GF, Geom/1F
        // TODO sensors.add(REACTIVE ENERGY);
        //TODO sensors.add(APPARENT ENERGY);
        // TODO sensors.add(ACTIVE ENERGY);
        sensors.add(actpw);
        sensors.add(pwf);
        sensors.add(cur1);
        sensors.add(cur3);
        sensors.add(cur2);
        sensors.add(appw);
        sensors.add(reactpw);

        list.put(new Meter("QS", "Blocco Sportivo"), sensors);
        list.put(new Meter("QG", "Blocco didattico"), sensors);
        list.put(new Meter("Geom/GF", "Piano Terra"), sensors);
        list.put(new Meter("Geom/1F", "Primo Piano"), sensors);
        list.put(new Meter("Geom/GF/Labs/MP", "Forza motrice Laboratori"), sensors);

    }


    //restituisce la lista dei meter (chiavi della lista)
    public Collection<Meter> getMeters() {
        return list.keySet();
    }


    //restituisce una lista dei nomi delle meter
    public ArrayList<String> getMetersName() {

        ArrayList<String> names = new ArrayList<>();
        list.keySet().forEach(meter -> names.add(meter.getName()));
        return names;
    }


    //restituisce l'oggetto Meter dall'id
    public Meter getMeterById(int id) {

        return list.keySet().toArray(new Meter[0])[id];
    }


    //restituisce la lista dei sensori dalla chiave m
    public Collection<Sensor> getSensorsByMeter(Meter m) {

        return list.get(m);
    }


    //restituisce la lista dei parametri dalla chiave di indice i
    public Collection<Sensor> getSensorsByIdMeter(int i) {

        Meter m = list.keySet().toArray(new Meter[0])[i];
        return list.get(m);
    }


    //restituisce l'oggetto sensore di indice i nella lista associata al Meter m
    public Sensor getSensor(Meter m, int i) {

        return list.get(m).get(i);
    }


    //restituisce la lista dei nomi dei sensori dalla chiave Meter m
    public ArrayList<String> getSensorsNamesByMeter(Meter m) {

        ArrayList<String> names = new ArrayList<>();
        list.get(m).forEach(sensor -> names.add(sensor.getName()));
        return names;
    }


    //restituisce la lista dei nomi dei sensori dalla chiave di indice i
    public ArrayList<String> getSensorsNamesByIdMeter(int id) {

        Meter m = list.keySet().toArray(new Meter[0])[id];
        return getSensorsNamesByMeter(m);
    }
}
