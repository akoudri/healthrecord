package com.akoudri.healthrecord.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Appointment;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranch;
import com.akoudri.healthrecord.data.TherapyBranchTable;

import java.util.Calendar;
import java.util.List;


public class AppointmentFragment extends Fragment {

    private HealthRecordDataSource dataSource;
    private int personId;
    private Calendar currentDay;
    private View view;
    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;
    private int day, month, year;

    public static AppointmentFragment newInstance()
    {
        return new AppointmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appointment, container, false);
        layout = (GridLayout) view.findViewById(R.id.my_appointments_grid);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        day = 0;
        month = 0;
        year = 0;
        return view;
    }

    public void setCurrentDay(Calendar currentDay)
    {
        this.currentDay = currentDay;
        day = currentDay.get(Calendar.DAY_OF_MONTH);
        month = currentDay.get(Calendar.MONTH) + 1;
        year = currentDay.get(Calendar.YEAR);
    }

    private void populateWidgets()
    {
        layout.removeAllViews();
        //int day = currentDay.get(Calendar.DAY_OF_MONTH);
        //int month = currentDay.get(Calendar.MONTH) + 1;
        //int year = currentDay.get(Calendar.YEAR);
        String date = String.format("%02d/%02d/%4d", day, month, year);
        //FIXME: order appointment in regard to time
        List<Appointment> allAppointments = dataSource.getAppointmentTable().getDayAppointmentsForPerson(personId, date);
        if (allAppointments == null || allAppointments.size() == 0) return;
        int margin = 5;
        Button editButton;
        ImageButton removeButton;
        layout.setColumnCount(2);
        TherapistTable therapistTable = dataSource.getTherapistTable();
        TherapyBranchTable branchTable = dataSource.getTherapyBranchTable();
        Therapist therapist;
        TherapyBranch branch;
        StringBuilder sb;
        int r = 0; //row index
        for (Appointment appt : allAppointments)
        {
            final int apptId = appt.getId();
            therapist = therapistTable.getTherapistWithId(appt.getTherapist());
            branch = branchTable.getBranchWithId(therapist.getBranchId());
            sb = new StringBuilder();
            sb.append(therapist.getName());
            sb.append("-");
            sb.append(branch.getName());
            sb.append("\n");
            sb.append(appt.getHour());
            //edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0);
            editButton = new Button(getActivity());
            editButton.setText(sb.toString());
            editButton.setTextSize(16);
            editButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            editButton.setMinEms(10);
            editButton.setMaxEms(10);
            editButton.setBackgroundResource(R.drawable.healthrecord_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.akoudri.healthrecord.app.UpdateAppointment");
                    intent.putExtra("personId", personId);
                    intent.putExtra("apptId", apptId);
                    startActivity(intent);
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.RIGHT);
            editButton.setLayoutParams(params);
            layout.addView(editButton);
            //remove button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(1);
            removeButton = new ImageButton(getActivity());
            removeButton.setBackgroundResource(R.drawable.remove);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder((AppointmentFragment.this).getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.removing)
                            .setMessage(getResources().getString(R.string.remove_appt_question))
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dataSource.getAppointmentTable().removeAppointmentWithId(apptId);
                                    populateWidgets();
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
            params.setGravity(Gravity.LEFT);
            removeButton.setLayoutParams(params);
            layout.addView(removeButton);
            //next line
            r++;
        }
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateWidgets();
    }
}
