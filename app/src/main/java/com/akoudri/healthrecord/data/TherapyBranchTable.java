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
    public static final String THERAPYBRANCH_LANG = "lang";
    public static final String THERAPYBRANCH_NAME = "name";

    private String[] therapybranchCols = {THERAPYBRANCH_ID, THERAPYBRANCH_LANG, THERAPYBRANCH_NAME};

    public TherapyBranchTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createTherapyBranch()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists" + THERAPYBRANCH_TABLE + " (");
        sb.append(THERAPYBRANCH_ID + " integer primary key autoincrement,");
        sb.append(THERAPYBRANCH_LANG + " text not null,");
        sb.append(THERAPYBRANCH_NAME + " text not null,");
        sb.append("unique(" + THERAPYBRANCH_LANG + ", " + THERAPYBRANCH_NAME + "));");
        preload();
    }

    public long insertTherapyBranch(String lang, String name)
    {
        ContentValues values = new ContentValues();
        values.put(THERAPYBRANCH_LANG, lang);
        values.put(THERAPYBRANCH_NAME, name);
        return db.insert(THERAPYBRANCH_TABLE, null, values);
    }

    public List<TherapyBranch> getAllBranches(String lang)
    {
        List<TherapyBranch> res = new ArrayList<TherapyBranch>();
        Cursor cursor = db.query(THERAPYBRANCH_TABLE, therapybranchCols,
                THERAPYBRANCH_LANG + "=" + lang, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToTherapyBranch(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    private void preload()
    {
        //FIXME: preload from xml resources
        insertTherapyBranch("en", "Generalist");
        insertTherapyBranch("fr", "Généraliste");
    }

    private TherapyBranch cursorToTherapyBranch(Cursor cursor)
    {
        TherapyBranch tb = new TherapyBranch();
        tb.setId(cursor.getInt(0));
        tb.setLang(cursor.getString(1));
        tb.setName(cursor.getString(2));
        return tb;
    }

}
