package com.akoudri.healthrecord.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.akoudri.healthrecord.view.CalendarView;

//FIXME: for all classes, provide a better management of exceptions

public class MyCalendarFragment extends Fragment {

    private int personId = 0;
    private CalendarView calendarView;

    public static MyCalendarFragment newInstance()
    {
        return new MyCalendarFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        calendarView = new CalendarView(getActivity());
        return calendarView;
    }

}
