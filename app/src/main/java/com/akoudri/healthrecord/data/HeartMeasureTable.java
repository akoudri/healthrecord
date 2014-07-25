package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ali Koudri on 23/07/14.
 */
public class HeartMeasureTable {

    private SQLiteDatabase db;

    //Table
    public static final String HEART_MEASURE_TABLE = "heart_measure";
    public static final String HEART_MEASURE_ID = "_id";
    public static final String HEART_MEASURE_PERSON_REF = "personId";
    public static final String HEART_MEASURE_DATE = "date";
    public static final String HEART_MEASURE_DIASTOLIC = "diastolic";
    public static final String HEART_MEASURE_SYSTOLIC = "systolic";
    public static final String HEART_MEASURE_HEARTBEAT = "heartbeat";

    private String[] measureCols = {HEART_MEASURE_ID, HEART_MEASURE_PERSON_REF, HEART_MEASURE_DATE, HEART_MEASURE_DIASTOLIC, HEART_MEASURE_SYSTOLIC, HEART_MEASURE_HEARTBEAT};

    public HeartMeasureTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createMeasureTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + HEART_MEASURE_TABLE + "(");
        sb.append(HEART_MEASURE_ID + " integer primary key autoincrement,");
        sb.append(HEART_MEASURE_PERSON_REF + " integer not null,");
        sb.append(HEART_MEASURE_DATE + " integer not null,");
        sb.append(HEART_MEASURE_DIASTOLIC + " integer not null check(" + HEART_MEASURE_DIASTOLIC + " > 0),");
        sb.append(HEART_MEASURE_SYSTOLIC + " integer not null check(" + HEART_MEASURE_SYSTOLIC + " > 0),");
        sb.append(HEART_MEASURE_HEARTBEAT + " integer not null check(" + HEART_MEASURE_HEARTBEAT + " > 0),");
        sb.append(" foreign key(" + HEART_MEASURE_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    //zero values are considered as null
    public long insertMeasure(int personId, String date, String hour, int diastolic, int systolic, int heartbeat)
    {
        ContentValues values = new ContentValues();
        values.put(HEART_MEASURE_PERSON_REF, personId);
        values.put(HEART_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(HEART_MEASURE_DIASTOLIC, diastolic);
        values.put(HEART_MEASURE_SYSTOLIC, systolic);
        values.put(HEART_MEASURE_HEARTBEAT, heartbeat);
        return db.insert(HEART_MEASURE_TABLE, null, values);
    }

    //zero values are considered as null
    public boolean updateMeasureWithDate(int personId, String date, String hour,int diastolic, int systolic, int heartbeat)
    {
        ContentValues values = new ContentValues();
        values.put(HEART_MEASURE_DIASTOLIC, diastolic);
        values.put(HEART_MEASURE_SYSTOLIC, systolic);
        values.put(HEART_MEASURE_HEARTBEAT, heartbeat);
        long d = HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis();
        return db.update(HEART_MEASURE_TABLE, values, HEART_MEASURE_PERSON_REF + "=" + personId + " and " + HEART_MEASURE_DATE + "=" + d, null) > 0;
    }

    public List<HeartMeasure> getPersonMeasuresWithDate(int personId, String date)
    {
        List<HeartMeasure> res = new ArrayList<HeartMeasure>();
        long d = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        long de = d + 86400000L;
        Cursor cursor = db.query(HEART_MEASURE_TABLE, measureCols, HEART_MEASURE_PERSON_REF + "=" + personId + " and " + HEART_MEASURE_DATE + ">=" + d + " and "
                + HEART_MEASURE_DATE + "<" + de, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMeasure(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public int getTotalMeasureCountForPerson(int personId)
    {
        String req = "select count(*) from " + HEART_MEASURE_TABLE + " where " + HEART_MEASURE_PERSON_REF + "=" + personId;
        Cursor count  = db.rawQuery(req, null);
        if (! count.moveToFirst()) return 0;
        int res = count.getInt(0);
        count.close();
        return res;
    }

    public int[] getMonthMeasuresForPerson(int personId, Calendar cal)
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
        List<HeartMeasure> res = new ArrayList<HeartMeasure>();
        Cursor cursor = db.query(HEART_MEASURE_TABLE, measureCols,
                HEART_MEASURE_PERSON_REF + "=" + personId + " and " + HEART_MEASURE_DATE + ">=" + ms + " and " + HEART_MEASURE_DATE + "<" + me,
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMeasure(cursor));
            cursor.moveToNext();
        }
        int[] measures = new int[max];
        for (int i = 0; i < max; i++)
            measures[i] = 0;
        for (HeartMeasure measure : res)
        {
            long d = HealthRecordUtils.stringToCalendar(measure.getDate()).getTimeInMillis() - cal.getTimeInMillis();
            int i = (int)(d/86400000L);
            measures[i] ++;
        }
        return measures;
    }

    public int countMeasuresForDay(int personId, long date)
    {
        //We assume that date is set to 00h00
        long de = 86400000L;
        String req = "select count(*) from " + HEART_MEASURE_TABLE + " where " + HEART_MEASURE_PERSON_REF + "=" + personId +
                " and " + HEART_MEASURE_DATE + ">=" + date + " and " + HEART_MEASURE_DATE + "<" + de;
        Cursor count  = db.rawQuery(req, null);
        if (! count.moveToFirst()) return 0;
        int res = count.getInt(0);
        count.close();
        return res;
    }

    private HeartMeasure cursorToMeasure(Cursor cursor)
    {
        HeartMeasure measure = new HeartMeasure();
        measure.setId(cursor.getInt(0));
        measure.setPersonId(cursor.getInt(1));
        String[] d = HealthRecordUtils.millisToLongdatestring(cursor.getLong(2)).split("/");
        String date = String.format("%s/%s/%s", d[0], d[1], d[2]);
        String hour = String.format("%s:%s", d[3], d[4]);
        measure.setDate(date);
        measure.setHour(hour);
        measure.setDiastolic(cursor.getInt(3));
        measure.setSystolic(cursor.getInt(4));
        measure.setHeartbeat(cursor.getInt(5));
        return measure;
    }
}
