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
public class CholesterolMeasureTable {

    private SQLiteDatabase db;

    //Table
    public static final String CHOLESTEROL_MEASURE_TABLE = "dsh_m";
    public static final String CHOLESTEROL_MEASURE_ID = "_id";
    public static final String CHOLESTEROL_MEASURE_PERSON_REF = "personId";
    public static final String CHOLESTEROL_MEASURE_DATE = "date";
    public static final String CHOLESTEROL_MEASURE_TOTAL = "total";
    public static final String CHOLESTEROL_MEASURE_HDL = "hdl";
    public static final String CHOLESTEROL_MEASURE_LDL = "ldl";
    public static final String CHOLESTEROL_MEASURE_TRIGLYCERIDS = "triglycerids";

    private String[] measureCols = {CHOLESTEROL_MEASURE_ID, CHOLESTEROL_MEASURE_PERSON_REF, CHOLESTEROL_MEASURE_DATE,
            CHOLESTEROL_MEASURE_TOTAL, CHOLESTEROL_MEASURE_HDL, CHOLESTEROL_MEASURE_LDL, CHOLESTEROL_MEASURE_TRIGLYCERIDS};

    public CholesterolMeasureTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createMeasureTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + CHOLESTEROL_MEASURE_TABLE + "(");
        sb.append(CHOLESTEROL_MEASURE_ID + " integer primary key autoincrement,");
        sb.append(CHOLESTEROL_MEASURE_PERSON_REF + " integer not null,");
        sb.append(CHOLESTEROL_MEASURE_DATE + " integer not null,");
        sb.append(CHOLESTEROL_MEASURE_TOTAL + " real not null check(" + CHOLESTEROL_MEASURE_TOTAL + " > 0.0),");
        sb.append(CHOLESTEROL_MEASURE_HDL + " real not null check(" + CHOLESTEROL_MEASURE_HDL + " > 0.0),");
        sb.append(CHOLESTEROL_MEASURE_LDL + " real not null check(" + CHOLESTEROL_MEASURE_LDL + " > 0.0),");
        sb.append(CHOLESTEROL_MEASURE_TRIGLYCERIDS + " real not null check(" + CHOLESTEROL_MEASURE_TRIGLYCERIDS + " > 0.0),");
        sb.append(" foreign key(" + CHOLESTEROL_MEASURE_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    //zero values are considered as null
    public long insertMeasure(int personId, String date, String hour, float total, float hdl, float ldl, float triglycerid)
    {
        ContentValues values = new ContentValues();
        values.put(CHOLESTEROL_MEASURE_PERSON_REF, personId);
        values.put(CHOLESTEROL_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(CHOLESTEROL_MEASURE_TOTAL, total);
        values.put(CHOLESTEROL_MEASURE_HDL, hdl);
        values.put(CHOLESTEROL_MEASURE_LDL, ldl);
        values.put(CHOLESTEROL_MEASURE_TRIGLYCERIDS, triglycerid);
        return db.insert(CHOLESTEROL_MEASURE_TABLE, null, values);
    }

    //zero values are considered as null
    public boolean updateMeasureWithId(int measureId, String date, String hour, float total, float hdl, float ldl, float triglycerid)
    {
        ContentValues values = new ContentValues();
        values.put(CHOLESTEROL_MEASURE_DATE, HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis());
        values.put(CHOLESTEROL_MEASURE_TOTAL, total);
        values.put(CHOLESTEROL_MEASURE_HDL, hdl);
        values.put(CHOLESTEROL_MEASURE_LDL, ldl);
        values.put(CHOLESTEROL_MEASURE_TRIGLYCERIDS, triglycerid);
        return db.update(CHOLESTEROL_MEASURE_TABLE, values, CHOLESTEROL_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public boolean updateMeasure(CholesterolMeasure measure)
    {
        return updateMeasureWithId(measure.getId(), measure.getDate(), measure.getHour(), measure.getTotal(), measure.getHDL(),
                measure.getLDL(), measure.getTriglycerids());
    }

    public boolean removeMeasureWithId(int measureId)
    {
        return db.delete(CHOLESTEROL_MEASURE_TABLE, CHOLESTEROL_MEASURE_ID + "=" + measureId, null) > 0;
    }

    public CholesterolMeasure getMeasureWithId(int measureId)
    {
        Cursor cursor = db.query(CHOLESTEROL_MEASURE_TABLE, measureCols, CHOLESTEROL_MEASURE_ID + "=" + measureId, null, null, null, null);
        if (cursor.moveToFirst()) return cursorToMeasure(cursor);
        return null;
    }

    public List<CholesterolMeasure> getMeasuresInInterval(Calendar start, Calendar end)
    {
        List<CholesterolMeasure> res = new ArrayList<CholesterolMeasure>();
        long s = start.getTimeInMillis();
        long e = end.getTimeInMillis() + 86400000; //add 24h to be inclusive
        Cursor cursor = db.query(CHOLESTEROL_MEASURE_TABLE, measureCols, CHOLESTEROL_MEASURE_DATE + ">=" + s + " and " + CHOLESTEROL_MEASURE_DATE + "<" + e,
                null, null, null, CHOLESTEROL_MEASURE_DATE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMeasure(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    private CholesterolMeasure cursorToMeasure(Cursor cursor)
    {
        CholesterolMeasure measure = new CholesterolMeasure();
        measure.setId(cursor.getInt(0));
        measure.setPersonId(cursor.getInt(1));
        String[] d = HealthRecordUtils.millisToLongdatestring(cursor.getLong(2)).split("/");
        String date = String.format("%s/%s/%s", d[0], d[1], d[2]);
        String hour = String.format("%s:%s", d[3], d[4]);
        measure.setDate(date);
        measure.setHour(hour);
        measure.setTotal(cursor.getFloat(3));
        measure.setHDL(cursor.getFloat(4));
        measure.setLDL(cursor.getFloat(5));
        measure.setTriglycerids(cursor.getFloat(6));
        return measure;
    }
}
