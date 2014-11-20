package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Appointment;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;
import com.akoudri.healthrecord.utils.DatePickerFragment;
import com.akoudri.healthrecord.utils.HealthRecordUtils;
import com.akoudri.healthrecord.utils.HourPickerFragment;
import com.akoudri.healthrecord.utils.NotificationPublisher;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EditAppointmentActivity extends Activity {

    private LinearLayout dLayout, hLayout;
    private TextView dateTV, hourTV;
    private ImageButton dateButton, hourButton;

    private EditText dateET, hourET, commentET;
    private Spinner thSpinner;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private List<Therapist> therapists;
    private List<Integer> thIds;

    private int personId;
    private int day, month, year;
    private String selectedDate;

    private int apptId;
    private Appointment appt;

    private int margin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_appointment);
        dLayout = (LinearLayout) findViewById(R.id.appt_date_layout);
        hLayout = (LinearLayout) findViewById(R.id.appt_hour_layout);
        commentET = (EditText) findViewById(R.id.update_appt_comment);
        thSpinner = (Spinner) findViewById(R.id.thchoice_update);
        dataSource = HealthRecordDataSource.getInstance(this);
        margin = (int) HealthRecordUtils.convertPixelsToDp(2, this);
        //Existing appointment
        apptId = getIntent().getIntExtra("apptId", 0);
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
        if (apptId == 0 && (personId == 0 || day < 1 || month < 0 || year < 0)) return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            initHourLayout();
            if (apptId != 0) {
                initDateLayout();
                appt = dataSource.getAppointmentTable().getAppointmentWithId(apptId);
                retrieveTherapists();
                preFillWidgets();
                fillWidgets();
            } else {
                selectedDate = String.format("%02d/%02d/%04d", day, month + 1, year);
                retrieveTherapists();
                preFillWidgets();
            }
        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (apptId == 0) return;
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    private void retrieveTherapists()
    {
        therapists = new ArrayList<Therapist>();
        thIds = new ArrayList<Integer>();
        Therapist t;
        List<Integer> myTherapistIds;
        if (apptId != 0)
            myTherapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(appt.getPersonId());
        else
            myTherapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);

        for (Integer i : myTherapistIds) {
            t = dataSource.getTherapistTable().getTherapistWithId(i);
            therapists.add(t);
            thIds.add(t.getId());
        }
    }

    private void preFillWidgets()
    {
        String[] therapistsStr;
        therapistsStr = new String[therapists.size()];
        int i = 0;
        TherapyBranch branch;
        String tName;
        String therapyBranchStr;
        for (Therapist t : therapists) {
            tName = t.getName();
            if (tName.length() > 20) tName = tName.substring(0,20) + "...";
            branch = dataSource.getTherapyBranchTable().getBranchWithId(t.getBranchId());
            therapyBranchStr = branch.getName();
            if (therapyBranchStr.length() > 10) therapyBranchStr = therapyBranchStr.substring(0,10) + "...";
            therapistsStr[i++] = tName + "-" + therapyBranchStr;
        }
        ArrayAdapter<String> thChoicesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, therapistsStr);
        thSpinner.setAdapter(thChoicesAdapter);
    }

    private void fillWidgets()
    {
        String[] therapistsStr = new String[therapists.size()];
        int thIdx = 0;
        int i = 0;
        int it = 0;
        TherapyBranch branch;
        String tName;
        String therapyBranchStr;
        for (Therapist t : therapists) {
            tName = t.getName();
            if (tName.length() > 20) tName = tName.substring(0,20) + "...";
            if (t.getId() == appt.getTherapistId()) thIdx = it;
            branch = dataSource.getTherapyBranchTable().getBranchWithId(t.getBranchId());
            therapyBranchStr = branch.getName();
            if (therapyBranchStr.length() > 10) therapyBranchStr = therapyBranchStr.substring(0,10) + "...";
            therapistsStr[i++] = tName + " - " + therapyBranchStr;
            it++;
        }
        ArrayAdapter<String> thChoicesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, therapistsStr);
        thSpinner.setAdapter(thChoicesAdapter);
        thSpinner.setSelection(thIdx);
        dateET.setText(appt.getDate());
        hourET.setText(appt.getHour());
        commentET.setText(appt.getComment());
    }

    public void saveAppointment(View view)
    {
        if (apptId == 0 && (personId == 0 || day < 1 || month < 0 || year < 0)) return;
        if (!dataSourceLoaded) return;
        int thPos = thSpinner.getSelectedItemPosition();
        int therapistId = thIds.get(thPos);
        String hourStr = hourET.getText().toString();
        String comment = commentET.getText().toString();
        if (apptId != 0) {
            String dayStr = dateET.getText().toString();
            if (comment.equals("")) comment = null;
            Appointment a = new Appointment(appt.getPersonId(), therapistId, dayStr, hourStr, comment);
            if (appt.equalsTo(a)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_change), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            a.setId(apptId);
            boolean res = dataSource.getAppointmentTable().updateAppointment(apptId, therapistId, dayStr, hourStr, comment);
            if (res) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.update_saved), Toast.LENGTH_SHORT).show();
                //FIXME: Update alarm, the following code does not work
                Notification.Builder builder = new Notification.Builder(this);
                long alarm = HealthRecordUtils.datehourToCalendar(selectedDate, hourStr).getTimeInMillis() - 7200000;
                builder.setSmallIcon(R.drawable.health_record_app)
                        .setContentTitle(dataSource.getTherapistTable().getTherapistWithId(therapistId).getName() + " @ " + hourStr)
                        .setWhen(alarm)
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                Notification notification = builder.build();
                Intent notificationIntent = new Intent(this, NotificationPublisher.class);
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, apptId);
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, apptId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarm, pendingIntent);
                finish();
            } else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT).show();
        }
        else {
            if (checkFields(hourStr)) {
                if (comment.equals("")) comment = null;
                int notificationId = (int) dataSource.getAppointmentTable().insertAppointment(personId, therapistId, selectedDate,
                        hourStr, comment);
                if (notificationId > 0) {
                    Notification.Builder builder = new Notification.Builder(this);
                    long alarm = HealthRecordUtils.datehourToCalendar(selectedDate, hourStr).getTimeInMillis() - 7200000;
                    builder.setSmallIcon(R.drawable.health_record_app)
                            .setContentTitle(dataSource.getTherapistTable().getTherapistWithId(therapistId).getName() + " @ " + hourStr)
                            .setWhen(alarm)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    Notification notification = builder.build();
                    Intent notificationIntent = new Intent(this, NotificationPublisher.class);
                    notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId);
                    notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, alarm, pendingIntent);
                }
                finish();
            }
            else
            {
                Toast toast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.notValidData), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private boolean checkFields(String hour)
    {
        if (hour.equals("")){
            HealthRecordUtils.highlightActivityFields(this, hourET);
            return false;
        }
        return true;
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
                if (apptId == 0) {
                    dfrag.init(EditAppointmentActivity.this, dateET);
                }
                else {
                    dfrag.init(EditAppointmentActivity.this, dateET, HealthRecordUtils.stringToCalendar(appt.getDate()), null, null);
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
                if (apptId == 0) {
                    //TODO
                    hfrag.init(EditAppointmentActivity.this, hourET);
                }
                else {
                    String[] h = appt.getHour().split(":");
                    int hour = Integer.parseInt(h[0]);
                    int min = Integer.parseInt(h[1]);
                    hfrag.init(EditAppointmentActivity.this, hourET, hour, min);
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
