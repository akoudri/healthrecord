package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
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
import com.akoudri.healthrecord.app.PersonManager;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Appointment;
import com.akoudri.healthrecord.data.Person;
import com.akoudri.healthrecord.data.PersonTable;
import com.akoudri.healthrecord.utils.HealthRecordUtils;
import com.akoudri.healthrecord.utils.KeyManager;
import com.akoudri.healthrecord.utils.NotificationPublisher;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.apache.commons.lang3.RandomStringUtils;

import java.sql.SQLException;
import java.util.List;

//Ads

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
    private Person person = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        dataSource = HealthRecordDataSource.getInstance(this);
        layout = (GridLayout) findViewById(R.id.person_grid);
        Context context = getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        isFirstLoad = prefs.getBoolean(firstLoad, false);
        //Ads
        AdView adView = (AdView)this.findViewById(R.id.main_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

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
        calendarButton = new ImageButton(this);
        calendarButton.setBackgroundResource(R.drawable.calendar);
        therapistButton = new ImageButton(this);
        therapistButton.setBackgroundResource(R.drawable.doctor);
        analysisButton = new ImageButton(this);
        analysisButton.setBackgroundResource(R.drawable.analysis);
        editButton = new ImageButton(this);
        editButton.setBackgroundResource(R.drawable.edit);
        removeButton = new ImageButton(this);
        removeButton.setBackgroundResource(R.drawable.remove);
        Drawable d = getResources().getDrawable(R.drawable.calendar);
        int imgWidth = d.getIntrinsicWidth();
        int mWidth = 5 * imgWidth + 8 * margin;
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
            personButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int id = MainActivity.this.personId;
                    if (id == personId)
                    {
                        MainActivity.this.personId = 0;
                        person = null;
                    }
                    else
                    {
                        MainActivity.this.personId = personId;
                        person = dataSource.getPersonTable().getPersonWithId(personId);
                        PersonManager.getInstance().setPerson(person);
                        createWidgets();
                    }
                }
            });
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.width = mWidth;
            params.setGravity(Gravity.CENTER);
            personButton.setLayoutParams(params);
            layout.addView(personButton);
            if (this.personId == personId) {
                //next line
                r++;
                //Calendar Button
                rowSpec = GridLayout.spec(r);
                colSpec = GridLayout.spec(0);

                calendarButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
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

                therapistButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, TherapistActivity.class);
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

                analysisButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, AnalysisActivity.class);
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

                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, EditPersonActivity.class);
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
                                        for (Appointment appt : dataSource.getAppointmentTable().getAllAppointmentsForPerson(personId))
                                        {
                                            Notification.Builder builder = new Notification.Builder(MainActivity.this);
                                            long alarm = HealthRecordUtils.datehourToCalendar(appt.getDate(), appt.getHour()).getTimeInMillis() - 7200000;
                                            int apptId = appt.getId();
                                            builder.setSmallIcon(R.drawable.health_record_app)
                                                    .setContentTitle(dataSource.getTherapistTable().getTherapistWithId(appt.getTherapistId()).getName() + " @ " + appt.getHour())
                                                    .setWhen(alarm)
                                                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                                            Notification notification = builder.build();
                                            Intent notificationIntent = new Intent(MainActivity.this, NotificationPublisher.class);
                                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, apptId);
                                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
                                            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, apptId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                            AlarmManager alarmManager = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
                                            alarmManager.cancel(pendingIntent);
                                        }
                                        dataSource.getPersonTherapistTable().removePersonRelations(personId);//FIXME: manage deletion with trigger
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
