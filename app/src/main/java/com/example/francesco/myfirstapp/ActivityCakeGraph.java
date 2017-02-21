package com.example.francesco.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_CAKE;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_FROM_TIME;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_CONVERSION_FACTOR;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_NAME;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_UNIT;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_TO_TIME;

public class ActivityCakeGraph extends AppCompatActivity {
    String sensorName, sensorUnit, prefix;
    long fromMillis, toMillis;
    int conversionFactor;
    HashMap<String, Double> data;

    TextView tvFrom, tvTo;
    private PieChart pieChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cake_graph);


        extractDataFromIntent();

        setTitleBar();

        displayDataOnTextView();

        displayCakeGraph();

    }



    private void setTitleBar(){
        //set the Title of Activity
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(sensorName + "  [" + prefix + sensorUnit + "]  ");
    }



    private void displayCakeGraph() {

        pieChart = (PieChart) findViewById(R.id.chart);
        //chart.setDescription(sensorName);

        // enable rotation of the chart by touch
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);

        // enable hole and configure
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(25);
        pieChart.setTransparentCircleRadius(30);

        setCakeListener();

        // add data
        addData();


        // customize legends
        Legend l = pieChart.getLegend();
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
    }


    private void addData() {
        List<PieEntry> entries = new ArrayList<>();


        //insert data in entries
        for (int i = 0; i < data.size(); i++)
            entries.add(new PieEntry(Float.valueOf(data.get(data.keySet().toArray()[i]).toString())
                    , data.keySet().toArray()[i].toString()));


        PieDataSet set = new PieDataSet(entries, sensorName);
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);

        //set preferences
        data.setValueTextSize(20);

        //apply dataset at chart
        pieChart.setData(data);
        pieChart.invalidate(); // refresh


}






    private void displayDataOnTextView(){

        tvFrom       = (TextView)findViewById(R.id.cakeFromView);
        tvTo         = (TextView)findViewById(R.id.cakeToView);

        //display on textView
        SensorProjectApp.fromMillisToDateOnTextView(fromMillis, tvFrom, false);
        SensorProjectApp.fromMillisToDateOnTextView(toMillis, tvTo, false);

    }




    private void setCakeListener() {
        // set a chart value selected listener
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }
        });

    }




    private void extractDataFromIntent(){
        //ectract data from intent
        Intent intent    = getIntent();
        data             = (HashMap<String, Double>)intent.getSerializableExtra(EXTRA_CAKE);
        sensorName       = intent.getStringExtra(EXTRA_SENSOR_NAME);
        sensorUnit       = intent.getStringExtra(EXTRA_SENSOR_UNIT);
        conversionFactor = intent.getIntExtra(EXTRA_SENSOR_CONVERSION_FACTOR, 0);
        fromMillis       = intent.getLongExtra(EXTRA_FROM_TIME, 0);
        toMillis         = intent.getLongExtra(EXTRA_TO_TIME, 0);

        //todo remove
        System.out.println("ActivityCakeGraph POST extractDataFromIntent:");
        System.out.println(" + sensorUnit: " + sensorUnit);

        //convert from "centiUnit" to "Unit"
        for( String meter: data.keySet()){
            data.put(meter, data.get(meter)/conversionFactor);

            //todo remove
            System.out.println(meter + "->  " + data.get(meter));
        }

        //todo remove
        System.out.println(" + prefix: " + prefix);
        prefix = fixPrefixOfUnit();


    }





    /** control the bigger value in data, and check if is milli, kilo or normal Unit
     * then convert the value and return the prefic
     * @return
     */
    private String fixPrefixOfUnit() {
        String prefix = "";

        String maxMeter = findMax();

        System.out.println("fixPrefixOfUnit - unit: " + sensorUnit);

        //find prefix
        if (!sensorUnit.equals(" ")) {
            //parameter is not Power Factor, then i fix the unit:
            if (data.get(maxMeter) < 1) {
                prefix = "m";
            }

            if (data.get(maxMeter) > 1000) {
                prefix = "K";
            }
        }

        //fix value
        switch (prefix){
            case "m": {
                for (String meter : data.keySet()) {
                    data.put(meter, data.get(meter) * 1000);
                }
            }
            case "K": {
                for (String meter : data.keySet()) {
                    data.put(meter, data.get(meter) / 1000);
                }
            }
        }

        return prefix;
    }








    private String findMax(){
        Double max      = 0.0;
        String winner   = "";

        for( String meter : data.keySet()){
            if(data.get(meter) > max){
                winner = meter;
                max = data.get(meter);
            }
        }

        return winner;
    }
}
