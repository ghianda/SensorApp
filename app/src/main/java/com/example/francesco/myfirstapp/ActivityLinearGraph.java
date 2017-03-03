package com.example.francesco.myfirstapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.francesco.myfirstapp.SensorProjectApp.findPrefixOfMeasure;
import static com.example.francesco.myfirstapp.SensorProjectApp.fixUnit;
import static java.lang.Math.abs;

public class ActivityLinearGraph extends AppCompatActivity {

    Sensor parcSens;
    String label, meterUrl, prefix, sensorUnit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if view or not title bar (if landscape or not)
        hideTitleBarIfLandscape();
        setContentView(R.layout.activity_line_graph);

        //get the data from extra
        parcSens = getIntent().getParcelableExtra(SensorProjectApp.EXTRA_PARCDATARESPONSE);
        meterUrl = getIntent().getStringExtra(SensorProjectApp.EXTRA_METER);

        setTitle();

        //set the Min and Max into TextView
        setMinAndMaxTv(parcSens);

        //put data into LineChart and Display it
        displayLineChart(parcSens);
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



    private void setTitle(){

        //create label for title and legend
        String sensorLabel = createSensorLabel(parcSens);

        TextView tvMeter = (TextView)findViewById(R.id.titleLineGraphMeter);
        tvMeter.setText(SensorProjectApp.getGlobalSensorData().getMeterNameByUrl(meterUrl));

        TextView tvSensor = (TextView)findViewById(R.id.titleLineGraphSensor);
        tvSensor.setText(sensorLabel);
    }



    //set the minimun and maximun value in the textview
    private void setMinAndMaxTv(Sensor ss) {

        if (ss.getDatas().size() != 0) {

            //find and set Min
            TextView tvMin = (TextView) findViewById(R.id.tvMin);
            TextView tvMinTime = (TextView) findViewById(R.id.tvMinTime);
            setMinTv(ss, tvMin, tvMinTime);

            //find and set max
            TextView tvMax = (TextView) findViewById(R.id.tvMax);
            TextView tvMaxTime = (TextView) findViewById(R.id.tvMaxTime);
            setMaxTv(ss, tvMax, tvMaxTime);

        } else {
            ((TextView) findViewById(R.id.tvMin)).setText(R.string.dataError);
            ((TextView) findViewById(R.id.tvMax)).setText(R.string.dataError);
        }
    }


    //search the min value in ss.datas and set in textview tv
    private void setMinTv(Sensor ss, TextView tvValue, TextView tvTime) {

        Data minValueData = ss.findDataWithMinValue();

        setValueInTv(minValueData.getValue(), ss.getUnitOfMeasure(), tvValue);
        setTimeInTv(minValueData, tvTime);
    }


    //search the max value in ss.datas and set in textview tv
    private void setMaxTv(Sensor ss, TextView tvValue, TextView tvTime) {

        Data maxValueData = ss.findDataWithMaxValue();

        setValueInTv(maxValueData.getValue(), ss.getUnitOfMeasure(), tvValue);
        setTimeInTv(maxValueData, tvTime);

    }


    private void setTimeInTv(Data data, TextView tv) {
        boolean shortVersion = true;
        SensorProjectApp.fromMillisToDateOnTextView(data.getTimestamp(), tv, shortVersion);
    }



    //set the value in the textview with correct format and unit of measure
    private void setValueInTv(double v, String unit, TextView tv) {

        String stringValue = fixUnit(v, unit);
        tv.setText(stringValue);
    }


    private String createSensorLabel(Sensor ss) {

        ss.setConversionFactorByUrlCode();
        if (ss.getUnitOfMeasure().equals(" ")) {
            //power factor
            return new StringBuilder()
                    .append(ss.getName())
                    .append(sensorUnit)
                    .toString();
        }
        else{
            //not power

            prefix = findPrefixOfMeasure(ss);
            sensorUnit = ss.getUnitOfMeasure();

            return new StringBuilder()
                    .append(ss.getName())
                    .append("  [").append(prefix).append(sensorUnit).append("]  ")
                    .toString();

        }
    }






    public void displayLineChart(Sensor ss) {

        LineChart chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<>();

        //display the avg line
        displayAverageInGraph(ss, chart);


        //Extract the Timestamp array for set the dayAxisFormatter
        ArrayList<Long> oldTS = new ArrayList<>();
        for (Data data : ss.getDatas()) {
            oldTS.add(data.getTimestamp());
        }

        //find the min Timestamp
        long referenceTimestamp = Collections.min(oldTS);


        /** NOTE: here i control data values and remove it if zero or too big*/
        //put rearranged timestamp and data value into entries
        for (Data data : ss.getDatas()) {
            if (data.getValue() != 0 && abs(data.getValue())<1000000000)
            entries.add(new Entry((float) data.getTimestamp() - referenceTimestamp,
                    (float) data.getValue()));
        }



        //creo LineDataSet
        LineDataSet dataSet = new LineDataSet(entries, label);

        //associo LineDataSet all'oggetto LineData da visualizzare
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        //layout preferences
        dataSet.setDrawValues(false);
        dataSet.setColor(getResources().getColor(R.color.colorLineGraph));
        dataSet.setDrawCircles(false);
        chart.getLegend().setEnabled(false);   // Hide the legend


        //X axis formatter
        IAxisValueFormatter xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);


        //hide Y right Axis
        chart.getAxisRight().setDrawLabels(false);

        //show the graph
        chart.invalidate();
    }




    private void displayAverageInGraph(Sensor ss, LineChart chart){

        DecimalFormat frmt = new DecimalFormat(SensorProjectApp.notifyValueFormat);
        YAxis leftAxis = chart.getAxisLeft();

        //build avg value and label
        double avg = doAverage(ss);
        String avgLabel = new StringBuilder()
                .append(getString(R.string.average))
                .append(" ")
                .append(fixUnit(avg, ss.getUnitOfMeasure(), frmt))
                .toString();

        //build LimitLine
        LimitLine ll = new LimitLine((float)avg, avgLabel);

        ll.setLineColor(Color.RED);
        ll.setLineWidth(1f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);

        leftAxis.addLimitLine(ll);

    }






    private double doAverage(Sensor ss) {
        double avg = 0;

        if (ss.getDatas().size() > 0) {
            for (Data data : ss.getDatas()) {
                avg += data.getValue();
            }
            avg /= ss.getDatas().size();

        }

        return avg;
    }




}
