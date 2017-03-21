package com.example.francesco.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
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
import static com.github.mikephil.charting.components.Legend.LegendPosition.PIECHART_CENTER;

public class ActivityCakeGraph extends AppCompatActivity {
    private String sensorName, sensorUnit, prefix;
    private long fromMillis, toMillis;
    private float conversionFactor;
    private HashMap<String, Float> data;

    private TextView tvFrom, tvTo, tvCurrentCake;
    private ImageButton backBt;
    private PieChart pieChart;

    private String lastChartTag;

    private final String totCakeTag = "tot";
    private final String qgCakeTag = "QG";
    private final String geom1FCakeTag = "geom1F";
    private final String geomGFCakeTag = "geomGF";




    private PieData totPieData;
    private PieData qgPieData;
    private PieData geomGroundFloorPieData;
    private PieData geomFirstFloorPieData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hideTitleBarIfLandscape();
        setContentView(R.layout.activity_cake_graph);

        backBt = (ImageButton)findViewById(R.id.cakeBackButton);
        tvCurrentCake = (TextView)findViewById(R.id.tvCakeCurrentCake);

        setButtonListener(backBt);

        extractDataFromIntent();

        setTitleBar();
        displayTimeRangeOnTextView();

        preparePieChart();
        preparePieDatasets(data);


