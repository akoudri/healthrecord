package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koudri on 12/04/14.
 */
public class PersonTherapistTable {

    private SQLiteDatabase db;

    //Person Therapist relationship table
    public static final String PERSON_THERAPIST_TABLE = "person_therapist";
    public static final String PT_PERSON_REF = "personId";
    public static final String PT_THERAPIST_REF = "therapistId";

    private String[] personTherapistCols = {PT_PERSON_REF, PT_THERAPIST_REF};

    public PersonTherapistTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createPersonTherapistTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + PERSON_THERAPIST_TABLE + "(");
        sb.append(PT_PERSON_REF + " integer not null,");
        sb.append(PT_THERAPIST_REF + " integer not null,");
        sb.append("unique (" + PT_PERSON_REF + ", " + PT_THERAPIST_REF + "),");
        sb.append("foreign key(" + PT_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
            "(" + PersonTable.PERSON_ID + "),");
        sb.append("foreign key(" + PT_THERAPIST_REF + ") references " + TherapistTable.THERAPIST_TABLE +
                "(" + TherapistTable.THERAPIST_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertRelation(int personId, int therapistId)
    {
        ContentValues values = new ContentValues();
        values.put(PT_PERSON_REF, personId);
        values.put(PT_THERAPIST_REF, therapistId);
        return db.insert(PERSON_THERAPIST_TABLE, null, values);
    }

    public boolean removeRelation(int personId, int therapistId)
    {
        String req = PT_PERSON_REF + "=" + personId + " and " + PT_THERAPIST_REF + "=" + therapistId;
        return db.delete(PERSON_THERAPIST_TABLE, req, null) > 0;
    }

    public boolean removePersonRelations(int personId)
    {
        String req = PT_PERSON_REF + "=" + personId;
        return db.delete(PERSON_THERAPIST_TABLE, req, null) > 0;
    }

    public List<Integer> getTherapistIdsForPersonId(int personId)
    {
        List<Integer> res = new ArrayList<Integer>();
        Cursor cursor = db.query(PERSON_THERAPIST_TABLE, personTherapistCols,
                PT_PERSON_REF + "=" + personId, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(new Integer(cursor.getInt(1)));
            cursor.moveToNext();
        }
        return res;
    }

}
