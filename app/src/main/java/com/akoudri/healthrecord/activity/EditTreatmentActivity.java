package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

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
import com.akoudri.healthrecord.data.Treatment;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//FIXME: check the consistency of the code
public class EditTreatmentActivity extends Activity {

    private Spinner illnessSpinner;
    private Spinner therapistSpinner;
    private EditText startDateET, endDateET;
    private LinearLayout medicsLayout;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int treatmentId;
    private String selectedDate;
    private int day, month, year;
    private List<Ailment> ailments;
    private List<Therapist> therapists;
    private List<Medication> existingMedications;
    private List<Medication> medications;
    private Treatment treatment;

    private int reqCreateCode = 1;
    private int reqEditCode = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_treatment);
        dataSource = new HealthRecordDataSource(this);
        medications = new ArrayList<Medication>();
        illnessSpinner = (Spinner) findViewById(R.id.edit_illness_choice);
        therapistSpinner = (Spinner) findViewById(R.id.edit_therapist_choice);
        startDateET = (EditText) findViewById(R.id.edit_start_treatment);
        endDateET = (EditText) findViewById(R.id.edit_end_treatment);
        medicsLayout = (LinearLayout) findViewById(R.id.edit_medics_layout);
        treatmentId = getIntent().getIntExtra("treatmentId", 0);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (treatmentId == 0 || day <= 0 || month <= 0 || year <= 0)
            return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            treatment = dataSource.getTreatmentTable().getTreatmentWithId(treatmentId);
            existingMedications = dataSource.getMedicationTable().getMedicationsForTreatment(treatmentId);
            fillWidgets();
            createWidgets();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (treatmentId == 0 || day <= 0 || month <= 0 || year <= 0)
            return;
        if (dataSourceLoaded) {
            dataSource.close();
            dataSourceLoaded = false;
        }
    }

    private void fillWidgets()
    {
        selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
        startDateET.setText(treatment.getStartDate());
        endDateET.setText(treatment.getEndDate());
        retrieveAilments();
        retrieveTherapists();
    }

    private void retrieveAilments()
    {
        int personId = treatment.getPersonId();
        ailments = new ArrayList<Ailment>();
        List<Ailment> allAilments = dataSource.getAilmentTable().getDayAilmentsForPerson(personId, selectedDate);
        List<Treatment> existingTreatments = dataSource.getTreatmentTable().getDayTreatmentsForPerson(personId, selectedDate);
        int ailmentId;
        boolean found = false;
        for(Ailment ailment : allAilments)
        {
            ailmentId = ailment.getId();
            for(Treatment treatment : existingTreatments)
            {
                if (treatment.getAilmentId() == ailmentId) {
                    found = true;
                    break;
                }
            }
            if (!found) ailments.add(ailment);
            else found = false;
        }
        Ailment ailment = dataSource.getAilmentTable().getAilmentWithId(treatment.getAilmentId());
        ailments.add(ailment);
        String[] ailmentStr = new String[ailments.size()];
        IllnessTable illnessTable = dataSource.getIllnessTable();
        Illness illness;
        int i = 0;
        for (Ailment a : ailments)
        {
            illness = illnessTable.getIllnessWithId(a.getIllnessId());
            ailmentStr[i++] = illness.getName() + "-" + a.getStartDate();
        }
        ArrayAdapter<String> illnessAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ailmentStr);
        illnessSpinner.setAdapter(illnessAdapter);
        int ailmentIdx = ailments.indexOf(ailment);
        if (ailmentIdx > 0) illnessSpinner.setSelection(ailmentIdx);
    }

    private void retrieveTherapists()
    {
        int personId = treatment.getPersonId();
        List<Integer> therapistsId = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
        therapists = new ArrayList<Therapist>();
        TherapistTable thTable = dataSource.getTherapistTable();
        for (Integer i : therapistsId)
        {
           therapists.add(thTable.getTherapistWithId(i));
        }
        String[] therapistStr = new String[therapists.size()];
        TherapyBranchTable brTable = dataSource.getTherapyBranchTable();
        String branch;
        int i = 0;
        for (Therapist t : therapists)
        {
            branch = brTable.getBranchWithId(t.getBranchId()).getName();
            therapistStr[i++] = t.getName() + "-" + branch;
        }
        ArrayAdapter<String> thAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, therapistStr);
        therapistSpinner.setAdapter(thAdapter);
        Therapist therapist = dataSource.getTherapistTable().getTherapistWithId(treatment.getTherapistId());
        int thIndex = therapists.indexOf(therapist);
        if (thIndex >= 0) therapistSpinner.setSelection(thIndex);
    }

    private void createWidgets()
    {
        medicsLayout.removeAllViews();
        LinearLayout linearLayout;
        LinearLayout.LayoutParams llparams;
        int margin = 10;
        Button editButton;
        ImageButton removeButton;
        for (final Medication medic : existingMedications)
        {
            //Linear Layout
            linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            medicsLayout.addView(linearLayout);
            //Text View
            editButton = new Button(this);
            final String name = dataSource.getDrugTable().getDrugWithId(medic.getDrugId()).getName();
            editButton.setText(name);
            editButton.setTextSize(16);
            editButton.setMinEms(8);
            editButton.setMaxEms(8);
            editButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            editButton.setBackgroundResource(R.drawable.healthrecord_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.akoudri.healthrecord.app.EditMedication");
                    intent.putExtra("stored", true);
                    intent.putExtra("medicationId", medic.getId());
                    startActivity(intent);
                }
            });
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.LEFT|Gravity.CENTER;
            //llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            //llparams.topMargin = margin;
            llparams.rightMargin = margin;
            //FIXME: arbitrary value, see LinearLayout API
            llparams.weight = 0.25f;
            editButton.setLayoutParams(llparams);
            linearLayout.addView(editButton);
            //Remove Button
            removeButton = new ImageButton(this);
            removeButton.setBackgroundResource(R.drawable.remove);
            removeButton.setOnClickListener(new View.OnClickListener() {
                //FIXME: R.string.yes/no -> getResources().getString(...)
                //also in other files
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(EditTreatmentActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.removing)
                            .setMessage(getResources().getString(R.string.remove_question) + " " + name + " ?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    existingMedications.remove(medic);
                                    dataSource.getMedicationTable().removeMedicWithId(medic.getId());
                                    createWidgets();
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
            });
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //FIXME: arbitrary value, see LinearLayout API
            llparams.weight = 0.75f;
            llparams.gravity = Gravity.LEFT|Gravity.CENTER;
            //llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            //llparams.topMargin = margin;
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
            editButton.setText(name);
            editButton.setTextSize(16);
            editButton.setMinEms(8);
            editButton.setMaxEms(8);
            editButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            editButton.setBackgroundResource(R.drawable.healthrecord_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.akoudri.healthrecord.app.EditMedication");
                    intent.putExtra("pos",pos);
                    intent.putExtra("stored", false);
                    intent.putExtra("treatmentId", medic.getTreatmentId());
                    intent.putExtra("drugId", medic.getDrugId());
                    intent.putExtra("frequency", medic.getFrequency());
                    intent.putExtra("kind", medic.getKind().ordinal());
                    intent.putExtra("startDate", medic.getStartDate());
                    intent.putExtra("endDate", medic.getEndDate());
                    startActivityForResult(intent, reqEditCode);
                }
            });
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.LEFT|Gravity.CENTER;
            //llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            //llparams.topMargin = margin;
            llparams.rightMargin = margin;
            //FIXME: arbitrary value, see LinearLayout API
            llparams.weight = 0.25f;
            editButton.setLayoutParams(llparams);
            linearLayout.addView(editButton);
            //Remove Button
            removeButton = new ImageButton(this);
            removeButton.setBackgroundResource(R.drawable.remove);
            removeButton.setOnClickListener(new View.OnClickListener() {
                //FIXME: R.string.yes/no -> getResources().getString(...)
                //also in other files
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(EditTreatmentActivity.this)
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
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //FIXME: arbitrary value, see LinearLayout API
            llparams.weight = 0.75f;
            llparams.gravity = Gravity.LEFT|Gravity.TOP;
            llparams.gravity = Gravity.LEFT|Gravity.CENTER;
            //llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            //llparams.topMargin = margin;
            llparams.rightMargin = margin;
            removeButton.setLayoutParams(llparams);
            linearLayout.addView(removeButton);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //TODO:Manage the case where there is no data received
        if (requestCode == reqCreateCode)
        {
            if (resultCode == RESULT_OK)
            {
                Medication m = new Medication();
                m.setDrugId(data.getIntExtra("drugId", 1));
                m.setFrequency(data.getIntExtra("freq", 1));
                int kfreq = data.getIntExtra("kfreq", 1);
                m.setKind(HealthRecordUtils.int2kind(kfreq));
                m.setStartDate(data.getStringExtra("sDate"));
                m.setEndDate(data.getStringExtra("eDate"));
                medications.add(m);
            }
        }
        if (requestCode == reqEditCode)
        {
            if (resultCode == RESULT_OK)
            {
                int pos = data.getIntExtra("pos", 0);
                Medication m = medications.get(pos);
                m.setDrugId(data.getIntExtra("drugId", 1));
                m.setFrequency(data.getIntExtra("freq", 1));
                int kfreq = data.getIntExtra("kfreq", 1);
                m.setKind(HealthRecordUtils.int2kind(kfreq));
                m.setStartDate(data.getStringExtra("sDate"));
                m.setEndDate(data.getStringExtra("eDate"));
            }
        }
    }

    public void editAddMedic(View view)
    {
        if (treatmentId == 0 || day <= 0 || month <= 0 || year <= 0)
            return;
        if (!dataSourceLoaded) return;
        //TODO:Manage the case where there is no data received -> start activity with no result
        startActivityForResult(new Intent("com.akoudri.healthrecord.app.CreateMedication"), 1);
    }

    public void updateTreatment(View view)
    {
        if (treatmentId == 0 || day <= 0 || month <= 0 || year <= 0)
            return;
        if (!dataSourceLoaded) return;
        //FIXME: check values - add comment field
        Ailment a = ailments.get(illnessSpinner.getSelectedItemPosition());
        Therapist t = therapists.get(therapistSpinner.getSelectedItemPosition());
        String sDate = startDateET.getText().toString();
        String eDate = endDateET.getText().toString();
        treatment.setAilmentId(a.getId());
        treatment.setTherapistId(t.getId());
        treatment.setStartDate(sDate);
        treatment.setEndDate(eDate);
        dataSource.getTreatmentTable().updateTreatment(treatment);
        MedicationTable table = dataSource.getMedicationTable();
        for (Medication m : medications)
        {
            m.setTreatmentId(treatmentId);
            table.insertMedication(m);
        }
        finish();
    }

    public void editStartTreatmentPickerDialog(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, endDateET);
        dfrag.show(getFragmentManager(), "Select Start Treatment Date");
    }

    public void editEndTreatmentPickerDialog(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, endDateET);
        dfrag.show(getFragmentManager(),"Select End Treatment Date");
    }

}
