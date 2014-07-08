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
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;
import com.akoudri.healthrecord.utils.HealthRecordUtils;
import com.akoudri.healthrecord.utils.HourPickerFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CreateAppointmentActivity extends Activity {

    private EditText hourET, commentET;
    private Spinner thSpinner;
    
    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private List<Therapist> therapists;
    private List<Integer> thIds;
    private int personId;
    private int day, month, year;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_appointment);
        hourET = (EditText) findViewById(R.id.select_appt_hour);
        commentET = (EditText) findViewById(R.id.add_appt_comment);
        commentET = (EditText) findViewById(R.id.add_appt_comment);
        thSpinner = (Spinner) findViewById(R.id.thchoice_select);
        dataSource = new HealthRecordDataSource(this);
        personId = getIntent().getIntExtra("personId", 0);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0 || day <= 0 || month <= 0 || year <= 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            retrieveTherapists();
            fillWidgets();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (personId == 0 || day <= 0 || month <= 0 || year <= 0) return;
        if (!dataSourceLoaded) return;
        if (dataSourceLoaded)
        dataSource.close();
        dataSourceLoaded = false;
    }
    private void retrieveTherapists()
    {
        therapists = new ArrayList<Therapist>();
        thIds = new ArrayList<Integer>();
        List<Integer> myTherapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
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
        String[] therapistsStr;
        selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
        if (therapists.size() > 0) {
            therapistsStr = new String[therapists.size()];
            int i = 0;
            TherapyBranch branch;
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
        ArrayAdapter<String> thChoicesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, therapistsStr);
        thSpinner.setAdapter(thChoicesAdapter);
    }

    public void selectHour(View view)
    {
        HourPickerFragment hfrag = new HourPickerFragment();
        hfrag.init(this, hourET);
        hfrag.show(getFragmentManager(), "appointmentPicker");
    }

    public void addAppointment(View view)
    {
        if (personId == 0 || day <= 0 || month <= 0 || year <= 0) return;
        if (!dataSourceLoaded) return;
        //TODO: check values
        int thPos = thSpinner.getSelectedItemPosition();
        int therapistId = thIds.get(thPos);
        String hourStr = hourET.getText().toString();
        String comment = commentET.getText().toString();
        if (checkFields(hourStr)) {
            if (comment.equals("")) comment = null;
            dataSource.getAppointmentTable().insertAppointment(personId, therapistId, selectedDate,
                    hourStr, comment);
            finish();
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private boolean checkFields(String hour)
    {
        if (hour.equals("")){
            HealthRecordUtils.highlightActivityFields(this, hourET);
            return false;
        }
        return true;
    }

}