        //dislay firts data rapresentation
        displayTotCake();


    }


    private void displayTotCake(){

        lastChartTag = "";
        displayCakeGraph(totPieData);
        tvCurrentCake.setText(getString(R.string.cakeTot));
        setCakeListener(totListener);
        setButtonListener(backBt);
    }

    private void displayQgCake(){

        lastChartTag = totCakeTag;
        displayCakeGraph(qgPieData);
        tvCurrentCake.setText(getString(R.string.mm_qg));
        setCakeListener(qgListener);
        setButtonListener(backBt);
    }

    private void displayGeom1FCake(){

        lastChartTag = qgCakeTag;
        displayCakeGraph(geomFirstFloorPieData);
        tvCurrentCake.setText(getString(R.string.mm_geom_1f));
        setCakeListener(geomListener);
        setButtonListener(backBt);
    }

    private void displayGeomGFCake(){

        lastChartTag = qgCakeTag;
        displayCakeGraph(geomGroundFloorPieData);
        tvCurrentCake.setText(getString(R.string.mm_geom_gf));
        setCakeListener(geomListener);
        setButtonListener(backBt);
    }





    private OnChartValueSelectedListener totListener = new OnChartValueSelectedListener(){
        @Override
        public void onValueSelected(Entry e, Highlight h) {

            switch ((int)h.getX()){
                case 0: break;  //QS
                case 1: displayQgCake(); break;
                default: break;
            }
        }

        @Override
        public void onNothingSelected() {
        }
    };

    private OnChartValueSelectedListener qgListener = new OnChartValueSelectedListener(){
        @Override
        public void onValueSelected(Entry e, Highlight h) {

            switch ((int)h.getX()){
                case 0: displayGeomGFCake(); break;
                case 1: displayGeom1FCake(); break;
                case 2: break; //QG/Lighting
                case 3: break; //altro
                default: break;
            }
        }

        @Override
        public void onNothingSelected() {

        }
    };

    private OnChartValueSelectedListener geomListener = new OnChartValueSelectedListener(){
        @Override
        public void onValueSelected(Entry e, Highlight h) {

        }

        @Override
        public void onNothingSelected() {

        }
    };









    private void preparePieDatasets(HashMap<String, Float> data){

        totPieData = PrepareTotDataSet(data);
        qgPieData = PrepareQgDataSet(data);
        geomGroundFloorPieData = PrepareGeomGroundFloorDataSet(data);
        geomFirstFloorPieData = PrepareGeomFirstFloorDataSet(data);

    }


    /**Geom/1F = Geom/1F/Rooms/Lighting + altro
     * altro = Geom/1F - Geom/1F/Rooms/Lighting */
    private PieData PrepareGeomFirstFloorDataSet(HashMap<String, Float> data) {

        List<PieEntry> entries = new ArrayList<>();

        float other = data.get(getString(R.string.urlGeomFirstFloor)) - (data.get(getString(R.string.urlGeomRoomsLighting)));

        entries.add(
                new PieEntry(data.get(getString(R.string.urlGeomRoomsLighting)) , getString(R.string.cake_1f_roomslighting)));

        if(other>0)
            entries.add(new PieEntry( other , getString(R.string.other)));

        return makePieDataFromEntries(entries);

    }



    /**Geom/GF = Geom/GF/Labs/Lighting + Geom/GF/Labs/MP + altro
     * altro = Geom/GF - (Geom/GF/Labs/Lighting + Geom/GF/Labs/MP) */
    private PieData PrepareGeomGroundFloorDataSet(HashMap<String, Float> data) {

        List<PieEntry> entries = new ArrayList<>();

        float other = data.get(getString(R.string.urlGeomGF)) -
                (data.get(getString(R.string.urlGeomLabsLighting)) + data.get(getString(R.string.urlGeomLabsMP)));

        entries.add(
                new PieEntry(data.get(getString(R.string.urlGeomLabsLighting)) , getString(R.string.cake_gf_labslighting)));
        entries.add(
                new PieEntry(data.get(getString(R.string.urlGeomLabsMP)) , getString(R.string.cake_geom_gf_labsmotionpower)));

        if(other>0)
            entries.add(new PieEntry( other , getString(R.string.other)));

        return makePieDataFromEntries(entries);

    }







    /**QG = Geom/GF + Geom/1F + QG/Lighting + altro
     altro = QG - (Geom/GF + Geom/1F + QG/Lighting) */
    private PieData PrepareQgDataSet(HashMap<String, Float> data) {
        List<PieEntry> entries = new ArrayList<>();

        float other = data.get(getString(R.string.urlQG)) -
                (data.get(getString(R.string.urlGeomGF)) + data.get(getString(R.string.urlGeomFirstFloor))
                        + data.get(getString(R.string.urlQGHallLighting)));

        entries.add(
                new PieEntry(data.get(getString(R.string.urlGeomGF)) , getString(R.string.cake_geom_gf)));
        entries.add(
                new PieEntry(data.get(getString(R.string.urlGeomFirstFloor)) , getString(R.string.cake_geom_1f)));
        entries.add(
                new PieEntry(data.get(getString(R.string.urlQGHallLighting)) , getString(R.string.cake_qg_hall_lighting)));
        if(other>0)
            entries.add(new PieEntry( other , getString(R.string.other)));

        return makePieDataFromEntries(entries);

    }


    /**TOT = QS + QG */
    private PieData PrepareTotDataSet(HashMap<String, Float> data) {

        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(data.get(getString(R.string.urlQS)), getString(R.string.cake_qs)));
        entries.add(new PieEntry(data.get(getString(R.string.urlQG)), getString(R.string.cake_qg)));

        return makePieDataFromEntries(entries);

    }





    private PieData makePieDataFromEntries(List<PieEntry> entries){
        PieDataSet set = new PieDataSet(entries, "");

        //set preferences
        set.setColors(ColorTemplate.MATERIAL_COLORS);

        //set value formatter
        set.setValueFormatter(new PieValueFormatter(prefix, sensorUnit));


        PieData pieData = new PieData(set);

        //set preferences
        pieData.setValueTextSize(20);

        return pieData;
    }


    private void preparePieChart(){

        pieChart = (PieChart) findViewById(R.id.chart);
        //chart.setDescription(sensorName);

        // enable rotation of the chart by touch
        pieChart.setRotationAngle(180);
        pieChart.setRotationEnabled(true);

        // enable hole and configure
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(50);
        pieChart.setTransparentCircleRadius(55);
        pieChart.setDrawSliceText(false);

        // hide legends
        Legend l = pieChart.getLegend();
        //l.setEnabled(false);
        l.setPosition(PIECHART_CENTER);

        //hide description label
        pieChart.setContentDescription("");
        Description d = new Description();
        d.setText("");
        pieChart.setDescription(d);

    }





    private void displayCakeGraph(PieData pieData) {


        //apply dataset at chart
        pieChart.setData(pieData);

        // refresh
        pieChart.invalidate();
    }





    private void setCakeListener(OnChartValueSelectedListener listener) {

        // set a chart value selected listener
        pieChart.setOnChartValueSelectedListener(listener);

    }



    //TODO
    private void setButtonListener(final ImageButton backBt){

        backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (lastChartTag){
                    case "": break;
                    case totCakeTag: displayTotCake(); break;
                    case qgCakeTag: displayQgCake(); break;
                    case geom1FCakeTag: displayGeom1FCake(); break;
                    case geomGFCakeTag: displayGeomGFCake(); break;
                    default: break;
                }

            }
        });

    }





























    private void displayTimeRangeOnTextView(){

        tvFrom       = (TextView)findViewById(R.id.cakeFromView);
        tvTo         = (TextView)findViewById(R.id.cakeToView);

        //display on textView
        SensorProjectApp.fromMillisToDateOnTextView(fromMillis, tvFrom, true);
        SensorProjectApp.fromMillisToDateOnTextView(toMillis, tvTo, true);

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

        //convert to "Unit" from "centi" or other
        for( String meter: data.keySet()){
            data.put(meter, data.get(meter)/conversionFactor);
        }

        prefix = fixPrefixOfUnit();


    }


    public void hideTitleBarIfLandscape()
    {

        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        int width = display.getWidth();
        int height = display.getHeight();

        if(width > height)
        {   /* In Landscape */

            //hide the androd notify bar
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //hide action bar
            getSupportActionBar().hide();
        }

    }


    private void setTitleBar(){
        //set the Title of Activity
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(sensorName);
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
