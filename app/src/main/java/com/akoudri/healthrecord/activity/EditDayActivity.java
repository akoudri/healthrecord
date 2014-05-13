package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.fragment.MyCalendarFragment;
import com.akoudri.healthrecord.fragment.MyIllnessFragment;
import com.akoudri.healthrecord.fragment.MyMeasuresFragment;
import com.akoudri.healthrecord.fragment.MyMedicsFragment;
import com.akoudri.healthrecord.fragment.MyRvFragment;
import com.akoudri.healthrecord.fragment.MyTherapistsFragment;
import com.akoudri.healthrecord.fragment.UpdatePersonFragment;

public class EditDayActivity extends Activity {

    private Fragment measuresFrag, rvFrag, illnessFrag, medicsFrag;
    private Fragment currentFrag;
    private FragmentTransaction fragTrans;
    private int personId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_day);
        personId = getIntent().getIntExtra("personId", 0);
        measuresFrag = MyMeasuresFragment.newInstance();
        rvFrag = MyRvFragment.newInstance();
        illnessFrag = MyIllnessFragment.newInstance();
        medicsFrag = MyMedicsFragment.newInstance();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.day_layout, measuresFrag);
        fragTrans.commit();
        currentFrag = measuresFrag;
    }

    public void displayMeasures(View view)
    {
        if (currentFrag == measuresFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, measuresFrag);
        fragTrans.commit();
        currentFrag = measuresFrag;
    }

    public void displayRV(View view)
    {
        if (currentFrag == rvFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, rvFrag);
        fragTrans.commit();
        currentFrag = rvFrag;
    }

    public void displayIllness(View view)
    {
        if (currentFrag == illnessFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, illnessFrag);
        fragTrans.commit();
        currentFrag = illnessFrag;
    }

    public void displayMedics(View view)
    {
        if (currentFrag == medicsFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, medicsFrag);
        fragTrans.commit();
        currentFrag = medicsFrag;
    }
}
