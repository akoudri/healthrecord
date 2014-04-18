package com.akoudri.healthrecord.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;

import com.akoudri.healthrecord.data.BloodType;
import com.akoudri.healthrecord.data.Gender;
import com.akoudri.healthrecord.data.Person;

import java.sql.SQLException;
import java.util.List;

//FIXME: for all classes, provide a better management of exceptions

public class MainActivity extends ActionBarActivity {

    private HealthRecordDataSource dataSource;
    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;
    private static final String dbLoaded = "DB_LOADED";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        dataSource = new HealthRecordDataSource(this);
        layout = (GridLayout) findViewById(R.id.person_grid);
        Context context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isDbLoaded = prefs.getBoolean(dbLoaded, false);
        if (!isDbLoaded)
        {
            preloadDb();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(dbLoaded, true);
            editor.commit();
        }
        populateWidgets();
    }

    //TODO: complete the loading of default tuples into the DB
    //It is called once
    private void preloadDb() {
        String[] branches = getResources().getStringArray(R.array.branches);
        try {
            dataSource.open();
            //Insertion from xml
            for (String b : branches)
            {
                dataSource.getTherapyBranchTable().insertTherapyBranch(b);
                //FIXME: for debug only - to remove
                dataSource.getPersonTable().insertPerson("Ali", Gender.MALE, "ssn1", BloodType.ABPLUS, "27/08/1974");
                dataSource.getTherapistTable().insertTherapist("Hocine", "0169386556", 1);
                dataSource.getPersonTherapistTable().insertRelation(1,1);
            }
            dataSource.close();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }

    private void populateWidgets()
    {
        layout.removeAllViews();
        List<Person> allPersons = null;
        int margin = 10;
        try {
            dataSource.open();
            allPersons = dataSource.getPersonTable().getAllPersons();
            dataSource.close();
        } catch (SQLException ex)
        {
            ex.printStackTrace();
        }
        if (allPersons == null || allPersons.size() == 0)
            return;
        Button editButton;
        ImageButton removeButton;
        /*
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        int labelWidth = screenSize.x * 3 /5;
        int buttonWidth = screenSize.x / 5;
        */
        layout.setColumnCount(2);
        int r = 0; //row index
        for (final Person p : allPersons)
        {
            final int id = p.getId();
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
            //editButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            Drawable img = getResources().getDrawable(R.drawable.heart);
            editButton.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent("com.akoudri.healthrecord.app.EditPerson");
                    intent.putExtra("personId", id);
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

    @Override
    protected void onResume() {
        super.onResume();
        //FIXME: add just corresponding widget from adding new person - see AddPerson
        populateWidgets();
    }

    public void addPerson(View view)
    {
        //FIXME: use instead a startActivityForResult to not reload all widgets
        startActivity(new Intent("com.akoudri.healthrecord.app.AddPerson"));
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
