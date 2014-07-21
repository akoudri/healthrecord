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
        sb.append(AILMENT_START_DATE + " integer not null,");
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
        long sd = HealthRecordUtils.stringToCalendar(startDate).getTimeInMillis();
        if (isOverlapping(personId, illnessId, sd, duration)) return -2;
        ContentValues values = new ContentValues();
        values.put(AILMENT_PERSON_REF, personId);
        if (illnessId > 0)
            values.put(AILMENT_ILLNESS_REF, illnessId);
        if (therapistId > 0)
            values.put(AILMENT_THERAPIST_REF, therapistId);
        values.put(AILMENT_START_DATE, sd);
        if (duration >= 0)
            values.put(AILMENT_DURATION, duration);
        if (comment != null)
            values.put(AILMENT_COMMENT, comment);
        return db.insert(AILMENT_TABLE, null, values);
    }

    public int updateAilment(int ailmentId, int illnessId, int therapistId, String startDate, int duration, String comment) {
        long sd = HealthRecordUtils.stringToCalendar(startDate).getTimeInMillis();
        if (isOverlappingAilment(ailmentId, illnessId, sd, duration)) return -1;
        ContentValues values = new ContentValues();
        if (illnessId > 0)
            values.put(AILMENT_ILLNESS_REF, illnessId);
        else values.putNull(AILMENT_ILLNESS_REF);
        if (therapistId > 0)
            values.put(AILMENT_THERAPIST_REF, therapistId);
        else values.putNull(AILMENT_THERAPIST_REF);
        values.put(AILMENT_START_DATE, HealthRecordUtils.stringToCalendar(startDate).getTimeInMillis());
        if (duration >= 0)
            values.put(AILMENT_DURATION, duration);
        else
            values.putNull(AILMENT_DURATION);
        if (comment != null)
            values.put(AILMENT_COMMENT, comment);
        else values.putNull(AILMENT_COMMENT);
        return db.update(AILMENT_TABLE, values, AILMENT_ID + "=" + ailmentId, null);
    }

    public int updateAilment(Ailment ailment)
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
        long today = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        Cursor cursor = db.query(AILMENT_TABLE, ailmentCols, AILMENT_PERSON_REF + "=" + personId +
                " and " + AILMENT_START_DATE + "<=" + today + " and (" + AILMENT_DURATION + " is null or " + AILMENT_START_DATE + " + " +
                AILMENT_DURATION + " * 86400000 >= " + today + ")", null, null, null, null);
        Ailment ailment;
        cursor.moveToFirst();
        while (! cursor.isAfterLast())
        {
            ailment = cursorToAilment(cursor);
            res.add(ailment);
            cursor.moveToNext();
        }
        return res;
    }

    public boolean removeAilmentWithId(int ailmentId)
    {
        return db.delete(AILMENT_TABLE, AILMENT_ID + "=" + ailmentId, null) > 0;
    }

    private boolean isOverlapping(int personId, int illnessId, long startDate, int duration)
    {
        String req, sreq1, sreq2;
        if (duration == -1)
        {
            sreq1 = "(" + AILMENT_DURATION + " is null)";
            sreq2 = "(" + AILMENT_DURATION + " is not null and ((" + AILMENT_START_DATE + "<=" + startDate + " and " + AILMENT_START_DATE + ">=" +
                    startDate + "-" + AILMENT_DURATION + "* 86400000) or (" + AILMENT_START_DATE + ">=" + startDate +")))";
        }
        else
        {
            int d = duration * 86400000 - 1000;
            long endDate = startDate + d;
            sreq1 = "(" + AILMENT_DURATION + " is not null and ((" + AILMENT_START_DATE + "<=" + startDate + " and " +
                    AILMENT_START_DATE +">=" + startDate + "-" + AILMENT_DURATION + "* 86400000) or (" + AILMENT_START_DATE +
                    "<=" + endDate + " and " + AILMENT_START_DATE + ">=" + endDate + "-" + AILMENT_DURATION + "* 86400000) or (" +
                    AILMENT_START_DATE + ">=" + startDate + " and " + AILMENT_START_DATE + "<=" + endDate + "-" + AILMENT_DURATION + "* 86400000)))";
            sreq2 = "(" + AILMENT_DURATION + " is null and (" + AILMENT_START_DATE + "<=" + startDate + " or " + AILMENT_START_DATE + "<=" + endDate +"))";
        }
        req = "select count(*) from " + AILMENT_TABLE + " where " + AILMENT_PERSON_REF + "=" + personId + " and " +
                AILMENT_ILLNESS_REF + "=" + illnessId + " and (" + sreq1 + " or " + sreq2 + ")";
        Cursor count  = db.rawQuery(req, null);
        if (!count.moveToFirst()) return false;
        boolean res = (count.getInt(0) > 0);
        count.close();
        return res;
    }

    private boolean isOverlappingAilment(int ailmentId, int illnessId, long startDate, int duration)
    {
        String req, sreq1, sreq2;
        if (duration == -1)
        {
            sreq1 = "(" + AILMENT_DURATION + " is null)";
            sreq2 = "(" + AILMENT_DURATION + " is not null and ((" + AILMENT_START_DATE + "<=" + startDate + " and " + AILMENT_START_DATE + ">=" +
                    startDate + "-" + AILMENT_DURATION + "* 86400000) or (" + AILMENT_START_DATE + ">=" + startDate +")))";
        }
        else
        {
            int d = duration * 86400000 - 1000;
            long endDate = startDate + d;
            sreq1 = "(" + AILMENT_DURATION + " is not null and ((" + AILMENT_START_DATE + "<=" + startDate + " and " +
                    AILMENT_START_DATE +">=" + startDate + "-" + AILMENT_DURATION + "* 86400000) or (" + AILMENT_START_DATE +
                    "<=" + endDate + " and " + AILMENT_START_DATE + ">=" + endDate + "-" + AILMENT_DURATION + "* 86400000) or (" +
                    AILMENT_START_DATE + ">=" + startDate + " and " + AILMENT_START_DATE + "<=" + endDate + "-" + AILMENT_DURATION + "* 86400000)))";
            sreq2 = "(" + AILMENT_DURATION + " is null and (" + AILMENT_START_DATE + "<=" + startDate + " or " + AILMENT_START_DATE + "<=" + endDate +"))";
        }
        req = "select count(*) from " + AILMENT_TABLE + " where " + AILMENT_ID + "<>" + ailmentId + " and " +
                AILMENT_ILLNESS_REF + "=" + illnessId + " and (" + sreq1 + " or " + sreq2 + ")";
        Cursor count  = db.rawQuery(req, null);
        if (!count.moveToFirst()) return false;
        boolean res = (count.getInt(0) > 0);
        count.close();
        return res;
    }

    public int countAilmentsForDay(int personId, long date)
    {
        String req = "select count(*) from " + AILMENT_TABLE + " where " + AILMENT_ILLNESS_REF + " is not null and " + AILMENT_PERSON_REF + "=" + personId +
                " and " + AILMENT_START_DATE + "<=" + date + " and (" + AILMENT_DURATION + " is null or " + AILMENT_START_DATE + " + " +
                AILMENT_DURATION + " * 86400000 >= " + date + ")";
        Cursor count = db.rawQuery(req, null);
        if (!count.moveToFirst())
            return 0;
        int res = count.getInt(0);
        count.close();
        return res;
    }

    public int[] getMonthAilmentsForPerson(int personId, Calendar cal)
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
        List<Ailment> res = new ArrayList<Ailment>();
        Cursor cursor = db.query(AILMENT_TABLE, ailmentCols, AILMENT_ILLNESS_REF + " is not null and " + AILMENT_PERSON_REF + "=" + personId +  " and " + AILMENT_START_DATE + "<" + me +
                " and (" +AILMENT_DURATION + " is null or " + AILMENT_START_DATE + ">=" + ms + "-" + AILMENT_DURATION + "* 86400000)",
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToAilment(cursor));
            cursor.moveToNext();
        }
        int[] ailments = new int[max];
        int i;
        for (i = 0; i < max; i++)
            ailments[i] = 0;
        for (Ailment ailment : res)
        {
            long sa = HealthRecordUtils.stringToCalendar(ailment.getStartDate()).getTimeInMillis();
            int d = ailment.getDuration();
            for (i = 0; i < max; i++)
            {
                long ref = ms + i * 86400000L;
                if (d == -1)
                {
                    if (sa <= ref) ailments[i]++;
                }
                else
                {
                    long se = sa + d * 86400000L;
                    if (sa <= ref && se >= ref) ailments[i]++;
                }
            }
        }
        return ailments;
    }

    private Ailment cursorToAilment(Cursor cursor) {
        Ailment ailment = new Ailment();
        ailment.setId(cursor.getInt(0));
        ailment.setPersonId(cursor.getInt(1));
        ailment.setIllnessId((cursor.isNull(2)) ? 0 : cursor.getInt(2));
        ailment.setTherapistId((cursor.isNull(3))?0:cursor.getInt(3));
        ailment.setStartDate(HealthRecordUtils.millisToDatestring(cursor.getLong(4)));
        ailment.setDuration((cursor.isNull(5))?-1:cursor.getInt(5));
        ailment.setComment((cursor.isNull(6))?null:cursor.getString(6));
        return ailment;
    }

}