package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Koudri on 10/04/14.
 */
public class TherapyBranchTable {

    private SQLiteDatabase db;

    //Table
    public static final String THERAPYBRANCH_TABLE = "therapybranch";
    public static final String THERAPYBRANCH_ID = "_id";
    public static final String THERAPYBRANCH_NAME = "name";

    private String[] therapybranchCols = {THERAPYBRANCH_ID, THERAPYBRANCH_NAME};

    public TherapyBranchTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createTherapyBranchTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + THERAPYBRANCH_TABLE + " (");
        sb.append(THERAPYBRANCH_ID + " integer primary key autoincrement,");
        sb.append(THERAPYBRANCH_NAME + " text not null unique");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertTherapyBranch(String name)
    {
        ContentValues values = new ContentValues();
        values.put(THERAPYBRANCH_NAME, name);
        return db.insert(THERAPYBRANCH_TABLE, null, values);
    }

    public List<TherapyBranch> getAllBranches()
    {
        List<TherapyBranch> res = new ArrayList<TherapyBranch>();
        Cursor cursor = db.query(THERAPYBRANCH_TABLE, therapybranchCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToTherapyBranch(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public TherapyBranch getBranchWithId(int branchId)
    {
        Cursor cursor = db.query(THERAPYBRANCH_TABLE, therapybranchCols,
                THERAPYBRANCH_ID + "=" + branchId, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToTherapyBranch(cursor);
        return null;
    }

    public int getBranchId(String branchName)
    {
        Cursor cursor = db.query(THERAPYBRANCH_TABLE, therapybranchCols,
                THERAPYBRANCH_NAME + "='" + branchName + "'", null, null, null, null);
        if (cursor.moveToFirst())
            return cursor.getInt(0);
        return -1; //branch name not found
    }

    private TherapyBranch cursorToTherapyBranch(Cursor cursor)
    {
        TherapyBranch tb = new TherapyBranch();
        tb.setId(cursor.getInt(0));
        tb.setName(cursor.getString(1));
        return tb;
    }

}
