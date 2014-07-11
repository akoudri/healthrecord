package com.akoudri.healthrecord.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;

import java.util.ArrayList;
import java.util.List;


public class TherapistFragment extends Fragment {

    private HealthRecordDataSource dataSource;
    private int personId;

    private int therapistId = 0;

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private View view;


    public static TherapistFragment newInstance()
    {
        return new TherapistFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_therapist, container, false);
        layout = (GridLayout) view.findViewById(R.id.my_therapists_grid);
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

    public void setDataSource(HealthRecordDataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    private void createWidgets()
    {
        layout.removeAllViews();
        List<Therapist> allTherapists = new ArrayList<Therapist>();
        int margin = 4;
        List<Integer> therapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
        for (Integer i : therapistIds)
        {
            allTherapists.add(dataSource.getTherapistTable().getTherapistWithId(i));
        }
        if (allTherapists == null || allTherapists.size() == 0)
            return;
        Button therapistButton;
        ImageButton removeButton, phoneButton, cellphoneButton, smsButton, emailButton, editButton;
        TherapyBranch branch = null;
        String therapyBranch;
        layout.setColumnCount(5);
        int r = 0; //row index
        for (final Therapist p : allTherapists)
        {
            final int id = p.getId();
            branch = dataSource.getTherapyBranchTable().getBranchWithId(p.getBranchId());
            therapyBranch = branch.getName();
            //add edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0,4);
            therapistButton = new Button(getActivity());
            therapistButton.setText(p.getName() + "\n" + therapyBranch);
            therapistButton.setTextSize(16);
            therapistButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            therapistButton.setMinEms(11);
            therapistButton.setMaxEms(11);
            therapistButton.setBackgroundResource(R.drawable.healthrecord_button);
            //Drawable img = getResources().getDrawable(R.drawable.doctor);
            //therapistButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            therapistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int thId = TherapistFragment.this.therapistId;
                    if (thId == id) TherapistFragment.this.therapistId = 0;
                    else TherapistFragment.this.therapistId = id;
                    createWidgets();
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.CENTER);
            therapistButton.setLayoutParams(params);
            layout.addView(therapistButton);
            if (this.therapistId == id) {
                //add remove button
                colSpec = GridLayout.spec(4);
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
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataSource.getPersonTherapistTable().removeRelation(personId, p.getId());
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
                //Next line
                r++;
                //Phone Button
                rowSpec = GridLayout.spec(r);
                colSpec = GridLayout.spec(0);
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
                params.setGravity(Gravity.CENTER);
                phoneButton.setLayoutParams(params);
                layout.addView(phoneButton);
                //Cellphone Button
                colSpec = GridLayout.spec(1);
                cellphoneButton = new ImageButton(getActivity());
                cellphoneButton.setBackgroundResource(R.drawable.cellphone);
                if (p.getCellPhoneNumber() != null) {
                    cellphoneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + p.getCellPhoneNumber()));
                            startActivity(intent);
                        }
                    });
                } else
                    cellphoneButton.setEnabled(false);
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.setGravity(Gravity.CENTER);
                cellphoneButton.setLayoutParams(params);
                layout.addView(cellphoneButton);
                //Sms Button
                colSpec = GridLayout.spec(2);
                smsButton = new ImageButton(getActivity());
                smsButton.setBackgroundResource(R.drawable.sms);
                if (p.getPhoneNumber() != null) {
                    phoneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //FIXME: send sms
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
                params.setGravity(Gravity.CENTER);
                smsButton.setLayoutParams(params);
                layout.addView(smsButton);
                //Email Button
                colSpec = GridLayout.spec(3);
                emailButton = new ImageButton(getActivity());
                emailButton.setBackgroundResource(R.drawable.email);
                if (p.getEmail() != null) {
                    emailButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //FIXME: send an email
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
                params.setGravity(Gravity.CENTER);
                emailButton.setLayoutParams(params);
                layout.addView(emailButton);
                //Edit Button
                colSpec = GridLayout.spec(4);
                editButton = new ImageButton(getActivity());
                editButton.setBackgroundResource(R.drawable.edit);
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
                params.setGravity(Gravity.CENTER);
                editButton.setLayoutParams(params);
                layout.addView(editButton);
            }
            //next line
            r++;
        }
    }

    public void addTherapist()
    {
        Intent intent = new Intent("com.akoudri.healthrecord.app.AddTherapist");
        intent.putExtra("personId", personId);
        startActivity(intent);
    }

}
