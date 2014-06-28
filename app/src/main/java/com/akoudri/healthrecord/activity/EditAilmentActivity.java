package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Ailment;
import com.akoudri.healthrecord.data.Illness;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;


public class EditAilmentActivity extends Activity {

    private AutoCompleteTextView illnessActv;
    private CheckBox chronicCb;
    private EditText beginMedicET, endMedicET;
    private HealthRecordDataSource dataSource;
    private int ailmentId;
    private Ailment ailment;
    private List<Illness> illnesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_ailment);
        dataSource = new HealthRecordDataSource(this);
        illnessActv = (AutoCompleteTextView) findViewById(R.id.illness_edit);
        chronicCb = (CheckBox) findViewById(R.id.edit_checkbox_chronic);
        beginMedicET = (EditText) findViewById(R.id.edit_begin_medic);
        endMedicET = (EditText) findViewById(R.id.edit_end_medic);
        ailmentId = getIntent().getIntExtra("ailmentId", 1);
    }

    private void retrieveIllnesses()
    {
        illnesses = dataSource.getIllnessTable().getAllIllnesses();
        String[] illnessesStr = new String[illnesses.size()];
        int i = 0;
        for (Illness illness : illnesses)
        {
            illnessesStr[i++] = illness.getName();
        }
        ArrayAdapter<String> illnessesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, illnessesStr);
        illnessActv.setThreshold(1);
        illnessActv.setAdapter(illnessesAdapter);
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
        ailment = dataSource.getAilmentTable().getAilmentWithId(ailmentId);
        retrieveIllnesses();
        int illnessId = ailment.getIllnessId();
        illnessActv.setText(dataSource.getIllnessTable().getIllnessWithId(illnessId).getName());
        chronicCb.setChecked(ailment.isChronic());
        beginMedicET.setText(ailment.getStartDate());
        endMedicET.setText(ailment.getEndDate());
        //TODO: set comment also
    }

    public void updateAilment(View view)
    {
        //TODO: update ailment into db
        String illness = illnessActv.getText().toString();
        int illnessId = dataSource.getIllnessTable().getIllnessId(illness);
        if (illnessId < 0)
        {
            illnessId = (int) dataSource.getIllnessTable().insertIllness(illness);
        }
        ailment.setIllnessId(illnessId);
        ailment.setStartDate(beginMedicET.getText().toString());
        ailment.setEndDate(endMedicET.getText().toString());
        ailment.setChronic(chronicCb.isChecked());
        dataSource.getAilmentTable().updateAilment(ailment);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

    public void editBeginAilmentPickerDialog(View view)
    {
        AilmentDatePickerFragment dfrag = new AilmentDatePickerFragment();
        dfrag.setBdet(beginMedicET);
        dfrag.show(getFragmentManager(),"EditBeginAilmentDatePicker");
    }

    public void editEndAilmentPickerDialog(View view)
    {
        AilmentDatePickerFragment dfrag = new AilmentDatePickerFragment();
        dfrag.setBdet(endMedicET);
        dfrag.show(getFragmentManager(),"EditEndAilmentDatePicker");
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
    public static class AilmentDatePickerFragment extends DialogFragment
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
