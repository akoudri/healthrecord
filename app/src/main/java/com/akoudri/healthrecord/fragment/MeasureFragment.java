package com.akoudri.healthrecord.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Measure;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.List;


public class MeasureFragment extends Fragment {

    private View view;

    private EditText weightET, sizeET, cpET, tempET;
    private EditText glucoseET, diaET, sysET, hbET;

    private HealthRecordDataSource dataSource;
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
        day = getActivity().getIntent().getIntExtra("day", 0);
        month = getActivity().getIntent().getIntExtra("month", 0);
        year = getActivity().getIntent().getIntExtra("year", 0);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (personId == 0 || day <= 0 || month <= 0 || year <= 0) return;
        if (dataSource == null) return;
        String date = String.format("%02d/%02d/%04d", day, month + 1, year);
        measure = dataSource.getMeasureTable().getPersonMeasureWithDate(personId, date);
        if (measure != null)
            fillWidgets();
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    private void fillWidgets()
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

    public void saveMeasures()
    {
        String weightStr = weightET.getText().toString();
        String sizeStr = sizeET.getText().toString();
        String cpStr = cpET.getText().toString();
        String tempStr = tempET.getText().toString();
        String glucoseStr = glucoseET.getText().toString();
        String diaStr = diaET.getText().toString();
        String sysStr = sysET.getText().toString();
        String hbStr = hbET.getText().toString();
        if (!checkFields(weightStr, sizeStr, cpStr, tempStr, glucoseStr, diaStr, sysStr, hbStr)) return;
        double weight = (weightStr.equalsIgnoreCase(""))?0.0:Double.parseDouble(weightStr);
        int size = (sizeStr.equalsIgnoreCase(""))?0:Integer.parseInt(sizeStr);
        int cranialPerimeter = (cpStr.equalsIgnoreCase(""))?0:Integer.parseInt(cpStr);
        double temperature = (tempStr.equalsIgnoreCase(""))?0.0:Double.parseDouble(tempStr);
        double glucose = (glucoseStr.equalsIgnoreCase(""))?0.0:Double.parseDouble(glucoseStr);
        int diastolic = (diaStr.equalsIgnoreCase(""))?0:Integer.parseInt(diaStr);
        int systolic = (sysStr.equalsIgnoreCase(""))?0:Integer.parseInt(sysStr);
        int heartbeat = (hbStr.equalsIgnoreCase(""))?0:Integer.parseInt(hbStr);
        String date = String.format("%02d/%02d/%04d", day, month + 1, year);
        if (measure == null)
        {
            Measure m = new Measure(personId, date, weight, size, cranialPerimeter, temperature, glucose, diastolic, systolic, heartbeat);
            if (m.isNull())
            {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.no_change), Toast.LENGTH_SHORT).show();
            }
            else {
                dataSource.getMeasureTable().insertMeasure(personId, date, weight, size, cranialPerimeter, temperature, glucose, diastolic, systolic, heartbeat);
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Measure m = new Measure(personId, date, weight, size, cranialPerimeter, temperature, glucose, diastolic, systolic, heartbeat);
            if (!measure.equalsTo(m))
            {
                dataSource.getMeasureTable().updateMeasureWithDate(personId, date, weight, size, cranialPerimeter, temperature, glucose, diastolic, systolic, heartbeat);
                measure.setWeight(weight);
                measure.setSize(size);
                measure.setCranialPerimeter(cranialPerimeter);
                measure.setTemperature(temperature);
                measure.setGlucose(glucose);
                measure.setDiastolic(diastolic);
                measure.setSystolic(systolic);
                measure.setHeartbeat(heartbeat);
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.no_change), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkFields(String weight, String size, String cp, String temp, String glucose, String dia, String sys, String hb)
    {
        boolean res = true;
        List<EditText> toHighlight = new ArrayList<EditText>();
        List<EditText> notToHighlight = new ArrayList<EditText>();
        //Check weight
        boolean checkWeight = (weight.equals("") || weight.matches("1?\\d{1,2}(\\.\\d{1,2})?"));
        res = res && checkWeight;
        if (!checkWeight) toHighlight.add(weightET);
        else notToHighlight.add(weightET);
        //check size
        boolean checkSize = (size.equals("") || size.matches("(1|2)?\\d{2}"));
        res = res && checkSize;
        if (!checkSize) toHighlight.add(sizeET);
        else notToHighlight.add(sizeET);
        //check cp
        boolean checkCP = (cp.equals("") || cp.matches("[3-5]\\d"));
        res = res && checkCP;
        if (!checkCP) toHighlight.add(cpET);
        else notToHighlight.add(cpET);
        //check temp
        boolean checkTemp = (temp.equals("") || temp.matches("(3[5-9]|4[0-1])(\\.\\d{1})?"));
        res = res && checkTemp;
        if (!checkTemp) toHighlight.add(tempET);
        else notToHighlight.add(tempET);
        //check glucose
        boolean checkGlucose = (glucose.equals("") || glucose.matches("\\d{1}(\\.\\d{1,2})?"));
        res = res && checkGlucose;
        if (!checkGlucose) toHighlight.add(glucoseET);
        else notToHighlight.add(glucoseET);
        //check dia
        boolean checkDia = (dia.equals("") || dia.matches("1?\\d"));
        res = res && checkDia;
        if (!checkDia) toHighlight.add(diaET);
        else notToHighlight.add(diaET);
        //check sys
        boolean checkSys = (sys.equals("") || sys.matches("1?\\d"));
        res = res && checkSys;
        if (!checkSys) toHighlight.add(sysET);
        else notToHighlight.add(sysET);
        //check hb
        boolean checkHB = (hb.equals("") || hb.matches("1?\\d{2}"));
        res = res && checkHB;
        if (!checkHB) toHighlight.add(hbET);
        else notToHighlight.add(hbET);
        //display
        if (toHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(getActivity(), toHighlight, true);
        if (notToHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(getActivity(), notToHighlight, false);
        if (!res) {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
            toast.show();
        }
        return res;
    }

}
