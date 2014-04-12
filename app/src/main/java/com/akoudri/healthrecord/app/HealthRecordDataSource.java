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

    public HealthRecordDataSource(Context context)
    {
        dbHelper = new HealthRecordDatabase(context);
    }

    public void open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public HealthRecordDatabase getDb()
    {
        return dbHelper;
    }

}
