package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Measure;
import com.akoudri.healthrecord.data.CranialPerimeterMeasure;
import com.akoudri.healthrecord.data.GlucoseMeasure;
import com.akoudri.healthrecord.data.HeartMeasure;
import com.akoudri.healthrecord.data.SizeMeasure;
import com.akoudri.healthrecord.data.TemperatureMeasure;
import com.akoudri.healthrecord.data.WeightMeasure;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;
import com.akoudri.healthrecord.utils.HourPickerFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ali Koudri on 25/04/14.
 */
public class EditMeasureActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;

    private static final int CREATION_MODE = 0;
    private static final int EDITION_MODE = 1;

    private int measureType = 0;

    private GridLayout glayout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private LinearLayout hlayout;
    private LinearLayout llayout;

    private TextView choiceTV; //Date or Type Text View
    private Spinner measureSpinner;
    private EditText dateET;
    private EditText hourET;
    private ImageButton dateButton, hourButton;

    private int personId;
    private int day, month, year;
    private String selectedDate;

    private Measure measure;
    private int measureId;
    private int measureIdType;

    private EditText wET, sET, tET, cpET, gEt, sysET, diaET, hbET;

    private final int margin = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_measure);
        dataSource = HealthRecordDataSource.getInstance(this);
        glayout = (GridLayout) findViewById(R.id.add_measure_grid);
        hlayout = (LinearLayout) findViewById(R.id.measure_hour_layout);
        initHourLayout();
        personId = getIntent().getIntExtra("personId", 0);
        measureId = getIntent().getIntExtra("measureId", 0);
        measureIdType = getIntent().getIntExtra("measureIdType", 0);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
        if (measureId == 0)
            setEditionMode(CREATION_MODE);
        else
            setEditionMode(EDITION_MODE);
    }

    private void initHourLayout()
    {
        LinearLayout.LayoutParams llparams;
        //Date Text View
        choiceTV = new TextView(this);
        choiceTV.setText(getResources().getString(R.string.hour));
        choiceTV.setTextColor(getResources().getColor(R.color.regular_text_color));
        choiceTV.setMinEms(3);
        choiceTV.setMaxEms(3);
        choiceTV.setTypeface(null, Typeface.BOLD);
        llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparams.gravity = Gravity.CENTER_VERTICAL;
        llparams.bottomMargin = margin;
        llparams.leftMargin = margin;
        llparams.topMargin = margin;
        llparams.rightMargin = margin;
        choiceTV.setLayoutParams(llparams);
        hlayout.addView(choiceTV);
        //Date Edit Text
        hourET = new EditText(this);//TODO: disable direct edition
        hourET.setMinEms(5);
        hourET.setMaxEms(5);
        hourET.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
        hourET.setBackgroundColor(getResources().getColor(android.R.color.white));
        hourET.setKeyListener(null);
        llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparams.gravity = Gravity.CENTER_VERTICAL;
        llparams.bottomMargin = margin;
        llparams.leftMargin = margin;
        llparams.topMargin = margin;
        llparams.rightMargin = margin;
        hourET.setLayoutParams(llparams);
        hlayout.addView(hourET);
        //Date Image Button
        hourButton = new ImageButton(this);
        hourButton.setBackgroundResource(R.drawable.rv);
        hourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HourPickerFragment hfrag = new HourPickerFragment();
                if (measureId == 0) {
                    hfrag.init(EditMeasureActivity.this, hourET);
                }
                else {
                    String[] h = hourET.getText().toString().split(":");
                    int hour = Integer.parseInt(h[0]);
                    int min = Integer.parseInt(h[1]);
                    hfrag.init(EditMeasureActivity.this, hourET, hour, min);
                }
                hfrag.show(getFragmentManager(), "Measure Hour Picker");
            }
        });
        llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparams.gravity = Gravity.CENTER_VERTICAL;
        llparams.bottomMargin = margin;
        llparams.leftMargin = margin;
        llparams.topMargin = margin;
        llparams.rightMargin = margin;
        hourButton.setLayoutParams(llparams);
        hlayout.addView(hourButton);
    }

    private void setEditionMode(int editionMode)
    {
        LinearLayout.LayoutParams llparams;
        if (editionMode == EDITION_MODE)
        {
            llayout = (LinearLayout) findViewById(R.id.measure_date_layout);
            //Date Text View
            choiceTV = new TextView(this);
            choiceTV.setText(getResources().getString(R.string.date));
            choiceTV.setTextColor(getResources().getColor(R.color.regular_text_color));
            choiceTV.setMinEms(3);
            choiceTV.setMaxEms(3);
            choiceTV.setTypeface(null, Typeface.BOLD);
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.CENTER_VERTICAL;
            llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            llparams.topMargin = margin;
            llparams.rightMargin = margin;
            choiceTV.setLayoutParams(llparams);
            llayout.addView(choiceTV);
            //Date Edit Text
            dateET = new EditText(this);//TODO: disable direct edition
            dateET.setMinEms(5);
            dateET.setMaxEms(5);
            dateET.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);
            dateET.setBackgroundColor(getResources().getColor(android.R.color.white));
            dateET.setKeyListener(null);
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.CENTER_VERTICAL;
            llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            llparams.topMargin = margin;
            llparams.rightMargin = margin;
            dateET.setLayoutParams(llparams);
            llayout.addView(dateET);
            //Date Image Button
            dateButton = new ImageButton(this);
            dateButton.setBackgroundResource(R.drawable.calendar);
            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerFragment dpf = new DatePickerFragment();
                    dpf.init(EditMeasureActivity.this, dateET, HealthRecordUtils.stringToCalendar(measure.getDate()), null, null);
                    dpf.show(EditMeasureActivity.this.getFragmentManager(), "measure date picker");
                }
            });
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.CENTER_VERTICAL;
            llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            llparams.topMargin = margin;
            llparams.rightMargin = margin;
            dateButton.setLayoutParams(llparams);
            llayout.addView(dateButton);
        } else {
            llayout = (LinearLayout) findViewById(R.id.type_choice_layout);
            //Type Text View
            choiceTV = new TextView(this);
            choiceTV.setText(getResources().getString(R.string.type));
            choiceTV.setTextColor(getResources().getColor(R.color.regular_text_color));
            choiceTV.setMinEms(3);
            choiceTV.setMaxEms(3);
            choiceTV.setTypeface(null, Typeface.BOLD);
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.CENTER_VERTICAL;
            llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            llparams.topMargin = margin;
            llparams.rightMargin = margin;
            choiceTV.setLayoutParams(llparams);
            llayout.addView(choiceTV);
            //Measure Spinner
            measureSpinner = new Spinner(this);
            String[] measureChoices = getResources().getStringArray(R.array.measures);
            ArrayAdapter<String> measureChoicesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, measureChoices);
            measureSpinner.setAdapter(measureChoicesAdapter);
            measureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    measureType = i;
                    createWidgets();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //Nothing to do
                }
            });
            llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llparams.gravity = Gravity.CENTER_VERTICAL;
            llparams.bottomMargin = margin;
            llparams.leftMargin = margin;
            llparams.topMargin = margin;
            llparams.rightMargin = margin;
            measureSpinner.setLayoutParams(llparams);
            llayout.addView(measureSpinner);
        }
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (measureId == 0 && (personId == 0 || day <= 0 || month <= 0 || year <= 0)) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            if (measureId != 0)
            {
                switch (measureIdType)
                {
                    case 1:
                        measure = dataSource.getWeightMeasureTable().getMeasureWithId(measureId);
                        break;
                    case 2:
                        measure = dataSource.getSizeMeasureTable().getMeasureWithId(measureId);
                        break;
                    case 3:
                        measure = dataSource.getTempMeasureTable().getMeasureWithId(measureId);
                        break;
                    case 4:
                        measure = dataSource.getCpMeasureTable().getMeasureWithId(measureId);
                        break;
                    case 5:
                        measure = dataSource.getGlucoseMeasureTable().getMeasureWithId(measureId);
                        break;
                    case 6:
                        measure = dataSource.getHeartMeasureTable().getMeasureWithId(measureId);
                        break;
                }
                measureType = measureIdType;
                createWidgets();
                fillWidgets();
            }
            else {
                Calendar c = Calendar.getInstance();
                String hour = String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                hourET.setText(hour);
                createWidgets();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if ((measureId == 0 || measureIdType == 0) && (personId == 0 || day <= 0 || month <= 0 || year <= 0)) return;
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    private void createWidgets()
    {
        switch (measureType)
        {
            case 0:
                glayout.removeAllViews();
                break;
            case 1:
                createWeightWidgets();
                break;
            case 2:
                createSizeWidgets();
                break;
            case 3:
                createTemperatureWidgets();
                break;
            case 4:
                createCpWidgets();
                break;
            case 5:
                createGlucoseWidgets();
                break;
            default:
                createHeartWidgets();
        }
    }

    //Called only when editing existing measure
    private void fillWidgets()
    {
        dateET.setText(measure.getDate());
        hourET.setText(measure.getHour());
        switch (measureType)
        {
            case 1:
                fillWeightWidgets();
                break;
            case 2:
                fillSizeWidgets();
                break;
            case 3:
                fillTemperatureWidgets();
                break;
            case 4:
                fillCpWidgets();
                break;
            case 5:
                fillGlucoseWidgets();
                break;
            case 6:
                fillHeartWidgets();
        }
    }

    private void createWeightWidgets()
    {
        glayout.removeAllViews();
        glayout.setColumnCount(2);
        glayout.setRowCount(1);
        rowSpec = GridLayout.spec(0);
        colSpec = GridLayout.spec(0);
        TextView label = new TextView(this);
        label.setText(getResources().getString(R.string.weight));
        label.setTextColor(getResources().getColor(R.color.regular_text_color));
        label.setTextSize(16);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.LEFT);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        label.setLayoutParams(params);
        glayout.addView(label);
        colSpec = GridLayout.spec(1);
        wET = new EditText(this);
        wET.setMinEms(3);
        wET.setMaxEms(3);
        wET.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        wET.setLayoutParams(params);
        glayout.addView(wET);
    }

    private void fillWeightWidgets()
    {
        WeightMeasure m = (WeightMeasure) measure;
        wET.setText(m.getValue() + "");
    }

    private void createSizeWidgets()
    {
        glayout.removeAllViews();
        glayout.setColumnCount(2);
        glayout.setRowCount(1);
        rowSpec = GridLayout.spec(0);
        colSpec = GridLayout.spec(0);
        TextView label = new TextView(this);
        label.setText(getResources().getString(R.string.size));
        label.setTextColor(getResources().getColor(R.color.regular_text_color));
        label.setTextSize(16);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.LEFT);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        label.setLayoutParams(params);
        glayout.addView(label);
        colSpec = GridLayout.spec(1);
        sET = new EditText(this);
        sET.setMinEms(3);
        sET.setMaxEms(3);
        sET.setInputType(InputType.TYPE_CLASS_NUMBER);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        sET.setLayoutParams(params);
        glayout.addView(sET);
    }
    private void fillSizeWidgets()
    {
        SizeMeasure m = (SizeMeasure) measure;
        sET.setText(m.getValue() + "");
    }


    private void createTemperatureWidgets()
    {
        glayout.removeAllViews();
        glayout.setColumnCount(2);
        glayout.setRowCount(1);
        rowSpec = GridLayout.spec(0);
        colSpec = GridLayout.spec(0);
        TextView label = new TextView(this);
        label.setText(getResources().getString(R.string.temperature));
        label.setTextColor(getResources().getColor(R.color.regular_text_color));
        label.setTextSize(16);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.LEFT);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        label.setLayoutParams(params);
        glayout.addView(label);
        colSpec = GridLayout.spec(1);
        tET = new EditText(this);
        tET.setMinEms(3);
        tET.setMaxEms(3);
        tET.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        tET.setLayoutParams(params);
        glayout.addView(tET);
    }

    private void fillTemperatureWidgets()
    {
        TemperatureMeasure m = (TemperatureMeasure) measure;
        tET.setText(m.getValue() + "");
    }

    private void createCpWidgets()
    {
        glayout.removeAllViews();
        glayout.setColumnCount(2);
        glayout.setRowCount(1);
        rowSpec = GridLayout.spec(0);
        colSpec = GridLayout.spec(0);
        TextView label = new TextView(this);
        label.setText(getResources().getString(R.string.cp));
        label.setTextColor(getResources().getColor(R.color.regular_text_color));
        label.setTextSize(16);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.LEFT);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        label.setLayoutParams(params);
        glayout.addView(label);
        colSpec = GridLayout.spec(1);
        cpET = new EditText(this);
        cpET.setMinEms(3);
        cpET.setMaxEms(3);
        cpET.setInputType(InputType.TYPE_CLASS_NUMBER);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        cpET.setLayoutParams(params);
        glayout.addView(cpET);
    }

    private void fillCpWidgets()
    {
        CranialPerimeterMeasure m = (CranialPerimeterMeasure) measure;
        cpET.setText(m.getValue() + "");
    }

    private void createGlucoseWidgets()
    {
        glayout.removeAllViews();
        glayout.setColumnCount(2);
        glayout.setRowCount(1);
        rowSpec = GridLayout.spec(0);
        colSpec = GridLayout.spec(0);
        TextView label = new TextView(this);
        label.setText(getResources().getString(R.string.glucose));
        label.setTextColor(getResources().getColor(R.color.regular_text_color));
        label.setTextSize(16);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.LEFT);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        label.setLayoutParams(params);
        glayout.addView(label);
        colSpec = GridLayout.spec(1);
        gEt = new EditText(this);
        gEt.setMinEms(3);
        gEt.setMaxEms(3);
        gEt.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        gEt.setLayoutParams(params);
        glayout.addView(gEt);
    }

    private void fillGlucoseWidgets()
    {
        GlucoseMeasure m = (GlucoseMeasure) measure;
        gEt.setText(m.getValue() + "");
    }

    private void createHeartWidgets()
    {
        glayout.removeAllViews();
        glayout.setColumnCount(2);
        glayout.setRowCount(3);
        //diastolic
        rowSpec = GridLayout.spec(0);
        colSpec = GridLayout.spec(0);
        TextView label = new TextView(this);
        label.setText(getResources().getString(R.string.diastolic));
        label.setTextColor(getResources().getColor(R.color.regular_text_color));
        label.setTextSize(16);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.LEFT);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        label.setLayoutParams(params);
        glayout.addView(label);
        colSpec = GridLayout.spec(1);
        diaET = new EditText(this);
        diaET.setMinEms(3);
        diaET.setMaxEms(3);
        diaET.setInputType(InputType.TYPE_CLASS_NUMBER);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        diaET.setLayoutParams(params);
        glayout.addView(diaET);
        //systolic
        rowSpec = GridLayout.spec(1);
        colSpec = GridLayout.spec(0);
        label = new TextView(this);
        label.setText(getResources().getString(R.string.systolic));
        label.setTextColor(getResources().getColor(R.color.regular_text_color));
        label.setTextSize(16);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.LEFT);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        label.setLayoutParams(params);
        glayout.addView(label);
        colSpec = GridLayout.spec(1);
        sysET = new EditText(this);
        sysET.setMinEms(3);
        sysET.setMaxEms(3);
        sysET.setInputType(InputType.TYPE_CLASS_NUMBER);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        sysET.setLayoutParams(params);
        glayout.addView(sysET);
        //heartbeat
        rowSpec = GridLayout.spec(2);
        colSpec = GridLayout.spec(0);
        label = new TextView(this);
        label.setText(getResources().getString(R.string.heartbeat));
        label.setTextColor(getResources().getColor(R.color.regular_text_color));
        label.setTextSize(16);
        label.setTypeface(null, Typeface.BOLD);
        label.setGravity(Gravity.LEFT);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        label.setLayoutParams(params);
        glayout.addView(label);
        colSpec = GridLayout.spec(1);
        hbET = new EditText(this);
        hbET.setMinEms(3);
        hbET.setMaxEms(3);
        hbET.setInputType(InputType.TYPE_CLASS_NUMBER);
        params = new GridLayout.LayoutParams(rowSpec, colSpec);
        params.rightMargin = margin;
        params.leftMargin = margin;
        params.topMargin = margin;
        params.bottomMargin = margin;
        params.setGravity(Gravity.LEFT);
        hbET.setLayoutParams(params);
        glayout.addView(hbET);
    }

    private void fillHeartWidgets()
    {
        HeartMeasure m = (HeartMeasure) measure;
        diaET.setText(m.getDiastolic() + "");
        sysET.setText(m.getSystolic() + "");
        hbET.setText(m.getHeartbeat() + "");
    }

    public void saveMeasure(View view)
    {
        if ((measureId == 0 || measureIdType == 0) && (personId == 0 || day <= 0 || month <= 0 || year <= 0)) return;
        if (!dataSourceLoaded) return;
        switch (measureType)
        {
            case 0:
                return;
            case 1:
                saveWeightMeasure();
                break;
            case 2:
                saveSizeMeasure();
                break;
            case 3:
                saveTemperatureMeasure();
                break;
            case 4:
                saveCpMeasure();
                break;
            case 5:
                saveGlucoseMeasure();
                break;
            default:
                saveHeartMeasure();
        }
    }

    private void saveWeightMeasure()
    {
        String weight = wET.getText().toString();
        String hour = hourET.getText().toString();
        if (!weight.matches("1?\\d{1,2}(\\.\\d{1,2})?")) {
            HealthRecordUtils.highlightActivityFields(this, wET);
            Toast toast = Toast.makeText(this, getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (measureId == 0) {
            dataSource.getWeightMeasureTable().insertMeasure(personId, selectedDate, hour, Double.parseDouble(weight));
            Toast.makeText(this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        }
        else {
            //TODO: ensure change occurred
            WeightMeasure m = (WeightMeasure) measure;
            m.setHour(hour);
            m.setValue(Double.parseDouble(weight));
            dataSource.getWeightMeasureTable().updateMeasure(m);
            Toast.makeText(this, getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void saveSizeMeasure()
    {
        String size = sET.getText().toString();
        String hour = hourET.getText().toString();
        if (!size.matches("(1|2)?\\d{2}")) {
            HealthRecordUtils.highlightActivityFields(this, sET);
            Toast toast = Toast.makeText(this, getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (measureId == 0) {
            dataSource.getSizeMeasureTable().insertMeasure(personId, selectedDate, hour, Integer.parseInt(size));
            Toast.makeText(this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        }
        else {
            SizeMeasure m = (SizeMeasure) measure;
            m.setHour(hour);
            m.setValue(Integer.parseInt(size));
            dataSource.getSizeMeasureTable().updateMeasure(m);
            Toast.makeText(this, getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void saveTemperatureMeasure()
    {
        String hour = hourET.getText().toString();
        String temperature = tET.getText().toString();
        if (!temperature.matches("(3[5-9]|4[0-1])(\\.\\d{1})?")) {
            HealthRecordUtils.highlightActivityFields(this, tET);
            Toast toast = Toast.makeText(this, getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (measureId == 0) {
            dataSource.getTempMeasureTable().insertMeasure(personId, selectedDate, hour, Double.parseDouble(temperature));
            Toast.makeText(this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        } else {
            TemperatureMeasure m = (TemperatureMeasure) measure;
            m.setHour(hour);
            m.setValue(Double.parseDouble(temperature));
            dataSource.getTempMeasureTable().updateMeasure(m);
            Toast.makeText(this, getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void saveCpMeasure()
    {
        String cp = cpET.getText().toString();
        String hour = hourET.getText().toString();
        if (!cp.matches("[3-5]\\d")) {
            HealthRecordUtils.highlightActivityFields(this, cpET);
            Toast toast = Toast.makeText(this, getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (measureId == 0) {
            dataSource.getCpMeasureTable().insertMeasure(personId, selectedDate, hour, Integer.parseInt(cp));
            Toast.makeText(this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        } else {
            CranialPerimeterMeasure m = (CranialPerimeterMeasure) measure;
            m.setHour(hour);
            m.setValue(Integer.parseInt(cp));
            dataSource.getCpMeasureTable().updateMeasure(m);
            Toast.makeText(this, getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void saveGlucoseMeasure()
    {
        String glucose = gEt.getText().toString();
        String hour = hourET.getText().toString();
        if (!glucose.matches("1?\\d{1,2}(\\.\\d{1,2})?")) {
            HealthRecordUtils.highlightActivityFields(this, gEt);
            Toast toast = Toast.makeText(this, getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (measureId == 0) {
            dataSource.getGlucoseMeasureTable().insertMeasure(personId, selectedDate, hour, Double.parseDouble(glucose));
            Toast.makeText(this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        } else {
            GlucoseMeasure m = (GlucoseMeasure) measure;
            m.setHour(hour);
            m.setValue(Double.parseDouble(glucose));
            dataSource.getGlucoseMeasureTable().updateMeasure(m);
            Toast.makeText(this, getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void saveHeartMeasure()
    {
        String hour = hourET.getText().toString();
        boolean res = true;
        List<EditText> toHighlight = new ArrayList<EditText>();
        List<EditText> notToHighlight = new ArrayList<EditText>();
        //check dia
        String dia = diaET.getText().toString();
        boolean checkDia = (dia.matches("1?\\d"));
        res = res && checkDia;
        if (!checkDia) toHighlight.add(diaET);
        else notToHighlight.add(diaET);
        //check sys
        String sys = sysET.getText().toString();
        boolean checkSys = (sys.matches("1?\\d"));
        res = res && checkSys;
        if (!checkSys) toHighlight.add(sysET);
        else notToHighlight.add(sysET);
        //check hb
        String hb = hbET.getText().toString();
        boolean checkHB = (hb.matches("1?\\d{2}"));
        res = res && checkHB;
        if (!checkHB) toHighlight.add(hbET);
        else notToHighlight.add(hbET);
        //display
        if (toHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(this, toHighlight, true);
        if (notToHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(this, notToHighlight, false);
        if (!res) {
            Toast toast = Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (measureId == 0) {
            dataSource.getHeartMeasureTable().insertMeasure(personId, selectedDate, hour,
                    Integer.parseInt(dia), Integer.parseInt(sys), Integer.parseInt(hb));
            Toast.makeText(this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        } else {
            HeartMeasure m = (HeartMeasure) measure;
            m.setDate(dateET.getText().toString());
            m.setHour(hour);
            m.setDiastolic(Integer.parseInt(dia));
            m.setSystolic(Integer.parseInt(sys));
            m.setHeartbeat(Integer.parseInt(hb));
            dataSource.getHeartMeasureTable().updateMeasure(m);
            Toast.makeText(this, getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
