package com.akoudri.healthrecord.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.akoudri.healthrecord.view.CalendarView;

//FIXME: for all classes, provide a better management of exceptions

public class MyCalendarActivity extends Activity {

    private int personId;
    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        calendarView = new CalendarView(this);
        setContentView(calendarView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //FIXME: to adapt
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
