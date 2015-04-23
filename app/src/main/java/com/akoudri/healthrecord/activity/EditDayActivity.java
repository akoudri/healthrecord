package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.akoudri.healthrecord.fragment.EditDayFragment;
import com.akoudri.healthrecord.fragment.MeasureFragment;
import com.akoudri.healthrecord.fragment.MedicsFragment;
import com.akoudri.healthrecord.fragment.ObservationFragment;
import com.akoudri.healthrecord.fragment.RemindersFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

//STATUS: checked
public class EditDayActivity extends Activity implements View.OnTouchListener {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId = 0;
    private int day, month, year;
    private Calendar today, currentDay, oldCurrentDay;

    private TextView today_label;
    private LinearLayout dayMenuLayout;

    private EditDayFragment ailmentFrag, apptFrag, measureFrag, medFrag, obsFrag, remindFrag;
    private EditDayFragment currentFrag;
    private ImageButton measureButton, rvButton, illnessButton, obsButton, medicButton, reminderButton;
    private ImageButton currentButton;

    private FragmentTransaction fragTrans;

    private static final String ailmentID = "ailmentFrag";
    private static final String apptID = "apptFrag";
    private static final String measureID = "measureFrag";
    private static final String medID = "medFrag";
    private static final String obsID = "obsFrag";
    private static final String remindID = "remindFrag";

