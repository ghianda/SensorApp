package com.example.francesco.myfirstapp;

import java.util.ArrayList;

/**
 * Created by francesco on 28/12/2016.
 */

public class Sensor {

    private String urlCode;
    private String name;
    private ArrayList<Data> datas = new ArrayList<>();
    private String unitOfMeasure;
    private int conversionFactor; //(10^x): is for convert in better order (ex: kW instead mW)


    //sensore vuoto
    public Sensor() {
    }


    //sensore senza valori
    public Sensor(String urlCode, String name) {
        this.urlCode = urlCode;
        this.name = name;
    }

    //sensore con valore già acquisito
    public Sensor(String urlCode, String unit, ArrayList<Data> datas, String name, int conv) {
        this.urlCode = urlCode;
        this.unitOfMeasure = unit;
        this.datas = datas;
        this.name = name;
        this.conversionFactor = conv;
    }


    public void addValue(long value, long timestamp) {
        //todo da ogliere
        System.out.println("value-timestamp aggiunto: " + value + "-" + timestamp);
        //Data newData = new Data(value, timestamp);
        //this.datas.add(newData);
        this.datas.add(new Data(value, timestamp));
    }

    /*TODO
    public ArrayList<Long> getConvertedValues(){
        ArrayList<Long> convertedValues = new ArrayList<Long>();
        this.datas.forEach(d -> convertedValues.add(d.getValue() * this.conversionFactor));
        return  convertedValues;
    }
    */

    //insert the value
    /*TODO setValues
    public void setValue(??, unitOfMeasure, int conversionFactor) {
        this.unitOfMeasure = unitOfMeasure;
    }
    */

    public String getUrlString() {
        return urlCode;
    }

    public String getName() {
        return name;
    }


    public ArrayList<Data> getDatas() {
        return datas;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }
}
