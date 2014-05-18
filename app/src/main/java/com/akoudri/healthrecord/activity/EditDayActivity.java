package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.fragment.AilmentFragment;
import com.akoudri.healthrecord.fragment.AppointmentFragment;
import com.akoudri.healthrecord.fragment.MeasureFragment;
import com.akoudri.healthrecord.fragment.MedicationFragment;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;

public class EditDayActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private TextView today_label;
    private AppointmentFragment apptFrag;
    private MeasureFragment measureFrag;
    private AilmentFragment ailmentFrag;
    private MedicationFragment medicFrag;
    private Fragment currentFrag;
    private FragmentTransaction fragTrans;
    private int personId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_day);
        dataSource = new HealthRecordDataSource(this);
        //FIXME: retrieve person id from calendar
        //FIXME: make cranial perimeter visible if age > ?
        today_label = (TextView) findViewById(R.id.today_label);
        displayCurrentDay();
        personId = getIntent().getIntExtra("personId", 0);
        apptFrag = AppointmentFragment.newInstance();
        ailmentFrag = AilmentFragment.newInstance();
        medicFrag = MedicationFragment.newInstance();
        measureFrag = MeasureFragment.newInstance();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.day_layout, apptFrag);
        fragTrans.commit();
        currentFrag = apptFrag;
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
        if (currentFrag == measureFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, measureFrag);
        fragTrans.commit();
        currentFrag = measureFrag;
    }

    public void displayRV(View view)
    {
        if (currentFrag == apptFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, apptFrag);
        fragTrans.commit();
        currentFrag = apptFrag;
    }

    public void displayIllness(View view)
    {
        if (currentFrag == ailmentFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, ailmentFrag);
        fragTrans.commit();
        currentFrag = ailmentFrag;
    }

    public void displayMedics(View view)
    {
        if (currentFrag == medicFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, medicFrag);
        fragTrans.commit();
        currentFrag = medicFrag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //FIXME: Manage the case where data source could not be opened
        try {
            dataSource.open();
            apptFrag.setDataSource(dataSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
