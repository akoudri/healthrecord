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

/**
 * Recreated by Ali Koudri on 14/05/15.
 */
public class OverviewFragment extends EditDayFragment {

    private View view;
    private TextView nbMeasures, nbAppts, nbAilments, nbMedics, nbObs;

    private HealthRecordDataSource dataSource;
    private int personId;

    private String date;

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
        nbObs = (TextView) view.findViewById(R.id.number_of_observations);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (personId == 0) return;
        if (dataSource == null) return;
        fillWidgets();
    }

    public void setCurrentDate(int day, int month, int year)
    {
        date = String.format("%02d/%02d/%4d", day, month + 1, year);
    }

    public void refresh()
    {
        fillWidgets();
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Override
    public void resetObjectId() {
        //Nothing to do here
    }

    private void fillWidgets()
    {
        long d = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        int count = dataSource.getMeasureView().countPersonMeasureWithDate(personId, date);
        nbMeasures.setText(count + "");
        /*count = dataSource.getAppointmentTable().countAppointmentsForDay(personId, d);
        nbAppts.setText(count + "");
        count = dataSource.getAilmentTable().countAilmentsForDay(personId, d);
        nbAilments.setText(count + "");*/
        count = dataSource.getMedicationTable().countMedicsForDay(personId, d);
        nbMedics.setText(count + "");
        /*count = dataSource.getMedicalObservationTable().countObservationsForDay(personId, d);
        nbObs.setText(count + "");*/
    }

}
