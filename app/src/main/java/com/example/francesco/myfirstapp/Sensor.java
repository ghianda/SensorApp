package com.example.francesco.myfirstapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by francesco on 28/12/2016.
 */


public class Sensor implements Parcelable {

    private String urlCode;
    private String name;
    private ArrayList<Data> datas = new ArrayList<>();
    private String unitOfMeasure;
    private float conversionFactor; //(10^x): is for converted from centiUnit (10^-2)



    //sensore senza valori
    public Sensor(String urlCode, String name) {
        this.urlCode = urlCode;
        this.name = name;

        setConversionFactorByUrlCode();
    }

    //sensore con valore già acquisito
    public Sensor(String urlCode, String unit, ArrayList<Data> datas, String name, float conv) {
        this.urlCode = urlCode;
        this.unitOfMeasure = unit;
        this.datas = datas;
        this.name = name;
        this.conversionFactor = conv;

    }



    public void addValue(double value, long timestamp) {
        this.datas.add(new Data(value, timestamp));
    }



    public void setConversionFactorByUrlCode() {
        //the correct value is misuredValue / conversionFactor

        switch (this.urlCode) {
            case "/temp": {
                this.unitOfMeasure = "°C";
                this.conversionFactor = 1;
                break;
            }
            case "/light": {
                this.unitOfMeasure = "Lux";
                this.conversionFactor = 1;
                break;
            }
            case "/humid": {
                this.unitOfMeasure = "%";
                this.conversionFactor = 1;
                break;
            }
            case "/reactcon": {
                this.unitOfMeasure = "VARh";
                this.conversionFactor = (float)0.1; //centiKiloVARh (1 / 10^3 * 10^-2)
                break;
            }
            case "/apcon": {
                this.unitOfMeasure = "VA";
                this.conversionFactor = (float)0.1; //centiKiloVARh (1 / 10^3 * 10^-2)
                break;
            }
            case "/con": {
                this.unitOfMeasure = "Wh";
                this.conversionFactor = (float)0.1; //centiKiloVARh (1 / 10^3 * 10^-2)
                break;
            }
            case "/actpw": {
                this.unitOfMeasure = "W";
                this.conversionFactor = 100; //centi (10^-2)
                break;
            }
            case "/pwf": {
                this.unitOfMeasure = " ";
                this.conversionFactor = 1000; //( 0 < cos(x) < 1)
                break;
            }
            case "/cur/1": {
                this.unitOfMeasure = "A";
                this.conversionFactor = 100; //centi (10^-2)
                break;
            }
            case "/cur/3": {
                this.unitOfMeasure = "A";
                this.conversionFactor = 100; //centi (10^-2)
                break;
            }
            case "/cur/2": {
                this.unitOfMeasure = "A";
                this.conversionFactor = 100; //centi (10^-2)
                break;
            }
            case "/appw": {
                this.unitOfMeasure = "VA";
                this.conversionFactor = 100; //centi (10^-2)
                break;
            }
            case "/reactpw": {
                this.unitOfMeasure = "VAR";
                this.conversionFactor = 100; //centi (10^-2)
                break;
            }
            default:
                break;
        }
    }

    public float getConversionFactor() {
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


    //return the data with max value
    public Data findDataWithMaxValue() {


        double maxValue = -10 ^ 12;
        Data winnerData = null;

        for (Data data : this.getDatas()) {

            if (data.getValue() != 0 && abs(data.getValue())<1000000000)
                if (data.getValue() > maxValue) {
                    maxValue = data.getValue();
                    winnerData = data;
                }
        }

        return winnerData;
    }


    //return the data with min value
    public Data findDataWithMinValue() {


        double minValue = this.getDatas().get(0).getValue();
        Data winnerData = this.getDatas().get(0);

        for (Data data : this.getDatas()) {

            if (data.getValue() != 0 && abs(data.getValue())<1000000000)
                if (data.getValue() < minValue) {
                 minValue = data.getValue();
                    winnerData = data;
                }
        }

        return winnerData;
    }



    // IMPLEMENT PARCEABLE METHOD ***************************
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
        conversionFactor = in.readFloat();
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
        dest.writeFloat(conversionFactor);
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
