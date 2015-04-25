package com.akoudri.healthrecord.fragment;

import android.app.Fragment;

import com.akoudri.healthrecord.app.HealthRecordDataSource;

/**
 * Created by Ali Koudri on 29/03/15.
 */
public abstract class EditDayFragment extends Fragment {

    protected int personId;
    protected HealthRecordDataSource dataSource;
    protected int day, month, year;
    protected String date;

    public void setCurrentDate(int day, int month, int year)
    {
        this.day = day;
        this.month = month;
        this.year = year;
        date = String.format("%02d/%02d/%4d", day, month + 1, year);
    }

    public abstract void refresh();

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public abstract void resetObjectId();

}
