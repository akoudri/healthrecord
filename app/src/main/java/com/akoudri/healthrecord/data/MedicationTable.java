package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
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
        sb.append(MEDICATION_START_DATE + " text,");
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
        switch(kind)
        {
            case HOUR:
                values.put(MEDICATION_KIND, 0); break;
            case DAY:
                values.put(MEDICATION_KIND, 1); break;
            case WEEK:
                values.put(MEDICATION_KIND, 2); break;
            case MONTH:
                values.put(MEDICATION_KIND, 3); break;
            case YEAR:
                values.put(MEDICATION_KIND, 4); break;
            default:
                values.put(MEDICATION_KIND, 5);
        }
        if (startDate == null)
            values.putNull(MEDICATION_START_DATE);
        else
            values.put(MEDICATION_START_DATE, startDate);
        if (endDate == null)
            values.putNull(MEDICATION_END_DATE);
        else
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

    //FIXME: manage null values
    public boolean updateMedication(int medicationId, int treatmentId, int drugId, int frequency, DoseFrequencyKind kind, String startDate, String endDate) {
        ContentValues values = new ContentValues();
        values.put(MEDICATION_TREATMENT_REF, treatmentId);
        values.put(MEDICATION_DRUG_REF, drugId);
        values.put(MEDICATION_FREQUENCY, frequency);
        switch(kind)
        {
            case HOUR:
                values.put(MEDICATION_KIND, 0); break;
            case DAY:
                values.put(MEDICATION_KIND, 1); break;
            case WEEK:
                values.put(MEDICATION_KIND, 2); break;
            case MONTH:
                values.put(MEDICATION_KIND, 3); break;
            case YEAR:
                values.put(MEDICATION_KIND, 4); break;
            default:
                values.put(MEDICATION_KIND, 5);
        }
        if (startDate == null)
            values.putNull(MEDICATION_START_DATE);
        else
            values.put(MEDICATION_START_DATE, startDate);
        if (endDate == null)
            values.putNull(MEDICATION_END_DATE);
        else
            values.put(MEDICATION_END_DATE, endDate);
        return db.update(MEDICATION_TABLE, values, MEDICATION_ID + "=" + medicationId, null) > 0;
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

    public List<Medication> getDayMedicationsForTreatment(int treatmentId, String date)
    {
        List<Medication> res = new ArrayList<Medication>();
        Cursor cursor = db.query(MEDICATION_TABLE, medicationCols, MEDICATION_TREATMENT_REF + "=" + treatmentId, null, null, null, null);
        cursor.moveToFirst();
        Medication medication;
        Calendar currentDate, startDate, endDate;
        currentDate = stringToCalendar(date);
        while (! cursor.isAfterLast())
        {
            medication = cursorToMedication(cursor);
            startDate = stringToCalendar(medication.getStartDate());
            endDate = stringToCalendar(medication.getEndDate());
            if (endDate == null)
            {
                if (currentDate.equals(startDate) || currentDate.after(startDate)) {
                    res.add(medication);
                }
            }
            else
            {
                if (currentDate.equals(startDate) || currentDate.equals(endDate)) {
                    res.add(medication);
                }
                else if (currentDate.after(startDate) && currentDate.before(endDate)) {
                    res.add(medication);
                }
            }
            cursor.moveToNext();
        }
        return res;
    }

    public boolean removeMedicWithId(int medicId)
    {
        return db.delete(MEDICATION_TABLE, MEDICATION_ID + "=" + medicId, null) > 0;
    }

    //Date shall be formatted this way: dd/mm/yyyy
    //This is guaranteed because user does not access this method
    private Calendar stringToCalendar(String date)
    {
        if (date == null) return null;
        String[] dateArray = date.split("/");
        int dd = Integer.parseInt(dateArray[0]);
        int mm = Integer.parseInt(dateArray[1]) - 1;
        int yyyy = Integer.parseInt(dateArray[2]);
        Calendar res = Calendar.getInstance();
        res.set(yyyy, mm, dd, 0, 0, 0);
        res.set(Calendar.MILLISECOND, 0);
        return res;
    }

    private Medication cursorToMedication(Cursor cursor) {
        Medication medication = new Medication();
        medication.setId(cursor.getInt(0));
        medication.setTreatmentId(cursor.getInt(1));
        medication.setDrugId(cursor.getInt(2));
        medication.setFrequency(cursor.getInt(3));
        int kind = cursor.getInt(4);
        switch(kind)
        {
            case 0:
                medication.setKind(DoseFrequencyKind.HOUR); break;
            case 1:
                medication.setKind(DoseFrequencyKind.DAY); break;
            case 2:
                medication.setKind(DoseFrequencyKind.WEEK); break;
            case 3:
                medication.setKind(DoseFrequencyKind.MONTH); break;
            case 4:
                medication.setKind(DoseFrequencyKind.YEAR); break;
            default:
                medication.setKind(DoseFrequencyKind.LIFE);
        }
        medication.setStartDate(cursor.getString(5));
        medication.setEndDate(cursor.getString(6));
        return medication;
    }

}