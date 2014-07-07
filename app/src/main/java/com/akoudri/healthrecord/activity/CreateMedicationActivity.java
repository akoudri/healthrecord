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

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Drug;
import com.akoudri.healthrecord.utils.DatePickerFragment;

import java.sql.SQLException;
import java.util.List;

//TODO: voir si il est possible d'utiliser le code bar du médicament
public class CreateMedicationActivity extends Activity {

    private AutoCompleteTextView medicationActv;
    private Spinner freqSpinner;
    private EditText timesET, beginMedicET, endMedicET;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private List<Drug> drugs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_medication);
        dataSource = new HealthRecordDataSource(this);
        medicationActv = (AutoCompleteTextView) findViewById(R.id.medication_add);
        freqSpinner = (Spinner) findViewById(R.id.freq_add);
        String[] freqChoice = getResources().getStringArray(R.array.freqChoice);
        ArrayAdapter<String> freqChoiceAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, freqChoice);
        freqSpinner.setAdapter(freqChoiceAdapter);
        freqSpinner.setSelection(1);
        timesET = (EditText) findViewById(R.id.times_medic);
        beginMedicET = (EditText) findViewById(R.id.begin_medic);
        endMedicET = (EditText) findViewById(R.id.end_medic);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dataSource.open();
            dataSourceLoaded = true;
            retrieveDrugs();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dataSourceLoaded) {
            dataSource.close();
            dataSourceLoaded = false;
        }
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

    public void addMedication(View view)
    {
        if (!dataSourceLoaded) return;
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
        Intent data = new Intent();
        data.putExtra("drugId", drugId);
        data.putExtra("freq", freq);
        data.putExtra("kfreq", kfreq);
        data.putExtra("sDate", sDate);
        data.putExtra("eDate", eDate);
        setResult(RESULT_OK, data);
        finish();
    }

    public void showBeginMedicPickerDialog(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, beginMedicET);
        dfrag.show(getFragmentManager(),"Pick Start Treatment Date");
    }

    public void showEndMedicPickerDialog(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, endMedicET);
        dfrag.show(getFragmentManager(),"Pick End Treatment Date");
    }

}
