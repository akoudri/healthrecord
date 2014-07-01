package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Koudri on 03/04/14.
 */
public class PersonTable {

    private SQLiteDatabase db;

    //person table
    public static final String PERSON_TABLE = "person";
    public static final String PERSON_ID = "_id";
    public static final String PERSON_NAME = "name";
    public static final String PERSON_GENDER = "gender";
    public static final String PERSON_SSN = "ssn";
    public static final String PERSON_BLOODTYPE = "bloodType";
    public static final String PERSON_BIRTHDATE = "birthdate";

    private String[] personCols = {PERSON_ID, PERSON_NAME,
            PERSON_GENDER, PERSON_SSN,
            PERSON_BLOODTYPE, PERSON_BIRTHDATE};

    public PersonTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createPersonTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + PERSON_TABLE + "(");
        sb.append(PERSON_ID + " integer primary key autoincrement,");
        sb.append(PERSON_NAME + " text not null unique,");
        sb.append(PERSON_GENDER + " int not null,");
        sb.append(PERSON_SSN + " text unique,");
        sb.append(PERSON_BLOODTYPE + " int,");
        sb.append(PERSON_BIRTHDATE + " text not null");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertPerson(String name, Gender gender,
                             String ssn, BloodType bloodType, String birthdate) {
        ContentValues values = new ContentValues();
        values.put(PERSON_NAME, name);
        values.put(PERSON_GENDER, gender.ordinal());
        if (ssn != null)
            values.put(PERSON_SSN, ssn);
        values.put(PERSON_BLOODTYPE, bloodType.ordinal());
        values.put(PERSON_BIRTHDATE, birthdate);
        return db.insert(PERSON_TABLE, null, values);
    }

    public boolean updatePerson(int personId, String name, Gender gender,
                                String ssn, BloodType bloodType, String birthdate)
    {
        ContentValues values = new ContentValues();
        values.put(PERSON_NAME, name);
        values.put(PERSON_GENDER, gender.ordinal());
        if (ssn != null)
            values.put(PERSON_SSN, ssn);
        values.put(PERSON_BLOODTYPE, bloodType.ordinal());
        values.put(PERSON_BIRTHDATE, birthdate);
        return db.update(PERSON_TABLE, values, PERSON_ID + "=" + personId, null) > 0;
    }

    public List<Person> getAllPersons()
    {
        List<Person> res = new ArrayList<Person>();
        Cursor cursor = db.query(PERSON_TABLE, personCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToPerson(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public Person getPersonWithId(int personId)
    {
        Cursor cursor = db.query(PERSON_TABLE, personCols,
                PERSON_ID + "=" + personId, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToPerson(cursor);
        return null;
    }

    public boolean removePersonWithId(int personId)
    {
        return db.delete(PERSON_TABLE, PERSON_ID + "=" + personId, null) > 0;
    }

    private Person cursorToPerson(Cursor cursor)
    {
        Person person  = new Person();
        person.setId(cursor.getInt(0));
        person.setName(cursor.getString(1));
        int gender = cursor.getInt(2);
        switch (gender)
        {
            case 0: person.setGender(Gender.MALE); break;
            default: person.setGender(Gender.FEMALE);
        }
        person.setSsn(cursor.getString(3));
        int bloodType = cursor.getInt(4);
        person.setBloodType(HealthRecordUtils.int2bloodType(bloodType));
        person.setBirthdate(cursor.getString(5));
        return person;
    }

}
