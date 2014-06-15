package com.akoudri.healthrecord.fragment;

import android.app.Fragment;
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

import java.util.Calendar;
import java.util.List;


public class AilmentFragment extends Fragment {

    private HealthRecordDataSource dataSource;
    private int personId;
    private Calendar currentDay;
    private View view;
    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;
    private int day = 0;
    private int month = 0;
    private int  year = 0;

    public static AilmentFragment newInstance()
    {
        return new AilmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ailment, container, false);
        layout = (GridLayout) view.findViewById(R.id.ailments_grid);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
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
        String date = String.format("%02d/%02d/%4d", day, month, year);
        List<Ailment> dayAilments = dataSource.getAilmentTable().getDayAilmentsForPerson(personId, date);
        if (dayAilments == null || dayAilments.size() == 0) return;
        int margin = 5;
        Button editButton;
        ImageButton endButton, removeButton;
        layout.setColumnCount(3);
        IllnessTable illnessTable = dataSource.getIllnessTable();
        Illness illness;
        int r = 0; //row index
        for (Ailment ailment : dayAilments)
        {
            final int ailmentId = ailment.getId();
            illness = illnessTable.getIllnessWithId(ailment.getIllnessId());
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
            //TODO: add listener
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
            //TODO: add listener
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
            //TODO: add listener
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
