package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ali Koudri on 30/06/14.
 */
public class MeasureTable {

    private SQLiteDatabase db;

    //Table
    public static final String MEASURE_TABLE = "measure";
    public static final String MEASURE_ID = "_id";
    public static final String MEASURE_PERSON_REF = "personId";
    public static final String MEASURE_DATE = "date";
    public static final String MEASURE_WEIGHT = "weight";
    public static final String MEASURE_SIZE = "size";
    public static final String MEASURE_CRANIAL_PERIMETER = "cranialPerimeter";
    public static final String MEASURE_TEMPERATURE = "temperature";
    public static final String MEASURE_GLUCOSE = "glucose";
    public static final String MEASURE_DIASTOLIC = "diastolic";
    public static final String MEASURE_SYSTOLIC = "systolic";
    public static final String MEASURE_HEARTBEAT = "heartbeat";

    private String[] measureCols = {MEASURE_ID, MEASURE_PERSON_REF, MEASURE_DATE, MEASURE_WEIGHT, MEASURE_SIZE, MEASURE_CRANIAL_PERIMETER,
            MEASURE_TEMPERATURE, MEASURE_GLUCOSE, MEASURE_DIASTOLIC, MEASURE_SYSTOLIC, MEASURE_HEARTBEAT};

    public MeasureTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createMeasureTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + MEASURE_TABLE + "(");
        sb.append(MEASURE_ID + " integer primary key autoincrement,");
        sb.append(MEASURE_PERSON_REF + " integer not null,");
        sb.append(MEASURE_DATE + " text not null,");
        sb.append(MEASURE_WEIGHT + " real,");
        sb.append(MEASURE_SIZE + " integer,");
        sb.append(MEASURE_CRANIAL_PERIMETER + " integer,");
        sb.append(MEASURE_TEMPERATURE + " real,");
        sb.append(MEASURE_GLUCOSE + " real,");
        sb.append(MEASURE_DIASTOLIC + " integer,");
        sb.append(MEASURE_SYSTOLIC + " integer,");
        sb.append(MEASURE_HEARTBEAT + " integer,");
        sb.append(" foreign key(" + MEASURE_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    //zero values are considered as null
    public long insertMeasure(int personId, String date, double weight, int size, int cranialPerimeter, double temperature, double glucose,
                              int diastolic, int systolic, int heartbeat)
    {
        if (weight > 0.0 && size > 0 && cranialPerimeter > 0 && temperature > 0.0 && glucose > 0 && diastolic > 0 && systolic > 0 && heartbeat > 0)
            return -1;
        ContentValues values = new ContentValues();
        values.put(MEASURE_PERSON_REF, personId);
        values.put(MEASURE_DATE, date);
        if (weight > 0.0)
            values.put(MEASURE_WEIGHT, weight);
        if (size > 0)
            values.put(MEASURE_SIZE, size);
        if (cranialPerimeter > 0)
            values.put(MEASURE_CRANIAL_PERIMETER, cranialPerimeter);
        if (temperature > 0.0)
            values.put(MEASURE_TEMPERATURE, temperature);
        if (glucose > 0.0)
            values.put(MEASURE_GLUCOSE, glucose);
        if (diastolic > 0)
            values.put(MEASURE_DIASTOLIC, diastolic);
        if (systolic > 0)
            values.put(MEASURE_SYSTOLIC, systolic);
        if (heartbeat > 0)
            values.put(MEASURE_HEARTBEAT, heartbeat);
        return db.insert(MEASURE_TABLE, null, values);
    }

    //zero values are considered as null
    public boolean updateMeasureWithDate(int personId, String date, double weight, int size, int cranialPerimeter, double temperature, double glucose,
                                       int diastolic, int systolic, int heartbeat)
    {
        ContentValues values = new ContentValues();
        if (weight > 0.0)
            values.put(MEASURE_WEIGHT, weight);
        else
            values.putNull(MEASURE_WEIGHT);
        if (size > 0)
            values.put(MEASURE_SIZE, size);
        else
            values.putNull(MEASURE_SIZE);
        if (cranialPerimeter > 0)
            values.put(MEASURE_CRANIAL_PERIMETER, cranialPerimeter);
        else
            values.putNull(MEASURE_CRANIAL_PERIMETER);
        if (temperature > 0.0)
            values.put(MEASURE_TEMPERATURE, temperature);
        else
            values.putNull(MEASURE_TEMPERATURE);
        if (glucose > 0.0)
            values.put(MEASURE_GLUCOSE, glucose);
        else
            values.putNull(MEASURE_GLUCOSE);
        if (diastolic > 0)
            values.put(MEASURE_DIASTOLIC, diastolic);
        else
            values.putNull(MEASURE_DIASTOLIC);
        if (systolic > 0)
            values.put(MEASURE_SYSTOLIC, systolic);
        else
            values.putNull(MEASURE_SYSTOLIC);
        if (heartbeat > 0)
            values.put(MEASURE_HEARTBEAT, heartbeat);
        else
            values.putNull(MEASURE_HEARTBEAT);
        return db.update(MEASURE_TABLE, values, MEASURE_PERSON_REF + "=" + personId + " and " + MEASURE_DATE + "='" + date + "'", null) > 0;
    }

    public Measure getPersonMeasureWithDate(int personId, String date)
    {
        Cursor cursor = db.query(MEASURE_TABLE, measureCols, MEASURE_PERSON_REF + "=" + personId + " and " + MEASURE_DATE + "='" + date + "'", null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToMeasure(cursor);
        return null;
    }

    private Measure cursorToMeasure(Cursor cursor)
    {
        Measure measure = new Measure();
        measure.setId(cursor.getInt(0));
        measure.setPersonId(cursor.getInt(1));
        measure.setDate(cursor.getString(2));
        measure.setWeight(cursor.isNull(3)?0.0:cursor.getDouble(3));
        measure.setSize(cursor.isNull(4)?0:cursor.getInt(4));
        measure.setCranialPerimeter(cursor.isNull(5)?0:cursor.getInt(5));
        measure.setTemperature(cursor.isNull(6)?0.0:cursor.getDouble(6));
        measure.setGlucose(cursor.isNull(7)?0.0:cursor.getDouble(7));
        measure.setDiastolic(cursor.isNull(8)?0:cursor.getInt(8));
        measure.setSystolic(cursor.isNull(9)?0:cursor.getInt(9));
        measure.setHeartbeat(cursor.isNull(10)?0:cursor.getInt(10));
        return measure;
    }
}
