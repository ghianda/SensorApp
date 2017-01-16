package com.example.francesco.myfirstapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by francesco on 28/12/2016.
 */


//VERSIONE CLASSE PARCEABLE
public class Sensor implements Parcelable {

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

        setConversionFactorByUrlCode();
    }

    //sensore con valore già acquisito
    public Sensor(String urlCode, String unit, ArrayList<Data> datas, String name, int conv) {
        this.urlCode = urlCode;
        this.unitOfMeasure = unit;
        this.datas = datas;
        this.name = name;
        this.conversionFactor = conv;

    }


    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public void setConversionFactor(int conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public void addValue(double value, long timestamp) {
        //todo da togliere
        //System.out.println("value-timestamp aggiunto: " + value + "-" + timestamp);
        this.datas.add(new Data(value, timestamp));
    }

    /*TODO
    public ArrayList<Long> getConvertedValues(){
        ArrayList<Long> convertedValues = new ArrayList<Long>();
        this.datas.forEach(d -> convertedValues.add(d.getValue() * this.conversionFactor));
        return  convertedValues;
    }
    */


    public void setConversionFactorByUrlCode() {

        //TODO aggiungere casi mancanti al metodo (vedi sigle mancanti in sensorlist)
        switch (this.urlCode) {
            case "/actpw": {
                unitOfMeasure = "kW";
                conversionFactor = 100000; //5-6 cifre
                break;
            }
            case "/pwf": {
                unitOfMeasure = "-";
                conversionFactor = 100; //3 cifre
                break;
            }
            case "/cur/1": {
                unitOfMeasure = "A";
                conversionFactor = 100; //3 cifre
                break;
            }
            case "/cur/3": {
                unitOfMeasure = "A";
                conversionFactor = 100; //3 cifre
                break;
            }
            case "/cur/2": {
                unitOfMeasure = "A";
                conversionFactor = 100; // 3 cifre
                break;
            }
            case "/appw": {
                unitOfMeasure = "kVA"; //6-7 cifre
                conversionFactor = 10000;
                break;
            }
            case "/reactpw": {
                unitOfMeasure = "kVAR"; //6-7 cifre
                conversionFactor = 10000;
                break;
            }
            default:
                break;
        }
    }

    public int getConversionFactor() {
        return conversionFactor;
    }

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


    //static method for mapping Sensor UrlCode to Unit of Measure
    public static String getUnitOfMeasureByUrlCode(String urlcode) {
        switch (urlcode) {
            case "/actpw": {
                return "kW";
            }
            case "/pwf": {
                return " ";
            }
            case "/cur/1": {
                return "A";
            }
            case "/cur/3": {
                return "A";
            }
            case "/cur/2": {
                return "A";
            }
            case "/appw": {
                return "kVA";
            }
            case "/reactpw": {
                return "kVAR";
            }
            default:
                return "";
        }
    }


    // IMPLEMENT PARCEABLE METHOD
    protected Sensor(Parcel in) {
        urlCode = in.readString();
        name = in.readString();
        if (in.readByte() == 0x01) {
            datas = new ArrayList<Data>();
            in.readList(datas, Data.class.getClassLoader());
        } else {
            datas = null;
        }
        unitOfMeasure = in.readString();
        conversionFactor = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(urlCode);
        dest.writeString(name);
        if (datas == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(datas);
        }
        dest.writeString(unitOfMeasure);
        dest.writeInt(conversionFactor);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Sensor> CREATOR = new Parcelable.Creator<Sensor>() {
        @Override
        public Sensor createFromParcel(Parcel in) {
            return new Sensor(in);
        }

        @Override
        public Sensor[] newArray(int size) {
            return new Sensor[size];
        }
    };
}




/* VERSIONE CLASSE NORMALE-----------------------------------------------------
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

        setConversionFactorByUrlCode();
    }

    //sensore con valore già acquisito
    public Sensor(String urlCode, String unit, ArrayList<Data> datas, String name, int conv) {
        this.urlCode = urlCode;
        this.unitOfMeasure = unit;
        this.datas = datas;
        this.name = name;
        this.conversionFactor = conv;

    }


    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public void setConversionFactor(int conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    public void addValue(double value, long timestamp) {
        //todo da togliere
        //System.out.println("value-timestamp aggiunto: " + value + "-" + timestamp);
        this.datas.add(new Data(value, timestamp));
    }



    public void setConversionFactorByUrlCode() {

        //TODO aggiungere casi mancanti al metodo (vedi sigle mancanti in sensorlist)
        switch (this.urlCode) {
            case "/actpw": {
                unitOfMeasure = "kW";
                conversionFactor = 100000; //5-6 cifre
                break;
            }
            case "/pwf": {
                unitOfMeasure = "-";
                conversionFactor = 100; //3 cifre
                break;
            }
            case "/cur/1": {
                unitOfMeasure = "A";
                conversionFactor = 100; //3 cifre
                break;
            }
            case "/cur/3": {
                unitOfMeasure = "A";
                conversionFactor = 100; //3 cifre
                break;
            }
            case "/cur/2": {
                unitOfMeasure = "A";
                conversionFactor = 100; // 3 cifre
                break;
            }
            case "/appw": {
                unitOfMeasure = "kVA"; //6-7 cifre
                conversionFactor = 10000;
                break;
            }
            case "/reactpw": {
                unitOfMeasure = "kVAR"; //6-7 cifre
                conversionFactor = 10000;
                break;
            }
            default:
                break;
        }
    }

    public int getConversionFactor() {
        return conversionFactor;
    }

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


    //static method for mapping Sensor UrlCode to Unit of Measure
    public static String getUnitOfMeasureByUrlCode(String urlcode) {
        switch (urlcode) {
            case "/actpw": {
                return "kW";
            }
            case "/pwf": {
                return " ";
            }
            case "/cur/1": {
                return "A";
            }
            case "/cur/3": {
                return "A";
            }
            case "/cur/2": {
                return "A";
            }
            case "/appw": {
                return "kVA";
            }
            case "/reactpw": {
                return "kVAR";
            }
            default:
                return "";
        }
    }

}

--------------------------------------------------*/


