package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Ailment;
import com.akoudri.healthrecord.data.Illness;
import com.akoudri.healthrecord.utils.DatePickerFragment;

import java.sql.SQLException;
import java.util.List;


public class EditAilmentActivity extends Activity {

    private AutoCompleteTextView illnessActv;
    private EditText beginMedicET, endMedicET;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
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
        beginMedicET = (EditText) findViewById(R.id.edit_begin_medic);
        endMedicET = (EditText) findViewById(R.id.edit_end_medic);
        ailmentId = getIntent().getIntExtra("ailmentId", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ailmentId == 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            ailment = dataSource.getAilmentTable().getAilmentWithId(ailmentId);
            fillWidgets();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (ailmentId == 0) return;
        if (dataSourceLoaded) {
            dataSource.close();
            dataSourceLoaded = false;
        }
    }

    private void fillWidgets()
    {
        retrieveIllnesses();
        int illnessId = ailment.getIllnessId();
        illnessActv.setText(dataSource.getIllnessTable().getIllnessWithId(illnessId).getName());
        beginMedicET.setText(ailment.getStartDate());
        endMedicET.setText(ailment.getEndDate());
        //TODO: set comment also
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

    public void updateAilment(View view)
    {
        if (ailmentId == 0) return;
        if (!dataSourceLoaded) return;
        //TODO: check values
        String illness = illnessActv.getText().toString();
        int illnessId = dataSource.getIllnessTable().getIllnessId(illness);
        if (illnessId < 0)
        {
            illnessId = (int) dataSource.getIllnessTable().insertIllness(illness);
        }
        ailment.setIllnessId(illnessId);
        ailment.setStartDate(beginMedicET.getText().toString());
        ailment.setEndDate(endMedicET.getText().toString());
        dataSource.getAilmentTable().updateAilment(ailment);
        finish();
    }

    public void editBeginAilmentPickerDialog(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, beginMedicET);
        dfrag.show(getFragmentManager(), "Edit Begin Ailment Date");
    }

    public void editEndAilmentPickerDialog(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, endMedicET);
        dfrag.show(getFragmentManager(),"Edit End Ailment Date");
    }

}
