package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Appointment;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;
import com.akoudri.healthrecord.utils.HourPickerFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EditAppointmentActivity extends Activity {

    private EditText dayET, hourET, commentET;
    private Spinner thSpinner;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private List<Therapist> therapists;
    private List<Integer> thIds;
    private int apptId;
    private Appointment appt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_appointment);
        dayET = (EditText) findViewById(R.id.day_appt_update);
        dayET.setKeyListener(null);
        hourET = (EditText) findViewById(R.id.update_appt_hour);
        hourET.setKeyListener(null);
        commentET = (EditText) findViewById(R.id.update_appt_comment);
        thSpinner = (Spinner) findViewById(R.id.thchoice_update);
        dataSource = new HealthRecordDataSource(this);
        apptId = getIntent().getIntExtra("apptId", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (apptId == 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            appt = dataSource.getAppointmentTable().getAppointmentWithId(apptId);
            retrieveTherapists();
            fillWidgets();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (apptId == 0) return;
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    private void retrieveTherapists()
    {
        List<Integer> myTherapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(appt.getPersonId());
        therapists = new ArrayList<Therapist>();
        thIds = new ArrayList<Integer>();
        Therapist t;
        for (Integer i : myTherapistIds)
        {
            t = dataSource.getTherapistTable().getTherapistWithId(i);
            therapists.add(t);
            thIds.add(t.getId());
        }
    }

    private void fillWidgets()
    {
        String[] therapistsStr = new String[therapists.size()];
        int thIdx = 0;
        int i = 0;
        int it = 0;
        TherapyBranch branch;
        String tName;
        String therapyBranchStr;
        for (Therapist t : therapists) {
            tName = t.getName();
            if (tName.length() > 20) tName = tName.substring(0,20) + "...";
            if (t.getId() == appt.getTherapistId()) thIdx = it;
            branch = dataSource.getTherapyBranchTable().getBranchWithId(t.getBranchId());
            therapyBranchStr = branch.getName();
            if (therapyBranchStr.length() > 10) therapyBranchStr = therapyBranchStr.substring(0,10) + "...";
            therapistsStr[i++] = tName + " - " + therapyBranchStr;
            it++;
        }
        ArrayAdapter<String> thChoicesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, therapistsStr);
        thSpinner.setAdapter(thChoicesAdapter);
        thSpinner.setSelection(thIdx);
        dayET.setText(appt.getDate());
        hourET.setText(appt.getHour());
        commentET.setText(appt.getComment());
    }

    public void updateAppointmentDay(View view)
    {
        Calendar today = Calendar.getInstance();
        Calendar apptDay = HealthRecordUtils.stringToCalendar(appt.getDate());
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, dayET, apptDay, today, null);
        dfrag.show(getFragmentManager(), "Appointment Date Picker");
    }

    public void updateAppointmentHour(View view)
    {
        if (apptId == 0) return;
        if (!dataSourceLoaded) return;
        String[] h = appt.getHour().split(":");
        int hour = Integer.parseInt(h[0]);
        int min = Integer.parseInt(h[1]);
        HourPickerFragment hfrag = new HourPickerFragment();
        hfrag.init(this, hourET, hour, min);
        hfrag.show(getFragmentManager(), "Appointment Hour Picker");
    }

    public void updateAppointment(View view)
    {
        if (apptId == 0) return;
        if (!dataSourceLoaded) return;
        int thPos = thSpinner.getSelectedItemPosition();
        int therapistId = thIds.get(thPos);
        String dayStr = dayET.getText().toString();
        String hourStr = hourET.getText().toString();
        String comment = commentET.getText().toString();
        if (comment.equals("")) comment = null;
        Appointment a = new Appointment(appt.getPersonId(), therapistId, dayStr, hourStr, comment);
        if (appt.equalsTo(a))
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_change), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        a.setId(apptId);
        boolean res = dataSource.getAppointmentTable().updateAppointment(apptId, therapistId, dayStr, hourStr, comment);
        if (res)
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
            finish();
        }
        else Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
    }

}
