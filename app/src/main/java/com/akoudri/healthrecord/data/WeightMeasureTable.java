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
public class WeightMeasureTable {

    private SQLiteDatabase db;

    //Table
    public static final String WEIGHT_MEASURE_TABLE = "weight_measure";
    public static final String WEIGHT_MEASURE_ID = "_id";
    public static final String WEIGHT_MEASURE_PERSON_REF = "personId";
    public static final String WEIGHT_MEASURE_DATE = "date";
    public static final String WEIGHT_MEASURE_VALUE = "value";

    private String[] measureCols = {WEIGHT_MEASURE_ID, WEIGHT_MEASURE_PERSON_REF, WEIGHT_MEASURE_DATE, WEIGHT_MEASURE_VALUE};

    public WeightMeasureTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createMeasureTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + WEIGHT_MEASURE_TABLE + "(");
        sb.append(WEIGHT_MEASURE_ID + " integer primary key autoincrement,");
        sb.append(WEIGHT_MEASURE_PERSON_REF + " integer not null,");
        sb.append(WEIGHT_MEASURE_DATE + " integer not null unique,");
        sb.append(WEIGHT_MEASURE_VALUE + " real not null check(" + WEIGHT_MEASURE_VALUE + " > 0.0),");
        sb.append(" foreign key(" + WEIGHT_MEASURE_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    //zero values are considered as null
    public long insertMeasure(int personId, String date, String hour, double weight)
    {
        ContentValues values = new ContentValues();
        values.put(WEIGHT_MEASURE_PERSON_REF, personId);
        values.put(WEIGHT_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(WEIGHT_MEASURE_VALUE, weight);
        return db.insert(WEIGHT_MEASURE_TABLE, null, values);
    }

    public boolean removeMeasureWithId(int measureId)
    {
        return db.delete(WEIGHT_MEASURE_TABLE, WEIGHT_MEASURE_ID + "=" + measureId, null) > 0;
    }

    //zero values are considered as null
    public boolean updateMeasureWithId(int measureId, String date, String hour, double weight)
    {
        ContentValues values = new ContentValues();
        values.put(WEIGHT_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(WEIGHT_MEASURE_VALUE, weight);
        return db.update(WEIGHT_MEASURE_TABLE, values, WEIGHT_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public boolean updateMeasure(WeightMeasure measure)
    {
        return updateMeasureWithId(measure.getId(), measure.getDate(), measure.getHour(), measure.getValue());
    }

    public WeightMeasure getMeasureWithId(int measureId)
    {
        Cursor cursor = db.query(WEIGHT_MEASURE_TABLE, measureCols, WEIGHT_MEASURE_ID + "=" + measureId, null, null, null, null);
        if (cursor.moveToFirst()) return cursorToMeasure(cursor);
        return null;
    }

    public List<WeightMeasure> getMeasuresInInterval(Calendar start, Calendar end)
    {
        List<WeightMeasure> res = new ArrayList<WeightMeasure>();
        long s = start.getTimeInMillis();
        long e = end.getTimeInMillis() + 86400000; //add 24h to be inclusive
        Cursor cursor = db.query(WEIGHT_MEASURE_TABLE, measureCols, WEIGHT_MEASURE_DATE + ">=" + s + " and " + WEIGHT_MEASURE_DATE + "<" + e,
                null, null, null, WEIGHT_MEASURE_DATE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMeasure(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    private WeightMeasure cursorToMeasure(Cursor cursor)
    {
        WeightMeasure measure = new WeightMeasure();
        measure.setId(cursor.getInt(0));
        measure.setPersonId(cursor.getInt(1));
        String[] d = HealthRecordUtils.millisToLongdatestring(cursor.getLong(2)).split("/");
        String date = String.format("%s/%s/%s", d[0], d[1], d[2]);
        String hour = String.format("%s:%s", d[3], d[4]);
        measure.setDate(date);
        measure.setHour(hour);
        measure.setValue(cursor.getDouble(3));
        return measure;
    }
}
