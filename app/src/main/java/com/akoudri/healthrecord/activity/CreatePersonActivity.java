package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.BloodType;
import com.akoudri.healthrecord.data.Gender;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreatePersonActivity extends Activity {

    private Spinner btSpinner;
    private RadioGroup genderRG;
    private EditText nameET, birthdateET, ssnET;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_person);
        String[] btChoices = {"O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+", " "};
        ArrayAdapter<String> btChoicesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, btChoices);
        btSpinner = (Spinner) findViewById(R.id.btchoice_add);
        btSpinner.setAdapter(btChoicesAdapter);
        btSpinner.setSelection(8);
        nameET = (EditText) findViewById(R.id.name_add);
        genderRG = (RadioGroup) findViewById(R.id.gender_add);
        ssnET = (EditText) findViewById(R.id.ssn_add);
        birthdateET = (EditText) findViewById(R.id.birthdate_add);
        dataSource = new HealthRecordDataSource(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dataSource.open();
            dataSourceLoaded = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dataSourceLoaded)
        {
            dataSource.close();
            dataSourceLoaded = false;
        }
    }

    public void addPerson(View view)
    {
        if (!dataSourceLoaded) return;
        //retrieve fields
        String name = nameET.getText().toString();
        RadioButton checked = (RadioButton) findViewById(genderRG.getCheckedRadioButtonId());
        int genderIdx = genderRG.indexOfChild(checked);
        Gender gender;
        switch (genderIdx)
        {
            case 0: gender = Gender.MALE; break;
            default: gender = Gender.FEMALE;
        }
        String ssn = ssnET.getText().toString();
        BloodType bt = HealthRecordUtils.int2bloodType(btSpinner.getSelectedItemPosition());
        String birthdate = birthdateET.getText().toString();
        if (checkFields(name, birthdate, ssn)) {
            if (ssn.equals("")) ssn = null;
            long id = dataSource.getPersonTable().insertPerson(name, gender, ssn, bt, birthdate);
            if (id == -1)
            {
                HealthRecordUtils.highlightActivityFields(this, nameET, ssnET);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.db_warning), Toast.LENGTH_SHORT).show();
            }
            else {
                finish();
            }
        }
    }

    private boolean checkFields(String name, String birthdate, String ssn)
    {
        boolean res = true;
        List<EditText> toHighlight = new ArrayList<EditText>();
        List<EditText> notToHighlight = new ArrayList<EditText>();
        //check name
        boolean checkName = HealthRecordUtils.isValidName(name);
        res = res && checkName;
        if (!checkName) toHighlight.add(nameET);
        else notToHighlight.add(nameET);
        //check birthdate
        boolean checkBirthdate = (! birthdate.equalsIgnoreCase(""));
        res = res && checkBirthdate;
        if (!checkBirthdate) toHighlight.add(birthdateET);
        else notToHighlight.add(birthdateET);
        //check ssn
        if (! ssn.equals("")) {
            boolean checkSsn = HealthRecordUtils.isValidSsn(ssn);
            res = res && checkSsn;
            if (!checkSsn) toHighlight.add(ssnET);
            else notToHighlight.add(ssnET);
        }
        else notToHighlight.add(ssnET);
        //display
        if (toHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(this, toHighlight, true);
        if (notToHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(this, notToHighlight, false);
        if (!res) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    public void pickBirthdate(View view)
    {
        Calendar c = Calendar.getInstance();
        Calendar initial = Calendar.getInstance();
        initial.set(Calendar.DAY_OF_MONTH, 1);
        initial.set(Calendar.MONTH, Calendar.JANUARY);
        initial.set(Calendar.YEAR, 2000);
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, birthdateET, initial, null, c);
        dfrag.show(getFragmentManager(),"Pick Birthdate");
    }

}
