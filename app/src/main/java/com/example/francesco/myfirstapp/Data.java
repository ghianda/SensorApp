package com.example.francesco.myfirstapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by francesco on 11/01/2017.
 */
/* DEF CLASSE NORMALE ----------------------------------
public class Data {
    private double value;
    private long timestamp;

    //costructor
    public Data(double value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "value " + "/ " + timestamp;
    }
}
----------------------------------------------*/

/*  VERSION: PARCEABLE CLASS */
public class Data implements Parcelable {
    private double value;
    private long timestamp;

    //costructor
    public Data(double value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "value " + "/ " + timestamp;
    }

    protected Data(Parcel in) {
        value = in.readDouble();
        timestamp = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(value);
        dest.writeLong(timestamp);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}

