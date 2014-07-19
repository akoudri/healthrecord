package com.akoudri.healthrecord.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.view.CalendarView;

public class CalendarFragment extends Fragment {

    private int personId = 0;
    private CalendarView calendarView;

    public static CalendarFragment newInstance()
    {
        return new CalendarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_calendar, container, false);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        calendarView = (CalendarView) view.findViewById(R.id.calendar_view);
        calendarView.setPersonId(personId);
        return view;
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        calendarView.setDataSource(dataSource);
    }
}
