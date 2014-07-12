package com.akoudri.healthrecord.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.utils.DatePickerFragment;


public class AnalysisFragment extends Fragment {

    private View view;
    Spinner measureSpinner;
    EditText startET, endET;

    public static AnalysisFragment newInstance()
    {
        return new AnalysisFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_analysis, container, false);
        measureSpinner = (Spinner) view.findViewById(R.id.measure_choice);
        String[] measureChoices = getResources().getStringArray(R.array.measures);
        ArrayAdapter<String> measureChoicesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, measureChoices);
        measureSpinner.setAdapter(measureChoicesAdapter);
        startET = (EditText) view.findViewById(R.id.start_measure);
        startET.setKeyListener(null);
        endET = (EditText) view.findViewById(R.id.end_measure);
        endET.setKeyListener(null);
        //TODO: add number of points ?
        return view;
    }

    public void setMeasureStartDate()
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(getActivity(), startET);
        dfrag.show(getFragmentManager(),"Pick Medication Start Date");
    }

    public void setMeasureEndDate()
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(getActivity(), endET);
        dfrag.show(getFragmentManager(),"Pick Medication Start Date");
    }

}
