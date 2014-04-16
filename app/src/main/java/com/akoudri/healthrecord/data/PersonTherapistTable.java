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
    public static final String PERSON_REF = "personId";
    public static final String THERAPIST_REF = "therapistId";

    private String[] personTherapistCols = {PERSON_REF, THERAPIST_REF};

    public PersonTherapistTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createPersonTherapistTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + PERSON_THERAPIST_TABLE + "(");
        sb.append(PERSON_REF + " integer not null,");
        sb.append(THERAPIST_REF + " integer not null,");
        sb.append("unique (" + PERSON_REF + ", " + THERAPIST_REF + "),");
        sb.append("foreign key(" + PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
            "(" + PersonTable.PERSON_ID + "),");
        sb.append("foreign key(" + THERAPIST_REF + ") references " + TherapistTable.THERAPIST_TABLE +
                "(" + TherapistTable.THERAPIST_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertRelation(int personId, int therapistId)
    {
        ContentValues values = new ContentValues();
        values.put(PERSON_REF, personId);
        values.put(THERAPIST_REF, therapistId);
        return db.insert(PERSON_THERAPIST_TABLE, null, values);
    }

    public List<Integer> getTherapistIdsForPersonId(int personId)
    {
        List<Integer> res = new ArrayList<Integer>();
        Cursor cursor = db.query(PERSON_THERAPIST_TABLE, personTherapistCols,
                PERSON_REF + "=" + personId, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(new Integer(cursor.getInt(1)));
            cursor.moveToNext();
        }
        return res;
    }

}
