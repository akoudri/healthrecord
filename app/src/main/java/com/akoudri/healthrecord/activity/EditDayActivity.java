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
import com.akoudri.healthrecord.fragment.OverviewFragment;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;

//FIXME: replace the ailment fragment by a "summary day" fragment and set it to default
public class EditDayActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId = 0;
    private int day, month, year;
    private Calendar currentDay;

    private TextView today_label;
    private OverviewFragment ovFrag;
    private AppointmentFragment apptFrag;
    private MeasureFragment measureFrag;
    private AilmentFragment ailmentFrag;
    private Fragment currentFrag;
    private FragmentTransaction fragTrans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_day);
        dataSource = new HealthRecordDataSource(this);
        today_label = (TextView) findViewById(R.id.today_label);
        personId = getIntent().getIntExtra("personId", 0);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        ovFrag = OverviewFragment.newInstance();
        apptFrag = AppointmentFragment.newInstance();
        ailmentFrag = AilmentFragment.newInstance();
        measureFrag = MeasureFragment.newInstance();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.day_layout, ovFrag);
        fragTrans.commit();
        currentFrag = ovFrag;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0 || day <= 0 || month <= 0 || year <= 0)
            return;
        displayCurrentDay();
        try {
            dataSource.open();
            dataSourceLoaded = true;
            ovFrag.setDataSource(dataSource);
            measureFrag.setDataSource(dataSource);
            apptFrag.setDataSource(dataSource);
            ailmentFrag.setDataSource(dataSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (dataSourceLoaded)
        {
            dataSource.close();
            dataSourceLoaded = false;
        }
    }

    private void displayCurrentDay()
    {
        currentDay = Calendar.getInstance();
        currentDay.set(Calendar.DAY_OF_MONTH, day);
        currentDay.set(Calendar.MONTH, month);
        currentDay.set(Calendar.YEAR, year);
        StringBuilder sb = new StringBuilder();
        sb.append(currentDay.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).toUpperCase());
        sb.append(" ");
        sb.append(day);
        sb.append(" ");
        sb.append(currentDay.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()).toUpperCase());
        sb.append(" ");
        sb.append(year);
        today_label.setText(sb.toString());
    }

    public void displayOverview(View view)
    {
        if (currentFrag == ovFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, ovFrag);
        fragTrans.commit();
        currentFrag = ovFrag;
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

    public void displayAilments(View view)
    {
        if (currentFrag == ailmentFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, ailmentFrag);
        fragTrans.commit();
        currentFrag = ailmentFrag;
    }

    public void createAppt(View view)
    {
        Intent intent = new Intent("com.akoudri.healthrecord.app.AddAppointment");
        intent.putExtra("personId", personId);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void createAilment(View view)
    {
        Intent intent = new Intent("com.akoudri.healthrecord.app.CreateAilment");
        intent.putExtra("personId", personId);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void saveMeasures(View view)
    {
        if (personId == 0 || day <= 0 || month <= 0 || year <= 0) return;
        measureFrag.saveMeasures();
    }

}
