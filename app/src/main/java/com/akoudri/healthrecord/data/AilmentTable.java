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
public class AilmentTable {

    private SQLiteDatabase db;

    //Table
    public static final String AILMENT_TABLE = "ailment";
    public static final String AILMENT_ID = "_id";
    public static final String AILMENT_PERSON_REF = "personId";
    public static final String AILMENT_ILLNESS_REF = "illnessId";
    public static final String AILMENT_THERAPIST_REF = "therapistId";
    public static final String AILMENT_START_DATE = "startDate";
    public static final String AILMENT_DURATION = "duration";
    public static final String AILMENT_COMMENT = "comment";

    private String[] ailmentCols = {AILMENT_ID, AILMENT_PERSON_REF, AILMENT_ILLNESS_REF, AILMENT_THERAPIST_REF, AILMENT_START_DATE,
            AILMENT_DURATION, AILMENT_COMMENT};

    public AilmentTable(SQLiteDatabase db) {
        this.db = db;
    }

    public void createTreatmentTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + AILMENT_TABLE + " (");
        sb.append(AILMENT_ID + " integer primary key autoincrement,");
        sb.append(AILMENT_PERSON_REF + " integer not null,");
        sb.append(AILMENT_ILLNESS_REF + " integer,"); //not null for the case of preventive ailment
        sb.append(AILMENT_THERAPIST_REF + " integer,"); //not null for the case of auto-medication
        sb.append(AILMENT_START_DATE + " text not null,");
        sb.append(AILMENT_DURATION + " integer,"); //ailment with no end date can be considered permanent
        sb.append(AILMENT_COMMENT + " text,");
        sb.append(" foreign key(" + AILMENT_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + "),");
        sb.append(" foreign key(" + AILMENT_ILLNESS_REF + ") references " + IllnessTable.ILLNESS_TABLE +
                "(" + IllnessTable.ILLNESS_ID + "),");
        sb.append(" foreign key(" + AILMENT_THERAPIST_REF + ") references " + TherapistTable.THERAPIST_TABLE +
                "(" + TherapistTable.THERAPIST_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertAilment(int personId, int illnessId, int therapistId, String startDate, int duration, String comment) {
        ContentValues values = new ContentValues();
        values.put(AILMENT_PERSON_REF, personId);
        if (illnessId >= 0)
            values.put(AILMENT_ILLNESS_REF, illnessId);
        if (therapistId >= 0)
            values.put(AILMENT_THERAPIST_REF, therapistId);
        values.put(AILMENT_START_DATE, startDate);
        if (duration > 0)
            values.put(AILMENT_DURATION, duration);
        if (comment != null)
            values.put(AILMENT_COMMENT, comment);
        return db.insert(AILMENT_TABLE, null, values);
    }

    public boolean updateAilment(int ailmentId, int illnessId, int therapistId, String startDate, int duration, String comment) {
        ContentValues values = new ContentValues();
        if (illnessId >= 0)
            values.put(AILMENT_ILLNESS_REF, illnessId);
        if (therapistId >= 0)
            values.put(AILMENT_THERAPIST_REF, therapistId);
        values.put(AILMENT_START_DATE, startDate);
        if (duration > 0)
            values.put(AILMENT_DURATION, duration);
        if (comment != null)
            values.put(AILMENT_COMMENT, comment);
        return db.update(AILMENT_TABLE, values, AILMENT_ID + "=" + ailmentId, null) > 0;
    }

    public boolean updateAilment(Ailment ailment)
    {
        int ailmentId = ailment.getId();
        int illnessId = ailment.getIllnessId();
        int therapistId = ailment.getTherapistId();
        String startDate = ailment.getStartDate();
        int duration = ailment.getDuration();
        String comment = ailment.getComment();
        return updateAilment(ailmentId, illnessId, therapistId, startDate, duration, comment);
    }

    public Ailment getAilmentWithId(int id) {
        Cursor cursor = db.query(AILMENT_TABLE, ailmentCols, AILMENT_ID + "=" + id, null, null, null, null);
        if (cursor.moveToFirst())
            return  cursorToAilment(cursor);
        return null;
    }

    public List<Ailment> getDayAilmentsForPerson(int personId, String date)
    {
        List<Ailment> res = new ArrayList<Ailment>();
        Cursor cursor = db.query(AILMENT_TABLE, ailmentCols, AILMENT_PERSON_REF + "=" + personId, null, null, null, null);
        cursor.moveToFirst();
        Ailment ailment;
        String sDate;
        Calendar currentDate, startDate, endDate;
        currentDate = HealthRecordUtils.stringToCalendar(date);
        //FIXME: improve the following algorithm with the use of duration
        while (! cursor.isAfterLast())
        {
            ailment = cursorToAilment(cursor);
            sDate = ailment.getStartDate();
            startDate = HealthRecordUtils.stringToCalendar(sDate);
            endDate = HealthRecordUtils.stringToCalendar(HealthRecordUtils.computeEndDate(sDate, ailment.getDuration()));
            if (endDate == null)
            {
                if (currentDate.equals(startDate) || currentDate.after(startDate)) {
                    res.add(ailment);
                }
            }
            else
            {
                if (currentDate.equals(startDate) || currentDate.equals(endDate)) {
                    res.add(ailment);
                }
                else if (currentDate.after(startDate) && currentDate.before(endDate)) {
                    res.add(ailment);
                }
            }
            cursor.moveToNext();
        }
        return res;
    }

    public boolean removeAilmentWithId(int ailmentId)
    {
        return db.delete(AILMENT_TABLE, AILMENT_ID + "=" + ailmentId, null) > 0;
    }

    private Ailment cursorToAilment(Cursor cursor) {
        Ailment ailment = new Ailment();
        ailment.setId(cursor.getInt(0));
        ailment.setPersonId(cursor.getInt(1));
        ailment.setIllnessId((cursor.isNull(2)) ? 0 : cursor.getInt(2));
        ailment.setTherapistId((cursor.isNull(3))?0:cursor.getInt(3));
        ailment.setStartDate((cursor.isNull(4))?null:cursor.getString(4));
        ailment.setDuration((cursor.isNull(5))?-1:cursor.getInt(5));
        ailment.setComment((cursor.isNull(6))?null:cursor.getString(6));
        return ailment;
    }

}