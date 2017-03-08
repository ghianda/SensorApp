package com.example.francesco.myfirstapp;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * Created by francesco on 08/03/2017.
 */


public class PieValueFormatter implements IValueFormatter {

    private DecimalFormat mFormat;
    private String sensorUnit, prefix;

    public PieValueFormatter(String prefix, String sensorUnit) {
        mFormat = new DecimalFormat("###,###,##0.0"); // use one decimal
        this.sensorUnit = sensorUnit;
        this.prefix = prefix;
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        // write your logic here
        return mFormat.format(value) + " " + prefix+sensorUnit;
        //return value + " " + prefix+sensorUnit;
    }
}
