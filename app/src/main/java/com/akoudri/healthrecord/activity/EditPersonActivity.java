package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.fragment.AnalysisFragment;
import com.akoudri.healthrecord.fragment.CalendarFragment;
import com.akoudri.healthrecord.fragment.TherapistFragment;
import com.akoudri.healthrecord.fragment.UpdatePersonFragment;

import java.sql.SQLException;

public class EditPersonActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;

    private CalendarFragment calendarFrag;
    private TherapistFragment therapistsFrag;
    private UpdatePersonFragment personalFrag;
    private AnalysisFragment analysisFrag;
    private Fragment currentFrag;
    private FragmentTransaction fragTrans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_person);
        dataSource = new HealthRecordDataSource(this);
        personId = getIntent().getIntExtra("personId", 0);
        calendarFrag = CalendarFragment.newInstance();
        therapistsFrag = TherapistFragment.newInstance();
        personalFrag = UpdatePersonFragment.newInstance();
        analysisFrag = AnalysisFragment.newInstance();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.day_layout, calendarFrag);
        fragTrans.commit();
        currentFrag = calendarFrag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            therapistsFrag.setDataSource(dataSource);
            personalFrag.setDataSource(dataSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (personId == 0) return;
        if (dataSourceLoaded)
        {
            dataSource.close();
            dataSourceLoaded = false;
        }
    }

    public void displayCalendar(View view)
    {
        if (currentFrag == calendarFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, calendarFrag);
        fragTrans.commit();
        currentFrag = calendarFrag;
    }

    public void displayTherapists(View view)
    {
        if (currentFrag == therapistsFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, therapistsFrag);
        fragTrans.commit();
        currentFrag = therapistsFrag;
    }

    public void displayData(View view)
    {
        if (currentFrag == personalFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, personalFrag);
        fragTrans.commit();
        currentFrag = personalFrag;
    }

    public void displayAnalysis(View view)
    {
        if (currentFrag == analysisFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, analysisFrag);
        fragTrans.commit();
        currentFrag = analysisFrag;
    }

    public void pickUpdateBirthdate(View view)
    {
        personalFrag.pickUpdateBirthdate();
    }

    public void addTherapist(View view)
    {
        therapistsFrag.addTherapist(view);
    }


    public void setMeasureStartDate(View view)
    {
        analysisFrag.setMeasureStartDate();
    }

    public void setMeasureEndDate(View view)
    {
        analysisFrag.setMeasureEndDate();
    }
}
