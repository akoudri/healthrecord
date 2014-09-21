package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.List;

//STATUS: checked
public class MainActivity extends Activity {

    private static final String dbLoaded = "DB_LOADED";

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private SharedPreferences prefs;
    private HealthRecordDataSource dataSource;

    private boolean isDbLoaded = false;
    private boolean dataSourceLoaded = false;

    private int personId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        dataSource = HealthRecordDataSource.getInstance(this);
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
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
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
        int margin = (int) HealthRecordUtils.convertPixelsToDp(3, this);
        if (allPersons == null || allPersons.size() == 0)
            return;
        Button personButton;
        ImageButton editButton, removeButton, calendarButton, therapistButton, analysisButton;
        layout.setColumnCount(5);
        int r = 0; //row index
        int tsize = 16;
        for (final Person p : allPersons)
        {
            final int personId = p.getId();
            //add edit button
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0,5);
            personButton = new Button(this);
            String name = p.getName();
            final String pName;
            if (name.length() > 25) pName = name.substring(0,25) + "...";
            else pName = name;
            personButton.setText(pName);
            personButton.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            personButton.setTextSize(tsize);
            personButton.setTypeface(null, Typeface.BOLD);
            personButton.setMinEms(13);
            personButton.setMaxEms(13);
            personButton.setBackgroundResource(R.drawable.healthrecord_button);
            //Drawable img = getResources().getDrawable(R.drawable.heart);
            //personButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            personButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = MainActivity.this.personId;
                    if (id == personId) MainActivity.this.personId = 0;
                    else MainActivity.this.personId = personId;
                    createWidgets();
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.CENTER);
            personButton.setLayoutParams(params);
            layout.addView(personButton);
            if (this.personId == personId) {
                //next line
                r++;
                //Calendar Button
                rowSpec = GridLayout.spec(r);
                colSpec = GridLayout.spec(0);
                calendarButton = new ImageButton(this);
                calendarButton.setBackgroundResource(R.drawable.calendar);
                calendarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                        intent.putExtra("personId", personId);
                        startActivity(intent);
                    }
                });
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.setGravity(Gravity.CENTER);
                calendarButton.setLayoutParams(params);
                layout.addView(calendarButton);
                //Therapist Button
                colSpec = GridLayout.spec(1);
                therapistButton = new ImageButton(this);
                therapistButton.setBackgroundResource(R.drawable.doctor);
                therapistButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, TherapistActivity.class);
                        intent.putExtra("personId", personId);
                        startActivity(intent);
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
                //Analysis Button
                colSpec = GridLayout.spec(2);
                analysisButton = new ImageButton(this);
                analysisButton.setBackgroundResource(R.drawable.analysis);
                analysisButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
                        intent.putExtra("personId", personId);
                        startActivity(intent);
                    }
                });
                params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.rightMargin = margin;
                params.leftMargin = margin;
                params.topMargin = margin;
                params.bottomMargin = margin;
                params.setGravity(Gravity.CENTER);
                analysisButton.setLayoutParams(params);
                layout.addView(analysisButton);
                //Edit Button
                colSpec = GridLayout.spec(3);
                editButton = new ImageButton(this);
                editButton.setBackgroundResource(R.drawable.edit);
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, EditPersonActivity.class);
                        intent.putExtra("personId", personId);
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
                //add remove button
                rowSpec = GridLayout.spec(r);
                colSpec = GridLayout.spec(4);
                removeButton = new ImageButton(this);
                removeButton.setBackgroundResource(R.drawable.remove);
                removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.removing)
                                .setMessage(getResources().getString(R.string.remove_question)
                                        + " " + pName + "?")
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dataSource.getPersonTherapistTable().removePersonRelations(personId);
                                        dataSource.getPersonTable().removePersonWithId(personId);
                                        //Toast.makeText(MainActivity.this, getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
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
                params.setGravity(Gravity.CENTER);
                removeButton.setLayoutParams(params);
                layout.addView(removeButton);
            }
            //next line
            r++;
        }
    }

    public void addPerson(View view)
    {
        startActivity(new Intent(this, CreatePersonActivity.class));
    }

}
