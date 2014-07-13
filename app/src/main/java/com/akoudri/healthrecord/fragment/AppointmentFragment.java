package com.akoudri.healthrecord.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

import java.util.List;


public class AppointmentFragment extends Fragment {

    private HealthRecordDataSource dataSource;
    private int personId, day, month, year;

    private View view;

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private int appointmentId = 0;

    public static AppointmentFragment newInstance()
    {
        return new AppointmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appointment, container, false);
        layout = (GridLayout) view.findViewById(R.id.my_appointments_grid);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        day = getActivity().getIntent().getIntExtra("day", 0);
        month = getActivity().getIntent().getIntExtra("month", 0);
        year = getActivity().getIntent().getIntExtra("year", 0);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (personId == 0 || day <= 0 || month <= 0 || year <= 0) return;
        if (dataSource == null) return;
        createWidgets();
    }

    private void createWidgets()
    {
        layout.removeAllViews();
        String date = String.format("%02d/%02d/%4d", day, month + 1, year);
        List<Appointment> allAppointments = dataSource.getAppointmentTable().getDayAppointmentsForPerson(personId, date);
        if (allAppointments == null || allAppointments.size() == 0) return;
        int margin = 1;
        Button apptButton, removeButton, editButton;
        ImageButton rButton;
        layout.setColumnCount(2);
        int childWidth = layout.getWidth()/2 - 4*margin;
        TherapistTable therapistTable = dataSource.getTherapistTable();
        TherapyBranchTable branchTable = dataSource.getTherapyBranchTable();
        Therapist therapist;
        TherapyBranch branch;
        //FIXME: optimization -> clear StringBuilder instead on instanciating it for all classes
        StringBuilder sb;
        int r = 0; //row index
        for (Appointment appt : allAppointments)
        {
            final int apptId = appt.getId();
            therapist = therapistTable.getTherapistWithId(appt.getTherapistId());
            branch = branchTable.getBranchWithId(therapist.getBranchId());
            sb = new StringBuilder();
            sb.append(therapist.getName());
            sb.append("-");
            sb.append(branch.getName());
            sb.append("\n");
            sb.append(appt.getHour());
            //Appt button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0,2);
            apptButton = new Button(getActivity());
            apptButton.setText(sb.toString());
            apptButton.setTextSize(16);
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
                editButton.setMinEms(4);
                editButton.setMaxEms(4);
                editButton.setBackgroundResource(R.drawable.healthrecord_button);
                Drawable img = getResources().getDrawable(R.drawable.update);
                editButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent("com.akoudri.healthrecord.app.UpdateAppointment");
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
                                        dataSource.getAppointmentTable().removeAppointmentWithId(apptId);
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

}
