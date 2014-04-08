package com.akoudri.healthrecord.app;

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

import com.akoudri.healthrecord.data.BloodType;
import com.akoudri.healthrecord.data.Person;

import java.sql.SQLException;
import java.util.Calendar;


public class UpdatePersonActivity extends Activity {

    private Spinner btSpinner;
    private EditText firstNameET, lastNameET, birthdateET;
    private RadioGroup genderRG;
    private RadioButton maleBtn, femaleBtn;
    private HealthRecordDataSource dataSource;
    private int personId;
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_update_person);
        personId = getIntent().getIntExtra("personId", 0);
        String[] btChoices = {"O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+", " "};
        ArrayAdapter<String> btChoicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, btChoices);
        btSpinner = (Spinner) findViewById(R.id.btchoice_update);
        btSpinner.setAdapter(btChoicesAdapter);
        btSpinner.setSelection(8);
        firstNameET = (EditText) findViewById(R.id.first_name_update);
        lastNameET = (EditText) findViewById(R.id.last_name_update);
        genderRG = (RadioGroup) findViewById(R.id.gender_add);
        maleBtn = (RadioButton) findViewById(R.id.male_update);
        femaleBtn = (RadioButton) findViewById(R.id.female_update);
        birthdateET = (EditText) findViewById(R.id.birthdate_update);
        dataSource = new HealthRecordDataSource(this);
        retrievePerson();
        populateWidgets();
    }

    private void populateWidgets()
    {
        if (person == null) return;
        firstNameET.setText(person.getFirstName());
        lastNameET.setText(person.getLastName());
        switch (person.getGender())
        {
            case MALE: maleBtn.setChecked(true); break;
            default: femaleBtn.setChecked(true);
        }
        birthdateET.setText(person.getBirthdate());
        switch (person.getBloodType()) {
            case OMINUS:
                btSpinner.setSelection(0);
                break;
            case OPLUS:
                btSpinner.setSelection(1);
                break;
            case AMINUS:
                btSpinner.setSelection(2);
                break;
            case APLUS:
                btSpinner.setSelection(3);
                break;
            case BMINUS:
                btSpinner.setSelection(4);
                break;
            case BPLUS:
                btSpinner.setSelection(5);
                break;
            case ABMINUS:
                btSpinner.setSelection(6);
                break;
            case ABPLUS:
                btSpinner.setSelection(7);
                break;
            default:
                btSpinner.setSelection(8);
        }
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

    public void updatePerson(View view)
    {
        //TODO
        //FIXME: check values before inserting
        finish();
    }

    public void showBirthdayPickerDialog(View view)
    {
        DialogFragment dfrag = new BirthDatePickerFragment();
        dfrag.show(getFragmentManager(),"birthDatePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.update_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.update_action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class BirthDatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener
    {

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
            birthdateET.setText(toDisplay);
        }
    }

}
