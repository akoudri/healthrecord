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
    public static final String THERAPYBRANCH_EN = "en";
    public static final String THERAPYBRANCH_FR = "fr";

    private String[] therapybranchCols = {THERAPYBRANCH_ID, THERAPYBRANCH_EN, THERAPYBRANCH_FR};

    public TherapyBranchTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createTherapyBranchTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + THERAPYBRANCH_TABLE + " (");
        sb.append(THERAPYBRANCH_ID + " integer primary key autoincrement,");
        sb.append(THERAPYBRANCH_EN + " text not null,");
        sb.append(THERAPYBRANCH_FR + " text not null,");
        sb.append("unique(" + THERAPYBRANCH_EN + ", " + THERAPYBRANCH_FR + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertTherapyBranch(String en, String fr)
    {
        ContentValues values = new ContentValues();
        values.put(THERAPYBRANCH_EN, en);
        values.put(THERAPYBRANCH_FR, fr);
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

    private TherapyBranch cursorToTherapyBranch(Cursor cursor)
    {
        TherapyBranch tb = new TherapyBranch();
        tb.setId(cursor.getInt(0));
        tb.setEn(cursor.getString(1));
        tb.setFr(cursor.getString(2));
        return tb;
    }

}
