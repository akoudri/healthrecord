package com.akoudri.healthrecord.activity;

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

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Person;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;
import com.akoudri.healthrecord.data.TherapyBranchTable;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;


public class CreateTherapistActivity extends Activity {

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
        setContentView(R.layout.activity_create_therapist);
        thSpinner = (Spinner) findViewById(R.id.thchoice_add);
        nameET = (EditText) findViewById(R.id.name_add_therapist);
        specialityET = (AutoCompleteTextView) findViewById(R.id.speciality_add);
        phoneNumberET = (EditText) findViewById(R.id.phone_number_add);
        dataSource = new HealthRecordDataSource(this);
        //lang = Locale.getDefault().getDisplayName();
        personId = getIntent().getIntExtra("personId", 1);
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
        retrievePerson();
        retrieveBranches();
        retrieveOtherTherapists();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    private void retrievePerson()
    {
        person = dataSource.getPersonTable().getPersonWithId(personId);
    }

    private void retrieveBranches() {
        branches = dataSource.getTherapyBranchTable().getAllBranches();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getBranches());
        specialityET.setThreshold(1);
        specialityET.setAdapter(adapter);
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
        //retrieve all therapists
        otherTherapists = dataSource.getTherapistTable().getAllTherapists();
        List<Integer> myTherapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
        Therapist t;
        Iterator<Therapist> iterator = otherTherapists.iterator();
        while (iterator.hasNext())
        {
            t = iterator.next();
            if (myTherapistIds.contains(t.getId()))
                iterator.remove();
        }
        String[] otherTherapistsStr;
        if (otherTherapists.size() > 0) {
            otherTherapistsStr = new String[otherTherapists.size()];
            int i = 0;
            TherapyBranch branch = null;
            String therapyBranchStr;
            TherapyBranchTable branchTable = dataSource.getTherapyBranchTable();
            for (Therapist th : otherTherapists) {
                branch = branchTable.getBranchWithId(th.getBranchId());
                therapyBranchStr = branch.getName();
                otherTherapistsStr[i++] = th.getName() + " - " + therapyBranchStr;
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
    }

    public void addTherapist(View view)
    {
        String name = nameET.getText().toString();
        String speciality = specialityET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        //FIXME: check values before inserting
        int branchId = dataSource.getTherapyBranchTable().getBranchId(speciality);
        if (branchId < 0)
        {
            branchId = (int) dataSource.getTherapyBranchTable().insertTherapyBranch(speciality);
        }
        int thId = (int) dataSource.getTherapistTable().insertTherapist(name, phoneNumber, branchId);
        if (thId >= 0)
        {
            dataSource.getPersonTherapistTable().insertRelation(personId, thId);
        }
        finish();
    }

    public void addExistingTherapist(View view)
    {
        int thIdx = thSpinner.getSelectedItemPosition();
        Therapist th = otherTherapists.get(thIdx);
        dataSource.getPersonTherapistTable().insertRelation(personId,th.getId());
        finish();
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
