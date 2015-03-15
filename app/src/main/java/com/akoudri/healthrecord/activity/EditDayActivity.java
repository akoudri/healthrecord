package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.fragment.AilmentFragment;
import com.akoudri.healthrecord.fragment.AppointmentFragment;
import com.akoudri.healthrecord.fragment.MeasureFragment;
import com.akoudri.healthrecord.fragment.MedicsFragment;
import com.akoudri.healthrecord.fragment.ObservationFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;

//STATUS: checked
public class EditDayActivity extends Activity implements View.OnTouchListener {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId = 0;
    private int day, month, year;
    private Calendar today, currentDay;

    private LinearLayout dayMenuLayout;

    private ImageButton measureButton, rvButton, illnessButton, obsButton, medicButton;
    private ImageButton currentButton;

    private TextView today_label;
    private AppointmentFragment apptFrag;
    private MeasureFragment measureFrag;
    private AilmentFragment ailmentFrag;
    private ObservationFragment obsFrag;
    private MedicsFragment medFrag;
    private Fragment currentFrag;
    private FragmentTransaction fragTrans;

    //used to manage navigation between days
    private int tx = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_day);
        dataSource = HealthRecordDataSource.getInstance(this);
        dayMenuLayout = (LinearLayout) findViewById(R.id.day_menu_layout);
        personId = getIntent().getIntExtra("personId", 0);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        //init day
        currentDay = Calendar.getInstance();
        currentDay.set(Calendar.DAY_OF_MONTH, day);
        currentDay.set(Calendar.MONTH, month);
        currentDay.set(Calendar.YEAR, year);
        //today
        today =  Calendar.getInstance();
        //widgets
        initDayMenuLayout();
        today_label = (TextView) findViewById(R.id.today_label);
        apptFrag = AppointmentFragment.newInstance();
        ailmentFrag = AilmentFragment.newInstance();
        measureFrag = MeasureFragment.newInstance();
        obsFrag = ObservationFragment.newInstance();
        medFrag = MedicsFragment.newInstance();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.add(R.id.day_layout, measureFrag);
        fragTrans.commit();
        currentFrag = measureFrag;
        currentButton = measureButton;
        currentButton.setEnabled(false);
        //Touch management
        View view = findViewById(R.id.today_layout);
        view.setOnTouchListener(this);
    }

    private void initDayMenuLayout()
    {
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, this);
        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparams.gravity = Gravity.CENTER_VERTICAL;
        llparams.bottomMargin = margin;
        llparams.leftMargin = margin;
        llparams.topMargin = margin;
        llparams.rightMargin = margin;
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
        //Illness
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
        //Medics and Reminders
        medicButton = new ImageButton(this);
        setMedicButton();
        medicButton.setLayoutParams(llparams);
        dayMenuLayout.addView(medicButton);
    }

    private void setMedicButton()
    {
        if (currentDay.after(today))
        {
            medicButton.setBackgroundResource(R.drawable.reminder);
            medicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayReminders();
                }
            });
        } else {
            medicButton.setBackgroundResource(R.drawable.medication);
            medicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    displayMedics();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0 || day < 1 || month < 0 || year < 0)
            return;
        displayCurrentDay();
        try {
            dataSource.open();
            dataSourceLoaded = true;
            //fragments
            measureFrag.setDataSource(dataSource);
            apptFrag.setDataSource(dataSource);
            ailmentFrag.setDataSource(dataSource);
            obsFrag.setDataSource(dataSource);
            medFrag.setDataSource(dataSource);
            refreshFrag();
        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
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

    private void displayMeasures()
    {
        if (currentFrag == measureFrag) return;
        measureFrag.setCurrentDate(day, month, year);
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
        apptFrag.setCurrentDate(day, month, year);
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
        ailmentFrag.setCurrentDate(day, month, year);
        ailmentFrag.resetAilmentId();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, ailmentFrag);
        fragTrans.commit();
        currentButton.setEnabled(true);
        illnessButton.setEnabled(false);
        currentButton = illnessButton;
        currentFrag = ailmentFrag;
    }

    private void displayMedics()
    {
        if (currentFrag == medFrag) return;
        medFrag.setCurrentDate(day, month, year);
        medFrag.resetMedicId();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, medFrag);
        fragTrans.commit();
        currentButton.setEnabled(true);
        medicButton.setEnabled(false);
        currentButton = medicButton;
        currentFrag = medFrag;
    }

    private void displayReminders()
    {
        if (currentFrag == medFrag) return;
        medFrag.setCurrentDate(day, month, year);
        medFrag.resetMedicId();
        fragTrans = getFragmentManager().beginTransaction();
        fragTrans.replace(R.id.day_layout, medFrag);
        fragTrans.commit();
        currentButton.setEnabled(true);
        medicButton.setEnabled(false);
        currentButton = medicButton;
        currentFrag = medFrag;
    }

    private void displayObservations()
    {
        if (currentFrag == obsFrag) return;
        obsFrag.setCurrentDate(day, month, year);
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
        Intent intent = new Intent(this, EditAppointmentActivity.class);
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

    public void createMedic(View view)
    {
        medFrag.resetMedicId();
        Intent intent = new Intent(this, CreateMedicationActivity.class);
        String selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
        intent.putExtra("personId", personId);
        intent.putExtra("date", selectedDate);
        startActivity(intent);
    }

    public void createReminder(View view)
    {
        medFrag.resetMedicId();
        Intent intent = new Intent(this, CreateMedicationActivity.class);
        String selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
        intent.putExtra("personId", personId);
        intent.putExtra("date", selectedDate);
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

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int xm;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                tx = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                xm = (int) event.getX();
                int xdiff = xm - tx;
                if (xdiff > 50) {
                    currentDay.add(Calendar.DAY_OF_MONTH, -1);
                    day = currentDay.get(Calendar.DAY_OF_MONTH);
                    month = currentDay.get(Calendar.MONTH);
                    year = currentDay.get(Calendar.YEAR);
                }
                else if (xdiff < -50) {
                    currentDay.add(Calendar.DAY_OF_MONTH, 1);
                    day = currentDay.get(Calendar.DAY_OF_MONTH);
                    month = currentDay.get(Calendar.MONTH);
                    year = currentDay.get(Calendar.YEAR);
                }
                displayCurrentDay();
                refreshFrag();
                setMedicButton();
                tx = 0;
        }
        return true;
    }

    public void goPrev(View view)
    {
        currentDay.add(Calendar.DAY_OF_MONTH, -1);
        day = currentDay.get(Calendar.DAY_OF_MONTH);
        month = currentDay.get(Calendar.MONTH);
        year = currentDay.get(Calendar.YEAR);
        displayCurrentDay();
        refreshFrag();
        setMedicButton();
    }

    public void goNext(View view)
    {
        currentDay.add(Calendar.DAY_OF_MONTH, 1);
        day = currentDay.get(Calendar.DAY_OF_MONTH);
        month = currentDay.get(Calendar.MONTH);
        year = currentDay.get(Calendar.YEAR);
        displayCurrentDay();
        refreshFrag();
        setMedicButton();
    }

    private void refreshFrag()
    {
        measureFrag.setCurrentDate(day, month, year);
        obsFrag.setCurrentDate(day, month, year);
        apptFrag.setCurrentDate(day, month, year);
        ailmentFrag.setCurrentDate(day, month, year);
        medFrag.setCurrentDate(day, month, year);
        if (currentFrag instanceof MeasureFragment)
        {
            measureFrag.refresh();
            return;
        }
        if (currentFrag instanceof ObservationFragment)
        {
            obsFrag.refresh();
            return;
        }
        if (currentFrag instanceof AppointmentFragment)
        {
            apptFrag.refresh();
            return;
        }
        if (currentFrag instanceof AilmentFragment)
        {
            ailmentFrag.refresh();
            return;
        }
        if (currentFrag instanceof MedicsFragment)
        {
            medFrag.refresh();
            return;
        }
    }
}
