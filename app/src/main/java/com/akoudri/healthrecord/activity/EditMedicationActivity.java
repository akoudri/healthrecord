package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.DoseFrequencyKind;
import com.akoudri.healthrecord.data.Drug;
import com.akoudri.healthrecord.data.Medication;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

//FIXME: voir si il est possible d'utiliser le code bar du médicament
public class EditMedicationActivity extends Activity {

    private AutoCompleteTextView medicationActv;
    private Spinner freqSpinner;
    private EditText timesET, beginMedicET, endMedicET;
    private HealthRecordDataSource dataSource;
    private Medication medic;
    private boolean stored;
    private int pos = 0;
    private List<Drug> drugs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_medication);
        stored = getIntent().getBooleanExtra("stored", false);
        dataSource = new HealthRecordDataSource(this);
        medicationActv = (AutoCompleteTextView) findViewById(R.id.medication_edit);
        freqSpinner = (Spinner) findViewById(R.id.edit_freq_add);
        String[] freqChoice = getResources().getStringArray(R.array.freqChoice);
        ArrayAdapter<String> freqChoiceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, freqChoice);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        retrieveDrugs();
        if (stored) {
            int medicationId = getIntent().getIntExtra("medicationId", 0);
            medic = dataSource.getMedicationTable().getMedicationWithId(medicationId);
        }
        else
        {
            medic = new Medication();
            Intent intent = getIntent();
            pos = intent.getIntExtra("pos", 0);
            medic.setTreatmentId(intent.getIntExtra("treatmentId", 0));
            medic.setDrugId(intent.getIntExtra("drugId", 0));
            medic.setFrequency(intent.getIntExtra("frequency", 0));
            int kind = intent.getIntExtra("kind", 0);
            switch(kind)
            {
                case 0:
                    medic.setKind(DoseFrequencyKind.HOUR); break;
                case 1:
                    medic.setKind(DoseFrequencyKind.DAY); break;
                case 2:
                    medic.setKind(DoseFrequencyKind.WEEK); break;
                case 3:
                    medic.setKind(DoseFrequencyKind.MONTH); break;
                case 4:
                    medic.setKind(DoseFrequencyKind.YEAR); break;
                default:
                    medic.setKind(DoseFrequencyKind.LIFE);
            }
            medic.setStartDate(intent.getStringExtra("startDate"));
            medic.setEndDate(intent.getStringExtra("endDate"));
        }
        Drug drug = dataSource.getDrugTable().getDrugWithId(medic.getDrugId());
        medicationActv.setText(drug.getName());
        timesET.setText(medic.getFrequency()+"");
        switch(medic.getKind())
        {
            case HOUR:
                freqSpinner.setSelection(0); break;
            case DAY:
                freqSpinner.setSelection(1); break;
            case WEEK:
                freqSpinner.setSelection(2); break;
            case MONTH:
                freqSpinner.setSelection(3); break;
            case YEAR:
                freqSpinner.setSelection(4); break;
            default:
                freqSpinner.setSelection(5);
        }
        beginMedicET.setText(medic.getStartDate());
        endMedicET.setText(medic.getEndDate());
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

    public void editAddMedication(View view)
    {
        //FIXME: manage null values
        String name = medicationActv.getText().toString();
        int drugId = dataSource.getDrugTable().getDrugId(name);
        if (drugId < 0)
        {
            drugId = (int) dataSource.getDrugTable().insertDrug(name);
        }
        int freq = Integer.parseInt(timesET.getText().toString());
        int kfreq = freqSpinner.getSelectedItemPosition();
        String sDate = beginMedicET.getText().toString();
        String eDate = endMedicET.getText().toString();
        if (stored)
        {
            medic.setDrugId(drugId);
            medic.setFrequency(freq);
            int kind = freqSpinner.getSelectedItemPosition();
            switch (kind)
            {
                case 0:
                    medic.setKind(DoseFrequencyKind.HOUR); break;
                case 1:
                    medic.setKind(DoseFrequencyKind.DAY); break;
                case 2:
                    medic.setKind(DoseFrequencyKind.WEEK); break;
                case 3:
                    medic.setKind(DoseFrequencyKind.MONTH); break;
                case 4:
                    medic.setKind(DoseFrequencyKind.YEAR); break;
                default:
                    medic.setKind(DoseFrequencyKind.LIFE);
            }
            medic.setStartDate(sDate);
            medic.setEndDate(eDate);
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
            data.putExtra("eDate", eDate);
            setResult(RESULT_OK, data);
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_person, menu);
        return true;
    }

    public void editBeginMedicPickerDialog(View view)
    {
        MedicationDatePickerFragment dfrag = new MedicationDatePickerFragment();
        dfrag.setBdet(beginMedicET);
        dfrag.show(getFragmentManager(),"BeginMedicationDatePicker");
    }

    public void editEndMedicPickerDialog(View view)
    {
        MedicationDatePickerFragment dfrag = new MedicationDatePickerFragment();
        dfrag.setBdet(endMedicET);
        dfrag.show(getFragmentManager(),"EndMedicationDatePicker");
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

    //FIXME: restrict the choice of a date for both start date and end date
    public static class MedicationDatePickerFragment extends DialogFragment
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
