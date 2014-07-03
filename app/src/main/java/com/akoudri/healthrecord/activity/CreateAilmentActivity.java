package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Illness;

import java.sql.SQLException;
import java.util.List;


public class CreateAilmentActivity extends Activity {

    private AutoCompleteTextView illnessActv;
    
    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;
    private int day, month, year;
    private String selectedDate;
    private List<Illness> illnesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_ailment);
        dataSource = new HealthRecordDataSource(this);
        illnessActv = (AutoCompleteTextView) findViewById(R.id.illness_add);
        personId = getIntent().getIntExtra("personId", 0);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0 || day <= 0 || month <= 0 || year <= 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
            retrieveIllnesses();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (personId == 0 || day <= 0 || month <= 0 || year <= 0) return;
        if (dataSourceLoaded)
        {
            dataSource.close();
            dataSourceLoaded = false;
        }
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

    public void addAilment(View view)
    {
        if (personId == 0 || day <= 0 || month <= 0 || year <= 0) return;
        if (!dataSourceLoaded) return;
        //TODO: check values
        String illness = illnessActv.getText().toString();
        int illnessId = dataSource.getIllnessTable().getIllnessId(illness);
        if (illnessId < 0)
        {
            illnessId = (int) dataSource.getIllnessTable().insertIllness(illness);
        }
        dataSource.getAilmentTable().insertAilment(personId, illnessId, selectedDate, null, "no comment");
        finish();
    }

}
