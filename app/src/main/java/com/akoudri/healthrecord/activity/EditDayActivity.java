package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.fragment.MyCalendarFragment;
import com.akoudri.healthrecord.fragment.MyIllnessFragment;
import com.akoudri.healthrecord.fragment.MyMeasuresFragment;
import com.akoudri.healthrecord.fragment.MyMedicsFragment;
import com.akoudri.healthrecord.fragment.MyRvFragment;
import com.akoudri.healthrecord.fragment.MyTherapistsFragment;
import com.akoudri.healthrecord.fragment.UpdatePersonFragment;

import java.util.Calendar;
import java.util.Locale;

public class EditDayActivity extends Activity {

    private TextView today_label;
    private Fragment measuresFrag, rvFrag, illnessFrag, medicsFrag;
    private Fragment currentFrag;
    private FragmentTransaction fragTrans;
    private int personId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_day);
        //FIXME: retrieve person id from calendar
        //FIXME: make cranial perimeter visible if age > ?
        today_label = (TextView) findViewById(R.id.today_label);
        displayCurrentDay();
        personId = getIntent().getIntExtra("personId", 0);
        rvFrag = MyRvFragment.newInstance();
        illnessFrag = MyIllnessFragment.newInstance();
        medicsFrag = MyMedicsFragment.newInstance();
        measuresFrag = MyMeasuresFragment.newInstance();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.day_layout, rvFrag);
        fragTrans.commit();
        currentFrag = rvFrag;
    }

    private void displayCurrentDay()
    {
        int date = getIntent().getIntExtra("date", 0);
        int month = getIntent().getIntExtra("month", 0);
        int year = getIntent().getIntExtra("year", 0);
        Calendar currentDay = Calendar.getInstance();
        currentDay.set(Calendar.DAY_OF_MONTH, date);
        currentDay.set(Calendar.MONTH, month);
        currentDay.set(Calendar.YEAR, year);
        StringBuilder sb = new StringBuilder();
        sb.append(currentDay.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).toUpperCase());
        sb.append(" ");
        sb.append(date);
        sb.append(" ");
        sb.append(currentDay.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).toUpperCase());
        sb.append(" ");
        sb.append(year);
        today_label.setText(sb.toString());
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
