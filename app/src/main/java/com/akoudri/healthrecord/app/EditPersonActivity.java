package com.akoudri.healthrecord.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;


public class EditPersonActivity extends Activity {

    private Fragment calendarFrag, therapistsFrag, personalFrag;
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
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.edit_layout, calendarFrag);
        fragTrans.commit();
        currentFrag = calendarFrag;
    }

    public void displayCalendar(View view)
    {
        if (currentFrag == therapistsFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.edit_layout, calendarFrag);
        fragTrans.commit();
        currentFrag = calendarFrag;
    }

    public void displayTherapists(View view)
    {
        if (currentFrag == therapistsFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.edit_layout, therapistsFrag);
        fragTrans.commit();
        currentFrag = therapistsFrag;
    }

    public void displayData(View view)
    {
        if (currentFrag == personalFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.edit_layout, personalFrag);
        fragTrans.commit();
        currentFrag = personalFrag;
    }
}
