package com.akoudri.healthrecord.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TimePicker;

/**
 * Created by Ali Koudri on 03/07/14.
 */
public class HourPickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private Activity activity;
    private EditText et;
    private int hour = 12;
    private int min = 0;

    public void init(Activity activity, EditText et)
    {
        init(activity, et, 12, 0);
    }

    public void init(Activity activity, EditText et, int hour, int min)
    {
        this.activity = activity;
        this.et = et;
        if (hour >= 0 && hour < 24)
            this.hour = hour;
        if (min >= 0 && min < 60)
            this.min = min;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(activity, this, hour, min, true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int min) {
        String toDisplay = String.format("%02d:%02d", hour, min);
        et.setText(toDisplay);
    }

}
