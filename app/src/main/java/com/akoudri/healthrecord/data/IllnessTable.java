package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Koudri on 10/04/14.
 */
public class IllnessTable {

    private SQLiteDatabase db;

    //Table
    public static final String ILLNESS_TABLE = "illness";
    public static final String ILLNESS_ID = "_id";
    public static final String ILLNESS_NAME = "name";

    private String[] illnessCols = {ILLNESS_ID, ILLNESS_NAME};

    public IllnessTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createIllnessTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + ILLNESS_TABLE + " (");
        sb.append(ILLNESS_ID + " integer primary key autoincrement,");
        sb.append(ILLNESS_NAME + " text not null unique");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertIllness(String name)
    {
        ContentValues values = new ContentValues();
        values.put(ILLNESS_NAME, name);
        return db.insert(ILLNESS_TABLE, null, values);
    }

    public List<Illness> getAllIllnesses()
    {
        List<Illness> res = new ArrayList<Illness>();
        Cursor cursor = db.query(ILLNESS_TABLE, illnessCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToIllness(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public Illness getIllnessWithId(int illnessId)
    {
        Cursor cursor = db.query(ILLNESS_TABLE, illnessCols,
                ILLNESS_ID + "=" + illnessId, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToIllness(cursor);
        return null;
    }

    public int getIllnessId(String illnessName)
    {
        Cursor cursor = db.query(ILLNESS_TABLE, illnessCols,
                ILLNESS_NAME + "='" + illnessName + "'", null, null, null, null);
        if (cursor.moveToFirst())
            return cursor.getInt(0);
        return -1; //illness name not found
    }

    private Illness cursorToIllness(Cursor cursor)
    {
        Illness illness = new Illness();
        illness.setId(cursor.getInt(0));
        illness.setName(cursor.getString(1));
        return illness;
    }

}
