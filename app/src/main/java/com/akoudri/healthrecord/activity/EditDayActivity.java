package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
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

    private ImageButton ovButton, measureButton, rvButton, illnessButton;
    private ImageButton currentButton;

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
        ovButton = (ImageButton) findViewById(R.id.overview_button);
        measureButton = (ImageButton) findViewById(R.id.measure_button);
        rvButton = (ImageButton) findViewById(R.id.rv_button);
        illnessButton = (ImageButton) findViewById(R.id.illness_button);
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
        currentButton = ovButton;
        currentButton.setEnabled(false);
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
            int count = dataSource.getPersonTherapistTable().countTherapistsForPerson(personId);
            if (count == 0)
                rvButton.setEnabled(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
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
        currentButton.setEnabled(true);
        ovButton.setEnabled(false);
        currentButton = ovButton;
        currentFrag = ovFrag;
    }

    public void displayMeasures(View view)
    {
        if (currentFrag == measureFrag) return;
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, measureFrag);
        fragTrans.commit();
        currentButton.setEnabled(true);
        measureButton.setEnabled(false);
        currentButton = measureButton;
        currentFrag = measureFrag;
    }

    public void displayRV(View view)
    {
        if (!dataSourceLoaded) return;
        if (currentFrag == apptFrag) return;
        apptFrag.resetAppointmentId();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, apptFrag);
        fragTrans.commit();
        currentButton.setEnabled(true);
        rvButton.setEnabled(false);
        currentButton = rvButton;
        currentFrag = apptFrag;
    }

    public void displayAilments(View view)
    {
        if (currentFrag == ailmentFrag) return;
        ailmentFrag.resetAilmentId();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, ailmentFrag);
        fragTrans.commit();
        currentButton.setEnabled(true);
        illnessButton.setEnabled(false);
        currentButton = illnessButton;
        currentFrag = ailmentFrag;
    }

    public void createAppt(View view)
    {
        apptFrag.resetAppointmentId();
        Intent intent = new Intent("com.akoudri.healthrecord.app.AddAppointment");
        intent.putExtra("personId", personId);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void createAilment(View view)
    {
        ailmentFrag.resetAilmentId();
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
