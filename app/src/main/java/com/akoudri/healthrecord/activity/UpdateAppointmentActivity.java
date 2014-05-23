package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Appointment;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class UpdateAppointmentActivity extends Activity {

    private EditText dayET, hourET;
    private Spinner thSpinner;
    private HealthRecordDataSource dataSource;
    private List<Therapist> therapists;
    private List<Integer> thIds;
    private int personId, apptId;
    private Appointment appt;
    //private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_appointment);
        dayET = (EditText) findViewById(R.id.day_appt_update);
        hourET = (EditText) findViewById(R.id.update_appt_hour);
        thSpinner = (Spinner) findViewById(R.id.thchoice_update);
        dataSource = new HealthRecordDataSource(this);
        //lang = Locale.getDefault().getDisplayName();
        personId = getIntent().getIntExtra("personId", 0);
        apptId = getIntent().getIntExtra("apptId", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //FIXME: Manage the case where data source could not be opened
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        retrieveAppt();
        String apptDate = appt.getDate();
        String apptHour = appt.getHour();
        dayET.setText(apptDate);
        hourET.setText(apptHour);
        therapists = new ArrayList<Therapist>();
        thIds = new ArrayList<Integer>();
        retrieveTherapists();
        String[] therapistsStr;
        int thIdx = 0;
        therapistsStr = new String[therapists.size()];
        int i = 0;
        int it = 0;
        TherapyBranch branch = null;
        String therapyBranchStr;
        for (Therapist t : therapists) {
            if (t.getId() == appt.getTherapist()) thIdx = it;
            branch = dataSource.getTherapyBranchTable().getBranchWithId(t.getBranchId());
            therapyBranchStr = branch.getName();
            therapistsStr[i++] = t.getName() + " - " + therapyBranchStr;
            it ++;
        }
        ArrayAdapter<String> thChoicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, therapistsStr);
        thSpinner.setAdapter(thChoicesAdapter);
        thSpinner.setSelection(thIdx);
    }

    private void retrieveAppt()
    {
        appt = dataSource.getAppointmentTable().getAppointmentWithId(apptId);
    }

    public void retrieveTherapists()
    {
        //retrieve all therapists
        List<Integer> myTherapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
        Therapist t;
        for (Integer i : myTherapistIds)
        {
            t = dataSource.getTherapistTable().getTherapistWithId(i);
            therapists.add(t);
            thIds.add(t.getId());
        }
    }

    public void showBirthdayPickerDialog(View view)
    {
        BirthDatePickerFragment dfrag = new BirthDatePickerFragment();
        dfrag.setBdet(dayET);
        dfrag.show(getFragmentManager(),"birthDatePicker");
    }

    public void selectHour(View view)
    {
        TimePickerFragment frag = new TimePickerFragment();
        frag.setApptet(hourET);
        frag.show(getFragmentManager(), "appointmentPicker");
    }

    public void updateAppointment(View view)
    {
        int thPos = thSpinner.getSelectedItemPosition();
        int therapistId = thIds.get(thPos);
        String dayStr = dayET.getText().toString();
        String hourStr = hourET.getText().toString();
        //TODO: update also comment
        dataSource.getAppointmentTable().updateAppointment(apptId, personId, therapistId, dayStr, hourStr, " ");
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add_action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class BirthDatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener
    {
        private EditText apptet;

        public void setBdet(EditText apptet)
        {
            this.apptet = apptet;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            String toDisplay = String.format("%02d/%02d/%4d", day, month+1, year);
            apptet.setText(toDisplay);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener
    {
        private EditText apptet;

        public void setApptet(EditText apptet)
        {
            this.apptet = apptet;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int min = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, min, true);
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int min) {
            String toDisplay = String.format("%02d:%02d", hour, min);
            apptet.setText(toDisplay);
        }
    }

}
