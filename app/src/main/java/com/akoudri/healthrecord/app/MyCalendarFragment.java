package com.akoudri.healthrecord.app;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_my_calendar, container, false);
        /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        calendarView = new CalendarView(getActivity());
        calendarView.setLayoutParams(params);
        layout.addView(calendarView);*/
        return layout;
    }

}
