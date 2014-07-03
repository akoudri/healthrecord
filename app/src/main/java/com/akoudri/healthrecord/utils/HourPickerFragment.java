package com.akoudri.healthrecord.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Ali Koudri on 03/07/14.
 */
public class HourPickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private Activity activity;
    private EditText et;

    public void init(Activity activity, EditText et)
    {
        this.activity = activity;
        this.et = et;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);
        return new TimePickerDialog(activity, this, hour, min, true);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int min) {
        String toDisplay = String.format("%02d:%02d", hour, min);
        et.setText(toDisplay);
    }

}
