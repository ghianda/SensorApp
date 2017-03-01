package com.example.francesco.myfirstapp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

/**
 * Created by francesco on 28/02/2017.
 */

public class TimePickerFragment extends DialogFragment {
    TimePickerDialog.OnTimeSetListener ontimeSet;
    private int hour, minute;


    public TimePickerFragment() {
    }

    public void setCallBack(TimePickerDialog.OnTimeSetListener ontime) {
        ontimeSet = ontime;
    }


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        hour = args.getInt("hour");
        minute = args.getInt("minute");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), ontimeSet, hour, minute, true);
    }
}