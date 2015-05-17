package com.akoudri.healthrecord.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
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

import com.akoudri.healthrecord.activity.EditReminderActivity;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.DrugTable;
import com.akoudri.healthrecord.data.Reminder;
import com.akoudri.healthrecord.data.ReminderTable;
import com.akoudri.healthrecord.utils.HealthRecordUtils;
import com.akoudri.healthrecord.utils.NotificationPublisher;

import java.util.List;

//STATUS: checked
public class ReminderFragment extends EditDayFragment {

    private View view;
    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private int mId = 0;

    public static EditDayFragment newInstance()
    {
        return new ReminderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reminder, container, false);
        layout = (GridLayout) view.findViewById(R.id.reminders_grid);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (personId == 0) return;
        if (dataSource == null) return;
        createWidgets();
    }

    @Override
    public void refresh()
    {
        createWidgets();
    }

    private void createWidgets()
    {
        layout.removeAllViews();
        List<Reminder> dayReminders = dataSource.getReminderTable().getDayRemindersForPerson(personId, date);
        if (dayReminders == null || dayReminders.size() == 0) return;
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, getActivity());
        Button reminderButton, editButton, removeButton;
        layout.setColumnCount(2);
        int childWidth = layout.getWidth()/2 - margin;
        DrugTable drugTable = dataSource.getDrugTable();
        int r = 0; //row index
        for (final Reminder reminder : dayReminders)
        {
            final int reminderId = reminder.getId();
            //edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0,2);
            reminderButton = new Button(getActivity());
            String reminderName = drugTable.getDrugWithId(reminder.getDrugId()).getName();
            if (reminderName.length() > 20) reminderName = reminderName.substring(0,20) + "...";
            final String ReminderName = reminderName;
            reminderButton.setText(reminderName);
            reminderButton.setTextSize(16);
            reminderButton.setTypeface(null, Typeface.BOLD);
            reminderButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            reminderButton.setMinEms(14);
            reminderButton.setMaxEms(14);
            reminderButton.setBackgroundResource(R.drawable.healthrecord_button);
            reminderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = ReminderFragment.this.mId;
                    if (id == reminderId) ReminderFragment.this.mId = 0;
                    else ReminderFragment.this.mId = reminderId;
                    createWidgets();
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.CENTER);
            reminderButton.setLayoutParams(params);
            layout.addView(reminderButton);
            if (this.mId == reminderId) {
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
                editButton.setMinEms(5);
                editButton.setBackgroundResource(R.drawable.healthrecord_button);
                Drawable img = getResources().getDrawable(R.drawable.update);
                editButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), EditReminderActivity.class);
                        intent.putExtra("reminderId", reminderId);
                        intent.putExtra("day", day);
                        intent.putExtra("month", month);
                        intent.putExtra("year", year);
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
                removeButton.setMinEms(5);
                removeButton.setBackgroundResource(R.drawable.healthrecord_button);
                img = getResources().getDrawable(R.drawable.delete);
                removeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.removing)
                                .setMessage(getResources().getString(R.string.remove_question)
                                        + " " + ReminderName + "?")
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ReminderTable reminderTable = dataSource.getReminderTable();
                                        Reminder r = reminderTable.getReminderWithId(reminderId);
                                        boolean deleted = reminderTable.removeReminderWithId(reminderId);
                                        if (deleted)
                                        {
                                            Notification.Builder builder = new Notification.Builder(getActivity());
                                            long alarm = HealthRecordUtils.datehourToCalendar(r.getDate()).getTimeInMillis() - 7200000;
                                            int rId = r.getId();
                                            builder.setSmallIcon(R.drawable.health_record_app)
                                                    .setContentTitle(dataSource.getDrugTable().getDrugWithId(r.getDrugId()).getName())
                                                    .setWhen(alarm)
                                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                                            Notification notification = builder.build();
                                            Intent notificationIntent = new Intent(getActivity(), NotificationPublisher.class);
                                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, rId);
                                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), rId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
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

    @Override
    public void resetObjectId()
    {
        mId = 0;
    }

}
