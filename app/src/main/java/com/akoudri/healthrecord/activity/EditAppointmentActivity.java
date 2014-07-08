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
        hourET = (EditText) findViewById(R.id.update_appt_hour);
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
        String therapyBranchStr;
        for (Therapist t : therapists) {
            if (t.getId() == appt.getTherapistId()) thIdx = it;
            branch = dataSource.getTherapyBranchTable().getBranchWithId(t.getBranchId());
            therapyBranchStr = branch.getName();
            therapistsStr[i++] = t.getName() + " - " + therapyBranchStr;
            it++;
        }
        ArrayAdapter<String> thChoicesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, therapistsStr);
        thSpinner.setAdapter(thChoicesAdapter);
        thSpinner.setSelection(thIdx);
        dayET.setText(appt.getDate());
        hourET.setText(appt.getHour());
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
        HourPickerFragment hfrag = new HourPickerFragment();
        hfrag.init(this, hourET);
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
        //FIXME: eventually deactivate save button -> also for other activities
        if (! appt.equalsTo(a)) {
            dataSource.getAppointmentTable().updateAppointment(apptId, therapistId, dayStr, hourStr, comment);
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT);
            toast.show();
        }
        finish();
    }

}
