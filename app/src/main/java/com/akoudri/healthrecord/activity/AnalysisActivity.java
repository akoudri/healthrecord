package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.utils.DatePickerFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AnalysisActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;

    private Spinner measureSpinner;
    private EditText startET, endET;
    private EditText nbPointsET;

    //TODO: Use to activate / deactivate display of charts
    int nbWeightMeasures, nbSizeMeasures, nbTemperatureMeasures, nbCpMeasures, nbGlucoseMeasures, nbHeartMeasures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_analysis);
        personId = getIntent().getIntExtra("personId", 0);
        dataSource = HealthRecordDataSource.getInstance(this);
        measureSpinner = (Spinner) findViewById(R.id.measure_choice);
        String[] measureChoices = getResources().getStringArray(R.array.measures);
        ArrayAdapter<String> measureChoicesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, measureChoices);
        measureSpinner.setAdapter(measureChoicesAdapter);
        startET = (EditText) findViewById(R.id.start_measure);
        startET.setKeyListener(null);
        endET = (EditText) findViewById(R.id.end_measure);
        endET.setKeyListener(null);
        nbPointsET = (EditText) findViewById(R.id.nb_points);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            nbWeightMeasures = dataSource.getWeightMeasureTable().getTotalMeasureCountForPerson(personId);
            nbSizeMeasures = dataSource.getSizeMeasureTable().getTotalMeasureCountForPerson(personId);
            nbTemperatureMeasures = dataSource.getTempMeasureTable().getTotalMeasureCountForPerson(personId);
            nbCpMeasures = dataSource.getCpMeasureTable().getTotalMeasureCountForPerson(personId);
            nbGlucoseMeasures = dataSource.getGlucoseMeasureTable().getTotalMeasureCountForPerson(personId);
            nbHeartMeasures = dataSource.getHeartMeasureTable().getTotalMeasureCountForPerson(personId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initSpinner()
    {


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (personId == 0) return;
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    public void setAnalysisStartDate(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, startET);
        dfrag.show(getFragmentManager(),"Pick Analysis Start Date");
    }

    public void setAnalysisEndDate(View view)
    {
        DatePickerFragment dfrag = new DatePickerFragment();
        dfrag.init(this, endET);
        dfrag.show(getFragmentManager(),"Pick Analysis End Date");
    }

}
