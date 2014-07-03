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

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.BloodType;
import com.akoudri.healthrecord.data.Gender;
import com.akoudri.healthrecord.utils.DatePickerFragment;

import java.sql.SQLException;

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
        ArrayAdapter<String> btChoicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, btChoices);
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
        int btIdx = btSpinner.getSelectedItemPosition();
        BloodType bt;
        switch (btIdx)
        {
            case 0: bt = BloodType.OMINUS; break;
            case 1: bt = BloodType.OPLUS; break;
            case 2: bt = BloodType.AMINUS; break;
            case 3: bt = BloodType.APLUS; break;
            case 4: bt = BloodType.BMINUS; break;
            case 5: bt = BloodType.BPLUS; break;
            case 6: bt = BloodType.ABMINUS; break;
            case 7: bt = BloodType.ABPLUS; break;
            default:bt = BloodType.UNKNOWN;
        }
        String birthdate = birthdateET.getText().toString();
        //FIXME: check values before inserting
        dataSource.getPersonTable().insertPerson(name, gender, ssn, bt, birthdate);
        finish();
    }

    public void pickBirthdate(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, birthdateET);
        dfrag.show(getFragmentManager(),"Pick Birthdate");
    }

}
