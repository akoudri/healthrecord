package com.akoudri.healthrecord.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.data.PersonTable;
import com.akoudri.healthrecord.data.TherapyBranchTable;

import java.sql.SQLException;

/**
 * Created by Ali Koudri on 01/04/14.
 */
public class HealthRecordDataSource {

    private SQLiteDatabase db;
    private HealthRecordDatabase dbHelper;
    private PersonTable personTable;
    private TherapyBranchTable therapyBranchTable;

    public HealthRecordDataSource(Context context)
    {
        dbHelper = new HealthRecordDatabase(context);
    }

    public void open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        personTable = new PersonTable(db);
        therapyBranchTable = new TherapyBranchTable(db);
    }

    public void close()
    {
        therapyBranchTable = null;
        personTable = null;
        dbHelper.close();
    }

    public PersonTable getPersonTable()
    {
        return personTable;
    }

    public TherapyBranchTable getTherapyBranchTable()
    {
        return therapyBranchTable;
    }

}
