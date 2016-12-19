package com.example.francesco.myfirstapp;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by francesco on 08/12/2016.
 */

@Root(name= "Measure")
public class Measure {

    @Attribute(name="Unit")
    private final String unit;

    @Attribute(name="Meter")
    private final String meter;

    @Attribute(name="Value")
    private final float value;

    @Attribute(name="Timestamp")
    private final String timeStamp;


    // esempio:
    //<Measure Unit="Geometri-Labs" Meter="Lighting - Active Power" Value="113" Timestamp="1480503905000"/>

    public Measure(@Attribute(name="Unit") String unit,
                   @Attribute(name="Meter") String meter,
                   @Attribute(name="Value") float value,
                   @Attribute(name="Timestamp") String timeStamp)
    {
        this.unit       = unit;
        this.meter      = meter;
        this.value      = value;
        this.timeStamp  = timeStamp;
    }

    public String getUnit(){
        return  unit;
    }

    public String getMeter(){
        return  meter;
    }

    public float getValue(){ return  value; }

    public String getTimeStamp(){
        return  timeStamp;
    }

    @Override
    public String toString() {
        return
                "{unit='" + unit + '\'' +
                ", meter='" + meter + '\'' +
                ", value=" + value +
                ", timeStamp='" + timeStamp + '\'' +
                "}";
    }
}