package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Person;

import java.sql.SQLException;
import java.util.List;

public class MainActivity extends Activity {

    private static final String dbLoaded = "DB_LOADED";

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private SharedPreferences prefs;
    private HealthRecordDataSource dataSource;

    private boolean isDbLoaded = false;
    private boolean dataSourceLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        dataSource = new HealthRecordDataSource(this);
        layout = (GridLayout) findViewById(R.id.person_grid);
        Context context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        isDbLoaded = prefs.getBoolean(dbLoaded, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dataSource.open();
            dataSourceLoaded = true;
            if (!isDbLoaded)
            {
                preloadDb();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(dbLoaded, true);
                editor.commit();
                isDbLoaded = true;
            }
            createWidgets();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dataSourceLoaded) {
            dataSource.close();
            dataSourceLoaded = false;
        }
    }

    //Called at first use to preload default data
    private void preloadDb() {
        String[] branches = getResources().getStringArray(R.array.branches);
        //Insertion from xml
        for (String b : branches)
        {
            dataSource.getTherapyBranchTable().insertTherapyBranch(b);
        }
        String[] illnesses = getResources().getStringArray(R.array.illnesses);
        for (String i : illnesses)
        {
            dataSource.getIllnessTable().insertIllness(i);
        }
        String[] drugs = getResources().getStringArray(R.array.drugs);
        for (String d : drugs)
        {
            dataSource.getDrugTable().insertDrug(d);
        }
    }

    private void createWidgets()
    {
        layout.removeAllViews();
        List<Person> allPersons = dataSource.getPersonTable().getAllPersons();
        int margin = 10;
        if (allPersons == null || allPersons.size() == 0)
            return;
        Button editButton;
        ImageButton removeButton;
        layout.setColumnCount(2);
        int r = 0; //row index
        for (final Person p : allPersons)
        {
            final int personId = p.getId();
            //add edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0);
            editButton = new Button(this);
            editButton.setText(p.getName());
            editButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            editButton.setTextSize(16);
            editButton.setMinEms(8);
            editButton.setMaxEms(8);
            editButton.setBackgroundResource(R.drawable.healthrecord_button);
            Drawable img = getResources().getDrawable(R.drawable.heart);
            editButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.akoudri.healthrecord.app.EditPerson");
                    intent.putExtra("personId", personId);
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
            removeButton = new ImageButton(this);
            removeButton.setBackgroundResource(R.drawable.remove);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.removing)
                            .setMessage(getResources().getString(R.string.remove_question)
                                    + " " + p.getName() + "?")
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dataSource.getPersonTherapistTable().removePersonRelations(personId);
                                    dataSource.getPersonTable().removePersonWithId(personId);
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

    public void addPerson(View view)
    {
        startActivity(new Intent("com.akoudri.healthrecord.app.AddPerson"));
    }

}
