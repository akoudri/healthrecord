package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ali Koudri on 28/05/14.
 */
public class AilmentTable {

    private SQLiteDatabase db;

    //Table
    public static final String AILMENT_TABLE = "ailment";
    public static final String AILMENT_ID = "_id";
    public static final String AILMENT_PERSON_REF = "personId";
    public static final String AILMENT_ILLNESS_REF = "illnessId";
    public static final String AILMENT_START_DATE = "startDate";
    public static final String AILMENT_END_DATE = "endDate";
    public static final String AILMENT_COMMENT = "comment";

    private String[] ailmentCols = {AILMENT_ID, AILMENT_PERSON_REF,AILMENT_ILLNESS_REF,
            AILMENT_START_DATE, AILMENT_END_DATE, AILMENT_COMMENT};

    public AilmentTable(SQLiteDatabase db) {
        this.db = db;
    }

    public void createAilmentTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + AILMENT_TABLE + " (");
        sb.append(AILMENT_ID + " integer primary key autoincrement,");
        sb.append(AILMENT_PERSON_REF + " integer not null,");
        sb.append(AILMENT_ILLNESS_REF + " integer not null,");
        sb.append(AILMENT_START_DATE + " text,");//ailment with no start or end date can be considered chronic
        sb.append(AILMENT_END_DATE + " text,");
        sb.append(AILMENT_COMMENT + " text,");
        sb.append(" foreign key(" + AILMENT_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + "),");
        sb.append(" foreign key(" + AILMENT_ILLNESS_REF + ") references " + IllnessTable.ILLNESS_TABLE +
                "(" + IllnessTable.ILLNESS_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertAilment(int personId, int illnessId, String startDate, String endDate, String comment) {
        ContentValues values = new ContentValues();
        values.put(AILMENT_PERSON_REF, personId);
        values.put(AILMENT_ILLNESS_REF, illnessId);
        if (startDate != null)
            values.put(AILMENT_START_DATE, startDate);
        if (endDate != null)
            values.put(AILMENT_END_DATE, endDate);
        if (comment != null)
            values.put(AILMENT_COMMENT, comment);
        return db.insert(AILMENT_TABLE, null, values);
    }

    public boolean updateAilment(int ailmentId, int illnessId, String startDate, String endDate, String comment) {
        ContentValues values = new ContentValues();
        values.put(AILMENT_ILLNESS_REF, illnessId);
        if (startDate != null)
            values.put(AILMENT_START_DATE, startDate);
        if (endDate != null)
            values.put(AILMENT_END_DATE, endDate);
        if (comment != null)
            values.put(AILMENT_COMMENT, comment);
        return db.update(AILMENT_TABLE, values, AILMENT_ID + "=" + ailmentId, null) > 0;
    }

    public boolean updateAilment(Ailment ailment)
    {
        int ailmentId = ailment.getId();
        int illnessId = ailment.getIllnessId();
        String sDate = ailment.getStartDate();
        String eDate = ailment.getEndDate();
        String comment = ailment.getComment();
        return updateAilment(ailmentId, illnessId, sDate, eDate, comment);
    }

    public Ailment getAilmentWithId(int id) {
        Cursor cursor = db.query(AILMENT_TABLE, ailmentCols, AILMENT_ID + "=" + id, null, null, null, null);
        if (cursor.moveToFirst())
            return  cursorToAilment(cursor);
        return null;
    }

    public boolean removeAilmentWithId(int ailmentId)
    {
        return db.delete(AILMENT_TABLE, AILMENT_ID + "=" + ailmentId, null) > 0;
    }

    public List<Ailment> getDayAilmentsForPerson(int personId, String date)
    {
        List<Ailment> res = new ArrayList<Ailment>();
        Cursor cursor = db.query(AILMENT_TABLE, ailmentCols, AILMENT_PERSON_REF + "=" + personId, null, null, null, null);
        cursor.moveToFirst();
        Ailment ailment;
        Calendar currentDate, startDate, endDate;
        currentDate = HealthRecordUtils.stringToCalendar(date);
        while (! cursor.isAfterLast())
        {
            ailment = cursorToAilment(cursor);
            startDate = HealthRecordUtils.stringToCalendar(ailment.getStartDate());
            endDate = HealthRecordUtils.stringToCalendar(ailment.getEndDate());
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

    private Ailment cursorToAilment(Cursor cursor) {
        Ailment ailment = new Ailment();
        ailment.setId(cursor.getInt(0));
        ailment.setPersonId(cursor.getInt(1));
        ailment.setIllnessId(cursor.getInt(2));
        ailment.setStartDate((cursor.isNull(3))?null:cursor.getString(3));
        ailment.setEndDate((cursor.isNull(4))?null:cursor.getString(4));
        ailment.setComment((cursor.isNull(5))?null:cursor.getString(5));
        return ailment;
    }

}