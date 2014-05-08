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

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;

import java.sql.SQLException;
import java.util.List;


public class UpdateTherapistActivity extends Activity {

    private EditText nameET, phoneNumberET;
    private AutoCompleteTextView specialityET;
    private HealthRecordDataSource dataSource;
    private int thId;
    private List<TherapyBranch> branches;
    private Therapist therapist;
    //private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_therapist);
        nameET = (EditText) findViewById(R.id.name_update_therapist);
        specialityET = (AutoCompleteTextView) findViewById(R.id.speciality_update);
        phoneNumberET = (EditText) findViewById(R.id.phone_number_update);
        dataSource = new HealthRecordDataSource(this);
        //lang = Locale.getDefault().getDisplayName();
        thId = getIntent().getIntExtra("therapistId", 1);
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
        retrieveTherapist();
        retrieveBranches();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getBranches());
        specialityET.setThreshold(1);
        specialityET.setAdapter(adapter);
        populateWidgets();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    private void populateWidgets()
    {
        if (therapist == null) return;
        nameET.setText(therapist.getName());
        TherapyBranch branch = dataSource.getTherapyBranchTable().getBranchWithId(therapist.getBranchId());
        specialityET.setText(branch.getName());
        phoneNumberET.setText(therapist.getPhoneNumber());
    }

    public void updateTherapist(View view)
    {
        String name = nameET.getText().toString();
        String speciality = specialityET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        //FIXME: check values before inserting
        int branch = dataSource.getTherapyBranchTable().getBranchId(speciality);
        dataSource.getTherapistTable().updateTherapist(thId, name, phoneNumber, branch);
        finish();
    }

    private void retrieveTherapist()
    {
        therapist = dataSource.getTherapistTable().getTherapistWithId(thId);
    }

    private void retrieveBranches() {
        branches = dataSource.getTherapyBranchTable().getAllBranches();
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
