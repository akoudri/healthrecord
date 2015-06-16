package com.akoudri.healthrecord.fragment;

import android.app.AlertDialog;
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

import com.akoudri.healthrecord.activity.EditObservationActivity;
import com.akoudri.healthrecord.app.PersonManager;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.MedicalObservation;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.List;

//STATUS: checked
public class ObservationFragment extends EditDayFragment {

    private View view;

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private int observationId = 0;


    public static EditDayFragment newInstance()
    {
        return new ObservationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_observation, container, false);
        layout = (GridLayout) view.findViewById(R.id.my_observations_grid);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
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
        int personId = PersonManager.getInstance().getPerson().getId();
        List<MedicalObservation> allObservations = dataSource.getMedicalObservationTable().getDayObservationsForPerson(personId, date);
        if (allObservations == null || allObservations.size() == 0) return;
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, getActivity());
        Button obsButton, removeButton, editButton;
        layout.setColumnCount(2);
        int childWidth = layout.getWidth()/2 - 2*margin;
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
            //obsButton.setText(obs.getDate() + " - " + obs.getHour() + "\n" + obs.getDescription());
            obsButton.setText(obs.getDescription());
            obsButton.setTextSize(16);
            obsButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            //obsButton.setTypeface(null, Typeface.BOLD);
            obsButton.setMinEms(16);
            obsButton.setMaxEms(16);
            obsButton.setBackgroundResource(R.drawable.healthrecord_button);
            obsButton.setGravity(Gravity.LEFT);
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
                editButton.setTypeface(null, Typeface.BOLD);
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
                removeButton.setTypeface(null, Typeface.BOLD);
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

    @Override
    public void resetObjectId()
    {
        observationId = 0;
    }

}