    //used to manage navigation between days
    private int tx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_day);
        dataSource = HealthRecordDataSource.getInstance(this);
        dayMenuLayout = (LinearLayout) findViewById(R.id.day_menu_layout);
        today_label = (TextView) findViewById(R.id.today_label);
        personId = getIntent().getIntExtra("personId", 0);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        //init days
        currentDay = Calendar.getInstance();
        currentDay.set(Calendar.DAY_OF_MONTH, day);
        currentDay.set(Calendar.MONTH, month);
        currentDay.set(Calendar.YEAR, year);
        oldCurrentDay = (Calendar) currentDay.clone();
        today =  Calendar.getInstance();
        //fragments and buttons
        initFragmentsAndButtons();
        refreshDayMenu();
        if (currentDay.after(today))
            changeCurrentFragAndButton(apptID, rvButton);
        else
            changeCurrentFragAndButton(measureID, measureButton);
        //Touch management
        View view = findViewById(R.id.today_layout);
        view.setOnTouchListener(this);
    }

    private void changeCurrentFragAndButton(String fragID, ImageButton button)
    {
        if (currentButton != null) currentButton.setEnabled(true);
        FragmentManager fragmentManager = getFragmentManager();
        fragTrans = getFragmentManager().beginTransaction();
        if (currentFrag != null) fragTrans.detach(currentFrag);
        EditDayFragment frag = (EditDayFragment)(fragmentManager.findFragmentByTag(fragID));
        if (frag == null)
        {
            if (ailmentID.equalsIgnoreCase(fragID)) {
                frag = ailmentFrag;
                fragTrans.add(R.id.day_layout, frag, ailmentID);
            }
            if (apptID.equalsIgnoreCase(fragID)) {
                frag = apptFrag;
                fragTrans.add(R.id.day_layout, frag, apptID);
            }
            if (measureID.equalsIgnoreCase(fragID)) {
                frag = measureFrag;
                fragTrans.add(R.id.day_layout, frag, measureID);
            }
            if (medID.equalsIgnoreCase(fragID)) {
                frag = medFrag;
                fragTrans.add(R.id.day_layout, frag, medID);
            }
            if (obsID.equalsIgnoreCase(fragID)) {
                frag = obsFrag;
                fragTrans.add(R.id.day_layout, frag, obsID);
            }
            if (remindID.equalsIgnoreCase(fragID)) {
                frag = remindFrag;
                fragTrans.add(R.id.day_layout, frag, remindID);
            }

        } else {
            fragTrans.attach(frag);
        }
        frag.setCurrentDate(day, month, year);
        fragTrans.commit();
        currentFrag = frag;
        currentButton = button;
        currentButton.setEnabled(false);
    }

    private void initFragmentsAndButtons()
    {
        //Fragments
        measureFrag = MeasureFragment.newInstance();
        obsFrag = ObservationFragment.newInstance();
        ailmentFrag = AilmentFragment.newInstance();
        medFrag = MedicsFragment.newInstance();
        apptFrag = AppointmentFragment.newInstance();
        remindFrag = RemindersFragment.newInstance();
        //Measure
        measureButton = new ImageButton(this);
        measureButton.setBackgroundResource(R.drawable.measure);
        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMeasures();
            }
        });
        //Appointment
        rvButton = new ImageButton(this);
        rvButton.setBackgroundResource(R.drawable.rv);
        rvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayRV();
            }
        });
        //Observations
        obsButton = new ImageButton(this);
        obsButton.setBackgroundResource(R.drawable.observation);
        obsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayObservations();
            }
        });
        //Illness
        illnessButton = new ImageButton(this);
        illnessButton.setBackgroundResource(R.drawable.illness);
        illnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAilments();
            }
        });
        //Medic
        medicButton = new ImageButton(this);
        medicButton.setBackgroundResource(R.drawable.medication);
        medicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMedics();
            }
        });
        //Reminder
        reminderButton = new ImageButton(this);
        reminderButton.setBackgroundResource(R.drawable.reminder);
        reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayReminders();
            }
        });
    }

    private void refreshDayMenu()
    {
        dayMenuLayout.removeAllViews();
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, this);
        LinearLayout.LayoutParams llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparams.gravity = Gravity.CENTER_VERTICAL;
        llparams.bottomMargin = margin;
        llparams.leftMargin = margin;
        llparams.topMargin = margin;
        llparams.rightMargin = margin;
        if (currentDay.after(today))
        {
            //Appointment
            rvButton.setLayoutParams(llparams);
            dayMenuLayout.addView(rvButton);
            //Reminder
            reminderButton.setLayoutParams(llparams);
            dayMenuLayout.addView(reminderButton);
        } else {
            //Measure
            measureButton.setLayoutParams(llparams);
            dayMenuLayout.addView(measureButton);
            //Observation
            obsButton.setLayoutParams(llparams);
            dayMenuLayout.addView(obsButton);
            //Illness
            illnessButton.setLayoutParams(llparams);
            dayMenuLayout.addView(illnessButton);
            //Medic
            medicButton.setLayoutParams(llparams);
            dayMenuLayout.addView(medicButton);
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
            //Fragments
            measureFrag.setDataSource(dataSource);
            obsFrag.setDataSource(dataSource);
            ailmentFrag.setDataSource(dataSource);
            medFrag.setDataSource(dataSource);
            apptFrag.setDataSource(dataSource);
            remindFrag.setDataSource(dataSource);
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
        measureFrag.resetObjectId();
        changeCurrentFragAndButton(measureID, measureButton);
    }

    private void displayRV()
    {
        if (currentFrag == apptFrag) return;
        apptFrag.setCurrentDate(day, month, year);
        apptFrag.resetObjectId();
        changeCurrentFragAndButton(apptID, rvButton);
    }

    private void displayAilments()
    {
        if (currentFrag == ailmentFrag) return;
        ailmentFrag.setCurrentDate(day, month, year);
        ailmentFrag.resetObjectId();
        changeCurrentFragAndButton(ailmentID, illnessButton);
    }

    private void displayMedics()
    {
        if (currentFrag == medFrag) return;
        medFrag.setCurrentDate(day, month, year);
        medFrag.resetObjectId();
        changeCurrentFragAndButton(medID, medicButton);
    }

    private void displayReminders()
    {
        if (currentFrag == remindFrag) return;
        remindFrag.setCurrentDate(day, month, year);
        remindFrag.resetObjectId();
        changeCurrentFragAndButton(remindID, reminderButton);
    }

    private void displayObservations()
    {
        if (currentFrag == obsFrag) return;
        obsFrag.setCurrentDate(day, month, year);
        obsFrag.resetObjectId();
        changeCurrentFragAndButton(obsID, obsButton);
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
        ailmentFrag.resetObjectId();
        Intent intent = new Intent(this, CreateAilmentActivity.class);
        intent.putExtra("personId", personId);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void createMedic(View view)
    {
        medFrag.resetObjectId();
        Intent intent = new Intent(this, CreateMedicationActivity.class);
        String selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
        intent.putExtra("personId", personId);
        intent.putExtra("date", selectedDate);
        startActivity(intent);
    }

    public void createReminder(View view)
    {
        remindFrag.resetObjectId();
        Intent intent = new Intent(this, EditReminderActivity.class);
        intent.putExtra("personId", personId);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void createMeasure(View view)
    {
        measureFrag.resetObjectId();
        Intent intent = new Intent(this, EditMeasureActivity.class);
        intent.putExtra("personId", personId);
        intent.putExtra("day", day);
        intent.putExtra("month", month);
        intent.putExtra("year", year);
        startActivity(intent);
    }

    public void createObservation(View view)
    {
        obsFrag.resetObjectId();
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
                    oldCurrentDay = (Calendar) currentDay.clone();
                    currentDay.add(Calendar.DAY_OF_MONTH, -1);
                    day = currentDay.get(Calendar.DAY_OF_MONTH);
                    month = currentDay.get(Calendar.MONTH);
                    year = currentDay.get(Calendar.YEAR);
                }
                else if (xdiff < -50) {
                    oldCurrentDay = (Calendar) currentDay.clone();
                    currentDay.add(Calendar.DAY_OF_MONTH, 1);
                    day = currentDay.get(Calendar.DAY_OF_MONTH);
                    month = currentDay.get(Calendar.MONTH);
                    year = currentDay.get(Calendar.YEAR);
                }
                displayCurrentDay();
                refreshDayMenu();
                refreshFrag();
                tx = 0;
        }
        return true;
    }

    public void goPrev(View view)
    {
        oldCurrentDay = (Calendar) currentDay.clone();
        currentDay.add(Calendar.DAY_OF_MONTH, -1);
        day = currentDay.get(Calendar.DAY_OF_MONTH);
        month = currentDay.get(Calendar.MONTH);
        year = currentDay.get(Calendar.YEAR);
        displayCurrentDay();
        refreshDayMenu();
        refreshFrag();
    }

    public void goNext(View view)
    {
        oldCurrentDay = (Calendar) currentDay.clone();
        currentDay.add(Calendar.DAY_OF_MONTH, 1);
        day = currentDay.get(Calendar.DAY_OF_MONTH);
        month = currentDay.get(Calendar.MONTH);
        year = currentDay.get(Calendar.YEAR);
        displayCurrentDay();
        refreshDayMenu();
        refreshFrag();
    }

    //FIXME: NPE!!!
    private void refreshFrag()
    {
        if (currentDay.equals(today) && oldCurrentDay.after(today))
        {
            changeCurrentFragAndButton(measureID, measureButton);
        }
        else if (oldCurrentDay.equals(today) && currentDay.after(today))
        {
            changeCurrentFragAndButton(apptID, rvButton);
        }
        else {
            currentFrag.setCurrentDate(day, month, year);
            currentFrag.refresh();
        }
    }
}