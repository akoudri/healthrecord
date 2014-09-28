package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Person;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//STATUS: checked
public class TherapistActivity extends Activity {

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;
    private Person person;

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
            person = dataSource.getPersonTable().getPersonWithId(personId);
            createWidgets();
        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
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
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, this);
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
        int tsize = 16;
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
            therapistButton.setTextSize(tsize);
            therapistButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            therapistButton.setTypeface(null, Typeface.BOLD);
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
                        new AlertDialog.Builder(TherapistActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.removing)
                                .setMessage(getResources().getString(R.string.remove_question)
                                        + " " + p.getName() + "?")
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataSource.getPersonTherapistTable().removeRelation(personId, p.getId());
                                        //Toast.makeText(TherapistActivity.this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
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
                    smsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Send message without launching sms client
                            //Use <uses-permission android:name="android.permission.SEND_SMS" /> in manifest
                            /*
                            FragmentTransaction ft = getFragmentManager().beginTransaction();
                            DialogFragment fragment = SmsMessageDialog.newInstance(p.getCellPhoneNumber());
                            fragment.show(ft, "Sms dialog");
                            */
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:"+p.getCellPhoneNumber()));
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
                            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", p.getEmail(), null));
                            intent.putExtra(Intent.EXTRA_SUBJECT, person.getName());
                            startActivity(Intent.createChooser(intent, "email"));
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
