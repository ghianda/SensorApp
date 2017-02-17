package com.example.francesco.myfirstapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.francesco.myfirstapp.SensorProjectApp.fixUnit;

public class ActivityLinearGraph extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        //getActionBar().setDisplayHomeAsUpEnabled(true); //visualizza "freccia indietro" in actionbar

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
        displayLineChart(parcSens);
    }


    /*
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
    */


    //calculate the average of value and set into TextView
    private void setAvgTv(Sensor ss) {

        double avg = 0;
        TextView tvAvg = (TextView) findViewById(R.id.tvAverage);

        if (ss.getDatas().size() > 0) {
            for (Data data : ss.getDatas()) {
                avg += data.getValue();
            }
            avg /= ss.getDatas().size();

            setValueInTv(avg / ss.getConversionFactor(), ss.getUnitOfMeasure(), tvAvg);
        } else tvAvg.setText(R.string.dataError);
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

        //TODO da togliere
        System.out.println(minValueData);

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

        String stringValue = fixUnit(v, unit);
        tv.setText(stringValue);
    }


    private void setTitle(Sensor ss, String meterUrl) {

        ((TextView) findViewById(R.id.tvMeterSensorName)).setText(
                new StringBuilder()
                        .append(SensorProjectApp.getGlobalSensorData().getMeterNameByUrl(meterUrl))
                        .append("  -  ")
                        .append(ss.getName()));
    }


    public void displayLineChart(Sensor ss) {

        LineChart chart = (LineChart) findViewById(R.id.chart);

        //XAMPLE TO set the LiitLine_________________________________
        YAxis leftAxis = chart.getAxisLeft();

        LimitLine ll = new LimitLine(140f, "Example");
        ll.setLineColor(Color.RED);
        ll.setLineWidth(4f);
        ll.setTextColor(Color.BLACK);
        ll.setTextSize(12f);

        leftAxis.addLimitLine(ll);
        //___________________________________________________________


        //set the dayAxisFormatter------------------------------------------------------------
        List<Entry> entries = new ArrayList<>();

        //TODO c'è un modo piu furbo pe tirar fuori i valori dell'asse x inseriti nella chart?

        //Extract the Timestamp array
        ArrayList<Long> oldTS = new ArrayList<>();
        for (Data data : ss.getDatas()) {
            oldTS.add(data.getTimestamp());
        }

        //find the min Timestamp
        long referenceTimestamp = Collections.min(oldTS);


        //put rearranged timestamp and data value into entries
        for (Data data : ss.getDatas()) {
            entries.add(new Entry((float) data.getTimestamp() - referenceTimestamp,
                    (float) data.getValue()));
        }

        //creo LineDataSet
        LineDataSet dataSet = new LineDataSet(entries, "label");

        //associo LineDataSet all'oggetto LineData da visualizzare
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        IAxisValueFormatter xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);

        //-----------------------------------------------------------------------------------

        // Set the marker View
        /*
        MyMarkerView myMarkerView= new MyMarkerView(getApplicationContext(),
                R.layout.custom_marker_view,
                referenceTimestamp.getAsLong());
        chart.setMarkerView(myMarkerView);
        */

        //display the chart
        chart.invalidate();
    }

}
