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

import com.akoudri.healthrecord.activity.EditAilmentActivity;
import com.akoudri.healthrecord.app.PersonManager;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Ailment;
import com.akoudri.healthrecord.data.Illness;
import com.akoudri.healthrecord.data.IllnessTable;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.List;

//STATUS: checked
public class AilmentFragment extends EditDayFragment {

    private View view;
    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private int aId = 0;

    public static EditDayFragment newInstance()
    {
        return new AilmentFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ailment, container, false);
        layout = (GridLayout) view.findViewById(R.id.ailments_grid);
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
        List<Ailment> dayAilments = dataSource.getAilmentTable().getDayAilmentsForPerson(personId, date);
        if (dayAilments == null || dayAilments.size() == 0) return;
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, getActivity());;
        Button ailmentButton, editButton, stopButton, removeButton;
        layout.setColumnCount(3);
        int childWidth = layout.getWidth()/3 - 2*margin;
        IllnessTable illnessTable = dataSource.getIllnessTable();
        int r = 0; //row index
        for (final Ailment ailment : dayAilments)
        {
            final int ailmentId = ailment.getId();
            final Illness illness = illnessTable.getIllnessWithId(ailment.getIllnessId());
            //edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0,3);
            ailmentButton = new Button(getActivity());
            String pt = getResources().getString(R.string.preventive_treatment);
            String illnessName = (illness==null)?pt:illness.getName();
            if (illnessName.length() > 20) illnessName = illnessName.substring(0,20) + "...";
            ailmentButton.setText(illnessName);
            ailmentButton.setTextSize(16);
            ailmentButton.setTypeface(null, Typeface.BOLD);
            ailmentButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            ailmentButton.setMinEms(14);
            ailmentButton.setMaxEms(14);
            ailmentButton.setBackgroundResource(R.drawable.healthrecord_button);
            ailmentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = AilmentFragment.this.aId;
                    if (id == ailmentId) AilmentFragment.this.aId = 0;
                    else AilmentFragment.this.aId = ailmentId;
                    createWidgets();
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.CENTER);
            ailmentButton.setLayoutParams(params);
            layout.addView(ailmentButton);
            if (this.aId == ailmentId) {
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
                        Intent intent = new Intent(getActivity(), EditAilmentActivity.class);
                        intent.putExtra("ailmentId", ailmentId);
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
                boolean has_duration = (ailment.getDuration() >= 0);
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
                        Ailment a = ailment;
                        long start = HealthRecordUtils.stringToCalendar(ailment.getStartDate()).getTimeInMillis();
                        long end = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
                        long d = end - start;
                        int duration = (int)(d / 86400000);
                        a.setDuration(duration);
                        dataSource.getAilmentTable().updateAilment(a);
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
                                        + " " + illness.getName() + "?")
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataSource.getAilmentTable().removeAilmentWithId(ailmentId);
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
        this.aId = 0;
    }

}
