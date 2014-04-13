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
    public static final String THERAPIST_FIRSTNAME = "firstName";
    public static final String THERAPIST_LASTNAME = "lastName";
    public static final String THERAPIST_PHONENUMBER = "phoneNumber";
    public static final String THERAPIST_BRANCHID = "branchId";

    private String[] therapistCols = {THERAPIST_ID, THERAPIST_FIRSTNAME,THERAPIST_LASTNAME,THERAPIST_PHONENUMBER, THERAPIST_BRANCHID};

    public TherapistTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createTherapistTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists" + THERAPIST_TABLE + " (");
        sb.append(THERAPIST_ID + " integer primary key autoincrement,");
        sb.append(THERAPIST_FIRSTNAME + " text,");
        sb.append(THERAPIST_LASTNAME + " text not null,");
        sb.append(THERAPIST_PHONENUMBER + " text,");
        sb.append(THERAPIST_BRANCHID + " integer not null,");
        sb.append("unique(" + THERAPIST_FIRSTNAME + "," + THERAPIST_LASTNAME + "),");
        sb.append("foreign key(" + THERAPIST_BRANCHID + ") references "
                + TherapyBranchTable.THERAPYBRANCH_TABLE + "(" + TherapyBranchTable.THERAPYBRANCH_ID + ")");
        sb.append(");");
        //FIXME: to remove - only for debug purpose
        preload();
    }

    //FIXME: to remove - only for debug purpose
    private void preload()
    {
        insertTherapist("Hocine", "Koudri", "0169386556",0);
        insertTherapist("Marc", "Gamin", "0169386556",0);
    }

    public long insertTherapist(String firstName,String lastName, String phoneNumber, int branchId)
    {
        ContentValues values = new ContentValues();
        values.put(THERAPIST_FIRSTNAME, firstName);
        values.put(THERAPIST_LASTNAME, lastName);
        values.put(THERAPIST_PHONENUMBER, phoneNumber);
        values.put(THERAPIST_BRANCHID, branchId);
        return db.insert(THERAPIST_TABLE, null, values);
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

    private Therapist cursorToTherapist(Cursor cursor)
    {
        Therapist e = new Therapist();
        e.setId(cursor.getInt(0));
        e.setFirstName(cursor.getString(1));
        e.setLastName(cursor.getString(2));
        e.setPhoneNumber(cursor.getString(3));
        e.setBranchId(cursor.getInt(4));
        return e;
    }

}
