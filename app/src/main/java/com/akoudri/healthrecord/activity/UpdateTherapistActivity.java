package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.os.Bundle;
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

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int thId;
    private Therapist therapist;
    private List<TherapyBranch> branches;

    private EditText nameET, phoneNumberET;
    private AutoCompleteTextView specialityET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_therapist);
        nameET = (EditText) findViewById(R.id.name_update_therapist);
        specialityET = (AutoCompleteTextView) findViewById(R.id.speciality_update);
        phoneNumberET = (EditText) findViewById(R.id.phone_number_update);
        dataSource = new HealthRecordDataSource(this);
        thId = getIntent().getIntExtra("therapistId", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (thId == 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            therapist = dataSource.getTherapistTable().getTherapistWithId(thId);
            branches = dataSource.getTherapyBranchTable().getAllBranches();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, getBranches());
            specialityET.setThreshold(1);
            specialityET.setAdapter(adapter);
            fillWidgets();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thId == 0) return;
        if (dataSourceLoaded)
        {
            dataSource.close();
            dataSourceLoaded = false;
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

    private void fillWidgets()
    {
        if (therapist == null) return;
        nameET.setText(therapist.getName());
        TherapyBranch branch = dataSource.getTherapyBranchTable().getBranchWithId(therapist.getBranchId());
        specialityET.setText(branch.getName());
        phoneNumberET.setText(therapist.getPhoneNumber());
    }

    public void updateTherapist(View view)
    {
        if (thId == 0) return;
        if (!dataSourceLoaded) return;
        String name = nameET.getText().toString();
        String speciality = specialityET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        //FIXME: check values before inserting
        int branch = dataSource.getTherapyBranchTable().getBranchId(speciality);
        dataSource.getTherapistTable().updateTherapist(thId, name, phoneNumber, branch);
        finish();
    }

}
