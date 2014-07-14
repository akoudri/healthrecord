package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.ChangeStatus;
import com.akoudri.healthrecord.fragment.AnalysisFragment;
import com.akoudri.healthrecord.fragment.CalendarFragment;
import com.akoudri.healthrecord.fragment.TherapistFragment;
import com.akoudri.healthrecord.fragment.UpdatePersonFragment;

import java.sql.SQLException;

public class EditPersonActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;
    private int numFrag;

    private Fragment currentFrag;
    private FragmentTransaction fragTrans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_person);
        dataSource = new HealthRecordDataSource(this);
        personId = getIntent().getIntExtra("personId", 0);
        numFrag = getIntent().getIntExtra("numFrag", 0);
        switch (numFrag)
        {
            case 0:
                currentFrag = UpdatePersonFragment.newInstance(); break;
            case 1:
                currentFrag = CalendarFragment.newInstance(); break;
            case 2:
                currentFrag = TherapistFragment.newInstance(); break;
            default:
                currentFrag = AnalysisFragment.newInstance();
        }
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.day_layout, currentFrag);
        fragTrans.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            if (numFrag == 0)
                ((UpdatePersonFragment)currentFrag).setDataSource(dataSource);
            if (numFrag == 2)
                ((TherapistFragment)currentFrag).setDataSource(dataSource);
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

    public void pickUpdateBirthdate(View view)
    {
        ((UpdatePersonFragment)currentFrag).pickUpdateBirthdate();
    }

    public void addTherapist(View view)
    {
        ((TherapistFragment)currentFrag).addTherapist();
    }

    public void setMeasureStartDate(View view)
    {
        ((AnalysisFragment)currentFrag).setMeasureStartDate();
    }

    public void setMeasureEndDate(View view)
    {
        ((AnalysisFragment)currentFrag).setMeasureEndDate();
    }

    public void updatePerson(View view)
    {
        ChangeStatus status = ((UpdatePersonFragment) currentFrag).updatePerson();
        if (status == ChangeStatus.CHANGED)
            finish();
    }
}
