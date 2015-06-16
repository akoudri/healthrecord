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
import com.akoudri.healthrecord.app.PersonManager;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.BloodType;
import com.akoudri.healthrecord.data.Gender;
import com.akoudri.healthrecord.data.Person;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EditPersonActivity extends Activity {

    private Spinner btSpinner;
    private EditText nameET, birthdateET, ssnET;
    private RadioGroup genderRG;
    private RadioButton maleBtn, femaleBtn;
    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_person);
        dataSource = HealthRecordDataSource.getInstance(this);
        String[] btChoices = {"O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+", " "};
        ArrayAdapter<String> btChoicesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, btChoices);
        btSpinner = (Spinner) findViewById(R.id.btchoice_update_frag);
        btSpinner.setAdapter(btChoicesAdapter);
        btSpinner.setSelection(8);
        nameET = (EditText) findViewById(R.id.name_update_frag);
        genderRG = (RadioGroup) findViewById(R.id.gender_update_frag);
        ssnET = (EditText) findViewById(R.id.ssn_update_frag);
        maleBtn = (RadioButton) findViewById(R.id.male_update_frag);
        femaleBtn = (RadioButton) findViewById(R.id.female_update_frag);
        birthdateET = (EditText) findViewById(R.id.birthdate_update_frag);
        birthdateET.setKeyListener(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dataSource.open();
            dataSourceLoaded = true;
            fillWidgets();
        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    private void fillWidgets()
    {
        Person person = PersonManager.getInstance().getPerson();
        nameET.setText(person.getName());
        switch (person.getGender())
        {
            case MALE: maleBtn.setChecked(true); break;
            default: femaleBtn.setChecked(true);
        }
        ssnET.setText(person.getSsn());
        birthdateET.setText(person.getBirthdate());
        btSpinner.setSelection(person.getBloodType().ordinal());
    }

    public void updatePerson(View view)
    {
        if (dataSource == null) return;
        String name = nameET.getText().toString();
        RadioButton checked = (RadioButton) this.findViewById(genderRG.getCheckedRadioButtonId());
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
        if (checkFields(name, ssn)) {
            if (ssn.equals("")) ssn = null;
            Person person = PersonManager.getInstance().getPerson();
            Person p = new Person(name, gender, ssn, bt, birthdate);
            if (person.equalsTo(p)){
                Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.no_change), Toast.LENGTH_SHORT).show();
                return;
            }
            boolean res = dataSource.getPersonTable().updatePerson(person.getId(), name,
                    gender, ssn, bt, birthdate);
            if (res)
            {
                person.setName(name);
                person.setGender(gender);
                person.setSsn(ssn);
                person.setBloodType(bt);
                person.setBirthdate(birthdate);
                Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            else
            {
                Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        return;
    }

    private boolean checkFields(String name, String ssn) {
        boolean res = true;
        List<EditText> toHighlight = new ArrayList<EditText>();
        List<EditText> notToHighlight = new ArrayList<EditText>();
        //check name
        boolean checkName = HealthRecordUtils.isValidName(name);
        res = res && checkName;
        if (!checkName) toHighlight.add(nameET);
        else notToHighlight.add(nameET);
        //check ssn
        if (!ssn.equals("")) {
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
            Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    public void pickUpdateBirthdate(View view)
    {
        if (dataSource == null) return;
        Person person = PersonManager.getInstance().getPerson();
        Calendar today = Calendar.getInstance();
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, birthdateET, HealthRecordUtils.stringToCalendar(person.getBirthdate()), null, today);
        dfrag.show(getFragmentManager(),"Update Birthdate");
    }

}
