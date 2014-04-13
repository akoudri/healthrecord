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
import com.akoudri.healthrecord.data.Gender;
import com.akoudri.healthrecord.data.Person;

import java.sql.SQLException;
import java.util.Calendar;


public class AddTherapistActivity extends Activity {

    private EditText firstNameET, lastNameET, specialityET;
    private HealthRecordDataSource dataSource;
    private int personId;
    private Person person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_add_therapist);
        firstNameET = (EditText) findViewById(R.id.first_name_add_therapist);
        lastNameET = (EditText) findViewById(R.id.last_name_add_therapist);
        specialityET = (EditText) findViewById(R.id.speciality_add);
        dataSource = new HealthRecordDataSource(this);
        personId = getIntent().getIntExtra("personId", 0);
        retrievePerson();
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
