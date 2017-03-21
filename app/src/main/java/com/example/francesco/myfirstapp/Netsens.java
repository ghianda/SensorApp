package com.example.francesco.myfirstapp;


import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by francesco on 07/12/2016.
 */


@Root(name="Netsens")
public class Netsens {

    @ElementList(inline = true, data = false, empty = true, entry = "Measure", name = "", required = false, type = void.class)
    private List<Measure> measuresList;
    /** NOTE: measuresList is never assigned but this is writed by parsing xml */

    // public Netsens() {}

    public List<Measure> getMeasuresList() {
        return measuresList;
    }

    @Override
    public String toString() {
        return "Netsens{" +

                "first measure meter= " + measuresList.get(0).getMeter() +
                "and value= " + measuresList.get(0).getValue() +
                '}';
    }
}

