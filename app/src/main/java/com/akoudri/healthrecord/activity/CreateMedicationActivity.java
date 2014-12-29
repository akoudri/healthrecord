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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//TODO: voir si il est possible d'utiliser le code bar du médicament
//STATUS: checked
public class CreateMedicationActivity extends Activity {

    private AutoCompleteTextView medicationActv;
    private Spinner freqSpinner;
    private EditText timesET, beginMedicET, endMedicET;

    private HealthRecordDataSource dataSource;
    private int personId;
    private String selectedDate;
    private boolean dataSourceLoaded = false;
    private List<Drug> drugs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_medication);
        dataSource = HealthRecordDataSource.getInstance(this);
        personId = getIntent().getIntExtra("personId", 0);
        selectedDate = getIntent().getStringExtra("date");
        medicationActv = (AutoCompleteTextView) findViewById(R.id.medication_add);
        freqSpinner = (Spinner) findViewById(R.id.freq_add);
        String[] freqChoice = getResources().getStringArray(R.array.freqChoice);
        ArrayAdapter<String> freqChoiceAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, freqChoice);
        freqSpinner.setAdapter(freqChoiceAdapter);
        freqSpinner.setSelection(1);
        timesET = (EditText) findViewById(R.id.times_medic);
        beginMedicET = (EditText) findViewById(R.id.begin_medic);
        beginMedicET.setKeyListener(null);
        endMedicET = (EditText) findViewById(R.id.end_medic);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (selectedDate == null) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            beginMedicET.setText(selectedDate);
            retrieveDrugs();
        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (selectedDate == null) return;
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

    public void addMedication(View view)
    {
        if (!dataSourceLoaded) return;
        String name = medicationActv.getText().toString();
        String timesStr = timesET.getText().toString();
        if (!checkFields(name, timesStr))
            return;
        int drugId = dataSource.getDrugTable().getDrugId(name);
        if (drugId < 0)
        {
            drugId = (int) dataSource.getDrugTable().insertDrug(name);
        }
        int freq = Integer.parseInt(timesStr);
        int kfreq = freqSpinner.getSelectedItemPosition();
        String sDate = beginMedicET.getText().toString();
        String d = endMedicET.getText().toString();
        int duration = (d.equals(""))?-1:Integer.parseInt(d) - 1;
        if (personId == 0) {
            Intent data = new Intent();
            data.putExtra("drugId", drugId);
            data.putExtra("freq", freq);
            data.putExtra("kfreq", kfreq);
            data.putExtra("sDate", sDate);
            data.putExtra("duration", duration);
            setResult(RESULT_OK, data);
        } else {
            Medication m = new Medication();
            m.setPersonId(personId);
            m.setAilmentId(0);
            m.setDrugId(drugId);
            m.setFrequency(freq);
            m.setKind(HealthRecordUtils.int2kind(kfreq));
            m.setStartDate(sDate);
            m.setDuration(duration);
            dataSource.getMedicationTable().insertMedication(m);
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

    public void showBeginMedicPickerDialog(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        Calendar c = HealthRecordUtils.stringToCalendar(selectedDate);
        dfrag.init(this, beginMedicET, c, c, null);
        dfrag.show(getFragmentManager(),"Pick Start Treatment Date");
    }

    public void scanBarCode(View view)
    {
        //Toast.makeText(getApplicationContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
        IntentIntegrator integrator = new IntentIntegrator(CreateMedicationActivity.this);
        integrator.addExtra("SCAN_WIDTH", 800);
        integrator.addExtra("SCAN_HEIGHT", 200);
        integrator.addExtra("RESULT_DISPLAY_DURATION_MS", 3000L);
        integrator.addExtra("PROMPT_MESSAGE", "Custom prompt to scan a product");
        integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
    }

    public void scanQRCode(View view)
    {
        //Toast.makeText(getApplicationContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
        IntentIntegrator integrator = new IntentIntegrator(CreateMedicationActivity.this);
        integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
    }

    @Override
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
    if (result != null) {
      String contents = result.getContents();
      if (contents != null) {
          Toast.makeText(getApplicationContext(), "Scan succeeded", Toast.LENGTH_SHORT).show();
      } else {
          Toast.makeText(getApplicationContext(), "Scan failed", Toast.LENGTH_SHORT).show();
      }
    }
  }

}
