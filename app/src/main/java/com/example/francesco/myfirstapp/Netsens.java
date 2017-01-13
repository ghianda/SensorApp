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
    //455

    // public Netsens() {}

    public List<Measure> getMeasuresList() {
        return measuresList;
    }



}

