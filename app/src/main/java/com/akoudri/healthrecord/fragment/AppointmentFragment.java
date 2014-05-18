package com.akoudri.healthrecord.fragment;

import android.app.Fragment;
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

    public static AppointmentFragment newInstance()
    {
        return new AppointmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_appointment, container, false);
        layout = (GridLayout) view.findViewById(R.id.my_appointments_grid);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        return view;
    }

    public void setCurrentDay(Calendar currentDay)
    {
        this.currentDay = currentDay;
    }

    private void populateWidgets()
    {
        //FIXME: does not display anything!
        layout.removeAllViews();
        int day = currentDay.get(Calendar.DAY_OF_MONTH);
        int month = currentDay.get(Calendar.MONTH) + 1;
        int year = currentDay.get(Calendar.YEAR);
        String date = String.format("%02d/%02d/%4d", day, month, year);
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
            //FIXME:set on click listener
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
            //FIXME: set on click listener
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
