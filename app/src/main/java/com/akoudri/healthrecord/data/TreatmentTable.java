package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
    public static final String TREATMENT_AILMENT_REF = "ailmentId";
    public static final String TREATMENT_THERAPIST_REF = "therapistId";
    public static final String TREATMENT_START_DATE = "startDate";
    public static final String TREATMENT_END_DATE = "endDate";
    public static final String TREATMENT_COMMENT = "comment";

    private String[] treatmentCols = {TREATMENT_ID, TREATMENT_PERSON_REF, TREATMENT_AILMENT_REF, TREATMENT_THERAPIST_REF, TREATMENT_START_DATE,
            TREATMENT_END_DATE, TREATMENT_COMMENT};

    public TreatmentTable(SQLiteDatabase db) {
        this.db = db;
    }

    //FIXME: Maybe the persondId field is not needed because the information is born by the corresponding ailment table
    //but we can anyway keep it because it facilitates querying
    //Actually, we keep it to manage the fact that a treatment could correspond to no ailment!
    public void createTreatmentTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + TREATMENT_TABLE + " (");
        sb.append(TREATMENT_ID + " integer primary key autoincrement,");
        sb.append(TREATMENT_PERSON_REF + " integer not null,");
        sb.append(TREATMENT_AILMENT_REF + " integer,"); //not null for the case where there is no ailment
        sb.append(TREATMENT_THERAPIST_REF + " integer,"); //not null for the case of auto-medication
        sb.append(TREATMENT_START_DATE + " text,");
        sb.append(TREATMENT_END_DATE + " text,"); //treatment with no end date can be considered permanent
        sb.append(TREATMENT_COMMENT + " text,");
        sb.append(" foreign key(" + TREATMENT_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + "),");
        sb.append(" foreign key(" + TREATMENT_AILMENT_REF + ") references " + AilmentTable.AILMENT_TABLE +
                "(" + AilmentTable.AILMENT_ID + "),");
        sb.append(" foreign key(" + TREATMENT_THERAPIST_REF + ") references " + TherapistTable.THERAPIST_TABLE +
                "(" + TherapistTable.THERAPIST_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertTreatment(int personId, int ailmentId, int therapistId, String startDate, String endDate, String comment) {
        ContentValues values = new ContentValues();
        values.put(TREATMENT_PERSON_REF, personId);
        values.put(TREATMENT_AILMENT_REF, ailmentId);
        values.put(TREATMENT_THERAPIST_REF, therapistId);
        if (startDate == null)
            values.putNull(TREATMENT_START_DATE);
        else
            values.put(TREATMENT_START_DATE, startDate);
        if (endDate == null)
            values.putNull(TREATMENT_END_DATE);
        else
            values.put(TREATMENT_END_DATE, endDate);
        values.put(TREATMENT_COMMENT, comment);
        return db.insert(TREATMENT_TABLE, null, values);
    }

    //FIXME: manage null values
    public boolean updateTreatment(int treatmentId, int personId, int ailmentId, int therapistId, String startDate, String endDate, String comment) {
        ContentValues values = new ContentValues();
        values.put(TREATMENT_PERSON_REF, personId);
        values.put(TREATMENT_AILMENT_REF, ailmentId);
        values.put(TREATMENT_THERAPIST_REF, therapistId);
        values.put(TREATMENT_START_DATE, startDate);
        values.put(TREATMENT_END_DATE, endDate);
        values.put(TREATMENT_COMMENT, comment);
        return db.update(TREATMENT_TABLE, values, TREATMENT_ID + "=" + treatmentId, null) > 0;
    }

    public boolean updateTreatment(Treatment treatment)
    {
        int treatmentId = treatment.getId();
        int personId = treatment.getPersonId();
        int ailmentId = treatment.getAilmentId();
        int therapistId = treatment.getTherapistId();
        String startDate = treatment.getStartDate();
        String endDate = treatment.getEndDate();
        String comment = treatment.getComment();
        return updateTreatment(treatmentId, personId, ailmentId, therapistId, startDate, endDate, comment);
    }

    public Treatment getTreatmentWithId(int id) {
        Cursor cursor = db.query(TREATMENT_TABLE, treatmentCols, TREATMENT_ID + "=" + id, null, null, null, null);
        if (cursor.moveToFirst())
            return  cursorToTreatment(cursor);
        return null;
    }

    public List<Treatment> getTreatmentsForPerson(int personId)
    {
        List<Treatment> res = new ArrayList<Treatment>();
        Cursor cursor = db.query(TREATMENT_TABLE, treatmentCols, TREATMENT_PERSON_REF + "=" + personId, null, null, null, null);
        cursor.moveToFirst();
        while (! cursor.isAfterLast())
        {
            res.add(cursorToTreatment(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public List<Treatment> getDayTreatmentsForPerson(int personId, String date)
    {
        List<Treatment> res = new ArrayList<Treatment>();
        Cursor cursor = db.query(TREATMENT_TABLE, treatmentCols, TREATMENT_PERSON_REF + "=" + personId, null, null, null, null);
        cursor.moveToFirst();
        Treatment treatment;
        Calendar currentDate, startDate, endDate;
        currentDate = stringToCalendar(date);
        while (! cursor.isAfterLast())
        {
            treatment = cursorToTreatment(cursor);
            startDate = stringToCalendar(treatment.getStartDate());
            endDate = stringToCalendar(treatment.getEndDate());
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

    //Date shall be formatted this way: dd/mm/yyyy
    //This is guaranteed because user does not access this method
    private Calendar stringToCalendar(String date)
    {
        //FIXME: use regular expression instead
        if (date == null || date.equalsIgnoreCase("")) return null;
        String[] dateArray = date.split("/");
        int dd = Integer.parseInt(dateArray[0]);
        int mm = Integer.parseInt(dateArray[1]) - 1;
        int yyyy = Integer.parseInt(dateArray[2]);
        Calendar res = Calendar.getInstance();
        int fmm;
        switch (mm)
        {
            case 0:
                fmm = Calendar.JANUARY; break;
            case 1:
                fmm = Calendar.FEBRUARY; break;
            case 2:
                fmm = Calendar.MARCH; break;
            case 3:
                fmm = Calendar.APRIL; break;
            case 4:
                fmm = Calendar.MAY; break;
            case 5:
                fmm = Calendar.JUNE; break;
            case 6:
                fmm = Calendar.JULY; break;
            case 7:
                fmm = Calendar.AUGUST; break;
            case 8:
                fmm = Calendar.SEPTEMBER; break;
            case 9:
                fmm = Calendar.OCTOBER; break;
            case 10:
                fmm = Calendar.NOVEMBER; break;
            default:
                fmm = Calendar.DECEMBER; break;
        }
        res.set(yyyy, fmm, dd, 0, 0, 0);
        res.set(Calendar.MILLISECOND, 0);
        return res;
    }

    private Treatment cursorToTreatment(Cursor cursor) {
        Treatment treatment = new Treatment();
        treatment.setId(cursor.getInt(0));
        treatment.setPersonId(cursor.getInt(1));
        treatment.setAilmentId(cursor.getInt(2));
        treatment.setTherapistId(cursor.getInt(3));
        treatment.setStartDate(cursor.getString(4));
        treatment.setEndDate(cursor.getString(5));
        treatment.setComment(cursor.getString(6));
        return treatment;
    }

}