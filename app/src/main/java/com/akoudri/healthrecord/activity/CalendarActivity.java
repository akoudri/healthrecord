package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.view.CalendarContentProvider;
import com.akoudri.healthrecord.view.CalendarView;

import java.sql.SQLException;
import java.util.Calendar;

/**
 * Created by Ali Koudri on 19/07/14.
 */
public class CalendarActivity extends Activity implements CalendarContentProvider {

    private CalendarView calendarView;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;
    private int personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calendar);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        dataSource = HealthRecordDataSource.getInstance(this);
        personId = getIntent().getIntExtra("personId", 0);
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

        } catch (SQLException e) {
            e.printStackTrace();
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
        return dataSource.getMeasureTable().getMonthMeasuresForPerson(personId, cal);
    }

    @Override
    public int[] getMonthMedicationsForPerson(int personId, Calendar cal) {
        return dataSource.getMedicationTable().getMonthMedicationsForPerson(personId, cal);
    }
}
