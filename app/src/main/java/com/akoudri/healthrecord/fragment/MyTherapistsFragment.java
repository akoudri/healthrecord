package com.akoudri.healthrecord.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import com.akoudri.healthrecord.data.Person;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MyTherapistsFragment extends Fragment {

    private HealthRecordDataSource dataSource;
    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;
    private View view;
    private int personId;
    private Person person;

    public static MyTherapistsFragment newInstance()
    {
        return new MyTherapistsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_therapists, container, false);
        layout = (GridLayout) view.findViewById(R.id.my_therapists_grid);
        personId = getActivity().getIntent().getIntExtra("personId", 0);
        return view;
    }

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    @Override
    public void onResume() {
        super.onResume();
        //FIXME: add just corresponding widget from adding new person - see AddPerson
        retrievePerson();
        populateWidgets();
    }

    private void retrievePerson() {
        person = dataSource.getPersonTable().getPersonWithId(personId);
    }

    //FIXME: should be called one time at first load only
    private void populateWidgets()
    {
        layout.removeAllViews();
        List<Therapist> allTherapists = new ArrayList<Therapist>();
        int margin = 5;
        List<Integer> therapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
        for (Integer i : therapistIds)
        {
            allTherapists.add(dataSource.getTherapistTable().getTherapistWithId(i));
        }
        if (allTherapists == null || allTherapists.size() == 0)
            return;
        Button editButton;
        ImageButton removeButton, phoneButton;
        TherapyBranch branch = null;
        String therapyBranch;
        layout.setColumnCount(3);
        int r = 0; //row index
        for (final Therapist p : allTherapists)
        {
            final int id = p.getId();
            branch = dataSource.getTherapyBranchTable().getBranchWithId(p.getBranchId());
            therapyBranch = branch.getName();
            //add edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0);
            editButton = new Button(getActivity());
            editButton.setText(p.getName() + "\n" + therapyBranch);
            editButton.setTextSize(16);
            editButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            editButton.setMinEms(8);
            editButton.setMaxEms(8);
            editButton.setBackgroundResource(R.drawable.healthrecord_button);
            Drawable img = getResources().getDrawable(R.drawable.doctor);
            editButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.akoudri.healthrecord.app.UpdateTherapist");
                    intent.putExtra("therapistId", id);
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
            //add remove button
            colSpec = GridLayout.spec(1);
            removeButton = new ImageButton(getActivity());
            removeButton.setBackgroundResource(R.drawable.remove);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.removing)
                            .setMessage(getResources().getString(R.string.remove_question)
                                    + " " + p.getName() + "?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dataSource.getPersonTherapistTable().removeRelation(personId, p.getId());
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
            //Phone Button
            colSpec = GridLayout.spec(2);
            phoneButton = new ImageButton(getActivity());
            phoneButton.setBackgroundResource(R.drawable.phone);
            if (p.getPhoneNumber() != null) {
                phoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + p.getPhoneNumber()));
                        startActivity(intent);
                    }
                });
            }
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.LEFT);
            phoneButton.setLayoutParams(params);
            layout.addView(phoneButton);
            //next line
            r++;
        }
    }

    public void addTherapist(View view)
    {
        //FIXME: use instead a startActivityForResult to not reload all widgets
        Intent intent = new Intent("com.akoudri.healthrecord.app.AddTherapist");
        intent.putExtra("personId", personId);
        startActivity(intent);
    }

}
