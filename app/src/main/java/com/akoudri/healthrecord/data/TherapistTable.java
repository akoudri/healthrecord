package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Koudri on 12/04/14.
 */
public class TherapistTable {

    private SQLiteDatabase db;

    //Table
    public static final String THERAPIST_TABLE = "therapist";
    public static final String THERAPIST_ID = "_id";
    public static final String THERAPIST_NAME = "name";
    public static final String THERAPIST_PHONENUMBER = "phoneNumber";
    public static final String THERAPIST_CELLPHONENUMBER = "cellPhoneNumber";
    public static final String THERAPIST_EMAIL = "email_idle";
    public static final String THERAPIST_BRANCHID = "branchId";

    private String[] therapistCols = {THERAPIST_ID, THERAPIST_NAME,
            THERAPIST_PHONENUMBER, THERAPIST_CELLPHONENUMBER, THERAPIST_EMAIL, THERAPIST_BRANCHID};

    public TherapistTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createTherapistTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + THERAPIST_TABLE + " (");
        sb.append(THERAPIST_ID + " integer primary key autoincrement,");
        sb.append(THERAPIST_NAME + " text not null unique,");
        sb.append(THERAPIST_PHONENUMBER + " text,");
        sb.append(THERAPIST_CELLPHONENUMBER + " text,");
        sb.append(THERAPIST_EMAIL + " text,");
        sb.append(THERAPIST_BRANCHID + " integer not null,");
        sb.append("foreign key(" + THERAPIST_BRANCHID + ") references "
                + TherapyBranchTable.THERAPYBRANCH_TABLE + "(" + TherapyBranchTable.THERAPYBRANCH_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertTherapist(String name, String phoneNumber, String cellPhoneNumber, String email, int branchId)
    {
        ContentValues values = new ContentValues();
        values.put(THERAPIST_NAME, name);
        if (phoneNumber != null) values.put(THERAPIST_PHONENUMBER, phoneNumber);
        if (cellPhoneNumber != null) values.put(THERAPIST_CELLPHONENUMBER, cellPhoneNumber);
        if (email != null) values.put(THERAPIST_EMAIL, email);
        values.put(THERAPIST_BRANCHID, branchId);
        return db.insert(THERAPIST_TABLE, null, values);
    }

    public boolean updateTherapist(int therapistId, String name, String phoneNumber, String cellPhoneNumber, String email, int branchId)
    {
        ContentValues values = new ContentValues();
        values.put(THERAPIST_NAME, name);
        if (phoneNumber != null) values.put(THERAPIST_PHONENUMBER, phoneNumber);
        else values.putNull(THERAPIST_PHONENUMBER);
        if (cellPhoneNumber != null) values.put(THERAPIST_CELLPHONENUMBER, cellPhoneNumber);
        else values.putNull(THERAPIST_CELLPHONENUMBER);
        if (email != null) values.put(THERAPIST_EMAIL, email);
        else values.putNull(THERAPIST_EMAIL);
        values.put(THERAPIST_BRANCHID, branchId);
        return db.update(THERAPIST_TABLE, values, THERAPIST_ID + "=" + therapistId, null) > 0;
    }

    public List<Therapist> getAllTherapists()
    {
        List<Therapist> res = new ArrayList<Therapist>();
        Cursor cursor = db.query(THERAPIST_TABLE, therapistCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToTherapist(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public Therapist getTherapistWithId(int therapistId)
    {
        Cursor cursor = db.query(THERAPIST_TABLE, therapistCols,
                THERAPIST_ID + "=" + therapistId, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToTherapist(cursor);
        return null;
    }

    private Therapist cursorToTherapist(Cursor cursor)
    {
        Therapist e = new Therapist();
        e.setId(cursor.getInt(0));
        e.setName(cursor.getString(1));
        e.setPhoneNumber(cursor.getString(2));
        e.setCellPhoneNumber(cursor.getString(3));
        e.setEmail(cursor.getString(4));
        e.setBranchId(cursor.getInt(5));
        return e;
    }

}
