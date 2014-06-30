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
    private static final String MEASURE_ID = "_id";
    private static final String MEASURE_PERSON_REF = "personId";
    private static final String MEASURE_DATE = "date";
    private static final String MEASURE_WEIGHT = "weight";
    private static final String MEASURE_SIZE = "size";
    private static final String MEASURE_CRANIAL_PERIMETER = "cranialPerimeter";
    private static final String MEASURE_TEMPERATURE = "temperature";
    private static final String MEASURE_GLUCOSE = "glucose";
    private static final String MEASURE_DIASTOLIC = "diastolic";
    private static final String MEASURE_SYSTOLIC = "systolic";
    private static final String MEASURE_HEARTBEAT = "heartbeat";

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
        ContentValues values = new ContentValues();
        values.put(MEASURE_PERSON_REF, personId);
        values.put(MEASURE_DATE, date);
        values.put(MEASURE_WEIGHT, weight);
        values.put(MEASURE_SIZE, size);
        values.put(MEASURE_CRANIAL_PERIMETER, cranialPerimeter);
        values.put(MEASURE_TEMPERATURE, temperature);
        values.put(MEASURE_GLUCOSE, glucose);
        values.put(MEASURE_DIASTOLIC, diastolic);
        values.put(MEASURE_SYSTOLIC, systolic);
        values.put(MEASURE_HEARTBEAT, heartbeat);
        return db.insert(MEASURE_TABLE, null, values);
    }

    //zero values are considered as null
    public boolean updateMeasureWithId(int measureId, int personId, String date, double weight, int size, int cranialPerimeter, double temperature, double glucose,
                                 int diastolic, int systolic, int heartbeat)
    {
        ContentValues values = new ContentValues();
        values.put(MEASURE_PERSON_REF, personId);
        values.put(MEASURE_DATE, date);
        values.put(MEASURE_WEIGHT, weight);
        values.put(MEASURE_SIZE, size);
        values.put(MEASURE_CRANIAL_PERIMETER, cranialPerimeter);
        values.put(MEASURE_TEMPERATURE, temperature);
        values.put(MEASURE_GLUCOSE, glucose);
        values.put(MEASURE_DIASTOLIC, diastolic);
        values.put(MEASURE_SYSTOLIC, systolic);
        values.put(MEASURE_HEARTBEAT, heartbeat);
        return db.update(MEASURE_TABLE, values, MEASURE_ID + "=" + measureId, null) > 0;
    }

    //zero values are considered as null
    public boolean updateMeasureWithDate(int personId, String date, double weight, int size, int cranialPerimeter, double temperature, double glucose,
                                       int diastolic, int systolic, int heartbeat)
    {
        ContentValues values = new ContentValues();
        values.put(MEASURE_PERSON_REF, personId);
        values.put(MEASURE_WEIGHT, weight);
        values.put(MEASURE_SIZE, size);
        values.put(MEASURE_CRANIAL_PERIMETER, cranialPerimeter);
        values.put(MEASURE_TEMPERATURE, temperature);
        values.put(MEASURE_GLUCOSE, glucose);
        values.put(MEASURE_DIASTOLIC, diastolic);
        values.put(MEASURE_SYSTOLIC, systolic);
        values.put(MEASURE_HEARTBEAT, heartbeat);
        return db.update(MEASURE_TABLE, values, MEASURE_DATE + "='" + date + "'", null) > 0;
    }

    public Measure getMeasureWithId(int id)
    {
        Cursor cursor = db.query(MEASURE_TABLE, measureCols, MEASURE_ID + "=" + id, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToMeasure(cursor);
        return null;
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
        measure.setWeight(cursor.getDouble(3));
        measure.setSize(cursor.getInt(4));
        measure.setCranialPerimeter(cursor.getInt(5));
        measure.setTemperature(cursor.getDouble(6));
        measure.setGlucose(cursor.getDouble(7));
        measure.setDiastolic(cursor.getInt(8));
        measure.setSystolic(cursor.getInt(9));
        measure.setHeartbeat(cursor.getInt(10));
        return measure;
    }
}
