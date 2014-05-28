package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.BloodType;
import com.akoudri.healthrecord.data.Gender;

import java.sql.SQLException;
import java.util.Calendar;


public class CreatePersonActivity extends Activity {

    private Spinner btSpinner;
    private RadioGroup genderRG;
    private EditText nameET, birthdateET, ssnET;
    private HealthRecordDataSource dataSource;

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
        //FIXME: Manage the case where data source could not be opened
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    public void addPerson(View view)
    {
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
        //FIXME: do not capitalize all letters
        dataSource.getPersonTable().insertPerson(name, gender, ssn, bt, birthdate);
        finish();
    }

    public void showBirthdayPickerDialog(View view)
    {
        BirthDatePickerFragment dfrag = new BirthDatePickerFragment();
        dfrag.setBdet(birthdateET);
        dfrag.show(getFragmentManager(),"birthDatePicker");
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

    public static class BirthDatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener
    {
        private EditText bdet;

        public void setBdet(EditText bdet)
        {
            this.bdet = bdet;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            String toDisplay = String.format("%02d/%02d/%4d", day, month+1, year);
            bdet.setText(toDisplay);
        }
    }

}