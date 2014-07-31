package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TherapistActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;

    private int therapistId = 0;

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_therapist);
        layout = (GridLayout) findViewById(R.id.my_therapists_grid);
        dataSource = HealthRecordDataSource.getInstance(this);
        personId = getIntent().getIntExtra("personId", 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dataSource.open();
            dataSourceLoaded = true;
            createWidgets();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (! dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    private void createWidgets()
    {
        layout.removeAllViews();
        List<Therapist> allTherapists = new ArrayList<Therapist>();
        int margin = 1;
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
            if (therapyBranch.length() > 20) therapyBranch = therapyBranch.substring(0,20) + "...";
            //add edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0,4);
            therapistButton = new Button(this);
            String tName = p.getName();
            if (tName.length() > 20) tName = tName.substring(0,20) + "...";
            therapistButton.setText(tName + "\n" + therapyBranch);
            therapistButton.setTextSize(16);
            therapistButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            therapistButton.setMinEms(10);
            therapistButton.setMaxEms(10);
            therapistButton.setBackgroundResource(R.drawable.healthrecord_button);
            //Drawable img = getResources().getDrawable(R.drawable.doctor);
            //therapistButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            therapistButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int thId = TherapistActivity.this.therapistId;
                    if (thId == id) TherapistActivity.this.therapistId = 0;
                    else TherapistActivity.this.therapistId = id;
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
                removeButton = new ImageButton(this);
                removeButton.setBackgroundResource(R.drawable.remove);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(getApplicationContext())
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
                phoneButton = new ImageButton(this);
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
                } else phoneButton.setEnabled(false);
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.setGravity(Gravity.LEFT);
                phoneButton.setLayoutParams(params);
                layout.addView(phoneButton);
                //Cellphone Button
                colSpec = GridLayout.spec(1);
                cellphoneButton = new ImageButton(this);
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
                } else cellphoneButton.setEnabled(false);
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.setGravity(Gravity.LEFT);
                cellphoneButton.setLayoutParams(params);
                layout.addView(cellphoneButton);
                //Sms Button
                colSpec = GridLayout.spec(2);
                smsButton = new ImageButton(this);
                smsButton.setBackgroundResource(R.drawable.sms);
                if (p.getCellPhoneNumber() != null) {
                    phoneButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //FIXME: send sms
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + p.getPhoneNumber()));
                            startActivity(intent);
                        }
                    });
                } else smsButton.setEnabled(false);
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.setGravity(Gravity.LEFT);
                smsButton.setLayoutParams(params);
                layout.addView(smsButton);
                //Email Button
                colSpec = GridLayout.spec(3);
                emailButton = new ImageButton(this);
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
                } else emailButton.setEnabled(false);
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.setGravity(Gravity.LEFT);
                emailButton.setLayoutParams(params);
                layout.addView(emailButton);
                //Edit Button
                colSpec = GridLayout.spec(4);
                editButton = new ImageButton(this);
                editButton.setBackgroundResource(R.drawable.edit);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(TherapistActivity.this, EditTherapistActivity.class);
                        intent.putExtra("therapistId", id);
                        startActivity(intent);
                    }
                });
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.setGravity(Gravity.LEFT);
                editButton.setLayoutParams(params);
                layout.addView(editButton);
            }
            //next line
            r++;
        }
    }

    public void addNewTherapist(View view)
    {
        therapistId = 0;
        Intent intent = new Intent(this, CreateTherapistActivity.class);
        intent.putExtra("personId", personId);
        startActivity(intent);
    }

}
