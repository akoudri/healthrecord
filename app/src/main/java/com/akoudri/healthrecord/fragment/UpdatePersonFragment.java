package com.akoudri.healthrecord.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.BloodType;
import com.akoudri.healthrecord.data.ChangeStatus;
import com.akoudri.healthrecord.data.Gender;
import com.akoudri.healthrecord.data.Person;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


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
        view = inflater.inflate(R.layout.fragment_person, container, false);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        String[] btChoices = {"O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+", " "};
        ArrayAdapter<String> btChoicesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, btChoices);
        btSpinner = (Spinner) view.findViewById(R.id.btchoice_update_frag);
        btSpinner.setAdapter(btChoicesAdapter);
        btSpinner.setSelection(8);
        nameET = (EditText) view.findViewById(R.id.name_update_frag);
        genderRG = (RadioGroup) view.findViewById(R.id.gender_update_frag);
        ssnET = (EditText) view.findViewById(R.id.ssn_update_frag);
        maleBtn = (RadioButton) view.findViewById(R.id.male_update_frag);
        femaleBtn = (RadioButton) view.findViewById(R.id.female_update_frag);
        birthdateET = (EditText) view.findViewById(R.id.birthdate_update_frag);
        birthdateET.setKeyListener(null);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (personId == 0) return;
        if (dataSource == null) return;
        person = dataSource.getPersonTable().getPersonWithId(personId);
        fillWidgets();
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    private void fillWidgets()
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
        btSpinner.setSelection(person.getBloodType().ordinal());
    }

    public ChangeStatus updatePerson()
    {
        if (personId == 0) return ChangeStatus.INVALID_PERSON;
        if (dataSource == null) return ChangeStatus.DATABASE_CLOSED;
        String name = nameET.getText().toString();
        RadioButton checked = (RadioButton) getActivity().findViewById(genderRG.getCheckedRadioButtonId());
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
            Person p = new Person(name, gender, ssn, bt, birthdate);
            if (person.equalsTo(p)){
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.no_change), Toast.LENGTH_SHORT).show();
                return ChangeStatus.UNCHANGED;
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
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
                return ChangeStatus.CHANGED;
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
                return ChangeStatus.INVALID_DATA;
            }
        }
        return ChangeStatus.INVALID_DATA;
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
            HealthRecordUtils.highlightActivityFields(getActivity(), toHighlight, true);
        if (notToHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(getActivity(), notToHighlight, false);
        if (!res) {
            Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    public void pickUpdateBirthdate()
    {
        if (personId == 0) return;
        if (dataSource == null) return;
        Calendar today = Calendar.getInstance();
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(getActivity(), birthdateET, HealthRecordUtils.stringToCalendar(person.getBirthdate()), null, today);
        dfrag.show(getFragmentManager(),"Update Birthdate");
    }

}
