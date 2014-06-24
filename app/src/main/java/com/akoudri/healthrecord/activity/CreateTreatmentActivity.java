package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Ailment;
import com.akoudri.healthrecord.data.Illness;
import com.akoudri.healthrecord.data.IllnessTable;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranchTable;
import com.akoudri.healthrecord.data.Treatment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CreateTreatmentActivity extends Activity {

    private Spinner illnessAct;
    private Spinner therapistSpinner;
    private EditText endDateET;
    private CheckBox isPermanent;
    private LinearLayout medicsLayout;
    private HealthRecordDataSource dataSource;
    private int personId;
    private int date, month, year;
    private String selectedDate;
    private List<Ailment> ailments;
    private List<Illness> illnesses;
    private List<Therapist> therapists;
    private List<String> medics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_treatment);
        dataSource = new HealthRecordDataSource(this);
        medics = new ArrayList<String>();
        //FIXME: following lines are to remove
        medics.add("Medic 1");
        medics.add("Medic 2");
        medics.add("Medic 3");
        medics.add("Medic 4");
        medics.add("Medic Mon Medic 5");
        //-------------------
        illnessAct = (Spinner) findViewById(R.id.illness_choice);
        therapistSpinner = (Spinner) findViewById(R.id.therapist_choice);
        endDateET = (EditText) findViewById(R.id.end_treatment);
        isPermanent = (CheckBox) findViewById(R.id.checkbox_permanent);
        medicsLayout = (LinearLayout) findViewById(R.id.medics_layout);
        personId = getIntent().getIntExtra("personId", 0);
        date = getIntent().getIntExtra("date", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        selectedDate = String.format("%02d/%02d/%04d", date, month + 1, year);

    }

    private void retrieveAilments()
    {
        //FIXME: consider using the creation of a view into the database
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
        String[] ailmentStr = new String[ailments.size()];
        IllnessTable illnessTable = dataSource.getIllnessTable();
        Illness illness;
        int i = 0;
        for (Ailment a : ailments)
        {
            illness = illnessTable.getIllnessWithId(a.getIllnessId());
            ailmentStr[i++] = illness.getName() + " - " + a.getStartDate();
        }
        ArrayAdapter<String> illnessAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ailmentStr);
        illnessAct.setAdapter(illnessAdapter);
    }

    private void retrieveTherapists()
    {
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
            therapistStr[i++] = t.getName() + " - " + branch;
        }
        ArrayAdapter<String> thAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, therapistStr);
        therapistSpinner.setAdapter(thAdapter);
    }

    private void populateMedics()
    {
        medicsLayout.removeAllViews();
        LinearLayout linearLayout;
        LinearLayout.LayoutParams llparams;
        int margin = 10;
        TextView textView;
        ImageButton removeButton;
        for (String mStr : medics)
        {
            //Linear Layout
            linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            medicsLayout.addView(linearLayout);
            //Text View
            textView = new TextView(this);
            textView.setText(mStr);
            textView.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            textView.setTextSize(16);
            textView.setMaxEms(10);//FIXME: set the appropriate value
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.LEFT|Gravity.CENTER;
            //llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            //llparams.topMargin = margin;
            llparams.rightMargin = margin;
            //FIXME: arbitrary value, see LinearLayout API
            llparams.weight = 0.2f;
            textView.setLayoutParams(llparams);
            linearLayout.addView(textView);
            //Remove Button
            removeButton = new ImageButton(this);
            removeButton.setBackgroundResource(R.drawable.remove);
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //FIXME: arbitrary value, see LinearLayout API
            llparams.weight = 0.8f;
            llparams.gravity = Gravity.LEFT|Gravity.TOP;
            llparams.gravity = Gravity.LEFT|Gravity.CENTER;
            //llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            //llparams.topMargin = margin;
            llparams.rightMargin = margin;
            removeButton.setLayoutParams(llparams);
            linearLayout.addView(removeButton);
            //TODO: add listener
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
        retrieveAilments();
        retrieveTherapists();
        populateMedics();
    }

    public void addMedic(View view)
    {
        startActivity(new Intent("com.akoudri.healthrecord.app.CreateMedication"));
    }

    public void addTreatment(View view)
    {
        //TODO: store treatment into db
        //It is necessary to guarantee the consistency of the dates before inserting into db
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    public void showEndTreatmentPickerDialog(View view)
    {
        TreatmentDatePickerFragment dfrag = new TreatmentDatePickerFragment();
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
    public static class TreatmentDatePickerFragment extends DialogFragment
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
