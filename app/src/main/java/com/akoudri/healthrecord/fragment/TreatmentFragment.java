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
import com.akoudri.healthrecord.data.Ailment;
import com.akoudri.healthrecord.data.AilmentTable;
import com.akoudri.healthrecord.data.Illness;
import com.akoudri.healthrecord.data.IllnessTable;
import com.akoudri.healthrecord.data.Treatment;

import java.util.List;


public class TreatmentFragment extends Fragment {

    private HealthRecordDataSource dataSource;
    private int personId, day, month, year;

    private View view;
    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    public static TreatmentFragment newInstance()
    {
        return new TreatmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_treatment, container, false);
        layout = (GridLayout) view.findViewById(R.id.treatments_grid);
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

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    private void createWidgets()
    {
        layout.removeAllViews();
        final String date = String.format("%02d/%02d/%4d", day, month + 1, year);
        List<Treatment> dayTreatments = dataSource.getTreatmentTable().getDayTreatmentsForPerson(personId, date);
        if (dayTreatments == null || dayTreatments.size() == 0) return;
        int margin = 5;
        Button editButton;
        ImageButton endButton, removeButton;
        layout.setColumnCount(3);
        IllnessTable illnessTable = dataSource.getIllnessTable();
        AilmentTable ailmentTable = dataSource.getAilmentTable();
        int r = 0; //row index
        for (final Treatment treatment : dayTreatments)
        {
            final int treatmentId = treatment.getId();
            final Ailment ailment = ailmentTable.getAilmentWithId(treatment.getAilmentId());
            final Illness illness = illnessTable.getIllnessWithId(ailment.getIllnessId());
            //edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0);
            editButton = new Button(getActivity());
            editButton.setText(illness.getName());
            editButton.setTextSize(16);
            editButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            editButton.setMinEms(8);
            editButton.setMaxEms(8);
            editButton.setBackgroundResource(R.drawable.healthrecord_button);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.akoudri.healthrecord.app.EditTreatment");
                    intent.putExtra("treatmentId", treatmentId);
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
            params.setGravity(Gravity.RIGHT);
            editButton.setLayoutParams(params);
            layout.addView(editButton);
            //end button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(1);
            endButton = new ImageButton(getActivity());
            endButton.setBackgroundResource(R.drawable.end_illness);
            endButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //FIXME: disable end button when done
                    treatment.setEndDate(date);
                    dataSource.getTreatmentTable().updateTreatment(treatment);
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.LEFT);
            endButton.setLayoutParams(params);
            layout.addView(endButton);
            //remove button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(2);
            removeButton = new ImageButton(getActivity());
            removeButton.setBackgroundResource(R.drawable.remove);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.removing)
                            .setMessage(getResources().getString(R.string.remove_question)
                                    + " " + illness.getName() + "?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dataSource.getTreatmentTable().removeTreatmentWithId(treatmentId);
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
            params.setGravity(Gravity.LEFT);
            removeButton.setLayoutParams(params);
            layout.addView(removeButton);
            //next line
            r++;
        }
    }

}
