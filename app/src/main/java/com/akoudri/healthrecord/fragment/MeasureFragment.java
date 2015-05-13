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

import com.akoudri.healthrecord.activity.EditMeasureActivity;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Measure;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.List;

//STATUS: checked
public class MeasureFragment extends EditDayFragment {

    private View view;

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private int mID = 0;
    private int mType = 0;

    public static EditDayFragment newInstance()
    {
        return new MeasureFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_measure, container, false);
        layout = (GridLayout) view.findViewById(R.id.my_measures_grid);
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
        List<Measure> allMeasures = new ArrayList<Measure>();
        allMeasures.addAll(dataSource.getMeasureView().getPersonMeasuresWithDate(personId, date));
        Button measureButton, removeButton, editButton;
        Drawable img;
        layout.setColumnCount(2);
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, getActivity());
        int childWidth = layout.getWidth()/2 - 2*margin;
        int r = 0; //row index
        for (final Measure measure : allMeasures)
        {
            final int measureId = measure.getId();
            final int typeId = measure.getType();
            //Appt button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0,2);
            measureButton = new Button(getActivity());
            measureButton.setText(measure.getValueString());
            measureButton.setTextSize(16);
            measureButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            measureButton.setTypeface(null, Typeface.BOLD);
            measureButton.setMinEms(12);
            measureButton.setMaxEms(12);
            measureButton.setBackgroundResource(R.drawable.healthrecord_button);
            switch (measure.getType())
            {
                case 1:
                    img = getResources().getDrawable(R.drawable.weight);
                    break;
                case 2:
                    img = getResources().getDrawable(R.drawable.ruler);
                    break;
                case 3:
                    img = getResources().getDrawable(R.drawable.temperature);
                    break;
                case 4:
                    img = getResources().getDrawable(R.drawable.skull);
                    break;
                case 5:
                    img = getResources().getDrawable(R.drawable.sugar);
                    break;
                case 6:
                    img = getResources().getDrawable(R.drawable.heart);
                    break;
                default:
                    img = getResources().getDrawable(R.drawable.cholesterol);
            }
            measureButton.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            measureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = MeasureFragment.this.mID;
                    int tId = MeasureFragment.this.mType;
                    if (id == measureId && tId == typeId) {
                        MeasureFragment.this.mID = 0;
                        MeasureFragment.this.mType = 0;
                    } else {
                        MeasureFragment.this.mID = measureId;
                        MeasureFragment.this.mType = typeId;
                    }
                    createWidgets();
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.CENTER);
            measureButton.setLayoutParams(params);
            layout.addView(measureButton);
            if (this.mID == measureId && this.mType == typeId) {
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
                img = getResources().getDrawable(R.drawable.update);
                editButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), EditMeasureActivity.class);
                        intent.putExtra("measureId", measureId);
                        intent.putExtra("measureIdType", typeId);
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
                        new AlertDialog.Builder((MeasureFragment.this).getActivity())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.removing)
                                .setMessage(getResources().getString(R.string.remove_measure_question))
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (mType)
                                        {
                                            case 1:
                                                dataSource.getWeightMeasureTable().removeMeasureWithId(measureId);
                                                break;
                                            case 2:
                                                dataSource.getSizeMeasureTable().removeMeasureWithId(measureId);
                                                break;
                                            case 3:
                                                dataSource.getTempMeasureTable().removeMeasureWithId(measureId);
                                                break;
                                            case 4:
                                                dataSource.getCpMeasureTable().removeMeasureWithId(measureId);
                                                break;
                                            case 5:
                                                dataSource.getGlucoseMeasureTable().removeMeasureWithId(measureId);
                                                break;
                                            case 6:
                                                dataSource.getHeartMeasureTable().removeMeasureWithId(measureId);
                                                break;
                                            default:
                                                dataSource.getCholesterolMeasureTable().removeMeasureWithId(measureId);
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
        mID = 0;
        mType = 0;
    }

}
