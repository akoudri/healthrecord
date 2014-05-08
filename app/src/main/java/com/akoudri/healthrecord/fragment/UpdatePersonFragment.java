package com.akoudri.healthrecord.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.akoudri.healthrecord.data.Person;

import java.sql.SQLException;
import java.util.Calendar;


public class UpdatePersonFragment extends Fragment {

    private Spinner btSpinner;
    private EditText nameET, birthdateET, ssnET;
    private RadioGroup genderRG;
    private RadioButton maleBtn, femaleBtn;
    private HealthRecordDataSource dataSource;
    private View view;
    private int personId = 0;
    private Person person;

    public static UpdatePersonFragment newInstance()
    {
        return new UpdatePersonFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_person, container, false);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        String[] btChoices = {"O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+", " "};
        ArrayAdapter<String> btChoicesAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, btChoices);
        btSpinner = (Spinner) view.findViewById(R.id.btchoice_update_frag);
        btSpinner.setAdapter(btChoicesAdapter);
        btSpinner.setSelection(8);
        nameET = (EditText) view.findViewById(R.id.name_update_frag);
        genderRG = (RadioGroup) view.findViewById(R.id.gender_update_frag);
        ssnET = (EditText) view.findViewById(R.id.ssn_update_frag);
        maleBtn = (RadioButton) view.findViewById(R.id.male_update_frag);
        femaleBtn = (RadioButton) view.findViewById(R.id.female_update_frag);
        birthdateET = (EditText) view.findViewById(R.id.birthdate_update_frag);
        dataSource = new HealthRecordDataSource(getActivity());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //FIXME: Manage the case where data source could not be opened
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        retrievePerson();
        populateWidgets();
    }

    @Override
    public void onPause() {
        super.onPause();
        dataSource.close();
    }

    private void populateWidgets()
    {
        if (person == null) return;
        nameET.setText(person.getName());
        switch (person.getGender())
        {
            case MALE: maleBtn.setChecked(true); break;
            default: femaleBtn.setChecked(true);
        }
        ssnET.setText(person.getSsn());
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
        person = dataSource.getPersonTable().getPersonWithId(personId);
    }

    public void updatePerson(View view)
    {
        //FIXME: check values before inserting
        String name = nameET.getText().toString();
        RadioButton checked = (RadioButton) view.findViewById(genderRG.getCheckedRadioButtonId());
        int genderIdx = genderRG.indexOfChild(checked);
        Gender gender;
        //FIXME: the returned genderIdx is always -1 !!!
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
        dataSource.getPersonTable().updatePerson(person.getId(), name,
                gender, ssn, bt, birthdate);
    }

    public void showBirthdayPickerDialog(View view)
    {
        DialogFragment dfrag = new BirthDatePickerFragment();
        dfrag.show(getFragmentManager(),"birthDatePicker");
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
