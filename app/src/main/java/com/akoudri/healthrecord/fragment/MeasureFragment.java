package com.akoudri.healthrecord.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Measure;

import java.util.Calendar;


public class MeasureFragment extends Fragment {

    private View view;
    private HealthRecordDataSource dataSource;
    private EditText weightET, sizeET, cpET, tempET;
    private EditText glucoseET, diaET, sysET, hbET;
    private int personId;
    private int day, month, year;
    private Measure measure;

    public static MeasureFragment newInstance()
    {
        return new MeasureFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_measure, container, false);
        weightET = (EditText) view.findViewById(R.id.edit_weight);
        sizeET = (EditText) view.findViewById(R.id.edit_size);
        cpET = (EditText) view.findViewById(R.id.edit_cranial_perimeter);
        tempET = (EditText) view.findViewById(R.id.edit_temperature);
        glucoseET = (EditText) view.findViewById(R.id.edit_glucose);
        diaET = (EditText) view.findViewById(R.id.edit_diastolic);
        sysET = (EditText) view.findViewById(R.id.edit_systolic);
        hbET = (EditText) view.findViewById(R.id.edit_heartbeat);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        return view;
    }

    public void setCurrentDay(Calendar currentDay)
    {
        day = currentDay.get(Calendar.DAY_OF_MONTH);
        month = currentDay.get(Calendar.MONTH) + 1;
        year = currentDay.get(Calendar.YEAR);
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    private void populateWidgets()
    {
        if (measure.getWeight() > 0.0)
            weightET.setText(measure.getWeight() + "");
        if (measure.getSize() > 0)
            sizeET.setText(measure.getSize() + "");
        if (measure.getCranialPerimeter() > 0)
            cpET.setText(measure.getCranialPerimeter() + "");
        if (measure.getTemperature() > 0.0)
            tempET.setText(measure.getTemperature() + "");
        if (measure.getGlucose() > 0.0)
            glucoseET.setText(measure.getGlucose() + "");
        if (measure.getDiastolic() > 0)
            diaET.setText(measure.getDiastolic() + "");
        if (measure.getSystolic() > 0)
            sysET.setText(measure.getSystolic() + "");
        if (measure.getHeartbeat() > 0)
            hbET.setText(measure.getHeartbeat() + "");
    }

    public void saveMeasures(View view)
    {
        //TODO: make a diff between captured and saved measure to activate/deactivate save button
        String str;
        str = weightET.getText().toString();
        double weight = (str.equalsIgnoreCase(""))?0.0:Double.parseDouble(str);
        str = sizeET.getText().toString();
        int size = (str.equalsIgnoreCase(""))?0:Integer.parseInt(str);
        str = cpET.getText().toString();
        int cranialPerimeter = (str.equalsIgnoreCase(""))?0:Integer.parseInt(str);
        str = tempET.getText().toString();
        double temperature = (str.equalsIgnoreCase(""))?0.0:Double.parseDouble(str);
        str = glucoseET.getText().toString();
        double glucose = (str.equalsIgnoreCase(""))?0.0:Double.parseDouble(str);
        str = diaET.getText().toString();
        int diastolic = (str.equalsIgnoreCase(""))?0:Integer.parseInt(str);
        str = sysET.getText().toString();
        int systolic = (str.equalsIgnoreCase(""))?0:Integer.parseInt(str);
        str = hbET.getText().toString();
        int heartbeat = (str.equalsIgnoreCase(""))?0:Integer.parseInt(str);
        String date = String.format("%02d/%02d/%04d", day, month, year);
        if (measure == null)
        {
            dataSource.getMeasureTable().insertMeasure(personId, date, weight, size, cranialPerimeter, temperature, glucose, diastolic, systolic, heartbeat);
        }
        else
        {
            dataSource.getMeasureTable().updateMeasureWithDate(personId, date, weight, size, cranialPerimeter, temperature, glucose, diastolic, systolic, heartbeat);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String date = String.format("%02d/%02d/%04d", day, month, year);
        measure = dataSource.getMeasureTable().getPersonMeasureWithDate(personId, date);
        if (measure != null)
            populateWidgets();
    }

}
