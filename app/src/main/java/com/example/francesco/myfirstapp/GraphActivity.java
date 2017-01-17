package com.example.francesco.myfirstapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //get the data from extra
        Sensor parcSens = getIntent().getParcelableExtra(SensorProjectApp.EXTRA_PARCDATARESPONSE);
        String meterUrl = getIntent().getStringExtra(SensorProjectApp.EXTRA_METER);

        //Set the title of Activity
        setTitle(parcSens, meterUrl);

        //set the data into TextView (Avg, min, Max)
        //TODO
        setAvgTv(parcSens);
        setMinAndMaxTv(parcSens);


        //put data into LineChart and Display it
        displayChart(parcSens);
    }


    //calculate the average of value and set into TextView
    private void setAvgTv(Sensor ss) {

        OptionalDouble avg = ss.getDatas().stream().mapToDouble(Data::getValue).average();
        TextView tvAvg = (TextView) findViewById(R.id.tvAverage);

        //if the average is present
        avg.ifPresent((double value) ->
                setValueInTv(value / ss.getConversionFactor(), ss.getUnitOfMeasure(), tvAvg)
        );

        //else
        if (!avg.isPresent()) tvAvg.setText(R.string.dataError);
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

        setValueInTv(minValueData.getValue() / ss.getConversionFactor(), ss.getUnitOfMeasure(), tvValue);
        setTimeInTv(minValueData, tvTime);
    }


    //search the max value in ss.datas and set in textview tv
    private void setMaxTv(Sensor ss, TextView tvValue, TextView tvTime) {

        Data maxValueData = ss.findDataWithMaxValue();

        setValueInTv(maxValueData.getValue() / ss.getConversionFactor(), ss.getUnitOfMeasure(), tvValue);
        setTimeInTv(maxValueData, tvTime);

    }


    private void setTimeInTv(Data data, TextView tv) {
        boolean shortVersion = true;
        SensorProjectApp.fromMillisToDateOnTextView(data.getTimestamp(), tv, shortVersion);
    }


    //set the value in the textview with correct format and unit of measure
    private void setValueInTv(double v, String unit, TextView tv) {

        //formatto il valore in Stringa
        DecimalFormat frmt = new DecimalFormat(SensorProjectApp.valueFormat);
        String stringValue = frmt.format(v);
        tv.setText(new StringBuilder()
                .append(stringValue).append(" ")
                .append(unit));
    }


    private void setTitle(Sensor ss, String meterUrl) {

        ((TextView) findViewById(R.id.tvMeterSensorName)).setText(
                new StringBuilder()
                        .append(SensorProjectApp.getGlobalSensorData().getMeterNameByUrl(meterUrl))
                        .append("  -  ")
                        .append(ss.getName()));
    }


    public void displayChart(Sensor ss) {
        // in this example, a LineChart is initialized from xml
        LineChart chart = (LineChart) findViewById(R.id.chart);

        List<Entry> entries = new ArrayList<>();

        //put data int entries
        ss.getDatas().forEach(data -> {
            entries.add(new Entry((float) data.getTimestamp(), (float) data.getValue()));
        });

        //creo LineDataSet
        LineDataSet dataSet = new LineDataSet(entries, "label");

        //associo LineDataSet alloggetto LineData da visualizzare
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate();
    }

}
