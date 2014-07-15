package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Koudri on 18/06/14.
 */
public class MedicationTable {

    private SQLiteDatabase db;

    //Table
    public static final String MEDICATION_TABLE = "medication";
    public static final String MEDICATION_ID = "_id";
    public static final String MEDICATION_AILMENT_REF = "ailmentId";
    public static final String MEDICATION_DRUG_REF = "drugId";
    public static final String MEDICATION_FREQUENCY = "frequency";
    public static final String MEDICATION_KIND = "kind";
    public static final String MEDICATION_START_DATE = "startDate";
    public static final String MEDICATION_DURATION = "duration";


    private String[] medicationCols = {MEDICATION_ID, MEDICATION_AILMENT_REF, MEDICATION_DRUG_REF, MEDICATION_FREQUENCY, MEDICATION_KIND, MEDICATION_START_DATE,
            MEDICATION_DURATION};

    public MedicationTable(SQLiteDatabase db) {
        this.db = db;
    }

    public void createMedicationTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + MEDICATION_TABLE + " (");
        sb.append(MEDICATION_ID + " integer primary key autoincrement,");
        sb.append(MEDICATION_AILMENT_REF + " integer not null,");
        sb.append(MEDICATION_DRUG_REF + " integer not null,");
        sb.append(MEDICATION_FREQUENCY + " integer not null,");
        sb.append(MEDICATION_KIND + " integer not null,");
        sb.append(MEDICATION_START_DATE + " integer not null,");
        sb.append(MEDICATION_DURATION + " integer,"); //medication with no end date are for chronic ailments
        sb.append(" foreign key(" + MEDICATION_AILMENT_REF + ") references " + AilmentTable.AILMENT_TABLE +
                "(" + AilmentTable.AILMENT_ID + "),");
        sb.append(" foreign key(" + MEDICATION_DRUG_REF + ") references " + DrugTable.DRUG_TABLE +
                "(" + DrugTable.DRUG_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    //TODO: check conformity of dates with owning ailment
    public long insertMedication(int ailmentId, int drugId, int frequency, DoseFrequencyKind kind, String startDate, int duration) {
        ContentValues values = new ContentValues();
        values.put(MEDICATION_AILMENT_REF, ailmentId);
        values.put(MEDICATION_DRUG_REF, drugId);
        values.put(MEDICATION_FREQUENCY, frequency);
        values.put(MEDICATION_KIND, kind.ordinal());
        long d = HealthRecordUtils.stringToCalendar(startDate).getTimeInMillis();
        values.put(MEDICATION_START_DATE, d);
        if (duration < 0)
            values.put(MEDICATION_DURATION, duration);
        return db.insert(MEDICATION_TABLE, null, values);
    }

    public long insertMedication(Medication medication)
    {
        int ailmentId = medication.getAilmentId();
        int drugId = medication.getDrugId();
        int frequency = medication.getFrequency();
        DoseFrequencyKind kind = medication.getKind();
        String startDate = medication.getStartDate();
        int duration = medication.getDuration();
        return insertMedication(ailmentId, drugId, frequency, kind, startDate, duration);
    }

    public boolean updateMedication(int medicationId, int ailmentId, int drugId, int frequency, DoseFrequencyKind kind, String startDate, int duration) {
        ContentValues values = new ContentValues();
        values.put(MEDICATION_AILMENT_REF, ailmentId);
        values.put(MEDICATION_DRUG_REF, drugId);
        values.put(MEDICATION_FREQUENCY, frequency);
        values.put(MEDICATION_KIND, kind.ordinal());
        long d = HealthRecordUtils.stringToCalendar(startDate).getTimeInMillis();
        values.put(MEDICATION_START_DATE, d);
        if (duration > 0)
            values.put(MEDICATION_DURATION, duration);
        else
            values.putNull(MEDICATION_DURATION);
        return db.update(MEDICATION_TABLE, values, MEDICATION_ID + "=" + medicationId, null) > 0;
    }

    public boolean updateMedication(Medication medication)
    {
        int medicationId = medication.getId();
        int ailmentId = medication.getAilmentId();
        int drugId = medication.getDrugId();
        int frequency = medication.getFrequency();
        DoseFrequencyKind kind = medication.getKind();
        String startDate = medication.getStartDate();
        int duration = medication.getDuration();
        return updateMedication(medicationId, ailmentId, drugId, frequency, kind, startDate, duration);
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

    public boolean removeMedicWithId(int medicId)
    {
        return db.delete(MEDICATION_TABLE, MEDICATION_ID + "=" + medicId, null) > 0;
    }

    private Medication cursorToMedication(Cursor cursor) {
        Medication medication = new Medication();
        medication.setId(cursor.getInt(0));
        medication.setAilmentId(cursor.getInt(1));
        medication.setDrugId(cursor.getInt(2));
        medication.setFrequency(cursor.getInt(3));
        medication.setKind(HealthRecordUtils.int2kind(cursor.getInt(4)));
        long d = cursor.getLong(5);
        medication.setStartDate(HealthRecordUtils.millisToDatestring(d));
        medication.setDuration((cursor.isNull(6)) ? -1 : cursor.getInt(6));
        return medication;
    }

}