package com.example.francesco.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.WindowManager;
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

import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_DATA_CAKE;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_FROM_TIME;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_CONVERSION_FACTOR;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_NAME;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_SENSOR_UNIT;
import static com.example.francesco.myfirstapp.SensorProjectApp.EXTRA_TO_TIME;

public class ActivityCakeGraph extends AppCompatActivity {
    String sensorName, sensorUnit, prefix;
    long fromMillis, toMillis;
    float conversionFactor;
    HashMap<String, Float> data;

    TextView tvFrom, tvTo;
    private PieChart pieChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideTitleBarIfLandscape();
        setContentView(R.layout.activity_cake_graph);


        extractDataFromIntent();

        setTitleBar();

        displayDataOnTextView();

        displayCakeGraph();

    }


    public void hideTitleBarIfLandscape()
    {

        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        int width = display.getWidth();
        int height = display.getHeight();

        if(width > height)
        {
                           /* In Landscape */
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

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

        //TODO
        setCakeListener();

        // add data
        addData();


        // customize legends
        Legend l = pieChart.getLegend();
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);

        // refresh
        pieChart.invalidate();
    }


    private void addData() {
        List<PieEntry> entries = new ArrayList<>();

        //insert data in entries list
        for (String key : data.keySet()) {
            entries.add(new PieEntry(data.get(key), key));
        }


        PieDataSet set = new PieDataSet(entries, sensorName);
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(set);

        //set preferences
        data.setValueTextSize(20);

        //apply dataset at chart
        pieChart.setData(data);



    }






    private void displayDataOnTextView(){

        tvFrom       = (TextView)findViewById(R.id.cakeFromView);
        tvTo         = (TextView)findViewById(R.id.cakeToView);

        //display on textView
        SensorProjectApp.fromMillisToDateOnTextView(fromMillis, tvFrom, true);
        SensorProjectApp.fromMillisToDateOnTextView(toMillis, tvTo, true);

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
        data             = (HashMap<String, Float>)intent.getSerializableExtra(EXTRA_DATA_CAKE);
        sensorName       = intent.getStringExtra(EXTRA_SENSOR_NAME);
        sensorUnit       = intent.getStringExtra(EXTRA_SENSOR_UNIT);
        conversionFactor = intent.getFloatExtra(EXTRA_SENSOR_CONVERSION_FACTOR, (float)0.0);
        fromMillis       = intent.getLongExtra(EXTRA_FROM_TIME, 0);
        toMillis         = intent.getLongExtra(EXTRA_TO_TIME, 0);

        //convert to "Unit"
        for( String meter: data.keySet()){
            data.put(meter, data.get(meter)/conversionFactor);
        }

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
                prefix = "k";
            }
        }

        //fix value
        switch (prefix){
            case "m": {
                for (String meter : data.keySet()) {
                    data.put(meter, data.get(meter) * 1000);
                }
            }
            case "k": {
                for (String meter : data.keySet()) {
                    data.put(meter, data.get(meter) / 1000);
                }
            }
        }

        return prefix;
    }








    private String findMax(){
        float max     = 0;
        String winner = "";

        for( String meter : data.keySet()){
            if(data.get(meter) > max){
                winner = meter;
                max = data.get(meter);
            }
        }

        System.out.println("maxValue : " + max);
        System.out.println("maxMeter : " + winner);

        return winner;
    }
}
