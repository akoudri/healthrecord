package com.akoudri.healthrecord.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.akoudri.healthrecord.activity.EditAppointmentActivity;
import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Appointment;
import com.akoudri.healthrecord.data.AppointmentTable;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranch;
import com.akoudri.healthrecord.data.TherapyBranchTable;
import com.akoudri.healthrecord.utils.HealthRecordUtils;
import com.akoudri.healthrecord.utils.NotificationPublisher;

import java.util.Calendar;
import java.util.List;

//STATUS: checked
public class AppointmentFragment extends Fragment {

    private HealthRecordDataSource dataSource;
    private int personId;

    private View view;
    private Button addApptButton;

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private Calendar currentDay, today;

    private int appointmentId = 0;
    private int count = 0;

    private String selectedDate;

    public static AppointmentFragment newInstance()
    {
        return new AppointmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appointment, container, false);
        addApptButton = (Button) view.findViewById(R.id.add_appt_button);
        layout = (GridLayout) view.findViewById(R.id.my_appointments_grid);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        currentDay = HealthRecordUtils.stringToCalendar(selectedDate);
        today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        count = dataSource.getPersonTherapistTable().countTherapistsForPerson(personId);
        if (count == 0 || currentDay.before(today))
        {
            addApptButton.setEnabled(false);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (personId == 0) return;
        if (dataSource == null) return;
        createWidgets();
    }

    public void setCurrentDate(int day, int month, int year)
    {
        selectedDate = String.format("%02d/%02d/%4d", day, month + 1, year);
    }

    public void refresh()
    {
        currentDay = HealthRecordUtils.stringToCalendar(selectedDate);
        if ((currentDay.equals(today) || currentDay.after(today)) && count > 0)
            addApptButton.setEnabled(true);
        else
            addApptButton.setEnabled(false);
        createWidgets();
    }

    private void createWidgets()
    {
        layout.removeAllViews();
        List<Appointment> allAppointments = dataSource.getAppointmentTable().getDayAppointmentsForPerson(personId, selectedDate);
        if (allAppointments == null || allAppointments.size() == 0) return;
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, getActivity());
        Button apptButton, removeButton, editButton;
        ImageButton rButton;
        layout.setColumnCount(2);
        int childWidth = layout.getWidth()/2 - 2*margin;
        TherapistTable therapistTable = dataSource.getTherapistTable();
        TherapyBranchTable branchTable = dataSource.getTherapyBranchTable();
        Therapist therapist;
        TherapyBranch branch;
        StringBuilder sb;
        int r = 0; //row index
        for (Appointment appt : allAppointments)
        {
            final int apptId = appt.getId();
            therapist = therapistTable.getTherapistWithId(appt.getTherapistId());
            branch = branchTable.getBranchWithId(therapist.getBranchId());
            sb = new StringBuilder();
            String tName = therapist.getName();
            if (tName.length() > 25) tName = tName.substring(0,25) + "...";
            sb.append(tName);
            sb.append("\n");
            String bName = branch.getName();
            if (bName.length() > 25) bName = bName.substring(0,25) + "...";
            sb.append(bName);
            sb.append("\n");
            sb.append(appt.getHour());
            //Appt button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0,2);
            apptButton = new Button(getActivity());
            apptButton.setText(sb.toString());
            apptButton.setTextSize(16);
            apptButton.setTypeface(null, Typeface.BOLD);
            apptButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            apptButton.setMinEms(12);
            apptButton.setMaxEms(12);
            apptButton.setBackgroundResource(R.drawable.healthrecord_button);
            apptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = AppointmentFragment.this.appointmentId;
                    if (id == apptId) AppointmentFragment.this.appointmentId = 0;
                    else AppointmentFragment.this.appointmentId = apptId;
                    createWidgets();
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.CENTER);
            apptButton.setLayoutParams(params);
            layout.addView(apptButton);
            if (this.appointmentId == apptId) {
                //Next line
                r++;
                //Edit Button
                rowSpec = GridLayout.spec(r);
                colSpec = GridLayout.spec(0);
                editButton = new Button(getActivity());
                editButton.setText(getResources().getString(R.string.edit));
                editButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
                editButton.setTextSize(12);
                editButton.setTypeface(null, Typeface.BOLD);
                editButton.setMinEms(4);
                editButton.setMaxEms(4);
                editButton.setBackgroundResource(R.drawable.healthrecord_button);
                Drawable img = getResources().getDrawable(R.drawable.update);
                editButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), EditAppointmentActivity.class);
                        intent.putExtra("apptId", apptId);
                        startActivity(intent);
                    }
                });
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.width = childWidth;
                params.setGravity(Gravity.CENTER);
                editButton.setLayoutParams(params);
                layout.addView(editButton);
                //Remove Button
                colSpec = GridLayout.spec(1);
                removeButton = new Button(getActivity());
                removeButton.setText(getResources().getString(R.string.remove));
                removeButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
                removeButton.setTextSize(12);
                removeButton.setTypeface(null, Typeface.BOLD);
                removeButton.setMinEms(4);
                removeButton.setMaxEms(4);
                removeButton.setBackgroundResource(R.drawable.healthrecord_button);
                img = getResources().getDrawable(R.drawable.delete);
                removeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder((AppointmentFragment.this).getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.removing)
                                .setMessage(getResources().getString(R.string.remove_appt_question))
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO: see how to cancel corresponding alarm
                                        AppointmentTable apptTable = dataSource.getAppointmentTable();
                                        Appointment appt = apptTable.getAppointmentWithId(apptId);
                                        boolean deleted = apptTable.removeAppointmentWithId(apptId);
                                        if (deleted)
                                        {
                                            Notification.Builder builder = new Notification.Builder(getActivity());
                                            long alarm = HealthRecordUtils.datehourToCalendar(appt.getDate(), appt.getHour()).getTimeInMillis() - 7200000;
                                            int apptId = appt.getId();
                                            builder.setSmallIcon(R.drawable.health_record_app)
                                                    .setContentTitle(dataSource.getTherapistTable().getTherapistWithId(appt.getTherapistId()).getName() + " @ " + appt.getHour())
                                                    .setWhen(alarm)
                                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                                            Notification notification = builder.build();
                                            Intent notificationIntent = new Intent(getActivity(), NotificationPublisher.class);
                                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, apptId);
                                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), apptId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                                            alarmManager.cancel(pendingIntent);
                                        }
                                        createWidgets();
                                    }
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                });
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.width = childWidth;
                params.setGravity(Gravity.CENTER);
                removeButton.setLayoutParams(params);
                layout.addView(removeButton);
            }
            //next line
            r++;
        }
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public void resetAppointmentId()
    {
        appointmentId = 0;
    }

}
