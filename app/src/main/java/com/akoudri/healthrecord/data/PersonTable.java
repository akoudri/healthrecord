package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    public static final String PERSON_FIRSTNAME = "firstName";
    public static final String PERSON_LASTNAME = "lastName";
    public static final String PERSON_GENDER = "gender";
    public static final String PERSON_BLOODTYPE = "bloodType";
    public static final String PERSON_BIRTHDATE = "birthdate";

    private String[] personCols = {PersonTable.PERSON_ID, PersonTable.PERSON_FIRSTNAME,
            PersonTable.PERSON_LASTNAME, PersonTable.PERSON_GENDER,
            PersonTable.PERSON_BLOODTYPE, PersonTable.PERSON_BIRTHDATE};

    public PersonTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createPersonTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + PERSON_TABLE + "(");
        sb.append(PERSON_ID + " integer primary key autoincrement,");
        sb.append(PERSON_FIRSTNAME + " text not null,");
        sb.append(PERSON_LASTNAME + " text not null,");
        sb.append(PERSON_GENDER + " int not null,");
        sb.append(PERSON_BLOODTYPE + " int,");
        sb.append(PERSON_BIRTHDATE + " text not null,");
        sb.append("unique(" + PERSON_FIRSTNAME + "," + PERSON_LASTNAME + "));");
        db.execSQL(sb.toString());
    }

    public long insertPerson(String firstName, String lastName, Gender gender,
                             BloodType bloodType, String birthdate) {
        ContentValues values = new ContentValues();
        values.put(PersonTable.PERSON_FIRSTNAME, firstName);
        values.put(PersonTable.PERSON_LASTNAME, lastName);
        values.put(PersonTable.PERSON_GENDER, gender.ordinal());
        values.put(PersonTable.PERSON_BLOODTYPE, bloodType.ordinal());
        values.put(PersonTable.PERSON_BIRTHDATE, birthdate);
        return db.insert(PersonTable.PERSON_TABLE, null, values);
    }

    public List<Person> getAllPersons()
    {
        List<Person> res = new ArrayList<Person>();
        Cursor cursor = db.query(PersonTable.PERSON_TABLE, personCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToPerson(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public Person getPersonWithId(int id)
    {
        Cursor cursor = db.query(PersonTable.PERSON_TABLE, personCols,
                PERSON_ID + "=" + id, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToPerson(cursor);
        return null;
    }

    public boolean removePersonWithId(int id)
    {
        return db.delete(PERSON_TABLE, PERSON_ID + "=" + id, null) > 0;
    }

    private Person cursorToPerson(Cursor cursor)
    {
        Person person  = new Person();
        person.setId(cursor.getInt(0));
        person.setFirstName(cursor.getString(1));
        person.setLastName(cursor.getString(2));
        int gender = cursor.getInt(3);
        switch (gender)
        {
            case 0: person.setGender(Gender.MALE); break;
            default: person.setGender(Gender.FEMALE);
        }
        int bloodType = cursor.getInt(4);
        switch (bloodType)
        {
            case 0: person.setBloodType(BloodType.OMINUS); break;
            case 1: person.setBloodType(BloodType.OPLUS); break;
            case 2: person.setBloodType(BloodType.AMINUS); break;
            case 3: person.setBloodType(BloodType.APLUS); break;
            case 4: person.setBloodType(BloodType.BMINUS); break;
            case 5: person.setBloodType(BloodType.BPLUS); break;
            case 6: person.setBloodType(BloodType.ABMINUS); break;
            case 7: person.setBloodType(BloodType.ABPLUS); break;
            default: person.setBloodType(BloodType.UNKNOWN);
        }

        person.setBirthdate(cursor.getString(5));
        return person;
    }

}
