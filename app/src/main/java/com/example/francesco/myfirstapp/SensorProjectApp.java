package com.example.francesco.myfirstapp;

import android.app.Application;

/**
 * Created by francesco on 11/01/2017.
 */

public class SensorProjectApp extends Application {

    //Global data (here we store the sensor value(s)
    private SensorList globalSensorData = new SensorList(); //data with only name parameter (like the spinner menu)


    public SensorList getGlobalSensorData() {
        return globalSensorData;
    }


    public void testUpdate() {
        System.out.println("test update: \n \n");
        globalSensorData.getMeters().forEach(meter -> {
            System.out.println("meter name: " + meter.getName());
            globalSensorData.getSensorsByMeter(meter).forEach(sensor -> {
                System.out.println(" - sensore: " + sensor.getName());
                sensor.getDatas().forEach(d -> {
                    System.out.println("    .. " + d.getValue() + "-" + d.getTimestamp());
                });
            });
        });
    }


    public void setGlobalData(Netsens response, Meter chosenMeter, Sensor chosenSensor) {
        //per ogni misura contenuta in response
        response.getMeasuresList().forEach(measure -> {

            //cerca dentro globaldata la giusta coppia Meter-Sensor e aggiungici i dati contenuti nella misura
            this.globalSensorData.getSensorsByUrlMeter(chosenMeter.getUrlString()).forEach(sensor -> {
                if (sensor.getUrlString().equals(chosenSensor.getUrlString())) {
                    sensor.addValue((long) measure.getValue(), measure.getTimeStamp());
                }
            });
        });


        //TODO DA TOGLiere
        testUpdate(); //STAMPA A VIDEO la GlobalSensorData

    }
}
