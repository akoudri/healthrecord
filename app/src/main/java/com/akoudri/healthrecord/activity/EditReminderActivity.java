package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Drug;
import com.akoudri.healthrecord.data.Reminder;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//STATUS: checked
public class EditReminderActivity extends Activity {

    private LinearLayout dLayout;
    private TextView dateTV;
    private ImageButton dateButton;

    private AutoCompleteTextView medET;
    private EditText dateET, commentET;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;

    private int personId;
    private int day, month, year;
    private String selectedDate;

    private int reminderId;
    private Reminder reminder;

    private List<Drug> drugs;

    private int margin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_reminder);
        dLayout = (LinearLayout) findViewById(R.id.reminder_date_layout);
        medET = (AutoCompleteTextView) findViewById(R.id.med_actv_edit);
        commentET = (EditText) findViewById(R.id.edit_reminder_comment);
        dataSource = HealthRecordDataSource.getInstance(this);
        margin = (int) HealthRecordUtils.convertPixelsToDp(2, this);
        //Existing appointment
        reminderId = getIntent().getIntExtra("reminderId", 0);
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
        if (reminderId == 0 && (personId == 0 || day < 1 || month < 0 || year < 0)) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            retrieveDrugs();
            if (reminderId != 0) {
                initDateLayout();
                reminder = dataSource.getReminderTable().getReminderWithId(reminderId);
                fillWidgets();
            } else {
                selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
            }
        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (reminderId == 0) return;
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    private void fillWidgets()
    {
        dateET.setText(reminder.getDate());
        commentET.setText(reminder.getComment());
    }

    public void saveReminder(View view)
    {
        if (reminderId == 0 && (personId == 0 || day < 1 || month < 0 || year < 0)) return;
        if (!dataSourceLoaded) return;
        String comment = commentET.getText().toString();
        if (reminderId != 0) {
            String dayStr = dateET.getText().toString();
            if (comment.equals("")) comment = null;
            //TODO: retrieve drug id and replace "1" value
            Reminder rem = new Reminder(reminder.getPersonId(), 1, dayStr, comment);
            if (reminder.equalsTo(rem)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_change), Toast.LENGTH_SHORT).show();
                return;
            }
            rem.setId(reminderId);
            boolean res = dataSource.getReminderTable().updateReminder(rem);
            if (res) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
                finish();
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
        }
        else {
            if (checkFields(comment)) {
                //TODO: retrieve drug id and replace "1" value
                dataSource.getReminderTable().insertReminder(personId, 1, selectedDate, comment);
                finish();
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private boolean checkFields(String comment)
    {
        boolean res = true;
        List<EditText> toHighlight = new ArrayList<EditText>();
        List<EditText> notToHighlight = new ArrayList<EditText>();
        boolean checkComment = (!comment.equals(""));
        res = res && checkComment;
        if (!checkComment) toHighlight.add(commentET);
        else notToHighlight.add(commentET);
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
        dateTV.setText(getResources().getString(R.string.date));
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
                if (reminderId == 0) {
                    dfrag.init(EditReminderActivity.this, dateET);
                }
                else {
                    Calendar c = Calendar.getInstance();
                    dfrag.init(EditReminderActivity.this, dateET, HealthRecordUtils.stringToCalendar(reminder.getDate()), null, c);
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

    private void retrieveDrugs()
    {
        drugs = dataSource.getDrugTable().getAllDrugs();
        String[] drugsStr = new String[drugs.size()];
        int i = 0;
        for (Drug drug : drugs)
        {
            drugsStr[i++] = drug.getName();
        }
        ArrayAdapter<String> drugsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, drugsStr);
        medET.setThreshold(1);
        medET.setAdapter(drugsAdapter);
    }

}
