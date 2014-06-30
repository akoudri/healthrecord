package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.fragment.AilmentFragment;
import com.akoudri.healthrecord.fragment.AppointmentFragment;
import com.akoudri.healthrecord.fragment.MeasureFragment;
import com.akoudri.healthrecord.fragment.TreatmentFragment;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;

public class EditDayActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private TextView today_label;
    private AppointmentFragment apptFrag;
    private MeasureFragment measureFrag;
    private AilmentFragment ailmentFrag;
    private TreatmentFragment treatmentFrag;
    private Fragment currentFrag;
    private FragmentTransaction fragTrans;
    private int personId = 0;
    private int date, month, year;
    private Calendar currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_day);
        dataSource = new HealthRecordDataSource(this);
        //FIXME: retrieve person id from calendar
        //FIXME: make cranial perimeter visible if age > ?
        today_label = (TextView) findViewById(R.id.today_label);
        personId = getIntent().getIntExtra("personId", 0);
        date = getIntent().getIntExtra("date", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        displayCurrentDay();
        apptFrag = AppointmentFragment.newInstance();
        ailmentFrag = AilmentFragment.newInstance();
        treatmentFrag = TreatmentFragment.newInstance();
        measureFrag = MeasureFragment.newInstance();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.day_layout, measureFrag);
        fragTrans.commit();
        currentFrag = measureFrag;
    }

    private void displayCurrentDay()
    {
        currentDay = Calendar.getInstance();
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
        if (currentFrag == treatmentFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, treatmentFrag);
        fragTrans.commit();
        currentFrag = treatmentFrag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //FIXME: Manage the case where data source could not be opened
        try {
            dataSource.open();
            measureFrag.setDataSource(dataSource);
            measureFrag.setCurrentDay(currentDay);
            apptFrag.setDataSource(dataSource);
            apptFrag.setCurrentDay(currentDay);
            ailmentFrag.setDataSource(dataSource);
            ailmentFrag.setCurrentDay(currentDay);
            treatmentFrag.setDataSource(dataSource);
            treatmentFrag.setCurrentDay(currentDay);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createAppt(View view)
    {
        Intent intent = new Intent("com.akoudri.healthrecord.app.AddAppointment");
        intent.putExtra("personId", personId);
        intent.putExtra("date", date);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void createAilment(View view)
    {
        Intent intent = new Intent("com.akoudri.healthrecord.app.CreateAilment");
        intent.putExtra("personId", personId);
        intent.putExtra("date", date);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void createTreatment(View view)
    {
        //FIXME: create treatment only if there is at least one ailment for which there is not treatment
        Intent intent = new Intent("com.akoudri.healthrecord.app.CreateTreatment");
        intent.putExtra("personId", personId);
        intent.putExtra("date", date);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void saveMeasures(View view)
    {
        measureFrag.saveMeasures(view);
    }
}
