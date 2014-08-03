package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.fragment.AilmentFragment;
import com.akoudri.healthrecord.fragment.AppointmentFragment;
import com.akoudri.healthrecord.fragment.MeasureFragment;
import com.akoudri.healthrecord.fragment.ObservationFragment;
import com.akoudri.healthrecord.fragment.OverviewFragment;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;

public class EditDayActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId = 0;
    private int day, month, year;
    private Calendar currentDay;

    private LinearLayout dayMenuLayout;

    private ImageButton ovButton, measureButton, rvButton, illnessButton, obsButton;
    private ImageButton currentButton;

    private TextView today_label;
    private OverviewFragment ovFrag;
    private AppointmentFragment apptFrag;
    private MeasureFragment measureFrag;
    private AilmentFragment ailmentFrag;
    private ObservationFragment obsFrag;
    private Fragment currentFrag;
    private FragmentTransaction fragTrans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_day);
        dataSource = HealthRecordDataSource.getInstance(this);
        dayMenuLayout = (LinearLayout) findViewById(R.id.day_menu_layout);
        initDayMenuLayout();
        today_label = (TextView) findViewById(R.id.today_label);
        //ovButton = (ImageButton) findViewById(R.id.overview_button);
        //measureButton = (ImageButton) findViewById(R.id.measure_button);
        //obsButton = (ImageButton) findViewById(R.id.observation_button);
        //rvButton = (ImageButton) findViewById(R.id.rv_button);
        //illnessButton = (ImageButton) findViewById(R.id.illness_button);
        personId = getIntent().getIntExtra("personId", 0);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        ovFrag = OverviewFragment.newInstance();
        apptFrag = AppointmentFragment.newInstance();
        ailmentFrag = AilmentFragment.newInstance();
        measureFrag = MeasureFragment.newInstance();
        obsFrag = ObservationFragment.newInstance();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.day_layout, ovFrag);
        fragTrans.commit();
        currentFrag = ovFrag;
        currentButton = ovButton;
        currentButton.setEnabled(false);
    }

    private void initDayMenuLayout()
    {
        int margin = 2;
        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparams.gravity = Gravity.CENTER_VERTICAL;
        llparams.bottomMargin = margin;
        llparams.leftMargin = margin;
        llparams.topMargin = margin;
        llparams.rightMargin = margin;
        //Overview
        ovButton = new ImageButton(this);
        ovButton.setBackgroundResource(R.drawable.overview);
        ovButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayOverview();
            }
        });
        ovButton.setLayoutParams(llparams);
        dayMenuLayout.addView(ovButton);
        //Measure
        measureButton = new ImageButton(this);
        measureButton.setBackgroundResource(R.drawable.measure);
        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMeasures();
            }
        });
        measureButton.setLayoutParams(llparams);
        dayMenuLayout.addView(measureButton);
        //Observation
        obsButton = new ImageButton(this);
        obsButton.setBackgroundResource(R.drawable.observation);
        obsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayObservations();
            }
        });
        obsButton.setLayoutParams(llparams);
        dayMenuLayout.addView(obsButton);
        //Appointment
        rvButton = new ImageButton(this);
        rvButton.setBackgroundResource(R.drawable.rv);
        rvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayRV();
            }
        });
        rvButton.setLayoutParams(llparams);
        dayMenuLayout.addView(rvButton);
        //Overview
        illnessButton = new ImageButton(this);
        illnessButton.setBackgroundResource(R.drawable.illness);
        illnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAilments();
            }
        });
        illnessButton.setLayoutParams(llparams);
        dayMenuLayout.addView(illnessButton);
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
            obsFrag.setDataSource(dataSource);
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

    private void displayOverview()
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

    private void displayMeasures()
    {
        if (currentFrag == measureFrag) return;
        //TODO reset measure id in frag
        measureFrag.resetMeasureId();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, measureFrag);
        fragTrans.commit();
        currentButton.setEnabled(true);
        measureButton.setEnabled(false);
        currentButton = measureButton;
        currentFrag = measureFrag;
    }

    private void displayRV()
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

    private void displayAilments()
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

    private void displayObservations()
    {
        if (currentFrag == obsFrag) return;
        obsFrag.resetObservationId();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, obsFrag);
        fragTrans.commit();
        currentButton.setEnabled(true);
        obsButton.setEnabled(false);
        currentButton = obsButton;
        currentFrag = obsFrag;
    }

    public void createAppt(View view)
    {
        Intent intent = new Intent(this, EditAilmentActivity.class);
        intent.putExtra("personId", personId);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void createAilment(View view)
    {
        ailmentFrag.resetAilmentId();
        Intent intent = new Intent(this, CreateAilmentActivity.class);
        intent.putExtra("personId", personId);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void createMeasure(View view)
    {
        measureFrag.resetMeasureId();
        Intent intent = new Intent(this, EditMeasureActivity.class);
        intent.putExtra("personId", personId);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void createObservation(View view)
    {
        obsFrag.resetObservationId();
        Intent intent = new Intent(this, EditObservationActivity.class);
        intent.putExtra("personId", personId);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

}
