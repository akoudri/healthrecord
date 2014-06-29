package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Ailment;
import com.akoudri.healthrecord.data.DoseFrequencyKind;
import com.akoudri.healthrecord.data.Illness;
import com.akoudri.healthrecord.data.IllnessTable;
import com.akoudri.healthrecord.data.Medication;
import com.akoudri.healthrecord.data.MedicationTable;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranchTable;
import com.akoudri.healthrecord.data.Treatment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//FIXME: check the consistency of the code
public class EditTreatmentActivity extends Activity {

    private Spinner illnessAct;
    private Spinner therapistSpinner;
    private EditText startDateET, endDateET;
    private CheckBox isPermanent;
    private LinearLayout medicsLayout;
    private HealthRecordDataSource dataSource;
    private int treatmentId;
    private String selectedDate;
    private int day, month, year;
    private List<Ailment> ailments;
    private List<Illness> illnesses;
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
        illnessAct = (Spinner) findViewById(R.id.edit_illness_choice);
        therapistSpinner = (Spinner) findViewById(R.id.edit_therapist_choice);
        startDateET = (EditText) findViewById(R.id.edit_start_treatment);
        endDateET = (EditText) findViewById(R.id.edit_end_treatment);
        isPermanent = (CheckBox) findViewById(R.id.edit_checkbox_permanent);
        medicsLayout = (LinearLayout) findViewById(R.id.edit_medics_layout);
        treatmentId = getIntent().getIntExtra("treatmentId", 1);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
    }

    private void retrieveAilments()
    {
        //FIXME: consider using the creation of a view into the database
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
        illnessAct.setAdapter(illnessAdapter);
        int ailmentIdx = ailments.indexOf(ailment);
        if (ailmentIdx > 0) illnessAct.setSelection(ailmentIdx);
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

    private void retrieveMedics()
    {
        existingMedications = dataSource.getMedicationTable().getMedicationsForTreatment(treatmentId);
    }

    private void populateMedics()
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
                                    populateMedics();
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
                    DoseFrequencyKind kind = medic.getKind();
                    switch(kind)
                    {
                        case HOUR:
                            intent.putExtra("kind", 0); break;
                        case DAY:
                            intent.putExtra("kind", 1); break;
                        case WEEK:
                            intent.putExtra("kind", 2); break;
                        case MONTH:
                            intent.putExtra("kind", 3); break;
                        case YEAR:
                            intent.putExtra("kind", 4); break;
                        default:
                            intent.putExtra("kind", 5);
                    }
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
                                    populateMedics();
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
    protected void onResume() {
        super.onResume();
        //FIXME: Manage the case where data source could not be opened
        try {
            dataSource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        treatment = dataSource.getTreatmentTable().getTreatmentWithId(treatmentId);
        startDateET.setText(treatment.getStartDate());
        endDateET.setText(treatment.getEndDate());
        isPermanent.setChecked(treatment.isPermanent());
        retrieveAilments();
        retrieveTherapists();
        retrieveMedics();
        populateMedics();
    }

    public void editAddMedic(View view)
    {
        //TODO:Manage the case where there is no data received -> start activity with no result
        startActivityForResult(new Intent("com.akoudri.healthrecord.app.CreateMedication"), 1);
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
                switch (kfreq)
                {
                    case 0:
                        m.setKind(DoseFrequencyKind.HOUR); break;
                    case 1:
                        m.setKind(DoseFrequencyKind.DAY); break;
                    case 2:
                        m.setKind(DoseFrequencyKind.WEEK); break;
                    case 3:
                        m.setKind(DoseFrequencyKind.MONTH); break;
                    case 4:
                        m.setKind(DoseFrequencyKind.YEAR); break;
                    default:
                        m.setKind(DoseFrequencyKind.LIFE);
                }
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
                switch (kfreq)
                {
                    case 0:
                        m.setKind(DoseFrequencyKind.HOUR); break;
                    case 1:
                        m.setKind(DoseFrequencyKind.DAY); break;
                    case 2:
                        m.setKind(DoseFrequencyKind.WEEK); break;
                    case 3:
                        m.setKind(DoseFrequencyKind.MONTH); break;
                    case 4:
                        m.setKind(DoseFrequencyKind.YEAR); break;
                    default:
                        m.setKind(DoseFrequencyKind.LIFE);
                }
                m.setStartDate(data.getStringExtra("sDate"));
                m.setEndDate(data.getStringExtra("eDate"));
            }
        }
    }

    public void updateTreatment(View view)
    {
        //TODO: store treatment into db
        //It is necessary to guarantee the consistency of the dates before inserting into db
        //FIXME: check values - add comment field
        Ailment a = ailments.get(illnessAct.getSelectedItemPosition());
        Therapist t = therapists.get(therapistSpinner.getSelectedItemPosition());
        boolean permanent = isPermanent.isChecked();
        String sDate = startDateET.getText().toString();
        String eDate = endDateET.getText().toString();
        treatment.setAilmentId(a.getId());
        treatment.setTherapistId(t.getId());
        treatment.setPermanent(permanent);
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

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    public void editStartTreatmentPickerDialog(View view)
    {
        EditTreatmentDatePickerFragment dfrag = new EditTreatmentDatePickerFragment();
        dfrag.setBdet(endDateET);
        dfrag.show(getFragmentManager(),"StartTreatmentDatePicker");
    }

    public void editEndTreatmentPickerDialog(View view)
    {
        EditTreatmentDatePickerFragment dfrag = new EditTreatmentDatePickerFragment();
        dfrag.setBdet(endDateET);
        dfrag.show(getFragmentManager(),"EndTreatmentDatePicker");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO
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

    //FIXME: restrict the choice of a date for both start date and end date
    public static class EditTreatmentDatePickerFragment extends DialogFragment
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
