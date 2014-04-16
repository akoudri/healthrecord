package com.akoudri.healthrecord.app;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.akoudri.healthrecord.data.Person;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MyTherapistsActivity extends ActionBarActivity {

    private HealthRecordDataSource dataSource;
    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;
    private int personId;
    private Person person;
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_my_therapists);
        lang = Locale.getDefault().getDisplayName();
        dataSource = new HealthRecordDataSource(this);
        layout = (GridLayout) findViewById(R.id.my_therapists_grid);
        personId = getIntent().getIntExtra("personId", 0);
        retrievePerson();
        populateWidgets();
    }

    private void retrievePerson() {
        try {
            dataSource.open();
            person = dataSource.getPersonTable().getPersonWithId(personId);
            dataSource.close();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    //FIXME: should be called one time at first load only
    private void populateWidgets()
    {
        layout.removeAllViews();
        List<Therapist> allTherapists = new ArrayList<Therapist>();
        int margin = 5;
        try {
            dataSource.open();
            List<Integer> therapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
            for (Integer i : therapistIds)
            {
               allTherapists.add(dataSource.getTherapistTable().getTherapistWithId(i));
            }
            dataSource.close();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        if (allTherapists == null || allTherapists.size() == 0)
            return;
        Button editButton;
        ImageButton removeButton, phoneButton;
        int branchId;
        TherapyBranch branch = null;
        String therapyBranch;
        layout.setColumnCount(3);
        int r = 0; //row index
        for (final Therapist p : allTherapists)
        {
            final int id = p.getId();
            try {
                dataSource.open();
                branch = dataSource.getTherapyBranchTable().getBranchWithId(p.getBranchId());
                dataSource.close();
            } catch (SQLException ex)
            {
                ex.printStackTrace();
            }
            //FIXME: See if we can improve this
            if (lang.toLowerCase().startsWith("fr"))
                therapyBranch = branch.getFr();
            else
                therapyBranch = branch.getEn();
            //add edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0);
            editButton = new Button(this);
            editButton.setText(p.getFirstName() + " " +
                    p.getLastName() + "\n" + therapyBranch);
            editButton.setTextSize(16);
            editButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            editButton.setMinEms(8);
            editButton.setMaxEms(8);
            editButton.setBackgroundResource(R.drawable.healthrecord_button);
            Drawable img = getResources().getDrawable(R.drawable.plume);
            editButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            /*
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.akoudri.healthrecord.app.EditPerson");
                    intent.putExtra("personId", id);
                    startActivity(intent);
                }
            });
            */
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
            removeButton = new ImageButton(this);
            removeButton.setBackgroundResource(R.drawable.remove);
            //removeButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            /*
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(MyTherapistsActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.removing)
                            .setMessage(getResources().getString(R.string.remove_question)
                                    + " " + p.getFirstName() + "?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        dataSource.open();
                                        dataSource.getPersonTable().removePersonWithId(id);
                                        populateWidgets();
                                        dataSource.close();
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
            });
            */
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

    @Override
    protected void onResume() {
        super.onResume();
        //FIXME: add just corresponding widget from adding new person - see AddPerson
        populateWidgets();
    }

    public void addTherapist(View view)
    {
        //FIXME: use instead a startActivityForResult to not reload all widgets
        Intent intent = new Intent("com.akoudri.healthrecord.app.AddTherapist");
        intent.putExtra("personId", personId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds itemLast Names to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
