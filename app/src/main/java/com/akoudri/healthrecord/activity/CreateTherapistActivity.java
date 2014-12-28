package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CreateTherapistActivity extends Activity {

    private EditText nameET, phoneNumberET, cellPhoneET, emailET;
    private AutoCompleteTextView specialityET;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;

    private List<Therapist> otherTherapists;
    private List<TherapyBranch> branches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_therapist);
        nameET = (EditText) findViewById(R.id.name_add_therapist);
        specialityET = (AutoCompleteTextView) findViewById(R.id.speciality_add);
        phoneNumberET = (EditText) findViewById(R.id.phone_number_add);
        cellPhoneET = (EditText) findViewById(R.id.name_add_cellphone);
        emailET = (EditText) findViewById(R.id.name_add_email);
        dataSource = HealthRecordDataSource.getInstance(this);
        personId = getIntent().getIntExtra("personId", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            retrieveBranches();
        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (personId == 0) return;
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
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

    public void addTherapist(View view)
    {
        if (personId == 0) return;
        if (!dataSourceLoaded) return;
        String name = nameET.getText().toString();
        String speciality = specialityET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        String cellPhoneNumber = cellPhoneET.getText().toString();
        String email = emailET.getText().toString();
        if (checkFields(name, speciality, phoneNumber, cellPhoneNumber, email)) {
            int branchId = dataSource.getTherapyBranchTable().getBranchId(speciality);
            if (branchId < 0) {
                branchId = (int) dataSource.getTherapyBranchTable().insertTherapyBranch(speciality);
            }
            if (phoneNumber.equals("")) phoneNumber = null;
            if (cellPhoneNumber.equals("")) cellPhoneNumber = null;
            if (email.equals("")) email = null;
            int thId = (int) dataSource.getTherapistTable().insertTherapist(name, phoneNumber, cellPhoneNumber, email, branchId);
            if (thId >= 0) {
                dataSource.getPersonTherapistTable().insertRelation(personId, thId);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
                finish();
            }
            else
            {
                HealthRecordUtils.highlightActivityFields(this, nameET);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.db_warning), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkFields(String name, String specialty, String phoneNumber, String cellPhoneNumber, String email)
    {
        boolean res = true;
        List<EditText> toHighlight = new ArrayList<EditText>();
        List<EditText> notToHighlight = new ArrayList<EditText>();
        //check name
        boolean checkName = HealthRecordUtils.isValidName(name);
        res = res && checkName;
        if (!checkName) toHighlight.add(nameET);
        else notToHighlight.add(nameET);
        //Check specialty
        boolean checkSpecialty = HealthRecordUtils.isValidSpecialty(specialty);
        res = res && checkSpecialty;
        if (!checkSpecialty) toHighlight.add(specialityET);
        else notToHighlight.add(specialityET);
        //Check phone number
        if (! phoneNumber.equals(""))
        {
            boolean checkPhoneNumber = Patterns.PHONE.matcher(phoneNumber).matches();
            res = res && checkPhoneNumber;
            if (!checkPhoneNumber) toHighlight.add(phoneNumberET);
            else notToHighlight.add(phoneNumberET);
        }
        else notToHighlight.add(phoneNumberET);
        //check cell phone number
        if (! cellPhoneNumber.equals(""))
        {
            boolean checkCellPhoneNumber = Patterns.PHONE.matcher(cellPhoneNumber).matches();
            res = res && checkCellPhoneNumber;
            if (!checkCellPhoneNumber) toHighlight.add(cellPhoneET);
            else notToHighlight.add(cellPhoneET);
        }
        else notToHighlight.add(cellPhoneET);
        //check email
        if (! email.equals(""))
        {
            boolean checkEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
            res = res && checkEmail;
            if (!checkEmail) toHighlight.add(emailET);
            else notToHighlight.add(emailET);
        }
        else notToHighlight.add(emailET);
        //display
        if (toHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(this, toHighlight, true);
        if (notToHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(this, notToHighlight, false);
        if (!res) {
            Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
        }
        return res;
    }

}
