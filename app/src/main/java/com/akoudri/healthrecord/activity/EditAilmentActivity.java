package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Ailment;
import com.akoudri.healthrecord.data.Illness;
import com.akoudri.healthrecord.data.IllnessTable;
import com.akoudri.healthrecord.data.Medication;
import com.akoudri.healthrecord.data.MedicationTable;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranchTable;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//STATUS: checked
public class EditAilmentActivity extends Activity {

    private AutoCompleteTextView illnessActv;
    private Spinner therapistSpinner;
    private EditText startDateET, endDateET;
    private LinearLayout medicsLayout;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int ailmentId;
    private String selectedDate;
    private int day, month, year;

    private List<Illness> illnesses;
    private List<Therapist> therapists;
    private List<Medication> existingMedications;
    private List<Medication> medications;
    private Ailment ailment;
    private int thPos = 0;
    private boolean removeOccurred = false;
    private boolean medicUpdated = false;

    private int reqCreateCode = 1;
    private int reqEditCode = 2;
    private int reqUpdateCode = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_ailment);
        dataSource = HealthRecordDataSource.getInstance(this);
        medications = new ArrayList<Medication>();
        illnessActv = (AutoCompleteTextView) findViewById(R.id.edit_illness_choice);
        therapistSpinner = (Spinner) findViewById(R.id.edit_therapist_choice);
        therapistSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                thPos = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nothing to do
            }
        });
        startDateET = (EditText) findViewById(R.id.edit_start_ailment);
        startDateET.setKeyListener(null);
        endDateET = (EditText) findViewById(R.id.edit_end_ailment);
        medicsLayout = (LinearLayout) findViewById(R.id.edit_medics_layout);
        ailmentId = getIntent().getIntExtra("ailmentId", 0);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ailmentId == 0 || day <= 0 || month <= 0 || year <= 0)
            return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            ailment = dataSource.getAilmentTable().getAilmentWithId(ailmentId);
            existingMedications = dataSource.getMedicationTable().getMedicationsForAilment(ailmentId);
            fillWidgets();
            createWidgets();
        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ailmentId == 0 || day <= 0 || month <= 0 || year <= 0)
            return;
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    private void fillWidgets()
    {
        startDateET.setText(ailment.getStartDate());
        int d = ailment.getDuration() + 1;
        if (d > 0)
            endDateET.setText(d + "");
        retrieveIllnesses();
        retrieveTherapists();
    }

    private void retrieveIllnesses()
    {
        int illnessId = ailment.getIllnessId();
        String illness = (illnessId==0)?"":dataSource.getIllnessTable().getIllnessWithId(illnessId).getName();
        illnesses = dataSource.getIllnessTable().getAllIllnesses();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, getIllnesses());
        illnessActv.setThreshold(1);
        illnessActv.setAdapter(adapter);
        illnessActv.setText(illness);
    }

    private String[] getIllnesses()
    {
        String[] res = new String[illnesses.size()];
        int i = 0;
        for (Illness ill : illnesses)
            res[i++] = ill.getName();
        return res;
    }

    private void retrieveTherapists()
    {
        int personId = ailment.getPersonId();
        List<Integer> therapistsId = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
        therapists = new ArrayList<Therapist>();
        TherapistTable thTable = dataSource.getTherapistTable();
        for (Integer i : therapistsId)
        {
           therapists.add(thTable.getTherapistWithId(i));
        }
        String[] therapistStr = new String[therapists.size()+1];
        therapistStr[0] = getResources().getString(R.string.self);
        TherapyBranchTable brTable = dataSource.getTherapyBranchTable();
        String branch;
        int i = 1;
        for (Therapist t : therapists)
        {
            branch = brTable.getBranchWithId(t.getBranchId()).getName();
            if (branch.length() > 10) branch = branch.substring(0,10);
            String tName = t.getName();
            if (tName.length() > 20) tName = tName.substring(0,20);
            therapistStr[i++] = tName + "-" + branch;
        }
        ArrayAdapter<String> thAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, therapistStr);
        therapistSpinner.setAdapter(thAdapter);
        int thId = ailment.getTherapistId();
        if (thId != 0)
        {
            i = 0;
            for (Therapist th : therapists)
            {
                if (th.getId() == thId)
                {
                    therapistSpinner.setSelection(i+1);
                    return;
                }
                i++;
            }
        }
    }

    private void createWidgets()
    {
        medicsLayout.removeAllViews();
        LinearLayout linearLayout;
        LinearLayout.LayoutParams llparams;
        int margin = (int) HealthRecordUtils.convertPixelsToDp(1, this);
        Button editButton;
        ImageButton removeButton;
        for (final Medication medic : existingMedications)
        {
            //Linear Layout
            linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            medicsLayout.addView(linearLayout);
            //Text View
            editButton = new Button(this);
            final String name = dataSource.getDrugTable().getDrugWithId(medic.getDrugId()).getName();
            String eName;
            if (name.length() > 20) eName = name.substring(0,20) + "...";
            else eName = name;
            editButton.setText(eName);
            editButton.setTextSize(16);
            editButton.setMinEms(10);
            editButton.setMaxEms(10);
            editButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            editButton.setTypeface(null, Typeface.BOLD);
            editButton.setBackgroundResource(R.drawable.healthrecord_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EditAilmentActivity.this, EditMedicationActivity.class);
                    intent.putExtra("medicationId", medic.getId());
                    startActivityForResult(intent, reqUpdateCode);
                }
            });
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.CENTER_VERTICAL;
            llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            llparams.topMargin = margin;
            llparams.rightMargin = margin;
            editButton.setLayoutParams(llparams);
            linearLayout.addView(editButton);
            //Remove Button
            removeButton = new ImageButton(this);
            removeButton.setBackgroundResource(R.drawable.remove);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(EditAilmentActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.removing)
                            .setMessage(getResources().getString(R.string.remove_question) + " " + name + " ?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    existingMedications.remove(medic);
                                    boolean r = dataSource.getMedicationTable().removeMedicWithId(medic.getId());
                                    if (r) removeOccurred = true;
                                    createWidgets();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
            });
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.CENTER_VERTICAL;
            llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            llparams.topMargin = margin;
            llparams.rightMargin = margin;
            removeButton.setLayoutParams(llparams);
            linearLayout.addView(removeButton);
        }
        for (final Medication medic : medications)
        {
            final int pos = medications.indexOf(medic);
            //Linear Layout
            linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            medicsLayout.addView(linearLayout);
            //Text View
            editButton = new Button(this);
            final String name = dataSource.getDrugTable().getDrugWithId(medic.getDrugId()).getName();
            String eName;
            if (name.length() > 20) eName = name.substring(0,20) + "...";
            else eName = name;
            editButton.setText(eName);
            editButton.setTextSize(16);
            editButton.setMinEms(10);
            editButton.setMaxEms(10);
            editButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            editButton.setTypeface(null, Typeface.BOLD);
            editButton.setBackgroundResource(R.drawable.healthrecord_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(EditAilmentActivity.this, EditMedicationActivity.class);
                    intent.putExtra("pos", pos);
                    intent.putExtra("medicationId", 0);
                    intent.putExtra("drugId", medic.getDrugId());
                    intent.putExtra("frequency", medic.getFrequency());
                    intent.putExtra("kind", medic.getKind().ordinal());
                    intent.putExtra("startDate", medic.getStartDate());
                    intent.putExtra("duration", medic.getDuration());
                    startActivityForResult(intent, reqEditCode);
                }
            });
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.CENTER_VERTICAL;
            llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            llparams.topMargin = margin;
            llparams.rightMargin = margin;
            editButton.setLayoutParams(llparams);
            linearLayout.addView(editButton);
            //Remove Button
            removeButton = new ImageButton(this);
            removeButton.setBackgroundResource(R.drawable.remove);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(EditAilmentActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.removing)
                            .setMessage(getResources().getString(R.string.remove_question) + " " + name + " ?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    medications.remove(medic);
                                    createWidgets();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
            });
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.CENTER_VERTICAL;
            llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            llparams.topMargin = margin;
            llparams.rightMargin = margin;
            removeButton.setLayoutParams(llparams);
            linearLayout.addView(removeButton);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == reqCreateCode)
        {
            if (resultCode == RESULT_OK)
            {
                //FIXME: manage the case where ids are not valid - change default values
                Medication m = new Medication();
                m.setPersonId(ailment.getPersonId());
                m.setAilmentId(ailment.getId());
                m.setDrugId(data.getIntExtra("drugId", 1));
                m.setFrequency(data.getIntExtra("freq", 1));
                int kfreq = data.getIntExtra("kfreq", 1);
                m.setKind(HealthRecordUtils.int2kind(kfreq));
                m.setStartDate(data.getStringExtra("sDate"));
                m.setDuration(data.getIntExtra("duration", -1));
                medications.add(m);
            }
        }
        if (requestCode == reqEditCode)
        {
            if (resultCode == RESULT_OK)
            {
                //FIXME: manage the case where ids are not valid - change default values
                int pos = data.getIntExtra("pos", 0);
                Medication m = medications.get(pos);
                m.setPersonId(ailment.getPersonId());
                m.setAilmentId(ailment.getId());
                m.setDrugId(data.getIntExtra("drugId", 1));
                m.setFrequency(data.getIntExtra("freq", 1));
                int kfreq = data.getIntExtra("kfreq", 1);
                m.setKind(HealthRecordUtils.int2kind(kfreq));
                m.setStartDate(data.getStringExtra("sDate"));
                m.setDuration(data.getIntExtra("duration", -1));
            }
        }
        if (requestCode == reqUpdateCode)
        {
            if (resultCode == RESULT_OK)
            {
                medicUpdated = data.getBooleanExtra("medic_updated", false);
            }
        }
    }

    public void editAddMedic(View view)
    {
        if (ailmentId == 0 || day <= 0 || month <= 0 || year <= 0)
            return;
        if (!dataSourceLoaded) return;
        Intent intent = new Intent(this, CreateMedicationActivity.class);
        intent.putExtra("date", selectedDate);
        startActivityForResult(intent, 1);
    }

    public void updateAilment(View view)
    {
        if (ailmentId == 0 || day <= 0 || month <= 0 || year <= 0)
            return;
        if (!dataSourceLoaded) return;
        IllnessTable illnessTable = dataSource.getIllnessTable();
        String illness = illnessActv.getText().toString();
        int illnessId = 0;
        if (! illness.equals("")) {
            illnessId = dataSource.getIllnessTable().getIllnessId(illness);
            if (illnessId < 0) {
                illnessId = (int) illnessTable.insertIllness(illness);
            }
        }
        Therapist t;
        if (thPos == 0)
            t = null;
        else
            t = therapists.get(thPos-1);
        String sDate = startDateET.getText().toString();
        int duration = -1;
        String d = endDateET.getText().toString();
        if (!d.equals("")) duration = Integer.parseInt(d) - 1;
        int thId = (t == null)?0:t.getId();
        Ailment a = new Ailment(ailment.getPersonId(), illnessId, thId, sDate, duration);
        if (ailment.equalsTo(a) && medicUpdated)
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (ailment.equalsTo(a) && medications.size() == 0 && !removeOccurred)
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_change), Toast.LENGTH_SHORT).show();
            removeOccurred = false;
            return;
        }
        a.setId(ailmentId);
        int res = dataSource.getAilmentTable().updateAilment(a);
        if (res == -1)
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.overlapping_ailment), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (res > 0) {
            MedicationTable table = dataSource.getMedicationTable();
            for (Medication m : medications) {
                table.insertMedication(m);
            }
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
            finish();
        }
        else Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
    }

    public void editStartAilmentPickerDialog(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        Calendar today = Calendar.getInstance();
        dfrag.init(this, startDateET, HealthRecordUtils.stringToCalendar(ailment.getStartDate()), null, today);
        dfrag.show(getFragmentManager(), "Select Start Ailment Date");
    }

}
