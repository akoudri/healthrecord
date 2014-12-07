package com.akoudri.healthrecord.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
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

import com.akoudri.healthrecord.activity.EditMedicationActivity;
import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.DrugTable;
import com.akoudri.healthrecord.data.Medication;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.Calendar;
import java.util.List;

//STATUS: checked
public class MedicsFragment extends Fragment {

    private HealthRecordDataSource dataSource;
    private int personId;

    private View view;
    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;
    private Button add_btn;

    private int mId = 0;

    private Calendar currentDay, today;

    private int day, month, year;
    private String date;

    public static MedicsFragment newInstance()
    {
        return new MedicsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_medics, container, false);
        layout = (GridLayout) view.findViewById(R.id.medics_grid);
        add_btn = (Button) view.findViewById(R.id.add_medic_btn);
        today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (personId == 0) return;
        if (dataSource == null) return;
        currentDay = HealthRecordUtils.stringToCalendar(date);
        if (currentDay.after(today))
            add_btn.setEnabled(false);
        createWidgets();
    }

    public void setCurrentDate(int day, int month, int year)
    {
        this.day = day;
        this.month = month;
        this.year = year;
        date = String.format("%02d/%02d/%4d", day, month + 1, year);
    }

    public void refresh()
    {
        currentDay = HealthRecordUtils.stringToCalendar(date);
        if (currentDay.after(today))
            add_btn.setEnabled(false);
        else
            add_btn.setEnabled(true);
        createWidgets();
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    private void createWidgets()
    {
        layout.removeAllViews();
        List<Medication> dayMedics = dataSource.getMedicationTable().getDayMedicsForPerson(personId, date);
        if (dayMedics == null || dayMedics.size() == 0) return;
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, getActivity());
        Button medicButton, editButton, stopButton, removeButton;
        layout.setColumnCount(3);
        int childWidth = layout.getWidth()/3 - 2*margin;
        DrugTable drugTable = dataSource.getDrugTable();
        int r = 0; //row index
        for (final Medication medic : dayMedics)
        {
            final int medicId = medic.getId();
            //edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0,3);
            medicButton = new Button(getActivity());
            String drugName = drugTable.getDrugWithId(medic.getDrugId()).getName();
            if (drugName.length() > 20) drugName = drugName.substring(0,20) + "...";
            medicButton.setText(drugName);
            medicButton.setTextSize(16);
            medicButton.setTypeface(null, Typeface.BOLD);
            medicButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            medicButton.setMinEms(14);
            medicButton.setMaxEms(14);
            medicButton.setBackgroundResource(R.drawable.healthrecord_button);
            medicButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = MedicsFragment.this.mId;
                    if (id == medicId) MedicsFragment.this.mId = 0;
                    else MedicsFragment.this.mId = medicId;
                    createWidgets();
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.CENTER);
            medicButton.setLayoutParams(params);
            layout.addView(medicButton);
            if (this.mId == medicId) {
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
                        Intent intent = new Intent(getActivity(), EditMedicationActivity.class);
                        intent.putExtra("medicationId", medicId);
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
                //Stop Button
                colSpec = GridLayout.spec(1);
                boolean has_duration = (medic.getDuration() >= 0);
                stopButton = new Button(getActivity());
                stopButton.setText(getResources().getString(R.string.stop));
                stopButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
                stopButton.setTextSize(12);
                stopButton.setTypeface(null, Typeface.BOLD);
                stopButton.setMinEms(5);
                stopButton.setBackgroundResource(R.drawable.healthrecord_button);
                if (!has_duration)
                    img = getResources().getDrawable(R.drawable.stop);
                else
                    img = getResources().getDrawable(R.drawable.stop_disabled);
                stopButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                stopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Medication a = medic;
                        long start = HealthRecordUtils.stringToCalendar(medic.getStartDate()).getTimeInMillis();
                        long end = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
                        long d = end - start;
                        int duration = (int)(d / 86400000);
                        a.setDuration(duration);
                        dataSource.getMedicationTable().updateMedication(a);
                        createWidgets();
                    }
                });
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.width = childWidth;
                params.setGravity(Gravity.CENTER);
                stopButton.setLayoutParams(params);
                layout.addView(stopButton);
                if (has_duration) stopButton.setEnabled(false);
                //Remove Button
                colSpec = GridLayout.spec(2);
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
                                        + " " + "medic name" + "?")
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataSource.getMedicationTable().removeMedicWithId(medicId);
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

    public void resetMedicId()
    {
        mId = 0;
    }

}
