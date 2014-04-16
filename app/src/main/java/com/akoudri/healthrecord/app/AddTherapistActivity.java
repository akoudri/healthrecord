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
import java.util.Locale;


public class AddTherapistActivity extends Activity {

    private EditText firstNameET, lastNameET;
    private AutoCompleteTextView specialityET;
    private Spinner thSpinner;
    private HealthRecordDataSource dataSource;
    private int personId;
    private List<Therapist> otherTherapists;
    private List<TherapyBranch> branches;
    private Person person;
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_therapist);
        firstNameET = (EditText) findViewById(R.id.first_name_add_therapist);
        lastNameET = (EditText) findViewById(R.id.last_name_add_therapist);
        specialityET = (AutoCompleteTextView) findViewById(R.id.speciality_add);
        thSpinner = (Spinner) findViewById(R.id.thchoice_add);
        dataSource = new HealthRecordDataSource(this);
        lang = Locale.getDefault().getDisplayName();
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
                if (lang.toLowerCase().startsWith("fr"))
                    therapyBranchStr = branch.getFr();
                else
                    therapyBranchStr = branch.getEn();
                otherTherapistsStr[i++] = t.getFirstName() + " " + t.getLastName() + " - " +
                        therapyBranchStr;
            }
        }
        else
        {
            if (lang.toLowerCase().startsWith("fr"))
                otherTherapistsStr = new String[]{"Pas d'autres spécialistes"};//FIXME: use XML
            else
                otherTherapistsStr = new String[]{"No other therapists"};//FIXME: use XML
            //TODO: deactivate add button
        }
        ArrayAdapter<String> thChoicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, otherTherapistsStr);
        thSpinner.setAdapter(thChoicesAdapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getBranches(lang));
        specialityET.setThreshold(1);
        specialityET.setAdapter(adapter);
    }

    public void addTherapist(View view)
    {
        String firstName = firstNameET.getText().toString();
        String lastName = lastNameET.getText().toString();
        String speciality = specialityET.getText().toString();
        //FIXME: check values before inserting
        try {
            dataSource.open();
            //TODO
            dataSource.close();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        finish();
    }

    public void addExistingTherapist(View view)
    {
        //TODO
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

    private String[] getBranches(String lang)
    {
        String[] res = new String[branches.size()];
        int i = 0;
        for (TherapyBranch b : branches)
        {
            if (lang.toLowerCase().startsWith("fr"))
                res[i++] = b.getFr();
            else
                res[i++] = b.getEn();
        }
        return res;
    }

    private void retrieveOtherTherapists() {
        try {
            dataSource.open();
            //retrieve all therapists
            otherTherapists = dataSource.getTherapistTable().getAllTherapists();
            //and then remove from the list those who are already therapist for current person
            List<Integer> myTherapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
            Therapist t;
            for (Integer i : myTherapistIds)
            {
                t = dataSource.getTherapistTable().getTherapistWithId(i);
                for (Therapist th : otherTherapists)
                {
                    if (th.getFirstName().equalsIgnoreCase(t.getFirstName()) &&
                        th.getLastName().equalsIgnoreCase(t.getLastName()))
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
