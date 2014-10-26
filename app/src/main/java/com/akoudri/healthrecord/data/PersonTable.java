package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.Crypto;
import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Ali Koudri on 03/04/14.
 */
public class PersonTable {

    private SQLiteDatabase db;
    private Crypto crypto;

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

    public PersonTable(SQLiteDatabase db, Crypto crypto)
    {
        this.db = db;
        this.crypto = crypto;
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
        sb.append(PERSON_BIRTHDATE + " integer not null");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertPerson(String name, Gender gender,
                             String ssn, BloodType bloodType, String birthdate) {
        ContentValues values = new ContentValues();
        try {
            values.put(PERSON_NAME, crypto.armorEncrypt(name.getBytes()));
            values.put(PERSON_GENDER, gender.ordinal());
            if (ssn != null)
                values.put(PERSON_SSN, ssn);
            values.put(PERSON_BLOODTYPE, bloodType.ordinal());
            long bd = HealthRecordUtils.stringToCalendar(birthdate).getTimeInMillis();
            values.put(PERSON_BIRTHDATE, bd);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return db.insert(PERSON_TABLE, null, values);
    }

    public boolean updatePerson(int personId, String name, Gender gender,
                                String ssn, BloodType bloodType, String birthdate)
    {
        ContentValues values = new ContentValues();
        try {
            values.put(PERSON_NAME, crypto.armorEncrypt(name.getBytes()));
            values.put(PERSON_GENDER, gender.ordinal());
            if (ssn != null)
                values.put(PERSON_SSN, ssn);
            else
                values.putNull(PERSON_SSN);
            values.put(PERSON_BLOODTYPE, bloodType.ordinal());
            long bd = HealthRecordUtils.stringToCalendar(birthdate).getTimeInMillis();
            values.put(PERSON_BIRTHDATE, bd);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
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
        //cursor.getString(1)
        try {
            person.setName(crypto.armorDecrypt(cursor.getString(1)));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        int gender = cursor.getInt(2);
        switch (gender)
        {
            case 0: person.setGender(Gender.MALE); break;
            default: person.setGender(Gender.FEMALE);
        }
        person.setSsn(cursor.getString(3));
        int bloodType = cursor.getInt(4);
        person.setBloodType(HealthRecordUtils.int2bloodType(bloodType));
        long bd = cursor.getLong(5);
        person.setBirthdate(HealthRecordUtils.millisToDatestring(bd));
        return person;
    }

}
