package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Drug;
import com.akoudri.healthrecord.data.Medication;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//FIXME: voir si il est possible d'utiliser le code bar du médicament
public class EditMedicationActivity extends Activity {

    private AutoCompleteTextView medicationActv;
    private Spinner freqSpinner;
    private EditText timesET, beginMedicET, endMedicET;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int medicationId;
    private Medication medic;
    private boolean stored;
    private int pos = 0;
    private List<Drug> drugs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_medication);
        medicationId = getIntent().getIntExtra("medicationId", 0);
        stored = (medicationId != 0);
        dataSource = new HealthRecordDataSource(this);
        medicationActv = (AutoCompleteTextView) findViewById(R.id.medication_edit);
        freqSpinner = (Spinner) findViewById(R.id.edit_freq_add);
        String[] freqChoice = getResources().getStringArray(R.array.freqChoice);
        ArrayAdapter<String> freqChoiceAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, freqChoice);
        freqSpinner.setAdapter(freqChoiceAdapter);
        freqSpinner.setSelection(1);
        timesET = (EditText) findViewById(R.id.edit_times_medic);
        beginMedicET = (EditText) findViewById(R.id.edit_begin_medic);
        endMedicET = (EditText) findViewById(R.id.edit_end_medic);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dataSource.open();
            dataSourceLoaded = true;
            retrieveDrugs();
            retrieveMedic();
            fillWidgets();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    private void retrieveDrugs()
    {
        drugs = dataSource.getDrugTable().getAllDrugs();
        String[] drugsStr = new String[drugs.size()];
        int i = 0;
        for (Drug drug : drugs)
        {
            drugsStr[i++] = drug.getName();
        }
        ArrayAdapter<String> drugsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, drugsStr);
        medicationActv.setThreshold(1);
        medicationActv.setAdapter(drugsAdapter);
    }

    private void retrieveMedic()
    {
        if (stored) {
            medic = dataSource.getMedicationTable().getMedicationWithId(medicationId);
        }
        else
        {
            medic = new Medication();
            Intent intent = getIntent();
            pos = intent.getIntExtra("pos", 0);
            medic.setAilmentId(intent.getIntExtra("treatmentId", 0));
            medic.setDrugId(intent.getIntExtra("drugId", 0));
            medic.setFrequency(intent.getIntExtra("frequency", 0));
            int kind = intent.getIntExtra("kind", 0);
            medic.setKind(HealthRecordUtils.int2kind(kind));
            medic.setStartDate(intent.getStringExtra("startDate"));
            medic.setDuration(intent.getIntExtra("duration", -1));
        }
    }

    private void fillWidgets()
    {
        Drug drug = dataSource.getDrugTable().getDrugWithId(medic.getDrugId());
        medicationActv.setText(drug.getName());
        timesET.setText(medic.getFrequency() + "");
        freqSpinner.setSelection(medic.getKind().ordinal());
        beginMedicET.setText(medic.getStartDate());
        int d = medic.getDuration();
        if (d >= 0)
            endMedicET.setText(medic.getDuration()+"");
    }

    public void editAddMedication(View view)
    {
        if (!dataSourceLoaded) return;
        String name = medicationActv.getText().toString();
        String timesStr = timesET.getText().toString();
        if (!checkFields(name, timesStr))
        {
            Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        int drugId = dataSource.getDrugTable().getDrugId(name);
        if (drugId < 0)
        {
            drugId = (int) dataSource.getDrugTable().insertDrug(name);
        }
        int freq = Integer.parseInt(timesStr);
        int kfreq = freqSpinner.getSelectedItemPosition();
        String sDate = beginMedicET.getText().toString();
        String d = endMedicET.getText().toString();
        int duration = (d.equals(""))?-1:Integer.parseInt(d);
        if (stored)
        {
            medic.setDrugId(drugId);
            medic.setFrequency(freq);
            int kind = freqSpinner.getSelectedItemPosition();
            medic.setKind(HealthRecordUtils.int2kind(kind));
            medic.setStartDate(sDate);
            medic.setDuration(duration);
            dataSource.getMedicationTable().updateMedication(medic);
        }
        else
        {
            Intent data = new Intent();
            data.putExtra("pos", pos);
            data.putExtra("drugId", drugId);
            data.putExtra("freq", freq);
            data.putExtra("kfreq", kfreq);
            data.putExtra("sDate", sDate);
            data.putExtra("duration", duration);
            setResult(RESULT_OK, data);
        }
        finish();
    }

    private boolean checkFields(String name, String times)
    {
        boolean res = true;
        List<EditText> toHighlight = new ArrayList<EditText>();
        List<EditText> notToHighlight = new ArrayList<EditText>();
        //check name
        boolean checkName = (name != null && !name.equals(""));
        res = res && checkName;
        if (!checkName) toHighlight.add(medicationActv);
        else notToHighlight.add(medicationActv);
        //check times
        boolean checkTimes = (times != null && !times.equals(""));
        res = res && checkTimes;
        if (!checkTimes) toHighlight.add(timesET);
        else notToHighlight.add(timesET);
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

    public void editBeginMedicPickerDialog(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        Calendar c = HealthRecordUtils.stringToCalendar(medic.getStartDate());
        dfrag.init(this, beginMedicET, c, c, null);
        dfrag.show(getFragmentManager(),"Pick Medication Start Date");
    }

}
