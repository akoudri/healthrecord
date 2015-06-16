package com.akoudri.healthrecord.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akoudri.healthrecord.app.HealthRecordDataSource;
import com.akoudri.healthrecord.app.PersonManager;
import com.akoudri.healthrecord.app.R;
import com.akoudri.healthrecord.data.Therapist;
import com.akoudri.healthrecord.data.TherapyBranch;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;


public class ChooseTherapistActivity extends Activity {

    private GridLayout layout;
    private GridLayout.LayoutParams params;
    private GridLayout.Spec rowSpec, colSpec;

    private HealthRecordDataSource dataSource;
    private boolean dataSourceLoaded = false;

    private List<Therapist> otherTherapists;
    private CheckBox[] cb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_therapist);
        layout = (GridLayout) findViewById(R.id.existing_therapists_grid);
        dataSource = HealthRecordDataSource.getInstance(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            dataSource.open();
            dataSourceLoaded = true;
            retrieveOtherTherapists();
            createWidgets();
        } catch (SQLException e) {
            Toast.makeText(this, getResources().getString(R.string.database_access_impossible), Toast.LENGTH_SHORT).show();
        }
    }

    private void createWidgets()
    {
        if (otherTherapists.size() == 0) return;
        layout.removeAllViews();
        TextView therapistLabel;
        TherapyBranch branch;
        String therapyBranch;
        cb = new CheckBox[otherTherapists.size()];
        int margin = (int) HealthRecordUtils.convertPixelsToDp(2, this);
        layout.setColumnCount(2);
        int r = 0; //row index
        int tsize = 16;
        for (final Therapist p : otherTherapists) {
            final int id = p.getId();
            branch = dataSource.getTherapyBranchTable().getBranchWithId(p.getBranchId());
            therapyBranch = branch.getName();
            if (therapyBranch.length() > 20) therapyBranch = therapyBranch.substring(0, 20) + "...";
            //add checkbox
            cb[r] = new CheckBox(this);
            //cb[r].setBackground(getResources().getDrawable(R.drawable.custom_checkbox));
            rowSpec = GridLayout.spec(r);
            colSpec = GridLayout.spec(0);
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.LEFT);
            cb[r].setLayoutParams(params);
            layout.addView(cb[r]);
            //add label
            colSpec = GridLayout.spec(1);
            therapistLabel = new TextView(this);
            String tName = p.getName();
            if (tName.length() > 20) tName = tName.substring(0, 20) + "...";
            therapistLabel.setText(tName + " - " + therapyBranch);
            therapistLabel.setTextSize(tsize);
            therapistLabel.setTextColor(getResources().getColor(R.color.regular_button_text_color));
            therapistLabel.setTypeface(null, Typeface.BOLD);
            params = new GridLayout.LayoutParams(rowSpec, colSpec);
            params.rightMargin = margin;
            params.leftMargin = margin;
            params.topMargin = margin;
            params.bottomMargin = margin;
            params.setGravity(Gravity.LEFT);
            therapistLabel.setLayoutParams(params);
            layout.addView(therapistLabel);
            //new row
            r ++;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!dataSourceLoaded) return;
        dataSource.close();
        dataSourceLoaded = false;
    }

    private void retrieveOtherTherapists() {
        //retrieve all therapists
        otherTherapists = dataSource.getTherapistTable().getAllTherapists();
        int personId = PersonManager.getInstance().getPerson().getId();
        List<Integer> myTherapistIds = dataSource.getPersonTherapistTable().getTherapistIdsForPersonId(personId);
        Therapist t;
        Iterator<Therapist> iterator = otherTherapists.iterator();
        //remove related therapists
        while (iterator.hasNext())
        {
            t = iterator.next();
            if (myTherapistIds.contains(t.getId()))
                iterator.remove();
        }
    }

    public void addExistingTherapist(View view)
    {
        if (!dataSourceLoaded) return;
        if (cb == null || cb.length == 0) return;
        Therapist th;
        for (int i = 0; i < cb.length; i++)
        {
            if (cb[i].isChecked())
            {
                th = otherTherapists.get(i);
                int personId = PersonManager.getInstance().getPerson().getId();
                dataSource.getPersonTherapistTable().insertRelation(personId,th.getId());
            }
        }
        Toast.makeText(this.getApplicationContext(), getResources().getString(R.string.data_saved), Toast.LENGTH_SHORT).show();
        finish();
    }

}