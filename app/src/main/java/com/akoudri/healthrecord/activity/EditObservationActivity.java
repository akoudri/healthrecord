package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.MedicalObservation;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;
import com.akoudri.healthrecord.utils.HourPickerFragment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EditObservationActivity extends Activity {

    private LinearLayout dLayout, hLayout;
    private TextView dateTV, hourTV;
    private ImageButton dateButton, hourButton;

    private EditText dateET, hourET, descET;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;

    private int personId;
    private int day, month, year;
    private String selectedDate;

    private int obsId;
    private MedicalObservation observation;

    private final int margin = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_observation);
        dLayout = (LinearLayout) findViewById(R.id.obs_date_layout);
        hLayout = (LinearLayout) findViewById(R.id.obs_hour_layout);
        descET = (EditText) findViewById(R.id.edit_obs_desc);
        dataSource = HealthRecordDataSource.getInstance(this);
        //Existing appointment
        obsId = getIntent().getIntExtra("obsId", 0);
        //New appointment
        personId = getIntent().getIntExtra("personId", 0);
        day = getIntent().getIntExtra("day", 0);
        month = getIntent().getIntExtra("month", 0);
        year = getIntent().getIntExtra("year", 0);
        selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (obsId == 0 && (personId == 0 || day <= 0 || month <= 0 || year <= 0)) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            initHourLayout();
            if (obsId != 0) {
                initDateLayout();
                observation = dataSource.getMedicalObservationTable().getMedicalObservationWithId(obsId);
                fillWidgets();
            } else {
                selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
                Calendar c = Calendar.getInstance();
                String hour = String.format("%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
                hourET.setText(hour);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (obsId == 0) return;
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    private void fillWidgets()
    {
        dateET.setText(observation.getDate());
        hourET.setText(observation.getHour());
        descET.setText(observation.getDescription());
    }

    public void saveObservation(View view)
    {
        if (obsId == 0 && (personId == 0 || day <= 0 || month <= 0 || year <= 0)) return;
        if (!dataSourceLoaded) return;
        String hourStr = hourET.getText().toString();
        String desc = descET.getText().toString();
        if (obsId != 0) {
            String dayStr = dateET.getText().toString();
            if (desc.equals("")) desc = null;
            MedicalObservation obs = new MedicalObservation(personId, dayStr, hourStr, desc);
            if (observation.equalsTo(obs)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_change), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            obs.setId(obsId);
            boolean res = dataSource.getMedicalObservationTable().updateMedicalObservation(obs);
            if (res) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
                finish();
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
        }
        else {
            if (checkFields(hourStr, desc)) {
                dataSource.getMedicalObservationTable().insertMedicalObservation(personId, selectedDate, hourStr, desc);
                finish();
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private boolean checkFields(String hour, String desc)
    {
        boolean res = true;
        List<EditText> toHighlight = new ArrayList<EditText>();
        List<EditText> notToHighlight = new ArrayList<EditText>();
        boolean checkHour = (!hour.equals(""));
        res = res && checkHour;
        if (!checkHour) toHighlight.add(hourET);
        else notToHighlight.add(hourET);
        boolean checkDesc = (!desc.equals(""));
        res = res && checkDesc;
        if (!checkDesc) toHighlight.add(descET);
        else notToHighlight.add(descET);
        //display
        if (toHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(this, toHighlight, true);
        if (notToHighlight.size() > 0)
            HealthRecordUtils.highlightActivityFields(this, notToHighlight, false);
        if (!res) {
            Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
        }
        return res;
    }

    private void initDateLayout()
    {
        LinearLayout.LayoutParams llparams;
        //Date Text View
        dateTV = new TextView(this);
        dateTV.setText(getResources().getString(R.string.hour));
        dateTV.setTextColor(getResources().getColor(R.color.regular_text_color));
        dateTV.setMinEms(3);
        dateTV.setMaxEms(3);
        dateTV.setTypeface(null, Typeface.BOLD);
        llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparams.gravity = Gravity.CENTER_VERTICAL;
        llparams.bottomMargin = margin;
        llparams.leftMargin = margin;
        llparams.topMargin = margin;
        llparams.rightMargin = margin;
        dateTV.setLayoutParams(llparams);
        dLayout.addView(dateTV);
        //Date Edit Text
        dateET = new EditText(this);
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
        dLayout.addView(dateET);
        //Date Image Button
        dateButton = new ImageButton(this);
        dateButton.setBackgroundResource(R.drawable.calendar);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment dfrag = new DatePickerFragment();
                if (obsId == 0) {
                    //TODO
                    dfrag.init(EditObservationActivity.this, dateET);
                }
                else {
                    dfrag.init(EditObservationActivity.this, dateET, HealthRecordUtils.stringToCalendar(observation.getDate()), null, null);
                }
                dfrag.show(getFragmentManager(), "Appointment Date Picker");
            }
        });
        llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparams.gravity = Gravity.CENTER_VERTICAL;
        llparams.bottomMargin = margin;
        llparams.leftMargin = margin;
        llparams.topMargin = margin;
        llparams.rightMargin = margin;
        dateButton.setLayoutParams(llparams);
        dLayout.addView(dateButton);
    }

    private void initHourLayout()
    {
        LinearLayout.LayoutParams llparams;
        //Hour Text View
        hourTV = new TextView(this);
        hourTV.setText(getResources().getString(R.string.hour));
        hourTV.setTextColor(getResources().getColor(R.color.regular_text_color));
        hourTV.setMinEms(3);
        hourTV.setMaxEms(3);
        hourTV.setTypeface(null, Typeface.BOLD);
        llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparams.gravity = Gravity.CENTER_VERTICAL;
        llparams.bottomMargin = margin;
        llparams.leftMargin = margin;
        llparams.topMargin = margin;
        llparams.rightMargin = margin;
        hourTV.setLayoutParams(llparams);
        hLayout.addView(hourTV);
        //Date Edit Text
        hourET = new EditText(this);
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
        hLayout.addView(hourET);
        //Date Image Button
        hourButton = new ImageButton(this);
        hourButton.setBackgroundResource(R.drawable.rv);
        hourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HourPickerFragment hfrag = new HourPickerFragment();
                if (obsId == 0) {
                    //TODO
                    hfrag.init(EditObservationActivity.this, hourET);
                }
                else {
                    String[] h = observation.getHour().split(":");
                    int hour = Integer.parseInt(h[0]);
                    int min = Integer.parseInt(h[1]);
                    hfrag.init(EditObservationActivity.this, hourET, hour, min);
                }
                hfrag.show(getFragmentManager(), "Appointment Hour Picker");
            }
        });
        llparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparams.gravity = Gravity.CENTER_VERTICAL;
        llparams.bottomMargin = margin;
        llparams.leftMargin = margin;
        llparams.topMargin = margin;
        llparams.rightMargin = margin;
        hourButton.setLayoutParams(llparams);
        hLayout.addView(hourButton);
    }

}
