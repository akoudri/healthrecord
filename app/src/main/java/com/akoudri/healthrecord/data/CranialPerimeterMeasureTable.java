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
public class CranialPerimeterMeasureTable {

    private SQLiteDatabase db;

    //Table
    public static final String CP_MEASURE_TABLE = "cp_measure";
    public static final String CP_MEASURE_ID = "_id";
    public static final String CP_MEASURE_PERSON_REF = "personId";
    public static final String CP_MEASURE_DATE = "date";
    public static final String CP_MEASURE_VALUE = "value";

    private String[] measureCols = {CP_MEASURE_ID, CP_MEASURE_PERSON_REF, CP_MEASURE_DATE, CP_MEASURE_VALUE};

    public CranialPerimeterMeasureTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createMeasureTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + CP_MEASURE_TABLE + "(");
        sb.append(CP_MEASURE_ID + " integer primary key autoincrement,");
        sb.append(CP_MEASURE_PERSON_REF + " integer not null,");
        sb.append(CP_MEASURE_DATE + " integer not null,");
        sb.append(CP_MEASURE_VALUE + " integer not null check(" + CP_MEASURE_VALUE + " > 0),");
        sb.append(" foreign key(" + CP_MEASURE_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    //zero values are considered as null
    public long insertMeasure(int personId, String date, String hour, int cranialPerimeter)
    {
        ContentValues values = new ContentValues();
        values.put(CP_MEASURE_PERSON_REF, personId);
        values.put(CP_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(CP_MEASURE_VALUE, cranialPerimeter);
        return db.insert(CP_MEASURE_TABLE, null, values);
    }

    public boolean removeMeasureWithId(int measureId)
    {
        return db.delete(CP_MEASURE_TABLE, CP_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public CranialPerimeterMeasure getMeasureWithId(int measureId)
    {
        Cursor cursor = db.query(CP_MEASURE_TABLE, measureCols, CP_MEASURE_ID + "=" + measureId, null, null, null, null);
        if (cursor.moveToFirst()) return cursorToMeasure(cursor);
        return null;
    }

    //zero values are considered as null
    public boolean updateMeasureWithId(int measureId, String date, String hour, int cranialPerimeter)
    {
        ContentValues values = new ContentValues();
        values.put(CP_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(CP_MEASURE_VALUE, cranialPerimeter);
        return db.update(CP_MEASURE_TABLE, values, CP_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public boolean updateMeasure(CranialPerimeterMeasure measure)
    {
        return updateMeasureWithId(measure.getId(), measure.getDate(), measure.getHour(), measure.getValue());
    }

    public List<CranialPerimeterMeasure> getMeasuresInInterval(Calendar start, Calendar end)
    {
        List<CranialPerimeterMeasure> res = new ArrayList<CranialPerimeterMeasure>();
        long s = start.getTimeInMillis();
        long e = end.getTimeInMillis() + 86400000; //add 24h to be inclusive
        Cursor cursor = db.query(CP_MEASURE_TABLE, measureCols, CP_MEASURE_DATE + ">=" + s + " and " + CP_MEASURE_DATE + "<" + e,
                null, null, null, CP_MEASURE_DATE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMeasure(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    private CranialPerimeterMeasure cursorToMeasure(Cursor cursor)
    {
        CranialPerimeterMeasure measure = new CranialPerimeterMeasure();
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
