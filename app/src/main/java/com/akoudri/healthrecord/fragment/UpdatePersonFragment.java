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

import com.akoudri.healthrecord.activity.EditPersonActivity;
import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.BloodType;
import com.akoudri.healthrecord.data.Gender;
import com.akoudri.healthrecord.data.Person;
import com.akoudri.healthrecord.utils.DatePickerFragment;


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

    public void updatePerson()
    {
        if (personId == 0) return;
        //FIXME: check values before inserting
        //FIXME: check changes before saving -> change the state of the save button consequently
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
        dataSource.getPersonTable().updatePerson(person.getId(), name,
                gender, ssn, bt, birthdate);
        //TODO change the state of the save button
    }

    public void pickUpdateBirthdate(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(getActivity(), birthdateET);
        dfrag.show(getFragmentManager(),"Update Birthdate");
    }

}
