package com.akoudri.healthrecord.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.akoudri.healthrecord.view.CalendarView;

//FIXME: for all classes, provide a better management of exceptions

public class MyCalendarActivity extends Activity {

    private int personId = 0;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        personId = getIntent().getIntExtra("personId", 0);
        //FIXME: the 4 following lines have been added because setting style
        //through xml file does not work
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff2c2d28")));
        calendarView = new CalendarView(this, null);
        setContentView(calendarView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_calendar, menu);
        menu.getItem(0).setIcon(R.drawable.doctor);
        menu.getItem(1).setIcon(R.drawable.analysis);
        menu.getItem(2).setIcon(R.drawable.gear);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.calendar_my_therapists:
                intent = new Intent("com.akoudri.healthrecord.app.MyTherapists");
                intent.putExtra("personId", personId);
                startActivity(intent);
                return true;
            case R.id.calendar_personal_data:
                intent = new Intent("com.akoudri.healthrecord.app.UpdatePerson");
                intent.putExtra("personId", personId);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

}
