package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ali Koudri on 15/06/14.
 */
public class TreatmentTable {

    private SQLiteDatabase db;

    //Table
    public static final String TREATMENT_TABLE = "treatment";
    public static final String TREATMENT_ID = "_id";
    public static final String TREATMENT_PERSON_REF = "personId";
    public static final String TREATMENT_ILLNESS_REF = "illnessId";
    public static final String TREATMENT_THERAPIST_REF = "therapistId";
    public static final String TREATMENT_START_DATE = "startDate";
    public static final String TREATMENT_END_DATE = "endDate";
    public static final String TREATMENT_COMMENT = "comment";

    private String[] treatmentCols = {TREATMENT_ID, TREATMENT_PERSON_REF, TREATMENT_ILLNESS_REF, TREATMENT_THERAPIST_REF, TREATMENT_START_DATE,
            TREATMENT_END_DATE, TREATMENT_COMMENT};

    public TreatmentTable(SQLiteDatabase db) {
        this.db = db;
    }

    public void createTreatmentTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + TREATMENT_TABLE + " (");
        sb.append(TREATMENT_ID + " integer primary key autoincrement,");
        sb.append(TREATMENT_PERSON_REF + " integer not null,");
        sb.append(TREATMENT_ILLNESS_REF + " integer,"); //not null for the case of preventive treatment
        sb.append(TREATMENT_THERAPIST_REF + " integer,"); //not null for the case of auto-medication
        sb.append(TREATMENT_START_DATE + " text not null,");
        sb.append(TREATMENT_END_DATE + " text,"); //treatment with no end date can be considered permanent
        sb.append(TREATMENT_COMMENT + " text,");
        sb.append(" foreign key(" + TREATMENT_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + "),");
        sb.append(" foreign key(" + TREATMENT_ILLNESS_REF + ") references " + IllnessTable.ILLNESS_TABLE +
                "(" + IllnessTable.ILLNESS_ID + "),");
        sb.append(" foreign key(" + TREATMENT_THERAPIST_REF + ") references " + TherapistTable.THERAPIST_TABLE +
                "(" + TherapistTable.THERAPIST_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertTreatment(int personId, int illnessId, int therapistId, String startDate, String endDate, String comment) {
        ContentValues values = new ContentValues();
        values.put(TREATMENT_PERSON_REF, personId);
        if (illnessId > 0)
            values.put(TREATMENT_ILLNESS_REF, illnessId);
        if (therapistId > 0)
            values.put(TREATMENT_THERAPIST_REF, therapistId);
        values.put(TREATMENT_START_DATE, startDate);
        if (endDate != null)
            values.put(TREATMENT_END_DATE, endDate);
        if (comment != null)
            values.put(TREATMENT_COMMENT, comment);
        return db.insert(TREATMENT_TABLE, null, values);
    }

    public boolean updateTreatment(int treatmentId, int illnessId, int therapistId, String startDate, String endDate, String comment) {
        ContentValues values = new ContentValues();
        if (illnessId > 0)
            values.put(TREATMENT_ILLNESS_REF, illnessId);
        if (therapistId > 0)
            values.put(TREATMENT_THERAPIST_REF, therapistId);
        values.put(TREATMENT_START_DATE, startDate);
        if (endDate != null)
            values.put(TREATMENT_END_DATE, endDate);
        if (comment != null)
            values.put(TREATMENT_COMMENT, comment);
        return db.update(TREATMENT_TABLE, values, TREATMENT_ID + "=" + treatmentId, null) > 0;
    }

    public boolean updateTreatment(Treatment treatment)
    {
        int treatmentId = treatment.getId();
        int illnessId = treatment.getIllnessId();
        int therapistId = treatment.getTherapistId();
        String startDate = treatment.getStartDate();
        String endDate = treatment.getEndDate();
        String comment = treatment.getComment();
        return updateTreatment(treatmentId, illnessId, therapistId, startDate, endDate, comment);
    }

    public Treatment getTreatmentWithId(int id) {
        Cursor cursor = db.query(TREATMENT_TABLE, treatmentCols, TREATMENT_ID + "=" + id, null, null, null, null);
        if (cursor.moveToFirst())
            return  cursorToTreatment(cursor);
        return null;
    }

    public List<Treatment> getDayTreatmentsForPerson(int personId, String date)
    {
        List<Treatment> res = new ArrayList<Treatment>();
        Cursor cursor = db.query(TREATMENT_TABLE, treatmentCols, TREATMENT_PERSON_REF + "=" + personId, null, null, null, null);
        cursor.moveToFirst();
        Treatment treatment;
        Calendar currentDate, startDate, endDate;
        currentDate = HealthRecordUtils.stringToCalendar(date);
        while (! cursor.isAfterLast())
        {
            treatment = cursorToTreatment(cursor);
            startDate = HealthRecordUtils.stringToCalendar(treatment.getStartDate());
            endDate = HealthRecordUtils.stringToCalendar(treatment.getEndDate());
            if (endDate == null)
            {
                if (currentDate.equals(startDate) || currentDate.after(startDate)) {
                    res.add(treatment);
                }
            }
            else
            {
                if (currentDate.equals(startDate) || currentDate.equals(endDate)) {
                    res.add(treatment);
                }
                else if (currentDate.after(startDate) && currentDate.before(endDate)) {
                    res.add(treatment);
                }
            }
            cursor.moveToNext();
        }
        return res;
    }

    public boolean removeTreatmentWithId(int treatmentId)
    {
        return db.delete(TREATMENT_TABLE, TREATMENT_ID + "=" + treatmentId, null) > 0;
    }

    private Treatment cursorToTreatment(Cursor cursor) {
        Treatment treatment = new Treatment();
        treatment.setId(cursor.getInt(0));
        treatment.setPersonId(cursor.getInt(1));
        treatment.setIllnessId((cursor.isNull(2)) ? 0 : cursor.getInt(2));
        treatment.setTherapistId((cursor.isNull(3))?0:cursor.getInt(3));
        treatment.setStartDate((cursor.isNull(4))?null:cursor.getString(4));
        treatment.setEndDate((cursor.isNull(5))?null:cursor.getString(5));
        treatment.setComment((cursor.isNull(6))?null:cursor.getString(6));
        return treatment;
    }

}