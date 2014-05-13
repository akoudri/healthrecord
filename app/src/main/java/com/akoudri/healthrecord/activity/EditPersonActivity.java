package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.fragment.MyAnalysisFragment;
import com.akoudri.healthrecord.fragment.MyCalendarFragment;
import com.akoudri.healthrecord.fragment.MyTherapistsFragment;
import com.akoudri.healthrecord.fragment.UpdatePersonFragment;

public class EditPersonActivity extends Activity {

    private Fragment calendarFrag, therapistsFrag, personalFrag, analysisFrag;
    private Fragment currentFrag;
    private FragmentTransaction fragTrans;
    private int personId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_person);
        personId = getIntent().getIntExtra("personId", 0);
        calendarFrag = MyCalendarFragment.newInstance();
        therapistsFrag = MyTherapistsFragment.newInstance();
        personalFrag = UpdatePersonFragment.newInstance();
        analysisFrag = MyAnalysisFragment.newInstance();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.day_layout, calendarFrag);
        fragTrans.commit();
        currentFrag = calendarFrag;
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

    public void showBirthdayPickerDialog(View view)
    {
        ((UpdatePersonFragment)personalFrag).showBirthdayPickerDialog(view);
    }

    public void addTherapist(View view)
    {
        ((MyTherapistsFragment)therapistsFrag).addTherapist(view);
    }
}
