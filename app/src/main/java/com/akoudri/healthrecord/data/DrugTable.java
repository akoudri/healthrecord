package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Koudri on 15/06/14.
 */
public class DrugTable {

    private SQLiteDatabase db;

    //Table
    public static final String DRUG_TABLE = "drug";
    public static final String DRUG_ID = "_id";
    public static final String DRUG_NAME = "name";

    private String[] drugCols = {DRUG_ID, DRUG_NAME};

    public DrugTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createDrugTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + DRUG_TABLE + " (");
        sb.append(DRUG_ID + " integer primary key autoincrement,");
        sb.append(DRUG_NAME + " text not null unique");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertDrug(String name)
    {
        ContentValues values = new ContentValues();
        values.put(DRUG_NAME, name);
        return db.insert(DRUG_TABLE, null, values);
    }

    public boolean updateDrug(int drugId, String name)
    {
        ContentValues values = new ContentValues();
        values.put(DRUG_NAME, name);
        return db.update(DRUG_TABLE, values, DRUG_ID + "=" + drugId, null) > 0;
    }

    public List<Drug> getAllDrugs()
    {
        List<Drug> res = new ArrayList<Drug>();
        Cursor cursor = db.query(DRUG_TABLE, drugCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToDrug(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public Drug getDrugWithId(int drugId)
    {
        Cursor cursor = db.query(DRUG_TABLE, drugCols,
                DRUG_ID + "=" + drugId, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToDrug(cursor);
        return null;
    }

    private Drug cursorToDrug(Cursor cursor)
    {
        Drug d = new Drug();
        d.setId(cursor.getInt(0));
        d.setName(cursor.getString(1));
        return d;
    }

}
