package com.akoudri.healthrecord.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.akoudri.healthrecord.data.Person;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AddTherapistActivity extends Activity {

    private EditText nameET, phoneNumberET;
    private AutoCompleteTextView specialityET;
    private Spinner thSpinner;
    private HealthRecordDataSource dataSource;
    private int personId;
    private List<Therapist> otherTherapists;
    private List<TherapyBranch> branches;
    private Person person;
    //private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_therapist);
        thSpinner = (Spinner) findViewById(R.id.thchoice_add);
        nameET = (EditText) findViewById(R.id.name_add_therapist);
        specialityET = (AutoCompleteTextView) findViewById(R.id.speciality_add);
        phoneNumberET = (EditText) findViewById(R.id.phone_number_add);
        dataSource = new HealthRecordDataSource(this);
        //lang = Locale.getDefault().getDisplayName();
        personId = getIntent().getIntExtra("personId", 1);
        retrievePerson();
        retrieveBranches();
        retrieveOtherTherapists();
        String[] otherTherapistsStr;
        if (otherTherapists.size() > 0) {
            otherTherapistsStr = new String[otherTherapists.size()];
            int i = 0;
            TherapyBranch branch = null;
            String therapyBranchStr;
            for (Therapist t : otherTherapists) {
                try {
                    dataSource.open();
                    branch = dataSource.getTherapyBranchTable().getBranchWithId(t.getBranchId());
                    dataSource.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                therapyBranchStr = branch.getName();
                otherTherapistsStr[i++] = t.getName() + " - " + therapyBranchStr;
            }
        }
        else
        {
            String s = getResources().getString(R.string.no_other_therapist);
            otherTherapistsStr = new String[]{s};
            //TODO: deactivate add button
        }
        ArrayAdapter<String> thChoicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, otherTherapistsStr);
        thSpinner.setAdapter(thChoicesAdapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getBranches());
        specialityET.setThreshold(1);
        specialityET.setAdapter(adapter);
    }

    public void addTherapist(View view)
    {
        String name = nameET.getText().toString();
        String speciality = specialityET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        //FIXME: check values before inserting
        try {
            dataSource.open();
            int branchId = dataSource.getTherapyBranchTable().getBranchId(speciality);
            if (branchId < 0)
            {
                branchId = (int) dataSource.getTherapyBranchTable().insertTherapyBranch(speciality);
            }
            int thId = (int) dataSource.getTherapistTable().insertTherapist(name, phoneNumber, branchId);
            dataSource.getPersonTherapistTable().insertRelation(personId, thId);
            dataSource.close();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        finish();
    }

    public void addExistingTherapist(View view)
    {
        int thIdx = thSpinner.getSelectedItemPosition();
        Therapist th = otherTherapists.get(thIdx);
        try {
            dataSource.open();
            dataSource.getPersonTherapistTable().insertRelation(personId,th.getId());
            dataSource.close();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        finish();
    }

    private void retrievePerson()
    {
        try {
            dataSource.open();
            person = dataSource.getPersonTable().getPersonWithId(personId);
            dataSource.close();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    private void retrieveBranches() {
        try {
            dataSource.open();
            branches = dataSource.getTherapyBranchTable().getAllBranches();
            dataSource.close();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    private String[] getBranches()
    {
        String[] res = new String[branches.size()];
        int i = 0;
        for (TherapyBranch b : branches)
        {
            res[i++] = b.getName();
        }
        return res;
    }

    private void retrieveOtherTherapists() {
        try {
            dataSource.open();
            //retrieve all therapists
            otherTherapists = dataSource.getTherapistTable().getAllTherapists();
            List<Therapist> otherTherapistsTmp = new ArrayList<Therapist>();
            //FIXME: use iterator instead
            otherTherapistsTmp.addAll(otherTherapists);
            //and then remove from the list those who are already therapist for current person
            List<Integer> myTherapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
            Therapist t;
            for (Integer i : myTherapistIds)
            {
                t = dataSource.getTherapistTable().getTherapistWithId(i);
                for (Therapist th : otherTherapistsTmp)
                {
                    if (th.getName().equalsIgnoreCase(t.getName()))
                    {
                        otherTherapists.remove(th);
                    }
                }
            }
            dataSource.close();
        } catch (SQLException ex)
        {
            otherTherapists = new ArrayList<Therapist>();
            ex.printStackTrace();
        }
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

}
