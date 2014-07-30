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
public class TemperatureMeasureTable {

    private SQLiteDatabase db;

    //Table
    public static final String TEMPERATURE_MEASURE_TABLE = "temperature_measure";
    public static final String TEMPERATURE_MEASURE_ID = "_id";
    public static final String TEMPERATURE_MEASURE_PERSON_REF = "personId";
    public static final String TEMPERATURE_MEASURE_DATE = "date";
    public static final String TEMPERATURE_MEASURE_VALUE = "value";

    private String[] measureCols = {TEMPERATURE_MEASURE_ID, TEMPERATURE_MEASURE_PERSON_REF, TEMPERATURE_MEASURE_DATE, TEMPERATURE_MEASURE_VALUE};

    public TemperatureMeasureTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createMeasureTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + TEMPERATURE_MEASURE_TABLE + "(");
        sb.append(TEMPERATURE_MEASURE_ID + " integer primary key autoincrement,");
        sb.append(TEMPERATURE_MEASURE_PERSON_REF + " integer not null,");
        sb.append(TEMPERATURE_MEASURE_DATE + " integer not null,");
        sb.append(TEMPERATURE_MEASURE_VALUE + " real not null check(" + TEMPERATURE_MEASURE_VALUE + " > 0.0),");
        sb.append(" foreign key(" + TEMPERATURE_MEASURE_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    //zero values are considered as null
    public long insertMeasure(int personId, String date, String hour, double temperature)
    {
        ContentValues values = new ContentValues();
        values.put(TEMPERATURE_MEASURE_PERSON_REF, personId);
        values.put(TEMPERATURE_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(TEMPERATURE_MEASURE_VALUE, temperature);
        return db.insert(TEMPERATURE_MEASURE_TABLE, null, values);
    }

    //zero values are considered as null
    public boolean updateMeasureWithId(int measureId, String date, String hour, double temperature)
    {
        ContentValues values = new ContentValues();
        values.put(TEMPERATURE_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(TEMPERATURE_MEASURE_VALUE, temperature);
        return db.update(TEMPERATURE_MEASURE_TABLE, values, TEMPERATURE_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public boolean updateMeasure(TemperatureMeasure measure)
    {
        return updateMeasureWithId(measure.getId(), measure.getDate(), measure.getHour(), measure.getValue());
    }

    public boolean removeMeasureWithId(int measureId)
    {
        return db.delete(TEMPERATURE_MEASURE_TABLE, TEMPERATURE_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public TemperatureMeasure getMeasureWithId(int measureId)
    {
        Cursor cursor = db.query(TEMPERATURE_MEASURE_TABLE, measureCols, TEMPERATURE_MEASURE_ID + "=" + measureId, null, null, null, null);
        if (cursor.moveToFirst()) return cursorToMeasure(cursor);
        return null;
    }

    public List<TemperatureMeasure> getMeasuresInInterval(Calendar start, Calendar end)
    {
        List<TemperatureMeasure> res = new ArrayList<TemperatureMeasure>();
        long s = start.getTimeInMillis();
        long e = end.getTimeInMillis() + 86400000; //add 24h to be inclusive
        Cursor cursor = db.query(TEMPERATURE_MEASURE_TABLE, measureCols, TEMPERATURE_MEASURE_DATE + ">=" + s + " and " + TEMPERATURE_MEASURE_DATE + "<" + e,
                null, null, null, TEMPERATURE_MEASURE_DATE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMeasure(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    private TemperatureMeasure cursorToMeasure(Cursor cursor)
    {
        TemperatureMeasure measure = new TemperatureMeasure();
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
