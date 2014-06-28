package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Illness;

import java.sql.SQLException;
import java.util.List;


public class CreateAilmentActivity extends Activity {

    private AutoCompleteTextView illnessActv;
    private CheckBox chronicCb;
    private HealthRecordDataSource dataSource;
    private int personId;
    private int date, month, year;
    private String selectedDate;
    private List<Illness> illnesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_ailment);
        dataSource = new HealthRecordDataSource(this);
        illnessActv = (AutoCompleteTextView) findViewById(R.id.illness_add);
        chronicCb = (CheckBox) findViewById(R.id.checkbox_chronic);
        personId = getIntent().getIntExtra("personId", 0);
        date = getIntent().getIntExtra("date", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        selectedDate = String.format("%02d/%02d/%04d", date, month + 1, year);
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
        retrieveIllnesses();
    }

    public void addAilment(View view)
    {
        //TODO: store ailment into db
        //insertAilment(int personId, int illnessId, boolean isChronic, String startDate, String endDate, String comment)
        String illness = illnessActv.getText().toString();
        int illnessId = dataSource.getIllnessTable().getIllnessId(illness);
        if (illnessId < 0)
        {
            illnessId = (int) dataSource.getIllnessTable().insertIllness(illness);
        }
        boolean isChronic = chronicCb.isChecked();
        dataSource.getAilmentTable().insertAilment(personId, illnessId, isChronic, selectedDate, null, "no comment");
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

}
