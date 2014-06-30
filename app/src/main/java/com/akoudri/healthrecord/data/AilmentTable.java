package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
        sb.append(AILMENT_START_DATE + " text,");
        sb.append(AILMENT_END_DATE + " text,");//ailment with no end date can be considered chronic
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
        if (startDate == null)
            values.putNull(AILMENT_START_DATE);
        else
            values.put(AILMENT_START_DATE, startDate);
        if (endDate == null)
            values.putNull(AILMENT_END_DATE);
        else
            values.put(AILMENT_END_DATE, endDate);
        values.put(AILMENT_COMMENT, comment);
        return db.insert(AILMENT_TABLE, null, values);
    }

    public boolean updateAilment(int ailmentId, int personId, int illnessId, String startDate, String endDate, String comment) {
        ContentValues values = new ContentValues();
        values.put(AILMENT_PERSON_REF, personId);
        values.put(AILMENT_ILLNESS_REF, illnessId);
        values.put(AILMENT_START_DATE, startDate);
        values.put(AILMENT_END_DATE, endDate);
        values.put(AILMENT_COMMENT, comment);
        return db.update(AILMENT_TABLE, values, AILMENT_ID + "=" + ailmentId, null) > 0;
    }

    public boolean updateAilment(Ailment ailment)
    {
        int ailmentId = ailment.getId();
        int personId = ailment.getPersonId();
        int illnessId = ailment.getIllnessId();
        String sDate = ailment.getStartDate();
        String eDate = ailment.getEndDate();
        String comment = ailment.getComment();
        return updateAilment(ailmentId, personId, illnessId, sDate, eDate, comment);
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

    public List<Ailment> getAilmentsForPerson(int personId)
    {
        List<Ailment> res = new ArrayList<Ailment>();
        Cursor cursor = db.query(AILMENT_TABLE, ailmentCols, AILMENT_PERSON_REF + "=" + personId, null, null, null, null);
        cursor.moveToFirst();
        while (! cursor.isAfterLast())
        {
            res.add(cursorToAilment(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public List<Ailment> getDayAilmentsForPerson(int personId, String date)
    {
        List<Ailment> res = new ArrayList<Ailment>();
        Cursor cursor = db.query(AILMENT_TABLE, ailmentCols, AILMENT_PERSON_REF + "=" + personId, null, null, null, null);
        cursor.moveToFirst();
        Ailment ailment;
        Calendar currentDate, startDate, endDate;
        currentDate = stringToCalendar(date);
        while (! cursor.isAfterLast())
        {
            ailment = cursorToAilment(cursor);
            startDate = stringToCalendar(ailment.getStartDate());
            endDate = stringToCalendar(ailment.getEndDate());
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

    private Ailment cursorToAilment(Cursor cursor) {
        Ailment ailment = new Ailment();
        ailment.setId(cursor.getInt(0));
        ailment.setPersonId(cursor.getInt(1));
        ailment.setIllnessId(cursor.getInt(2));
        ailment.setStartDate(cursor.getString(3));
        ailment.setEndDate(cursor.getString(4));
        ailment.setComment(cursor.getString(5));
        return ailment;
    }

}