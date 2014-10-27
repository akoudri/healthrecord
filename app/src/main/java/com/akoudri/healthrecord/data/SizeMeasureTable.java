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
public class SizeMeasureTable {

    private SQLiteDatabase db;

    //Table
    public static final String SIZE_MEASURE_TABLE = "size_m";
    public static final String SIZE_MEASURE_ID = "_id";
    public static final String SIZE_MEASURE_PERSON_REF = "personId";
    public static final String SIZE_MEASURE_DATE = "date";
    public static final String SIZE_MEASURE_VALUE = "value";

    private String[] measureCols = {SIZE_MEASURE_ID, SIZE_MEASURE_PERSON_REF, SIZE_MEASURE_DATE, SIZE_MEASURE_VALUE};

    public SizeMeasureTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createMeasureTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + SIZE_MEASURE_TABLE + "(");
        sb.append(SIZE_MEASURE_ID + " integer primary key autoincrement,");
        sb.append(SIZE_MEASURE_PERSON_REF + " integer not null,");
        sb.append(SIZE_MEASURE_DATE + " integer not null,");
        sb.append(SIZE_MEASURE_VALUE + " integer not null check(" + SIZE_MEASURE_VALUE + " > 0),");
        sb.append(" foreign key(" + SIZE_MEASURE_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    //zero values are considered as null
    public long insertMeasure(int personId, String date, String hour, int size)
    {
        ContentValues values = new ContentValues();
        values.put(SIZE_MEASURE_PERSON_REF, personId);
        values.put(SIZE_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(SIZE_MEASURE_VALUE, size);
        return db.insert(SIZE_MEASURE_TABLE, null, values);
    }

    //zero values are considered as null
    public boolean updateMeasureWithId(int measureId, String date, String hour, int size)
    {
        ContentValues values = new ContentValues();
        values.put(SIZE_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(SIZE_MEASURE_VALUE, size);
        return db.update(SIZE_MEASURE_TABLE, values, SIZE_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public boolean updateMeasure(SizeMeasure measure)
    {
        return updateMeasureWithId(measure.getId(), measure.getDate(), measure.getHour(), measure.getValue());
    }

    public boolean removeMeasureWithId(int measureId)
    {
        return db.delete(SIZE_MEASURE_TABLE, SIZE_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public SizeMeasure getMeasureWithId(int measureId)
    {
        Cursor cursor = db.query(SIZE_MEASURE_TABLE, measureCols, SIZE_MEASURE_ID + "=" + measureId, null, null, null, null);
        if (cursor.moveToFirst()) return cursorToMeasure(cursor);
        return null;
    }

    public List<SizeMeasure> getMeasuresInInterval(Calendar start, Calendar end)
    {
        List<SizeMeasure> res = new ArrayList<SizeMeasure>();
        long s = start.getTimeInMillis();
        long e = end.getTimeInMillis() + 86400000; //add 24h to be inclusive
        Cursor cursor = db.query(SIZE_MEASURE_TABLE, measureCols, SIZE_MEASURE_DATE + ">=" + s + " and " + SIZE_MEASURE_DATE + "<" + e,
                null, null, null, SIZE_MEASURE_DATE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMeasure(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    private SizeMeasure cursorToMeasure(Cursor cursor)
    {
        SizeMeasure measure = new SizeMeasure();
        measure.setId(cursor.getInt(0));
        measure.setPersonId(cursor.getInt(1));
        String[] d = HealthRecordUtils.millisToLongdatestring(cursor.getLong(2)).split("/");
        String date = String.format("%s/%s/%s", d[0], d[1], d[2]);
        String hour = String.format("%s:%s", d[3], d[4]);
        measure.setDate(date);
        measure.setHour(hour);
        measure.setValue(cursor.getInt(3));
        return measure;
    }
}
