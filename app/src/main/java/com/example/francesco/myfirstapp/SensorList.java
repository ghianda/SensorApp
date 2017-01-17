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


    public SensorList() {

        list = new HashMap<Meter, ArrayList<Sensor>>();

        ArrayList<Sensor> sensors = new ArrayList<Sensor>();


        //make list for only meter Geom/GF/Labs/Lighting
        sensors.add(new Sensor("/con", "Active Energy"));
        sensors.add(new Sensor("/actpw", "Active Power"));
        sensors.add(new Sensor("/pwf", "Power Factor"));
        sensors.add(new Sensor("/cur/1", "Current - 1' phase"));
        list.put(new Meter("Geom/GF/Labs/Lighting", "Illuminazione Laboratori"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //make list for only meter Geom/1F/Rooms/Lighting
        sensors.add(new Sensor("/con", "Active Energy"));
        sensors.add(new Sensor("/actpw", "Active Power"));
        sensors.add(new Sensor("/pwf", "Power Factor"));
        sensors.add(new Sensor("/cur/1", "Current - 1' phase"));
        list.put(new Meter("Geom/1F/Rooms/Lighting", "Illuminazione Aule 1 Piano Geometri"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter QG/Lighting
        sensors.add(new Sensor("/con", "Active Energy"));
        sensors.add(new Sensor("/actpw", "Active Power"));
        sensors.add(new Sensor("/pwf", "Power Factor"));
        sensors.add(new Sensor("/cur/1", "Current - 1' phase"));
        sensors.add(new Sensor("/cur/2", "Current - 2' phase"));
        sensors.add(new Sensor("/cur/3", "Current - 3' phase"));
        list.put(new Meter("QG/Lighting", "Illuminazione Hall e Aree Comuni"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter QS
        sensors.add(new Sensor("/reactcon", "Reactive Energy"));
        sensors.add(new Sensor("/apcon", "Apparent Energy"));
        sensors.add(new Sensor("/con", "Active Energy"));
        sensors.add(new Sensor("/actpw", "Active Power"));
        sensors.add(new Sensor("/pwf", "Power Factor"));
        sensors.add(new Sensor("/cur/1", "Current - 1' phase"));
        sensors.add(new Sensor("/cur/2", "Current - 2' phase"));
        sensors.add(new Sensor("/cur/3", "Current - 3' phase"));
        sensors.add(new Sensor("/appw", "Apparent Power"));
        sensors.add(new Sensor("/reactpw", "Reactive Power"));
        list.put(new Meter("QS", "Blocco Sportivo"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter QG
        sensors.add(new Sensor("/reactcon", "Reactive Energy"));
        sensors.add(new Sensor("/apcon", "Apparent Energy"));
        sensors.add(new Sensor("/con", "Active Energy"));
        sensors.add(new Sensor("/actpw", "Active Power"));
        sensors.add(new Sensor("/pwf", "Power Factor"));
        sensors.add(new Sensor("/cur/1", "Current - 1' phase"));
        sensors.add(new Sensor("/cur/2", "Current - 2' phase"));
        sensors.add(new Sensor("/cur/3", "Current - 3' phase"));
        sensors.add(new Sensor("/appw", "Apparent Power"));
        sensors.add(new Sensor("/reactpw", "Reactive Power"));
        list.put(new Meter("QG", "Blocco didattico"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter Geom/GF
        sensors.add(new Sensor("/reactcon", "Reactive Energy"));
        sensors.add(new Sensor("/apcon", "Apparent Energy"));
        sensors.add(new Sensor("/con", "Active Energy"));
        sensors.add(new Sensor("/actpw", "Active Power"));
        sensors.add(new Sensor("/pwf", "Power Factor"));
        sensors.add(new Sensor("/cur/1", "Current - 1' phase"));
        sensors.add(new Sensor("/cur/2", "Current - 2' phase"));
        sensors.add(new Sensor("/cur/3", "Current - 3' phase"));
        sensors.add(new Sensor("/appw", "Apparent Power"));
        sensors.add(new Sensor("/reactpw", "Reactive Power"));
        list.put(new Meter("Geom/GF", "Geometri - Piano Terra"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter Geom/1F
        sensors.add(new Sensor("/reactcon", "Reactive Energy"));
        sensors.add(new Sensor("/apcon", "Apparent Energy"));
        sensors.add(new Sensor("/con", "Active Energy"));
        sensors.add(new Sensor("/actpw", "Active Power"));
        sensors.add(new Sensor("/pwf", "Power Factor"));
        sensors.add(new Sensor("/cur/1", "Current - 1' phase"));
        sensors.add(new Sensor("/cur/2", "Current - 2' phase"));
        sensors.add(new Sensor("/cur/3", "Current - 3' phase"));
        sensors.add(new Sensor("/appw", "Apparent Power"));
        sensors.add(new Sensor("/reactpw", "Reactive Power"));
        list.put(new Meter("Geom/1F", "Geometri - Primo Piano"), sensors);
        //pulisco la lista sensors
        sensors = new ArrayList<Sensor>();


        //add sensor for only meter Geom/GF/Labs/MP
        sensors.add(new Sensor("/reactcon", "Reactive Energy"));
        sensors.add(new Sensor("/apcon", "Apparent Energy"));
        sensors.add(new Sensor("/con", "Active Energy"));
        sensors.add(new Sensor("/actpw", "Active Power"));
        sensors.add(new Sensor("/pwf", "Power Factor"));
        sensors.add(new Sensor("/cur/1", "Current - 1' phase"));
        sensors.add(new Sensor("/cur/2", "Current - 2' phase"));
        sensors.add(new Sensor("/cur/3", "Current - 3' phase"));
        sensors.add(new Sensor("/appw", "Apparent Power"));
        sensors.add(new Sensor("/reactpw", "Reactive Power"));
        list.put(new Meter("Geom/GF/Labs/MP", "Geometri - Forza motrice Laboratori"), sensors);

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


    //nome del Meter dal suo urlString
    public String getMeterNameByUrl(String urlCode) {
        return list.keySet().stream().filter(m -> m.getUrlString().equals(urlCode))
                .findFirst().get().getName();
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


    //restituisce la lista dei sensori dal codice url della chiave
    public Collection<Sensor> getSensorsByUrlMeter(String urlCode) {

        return (
                getSensorsByMeter(list.keySet().stream().
                        filter(m -> m.getUrlString().equals(urlCode)).findFirst().get())
        );

    }
}
