package com.akoudri.healthrecord.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;


public class EditPersonActivity extends Activity {

    private int personId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_person);
        personId = getIntent().getIntExtra("personId", 0);
    }

    public void updatePerson(View view)
    {
        Intent intent = new Intent("com.akoudri.healthrecord.app.UpdatePerson");
        intent.putExtra("personId", personId);
        startActivity(intent);
    }

    public void myTherapists(View view)
    {
        Intent intent = new Intent("com.akoudri.healthrecord.app.MyTherapists");
        intent.putExtra("personId", personId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_person, menu);
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
