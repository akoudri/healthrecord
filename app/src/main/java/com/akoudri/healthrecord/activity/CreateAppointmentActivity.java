package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CreateAppointmentActivity extends Activity {

    private EditText hourET;
    private Spinner thSpinner;
    private HealthRecordDataSource dataSource;
    private List<Therapist> therapists;
    private List<Integer> thIds;
    private int personId;
    private int date, month, year;
    private String selectedDate;
    //private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_appointment);
        hourET = (EditText) findViewById(R.id.select_appt_hour);
        thSpinner = (Spinner) findViewById(R.id.thchoice_select);
        dataSource = new HealthRecordDataSource(this);
        //lang = Locale.getDefault().getDisplayName();
        personId = getIntent().getIntExtra("personId", 0);
        date = getIntent().getIntExtra("date", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        selectedDate = String.format("%02d/%02d/%04d", date, month + 1, year);
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
        therapists = new ArrayList<Therapist>();
        thIds = new ArrayList<Integer>();
        retrieveTherapists();
        String[] therapistsStr;
        if (therapists.size() > 0) {
            therapistsStr = new String[therapists.size()];
            int i = 0;
            TherapyBranch branch = null;
            String therapyBranchStr;
            for (Therapist t : therapists) {
                branch = dataSource.getTherapyBranchTable().getBranchWithId(t.getBranchId());
                therapyBranchStr = branch.getName();
                therapistsStr[i++] = t.getName() + " - " + therapyBranchStr;
            }
        }
        else
        {
            //FIXME: put appropriate string
            String s = getResources().getString(R.string.no_other_therapist);
            therapistsStr = new String[]{s};
            //TODO: deactivate add button
        }
        ArrayAdapter<String> thChoicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, therapistsStr);
        thSpinner.setAdapter(thChoicesAdapter);
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

    public void selectHour(View view)
    {
        TimePickerFragment frag = new TimePickerFragment();
        frag.setApptet(hourET);
        frag.show(getFragmentManager(), "appointmentPicker");
    }

    public void addAppointment(View view)
    {
        //TODO: store appt into db
        int thPos = thSpinner.getSelectedItemPosition();
        int therapistId = thIds.get(thPos);
        String hourStr = hourET.getText().toString();
        dataSource.getAppointmentTable().insertAppointment(personId, therapistId, selectedDate,
                hourStr, " "); //FIXME: manage null value for comment
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
