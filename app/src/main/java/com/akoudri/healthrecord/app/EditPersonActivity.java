package com.akoudri.healthrecord.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class EditPersonActivity extends Activity implements ActionBar.TabListener{

    private Fragment therapistsFrag, personalFrag;
    private int personId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person);
        personId = getIntent().getIntExtra("personId", 0);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab myTherapist = actionBar.newTab();
        myTherapist.setText("My Therapists"); //TODO: get from xml
        myTherapist.setTabListener(this);
        actionBar.addTab(myTherapist);
        ActionBar.Tab personalData = actionBar.newTab();
        personalData.setText("Personal Data");
        personalData.setTabListener(this);
        actionBar.addTab(personalData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        switch (tab.getPosition())
        {
            case 0:
                if (personalFrag == null)
                {
                    personalFrag = UpdatePersonFragment.newInstance();
                    fragmentTransaction.add(R.id.edit_layout, personalFrag, "Personal");
                }
                fragmentTransaction.attach(personalFrag);
                break;
            case 1:
                if (therapistsFrag == null)
                {
                    therapistsFrag = MyTherapistsFragment.newInstance();
                    fragmentTransaction.add(R.id.edit_layout, therapistsFrag, "Therapists");
                }
                fragmentTransaction.attach(therapistsFrag);
                break;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        switch (tab.getPosition())
        {
            case 0:
                if (personalFrag != null) fragmentTransaction.detach(personalFrag);
                break;
            case 1:
                if (therapistsFrag != null) fragmentTransaction.detach(therapistsFrag);
                break;
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        //Nothing to do
    }
}
