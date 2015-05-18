package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ali Koudri on 18/06/14.
 */
public class MedicationTable {

    private SQLiteDatabase db;

    //Table
    public static final String MEDICATION_TABLE = "treatment";
    public static final String MEDICATION_ID = "_id";
    public static final String MEDICATION_PERSON_REF = "personId";
    public static final String MEDICATION_AILMENT_REF = "ailmentId";
    public static final String MEDICATION_DRUG_REF = "drugId";
    public static final String MEDICATION_FREQUENCY = "frequency";
    public static final String MEDICATION_KIND = "kind";
    public static final String MEDICATION_START_DATE = "startDate";
    public static final String MEDICATION_DURATION = "duration";


    private String[] medicationCols = {MEDICATION_ID, MEDICATION_PERSON_REF, MEDICATION_AILMENT_REF, MEDICATION_DRUG_REF, MEDICATION_FREQUENCY, MEDICATION_KIND, MEDICATION_START_DATE,
            MEDICATION_DURATION};

    public MedicationTable(SQLiteDatabase db) {
        this.db = db;
    }

    public void createMedicationTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + MEDICATION_TABLE + " (");
        sb.append(MEDICATION_ID + " integer primary key autoincrement,");
        sb.append(MEDICATION_PERSON_REF + " integer not null,");
        sb.append(MEDICATION_AILMENT_REF + " integer,");
        sb.append(MEDICATION_DRUG_REF + " integer not null,");
        sb.append(MEDICATION_FREQUENCY + " integer not null,");
        sb.append(MEDICATION_KIND + " integer not null,");
        sb.append(MEDICATION_START_DATE + " integer not null,");
        sb.append(MEDICATION_DURATION + " integer check (" + MEDICATION_DURATION + " >= 0),"); //medication with no end date are for chronic ailments
        sb.append(" foreign key(" + MEDICATION_AILMENT_REF + ") references " + AilmentTable.AILMENT_TABLE +
                "(" + AilmentTable.AILMENT_ID + "),");
        sb.append(" foreign key(" + MEDICATION_DRUG_REF + ") references " + DrugTable.DRUG_TABLE +
                "(" + DrugTable.DRUG_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public void updateV2() {
        db.execSQL("alter table " + MEDICATION_TABLE + " rename to tmp;");
        createMedicationTable();
        StringBuilder sb = new StringBuilder();
        sb.append("insert into " + MEDICATION_TABLE + " select * from tmp;");
        db.execSQL(sb.toString());
    }

    public long insertMedication(int personId, int ailmentId, int drugId, int frequency, DoseFrequencyKind kind, String startDate, int duration) {
        ContentValues values = new ContentValues();
        values.put(MEDICATION_PERSON_REF, personId);
        values.put(MEDICATION_AILMENT_REF, ailmentId);
        values.put(MEDICATION_DRUG_REF, drugId);
        values.put(MEDICATION_FREQUENCY, frequency);
        values.put(MEDICATION_KIND, kind.ordinal());
        long d = HealthRecordUtils.stringToCalendar(startDate).getTimeInMillis();
        values.put(MEDICATION_START_DATE, d);
        if (duration >= 0)
            values.put(MEDICATION_DURATION, duration);
        return db.insert(MEDICATION_TABLE, null, values);
    }

    public long insertMedication(Medication medication)
    {
        int personId = medication.getPersonId();
        int ailmentId = medication.getAilmentId();
        int drugId = medication.getDrugId();
        int frequency = medication.getFrequency();
        DoseFrequencyKind kind = medication.getKind();
        String startDate = medication.getStartDate();
        int duration = medication.getDuration();
        return insertMedication(personId, ailmentId, drugId, frequency, kind, startDate, duration);
    }

    public boolean updateMedication(int medicationId, int personId, int ailmentId, int drugId, int frequency, DoseFrequencyKind kind, String startDate, int duration) {
        ContentValues values = new ContentValues();
        values.put(MEDICATION_PERSON_REF, personId);
        values.put(MEDICATION_AILMENT_REF, ailmentId);
        values.put(MEDICATION_DRUG_REF, drugId);
        values.put(MEDICATION_FREQUENCY, frequency);
        values.put(MEDICATION_KIND, kind.ordinal());
        long d = HealthRecordUtils.stringToCalendar(startDate).getTimeInMillis();
        values.put(MEDICATION_START_DATE, d);
        if (duration >= 0)
            values.put(MEDICATION_DURATION, duration);
        else
            values.putNull(MEDICATION_DURATION);
        return db.update(MEDICATION_TABLE, values, MEDICATION_ID + "=" + medicationId, null) > 0;
    }

    public boolean updateMedication(Medication medication)
    {
        int medicationId = medication.getId();
        int personId = medication.getPersonId();
        int ailmentId = medication.getAilmentId();
        int drugId = medication.getDrugId();
        int frequency = medication.getFrequency();
        DoseFrequencyKind kind = medication.getKind();
        String startDate = medication.getStartDate();
        int duration = medication.getDuration();
        return updateMedication(medicationId, personId, ailmentId, drugId, frequency, kind, startDate, duration);
    }

    public Medication getMedicationWithId(int id) {
        Cursor cursor = db.query(MEDICATION_TABLE, medicationCols, MEDICATION_ID + "=" + id, null, null, null, null);
        if (cursor.moveToFirst())
            return  cursorToMedication(cursor);
        return null;
    }

    public List<Medication> getMedicationsForAilment(int ailmentId)
    {
        List<Medication> res = new ArrayList<Medication>();
        Cursor cursor = db.query(MEDICATION_TABLE, medicationCols, MEDICATION_AILMENT_REF + "=" + ailmentId, null, null, null, null);
        cursor.moveToFirst();
        while (! cursor.isAfterLast())
        {
            res.add(cursorToMedication(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public List<Medication> getDayMedicsForPerson(int personId, String date)
    {
        List<Medication> res = new ArrayList<Medication>();
        long today = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        Cursor cursor = db.query(MEDICATION_TABLE, medicationCols, MEDICATION_PERSON_REF + "=" + personId +
                " and " + MEDICATION_START_DATE + "<=" + today + " and (" + MEDICATION_DURATION + " is null or " + MEDICATION_START_DATE + " + " +
                MEDICATION_DURATION + " * 86400000 >= " + today + ")", null, null, null, null);
        Medication medic;
        cursor.moveToFirst();
        while (! cursor.isAfterLast())
        {
            medic = cursorToMedication(cursor);
            res.add(medic);
            cursor.moveToNext();
        }
        return res;
    }

    public boolean removeMedicWithId(int medicId)
    {
        return db.delete(MEDICATION_TABLE, MEDICATION_ID + "=" + medicId, null) > 0;
    }

    public int countMedicsForDay(int personId, long date)
    {
        String req = "select count(*) from " + MEDICATION_TABLE + " where " + MEDICATION_PERSON_REF + "=" + personId +
                " and " + MEDICATION_START_DATE + "<=" + date + " and (" + MEDICATION_DURATION + " is null or " + MEDICATION_START_DATE + " + " +
                MEDICATION_DURATION + " * 86400000 >= " + date + ")";
        Cursor count = db.rawQuery(req, null);
        if (!count.moveToFirst())
            return 0;
        int res = count.getInt(0);
        count.close();
        return res;
    }

    public int[] getMonthMedicationsForPerson(int personId, Calendar cal)
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
        List<Medication> res = new ArrayList<Medication>();
        Cursor cursor = db.query(MEDICATION_TABLE, medicationCols, MEDICATION_PERSON_REF + "=" + personId +  " and " + MEDICATION_START_DATE + "<" + me +
                        " and (" +MEDICATION_DURATION + " is null or " + MEDICATION_START_DATE + ">=" + ms + "-" + MEDICATION_DURATION + "* 86400000)",
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMedication(cursor));
            cursor.moveToNext();
        }
        int[] medications = new int[max];
        int i;
        for (i = 0; i < max; i++)
            medications[i] = 0;
        for (Medication medication : res)
        {
            long sa = HealthRecordUtils.stringToCalendar(medication.getStartDate()).getTimeInMillis();
            int d = medication.getDuration();
            for (i = 0; i < max; i++)
            {
                long ref = ms + i * 86400000L;
                if (d == -1)
                {
                    if (sa <= ref) medications[i]++;
                }
                else
                {
                    long se = sa + d * 86400000L;
                    if (sa <= ref && se >= ref) medications[i]++;
                }
            }
        }
        return medications;
    }

    private Medication cursorToMedication(Cursor cursor) {
        Medication medication = new Medication();
        medication.setId(cursor.getInt(0));
        medication.setPersonId(cursor.getInt(1));
        medication.setAilmentId(cursor.getInt(2));
        medication.setDrugId(cursor.getInt(3));
        medication.setFrequency(cursor.getInt(4));
        medication.setKind(HealthRecordUtils.int2kind(cursor.getInt(5)));
        long d = cursor.getLong(6);
        medication.setStartDate(HealthRecordUtils.millisToDatestring(d));
        medication.setDuration((cursor.isNull(7)) ? -1 : cursor.getInt(7));
        return medication;
    }

}