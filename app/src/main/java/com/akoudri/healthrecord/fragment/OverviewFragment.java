package com.akoudri.healthrecord.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.utils.HealthRecordUtils;


public class OverviewFragment extends Fragment {

    private View view;
    private TextView nbMeasures, nbAppts, nbAilments, nbMedics;

    private HealthRecordDataSource dataSource;
    private int personId;
    private int day, month, year;

    public static OverviewFragment newInstance()
    {
        return new OverviewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_overview, container, false);
        nbMeasures = (TextView) view.findViewById(R.id.number_of_measures);
        nbAppts = (TextView) view.findViewById(R.id.number_of_appts);
        nbAilments = (TextView) view.findViewById(R.id.number_of_ailments);
        nbMedics = (TextView) view.findViewById(R.id.number_of_medics);
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
        fillWidgets(date);
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    private void fillWidgets(String date)
    {
        long d = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        int count = dataSource.getMeasureTable().countMeasuresForDay(personId, d);
        nbMeasures.setText(count + "");
        count = dataSource.getAppointmentTable().countAppointmentsForDay(personId, d);
        nbAppts.setText(count + "");
        count = dataSource.getAilmentTable().countAilmentsForDay(personId, d);
        nbAilments.setText(count + "");
        count = dataSource.getMedicationTable().countMedicsForDay(personId, d);
        nbMedics.setText(count + "");
    }

}
