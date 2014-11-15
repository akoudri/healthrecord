package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.akoudri.healthrecord.utils.KeyManager;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.List;

//STATUS: checked
//TODO: replace grid layout with table layout
public class MainActivity extends Activity {

    private static final String firstLoad = "FIRST_LOAD";

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private SharedPreferences prefs;
    private HealthRecordDataSource dataSource;

    private boolean isFirstLoad = false;
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
        isFirstLoad = prefs.getBoolean(firstLoad, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dataSource.open();
            dataSourceLoaded = true;
            if (!isFirstLoad)
            {
                createSecurityFiles();
                preloadDb();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(firstLoad, true);
                editor.commit();
                isFirstLoad = true;
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

    private void createSecurityFiles()
    {
        //FIXME: change key/iv
        String key = RandomStringUtils.randomNumeric(32);
        String iv = RandomStringUtils.randomNumeric(16);
        KeyManager km = new KeyManager(getApplicationContext());
        km.setIv(iv.getBytes());
        km.setId(key.getBytes());
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
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_menu_about)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.about);
            builder.setMessage(R.string.made_by);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
            return true;
        }
        if (id == R.id.main_menu_send_mail)
        {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "akoudri@free.fr", null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Healthrecord App");
            startActivity(Intent.createChooser(intent, "email"));
        }
        return false;
    }
}
