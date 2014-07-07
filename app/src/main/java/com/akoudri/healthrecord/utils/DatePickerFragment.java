package com.akoudri.healthrecord.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Ali Koudri on 02/07/14.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private Activity activity;
    private EditText et;
    private Calendar initial, min, max;

    public void init(Activity activity, EditText et)
    {
        init(activity, et, null, null, null);
    }

    public void init(Activity activity, EditText et, Calendar initial, Calendar min, Calendar max)
    {
        this.activity = activity;
        this.et = et;
        this.initial = initial;
        this.min = min;
        this.max = max;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (initial == null) initial = Calendar.getInstance();
        int year = initial.get(Calendar.YEAR);
        int month = initial.get(Calendar.MONTH);
        int day = initial.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(activity, this, year, month, day);
        if (min != null)
            dpd.getDatePicker().setMinDate(min.getTime().getTime());
        if (max != null)
            dpd.getDatePicker().setMaxDate(max.getTime().getTime());
        return dpd;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String toDisplay = String.format("%02d/%02d/%4d", day, month+1, year);
        et.setText(toDisplay);
    }

}
