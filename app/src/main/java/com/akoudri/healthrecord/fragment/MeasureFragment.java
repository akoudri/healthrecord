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
    private Calendar currentDay;
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
        this.currentDay = currentDay;
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
        weightET.setText(measure.getWeight() + "");
        sizeET.setText(measure.getSize() + "");
        cpET.setText(measure.getCranialPerimeter() + "");
        tempET.setText(measure.getTemperature() + "");
        glucoseET.setText(measure.getGlucose() + "");
        diaET.setText(measure.getDiastolic() + "");
        sysET.setText(measure.getSystolic() + "");
        hbET.setText(measure.getHeartbeat() + "");
    }

    public void saveMeasures(View view)
    {
        /*
        double weight;
        int size;
        int cranialPerimeter;
        double temperature;
        double glucose;
        int diastolic;
        int systolic;
        int heartbeat;
        String str;
        str = weightET.getText().toString();
        if (str.equalsIgnoreCase("")) weight = 0.0;
        else weight = Double.parseDouble(str);
        */
        //TODO
        if (measure == null)
        {

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
