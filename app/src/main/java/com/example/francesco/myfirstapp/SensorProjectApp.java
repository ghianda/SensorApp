package com.example.francesco.myfirstapp;

import android.app.Application;

/**
 * Created by francesco on 11/01/2017.
 */


public class SensorProjectApp extends Application {
    //key string for intent object
    public final static String EXTRA_PARCDATARESPONSE = "com.example.francesco.PARCDATARESPONSE";
    public final static String EXTRA_SENSOR = "com.example.francesco.SENSOR";
    public final static String EXTRA_METER = "com.example.francesco.METER";

    //parametri
    static final public String valueFormat = "###.###";

    //Global data (here we store the sensor value(s)
    static private SensorList globalSensorData = new SensorList(); //data with only name parameter (like the spinner menu)


    //METHOD________________________________________________________________________________


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
            globalSensorData.getSensorsByUrlMeter(chosenMeter.getUrlString()).forEach(sensor -> {
                if (sensor.getUrlString().equals(chosenSensor.getUrlString())) {

                    //set the measure unit and the conversion factor
                    //TODO aggiungere casi mancanti al metodo (vedi sigle mancanti in sensorlist)
                    //sensor.setConversionFactorByUrlCode();

                    //add convertedValue + timestamp
                    sensor.addValue(
                            (double) (measure.getValue() / sensor.getConversionFactor())
                            ,
                            measure.getTimeStamp());

                }
            });
        });


        //TODO DA TOGLiere
        //testUpdate(); //STAMPA A VIDEO la GlobalSensorData



    }


    public double getLastValueFromMeterAndSensor(Meter chosenMeter, Sensor chosenSensor) {
        //TODO aggiungere try catch per caso data.size = 0

        //extract the right sensor
        Sensor ss = globalSensorData.getSensorsByUrlMeter(chosenMeter.getUrlString()).stream().
                filter(sensor -> sensor.getUrlString().equals(chosenSensor.getUrlString())).findAny().get();

        //extract last value added to data list
        return ss.getDatas().get(ss.getDatas().size() - 1).getValue();
    }


    public long getLastTimestampFromMeterAndSensor(Meter chosenMeter, Sensor chosenSensor) {
        //TODO aggiungere try catch per caso data.size = 0

        //extract the right sensor
        Sensor ss = globalSensorData.getSensorsByUrlMeter(chosenMeter.getUrlString()).stream().
                filter(sensor -> sensor.getUrlString().equals(chosenSensor.getUrlString())).findAny().get();

        //extract last value added to data list
        return ss.getDatas().get(ss.getDatas().size() - 1).getTimestamp();
    }


    public String getUnitOfMeasureFromSensor(Sensor chosenSensor) {

        //TODO TOGLIERE
        System.out.println("unit finded: " + Sensor.getUnitOfMeasureByUrlCode(chosenSensor.getUrlString()));

        return Sensor.getUnitOfMeasureByUrlCode(chosenSensor.getUrlString());
    }


    public static Sensor createParceableDataResponse(Netsens response, Sensor chosenSensor) {
        //crea un oggetto Sensor (quindi Parceable) dai dati in response

        Sensor ss = new Sensor(chosenSensor.getUrlString(), chosenSensor.getName());
        //put data into object
        response.getMeasuresList().forEach(measure ->
                ss.addValue(measure.getValue(), measure.getTimeStamp())
        );

        return ss;

    }




}
