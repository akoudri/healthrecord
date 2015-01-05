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
import java.util.Calendar;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Ali Koudri on 23/07/14.
 * TODO: store image in V2
 */
public class MedicalObservationTable {

    private SQLiteDatabase db;
    private Crypto crypto;

    //Table
    public static final String MEDICAL_OBSERVATION_TABLE = "medical_observation";
    public static final String MEDICAL_OBSERVATION_ID = "_id";
    public static final String MEDICAL_OBSERVATION_PERSON_REF = "personId";
    public static final String MEDICAL_OBSERVATION_DATE = "date";
    public static final String MEDICAL_OBSERVATION_DESCRIPTION = "description";

    private String[] medicalObservationCols = {MEDICAL_OBSERVATION_ID, MEDICAL_OBSERVATION_PERSON_REF, MEDICAL_OBSERVATION_DATE,
            MEDICAL_OBSERVATION_DESCRIPTION};

    public MedicalObservationTable(SQLiteDatabase db, Crypto crypto)
    {
        this.db = db;
        this.crypto = crypto;
    }

    public void createMedicalObservationTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + MEDICAL_OBSERVATION_TABLE + "(");
        sb.append(MEDICAL_OBSERVATION_ID + " integer primary key autoincrement,");
        sb.append(MEDICAL_OBSERVATION_PERSON_REF + " integer not null,");
        sb.append(MEDICAL_OBSERVATION_DATE + " integer not null unique,");
        sb.append(MEDICAL_OBSERVATION_DESCRIPTION + " text not null,");
        sb.append(" foreign key(" + MEDICAL_OBSERVATION_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    //zero values are considered as null
    public long insertMedicalObservation(int personId, String date, String hour, String description)
    {
        ContentValues values = new ContentValues();
        values.put(MEDICAL_OBSERVATION_PERSON_REF, personId);
        values.put(MEDICAL_OBSERVATION_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        if (description != null) {
            try {
                values.put(MEDICAL_OBSERVATION_DESCRIPTION, crypto.armorEncrypt(description.getBytes()));
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
        }
        return db.insert(MEDICAL_OBSERVATION_TABLE, null, values);
    }

    public boolean removeMedicalObservationWithId(int medicalObservationId)
    {
        return db.delete(MEDICAL_OBSERVATION_TABLE, MEDICAL_OBSERVATION_ID + "=" + medicalObservationId, null) > 0;
    }

    //zero values are considered as null
    public boolean updateMedicalObservationWithId(int medicalObservationId, String date, String hour, String description)
    {
        ContentValues values = new ContentValues();
        values.put(MEDICAL_OBSERVATION_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        if (description == null)
            values.putNull(MEDICAL_OBSERVATION_DESCRIPTION);
        else {
            try {
                values.put(MEDICAL_OBSERVATION_DESCRIPTION, crypto.armorEncrypt(description.getBytes()));
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
        }
        return db.update(MEDICAL_OBSERVATION_TABLE, values, MEDICAL_OBSERVATION_ID + "=" + medicalObservationId, null) > 0;
    }

    public boolean updateMedicalObservation(MedicalObservation medicalObservation)
    {

        return updateMedicalObservationWithId(medicalObservation.getId(), medicalObservation.getDate(), medicalObservation.getHour(),
                medicalObservation.getDescription());
    }

    public MedicalObservation getMedicalObservationWithId(int medicalObservationId)
    {
        Cursor cursor = db.query(MEDICAL_OBSERVATION_TABLE, medicalObservationCols, MEDICAL_OBSERVATION_ID + "=" + medicalObservationId, null, null, null, null);
        if (cursor.moveToFirst()) return cursorToMedicalObservation(cursor);
        return null;
    }

    public int[] getMonthObservationsForPerson(int personId, Calendar cal)
    {
        int min = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, min);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long ms = cal.getTimeInMillis();
        long me = ms + 86400000L * max;
        List<MedicalObservation> res = new ArrayList<MedicalObservation>();
        Cursor cursor = db.query(MEDICAL_OBSERVATION_TABLE, medicalObservationCols,
                MEDICAL_OBSERVATION_PERSON_REF + "=" + personId + " and " + MEDICAL_OBSERVATION_DATE + ">=" + ms + " and " + MEDICAL_OBSERVATION_DATE + "<" + me,
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMedicalObservation(cursor));
            cursor.moveToNext();
        }
        int[] observations = new int[max];
        for (int i = 0; i < max; i++)
            observations[i] = 0;
        for (MedicalObservation obs : res)
        {
            long d = HealthRecordUtils.stringToCalendar(obs.getDate()).getTimeInMillis() - cal.getTimeInMillis();
            int i = (int)(d/86400000L);
            observations[i] ++;
        }
        return observations;
    }

    public List<MedicalObservation> getDayObservationsForPerson(int personId, String date)
    {
        long ms = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        long me = ms + 86400000L;//24h in ms
        List<MedicalObservation> res = new ArrayList<MedicalObservation>();
        Cursor cursor = db.query(MEDICAL_OBSERVATION_TABLE, medicalObservationCols,
                MEDICAL_OBSERVATION_PERSON_REF + "=" + personId + " and " + MEDICAL_OBSERVATION_DATE + ">=" + ms + " and " +
                        MEDICAL_OBSERVATION_DATE + "<" + me,
                null, null, null, MEDICAL_OBSERVATION_DATE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMedicalObservation(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    private MedicalObservation cursorToMedicalObservation(Cursor cursor)
    {
        MedicalObservation medicalObservation = new MedicalObservation();
        medicalObservation.setId(cursor.getInt(0));
        medicalObservation.setPersonId(cursor.getInt(1));
        String[] d = HealthRecordUtils.millisToLongdatestring(cursor.getLong(2)).split("/");
        String date = String.format("%s/%s/%s", d[0], d[1], d[2]);
        String hour = String.format("%s:%s", d[3], d[4]);
        medicalObservation.setDate(date);
        medicalObservation.setHour(hour);
        try {
            medicalObservation.setDescription(crypto.armorDecrypt(cursor.getString(3)));
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
        return medicalObservation;
    }
}
