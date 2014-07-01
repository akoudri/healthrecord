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
    public static final String MEDICATION_TREATMENT_REF = "treatmentId";
    public static final String MEDICATION_DRUG_REF = "drugId";
    public static final String MEDICATION_FREQUENCY = "frequency";
    public static final String MEDICATION_KIND = "kind";
    public static final String MEDICATION_START_DATE = "startDate";
    public static final String MEDICATION_END_DATE = "endDate";


    private String[] medicationCols = {MEDICATION_ID, MEDICATION_TREATMENT_REF, MEDICATION_DRUG_REF, MEDICATION_FREQUENCY, MEDICATION_KIND, MEDICATION_START_DATE,
            MEDICATION_END_DATE};

    public MedicationTable(SQLiteDatabase db) {
        this.db = db;
    }

    public void createMedicationTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + MEDICATION_TABLE + " (");
        sb.append(MEDICATION_ID + " integer primary key autoincrement,");
        sb.append(MEDICATION_TREATMENT_REF + " integer not null,");
        sb.append(MEDICATION_DRUG_REF + " integer not null,");
        sb.append(MEDICATION_FREQUENCY + " integer not null,");
        sb.append(MEDICATION_KIND + " integer not null,");
        sb.append(MEDICATION_START_DATE + " text,");//medication with no start and end date are for chronic ailments
        sb.append(MEDICATION_END_DATE + " text,");
        sb.append(" foreign key(" + MEDICATION_TREATMENT_REF + ") references " + TreatmentTable.TREATMENT_TABLE +
                "(" + TreatmentTable.TREATMENT_ID + "),");
        sb.append(" foreign key(" + MEDICATION_DRUG_REF + ") references " + DrugTable.DRUG_TABLE +
                "(" + DrugTable.DRUG_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    //TODO: check conformity of dates with owning treatment
    public long insertMedication(int treatmentId, int drugId, int frequency, DoseFrequencyKind kind, String startDate, String endDate) {
        ContentValues values = new ContentValues();
        values.put(MEDICATION_TREATMENT_REF, treatmentId);
        values.put(MEDICATION_DRUG_REF, drugId);
        values.put(MEDICATION_FREQUENCY, frequency);
        values.put(MEDICATION_KIND, kind.ordinal());
        if (startDate != null)
            values.put(MEDICATION_START_DATE, startDate);
        if (endDate != null)
            values.put(MEDICATION_END_DATE, endDate);
        return db.insert(MEDICATION_TABLE, null, values);
    }

    public long insertMedication(Medication medication)
    {
        int treatmentId = medication.getTreatmentId();
        int drugId = medication.getDrugId();
        int frequency = medication.getFrequency();
        DoseFrequencyKind kind = medication.getKind();
        String startDate = medication.getStartDate();
        String endDate = medication.getEndDate();
        return insertMedication(treatmentId, drugId, frequency, kind, startDate, endDate);
    }

    public boolean updateMedication(int medicationId, int treatmentId, int drugId, int frequency, DoseFrequencyKind kind, String startDate, String endDate) {
        ContentValues values = new ContentValues();
        values.put(MEDICATION_TREATMENT_REF, treatmentId);
        values.put(MEDICATION_DRUG_REF, drugId);
        values.put(MEDICATION_FREQUENCY, frequency);
        values.put(MEDICATION_KIND, kind.ordinal());
        if (startDate != null)
            values.put(MEDICATION_START_DATE, startDate);
        if (endDate != null)
            values.put(MEDICATION_END_DATE, endDate);
        return db.update(MEDICATION_TABLE, values, MEDICATION_ID + "=" + medicationId, null) > 0;
    }

    public boolean updateMedication(Medication medication)
    {
        int medicationId = medication.getId();
        int treatmentId = medication.getTreatmentId();
        int drugId = medication.getDrugId();
        int frequency = medication.getFrequency();
        DoseFrequencyKind kind = medication.getKind();
        String startDate = medication.getStartDate();
        String endDate = medication.getEndDate();
        return updateMedication(medicationId, treatmentId, drugId, frequency, kind, startDate, endDate);
    }

    public Medication getMedicationWithId(int id) {
        Cursor cursor = db.query(MEDICATION_TABLE, medicationCols, MEDICATION_ID + "=" + id, null, null, null, null);
        if (cursor.moveToFirst())
            return  cursorToMedication(cursor);
        return null;
    }

    public List<Medication> getMedicationsForTreatment(int treatmentId)
    {
        List<Medication> res = new ArrayList<Medication>();
        Cursor cursor = db.query(MEDICATION_TABLE, medicationCols, MEDICATION_TREATMENT_REF + "=" + treatmentId, null, null, null, null);
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
        medication.setTreatmentId(cursor.getInt(1));
        medication.setDrugId(cursor.getInt(2));
        medication.setFrequency(cursor.getInt(3));
        medication.setKind(HealthRecordUtils.int2kind(cursor.getInt(4)));
        medication.setStartDate((cursor.isNull(5))?null:cursor.getString(5));
        medication.setEndDate((cursor.isNull(6))?null:cursor.getString(6));
        return medication;
    }

}