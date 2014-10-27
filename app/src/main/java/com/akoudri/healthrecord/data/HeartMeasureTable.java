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
    public static final String HEART_MEASURE_TABLE = "dsh_m";
    public static final String HEART_MEASURE_ID = "_id";
    public static final String HEART_MEASURE_PERSON_REF = "personId";
    public static final String HEART_MEASURE_DATE = "date";
    public static final String HEART_MEASURE_DIASTOLIC = "dia";
    public static final String HEART_MEASURE_SYSTOLIC = "sys";
    public static final String HEART_MEASURE_HEARTBEAT = "heart";

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
    public boolean updateMeasureWithId(int measureId, String date, String hour,int diastolic, int systolic, int heartbeat)
    {
        ContentValues values = new ContentValues();
        values.put(HEART_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(HEART_MEASURE_DIASTOLIC, diastolic);
        values.put(HEART_MEASURE_SYSTOLIC, systolic);
        values.put(HEART_MEASURE_HEARTBEAT, heartbeat);
        return db.update(HEART_MEASURE_TABLE, values, HEART_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public boolean updateMeasure(HeartMeasure measure)
    {
        return updateMeasureWithId(measure.getId(), measure.getDate(), measure.getHour(), measure.getDiastolic(),
                measure.getSystolic(), measure.getHeartbeat());
    }

    public boolean removeMeasureWithId(int measureId)
    {
        return db.delete(HEART_MEASURE_TABLE, HEART_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public HeartMeasure getMeasureWithId(int measureId)
    {
        Cursor cursor = db.query(HEART_MEASURE_TABLE, measureCols, HEART_MEASURE_ID + "=" + measureId, null, null, null, null);
        if (cursor.moveToFirst()) return cursorToMeasure(cursor);
        return null;
    }

    public List<HeartMeasure> getMeasuresInInterval(Calendar start, Calendar end)
    {
        List<HeartMeasure> res = new ArrayList<HeartMeasure>();
        long s = start.getTimeInMillis();
        long e = end.getTimeInMillis() + 86400000; //add 24h to be inclusive
        Cursor cursor = db.query(HEART_MEASURE_TABLE, measureCols, HEART_MEASURE_DATE + ">=" + s + " and " + HEART_MEASURE_DATE + "<" + e,
                null, null, null, HEART_MEASURE_DATE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMeasure(cursor));
            cursor.moveToNext();
        }
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
