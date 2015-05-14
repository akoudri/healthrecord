package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.view.CalendarContentProvider;
import com.akoudri.healthrecord.view.CalendarView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.sql.SQLException;
import java.util.Calendar;

//Ads

/**
 * Created by Ali Koudri on 19/07/14.
 * STATUS: checked
 */
public class CalendarActivity extends Activity implements CalendarContentProvider {

    private CalendarView calendarView;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;

    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calendar);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        dataSource = HealthRecordDataSource.getInstance(this);
        personId = getIntent().getIntExtra("personId", 0);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        width = size.x;
        //Ads
        AdView adView = (AdView)this.findViewById(R.id.calendar_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (personId == 0)
            return;
        try {
            dataSource.open();
            dataSourceLoaded = true;
            calendarView.setPersonId(personId);
            calendarView.setCalendarContentProvider(this);
            calendarView.setWidth(width);

        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (personId == 0)
            return;
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    @Override
    public int[] getMonthAppointmentsForPerson(int personId, Calendar cal) {
        return dataSource.getAppointmentTable().getMonthAppointmentsForPerson(personId, cal);
    }

    @Override
    public int[] getMonthAilmentsForPerson(int personId, Calendar cal) {
        return dataSource.getAilmentTable().getMonthAilmentsForPerson(personId, cal);
    }

    @Override
    public int[] getMonthMeasuresForPerson(int personId, Calendar cal) {
        return dataSource.getMeasureView().getMonthMeasuresForPerson(personId, cal);
    }

    @Override
    public int[] getMonthMedicationsForPerson(int personId, Calendar cal) {
        return dataSource.getMedicationTable().getMonthMedicationsForPerson(personId, cal);
    }

    @Override
    public int[] getMonthObservationsForPerson(int personId, Calendar cal) {
        return dataSource.getMedicalObservationTable().getMonthObservationsForPerson(personId, cal);
    }

    @Override
    public int[] getMonthRemindersForPerson(int personId, Calendar cal) {
        return dataSource.getReminderTable().getMonthRemindersForPerson(personId, cal);
    }
}
