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

import com.akoudri.healthrecord.activity.EditAppointmentActivity;
import com.akoudri.healthrecord.activity.EditObservationActivity;
import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.MedicalObservation;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranch;
import com.akoudri.healthrecord.data.TherapyBranchTable;

import java.util.List;


public class ObservationFragment extends Fragment {

    private HealthRecordDataSource dataSource;
    private int personId, day, month, year;

    private View view;

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private int observationId = 0;

    public static ObservationFragment newInstance()
    {
        return new ObservationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_observation, container, false);
        layout = (GridLayout) view.findViewById(R.id.my_observations_grid);
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
        List<MedicalObservation> allObservations = dataSource.getMedicalObservationTable().getDayObservationsForPerson(personId, date);
        if (allObservations == null || allObservations.size() == 0) return;
        int margin = 1;
        Button obsButton, removeButton, editButton;
        layout.setColumnCount(2);
        int childWidth = layout.getWidth()/2 - 4*margin;
        MedicalObservation observation;
        int r = 0; //row index
        for (MedicalObservation obs : allObservations)
        {
            final int obsId = obs.getId();
            observation = dataSource.getMedicalObservationTable().getMedicalObservationWithId(obsId);
            //Obs button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0,2);
            obsButton = new Button(getActivity());
            obsButton.setText(obs.getDate() + " - " + obs.getHour());
            obsButton.setTextSize(16);
            obsButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            obsButton.setMinEms(12);
            obsButton.setMaxEms(12);
            obsButton.setBackgroundResource(R.drawable.healthrecord_button);
            obsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = ObservationFragment.this.observationId;
                    if (id == obsId) ObservationFragment.this.observationId = 0;
                    else ObservationFragment.this.observationId = obsId;
                    createWidgets();
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.CENTER);
            obsButton.setLayoutParams(params);
            layout.addView(obsButton);
            if (this.observationId == obsId) {
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
                        Intent intent = new Intent(getActivity(), EditObservationActivity.class);
                        intent.putExtra("obsId", obsId);
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
                        new AlertDialog.Builder((ObservationFragment.this).getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.removing)
                                .setMessage(getResources().getString(R.string.remove_appt_question))
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataSource.getMedicalObservationTable().removeMedicalObservationWithId(obsId);
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

    public void resetObservationId()
    {
        observationId = 0;
    }

}