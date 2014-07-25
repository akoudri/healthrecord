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

    //zero values are considered as null
    public boolean updateMeasureWithDate(int personId, String date, String hour, double weight)
    {
        ContentValues values = new ContentValues();
        values.put(WEIGHT_MEASURE_VALUE, weight);
        long d = HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis();
        return db.update(WEIGHT_MEASURE_TABLE, values, WEIGHT_MEASURE_PERSON_REF + "=" + personId + " and " + WEIGHT_MEASURE_DATE + "=" + d, null) > 0;
    }

    public int getTotalMeasureCountForPerson(int personId)
    {
        String req = "select count(*) from " + WEIGHT_MEASURE_TABLE + " where " + WEIGHT_MEASURE_PERSON_REF + "=" + personId;
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
        List<WeightMeasure> res = new ArrayList<WeightMeasure>();
        Cursor cursor = db.query(WEIGHT_MEASURE_TABLE, measureCols,
                WEIGHT_MEASURE_PERSON_REF + "=" + personId + " and " + WEIGHT_MEASURE_DATE + ">=" + ms + " and " + WEIGHT_MEASURE_DATE + "<" + me,
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
        for (WeightMeasure measure : res)
        {
            long d = HealthRecordUtils.stringToCalendar(measure.getDate()).getTimeInMillis() - cal.getTimeInMillis();
            int i = (int)(d/86400000L);
            measures[i] ++;
        }
        return measures;
    }

    public List<WeightMeasure> getPersonMeasuresWithDate(int personId, String date)
    {
        List<WeightMeasure> res = new ArrayList<WeightMeasure>();
        long d = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        long de = d + 86400000L;
        Cursor cursor = db.query(WEIGHT_MEASURE_TABLE, measureCols, WEIGHT_MEASURE_PERSON_REF + "=" + personId + " and " + WEIGHT_MEASURE_DATE + ">=" + d + " and "
                + WEIGHT_MEASURE_DATE + "<" + de, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMeasure(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public int countMeasuresForDay(int personId, long date)
    {
        //We assume that date is set to 00h00
        long de = 86400000L;
        String req = "select count(*) from " + WEIGHT_MEASURE_TABLE + " where " + WEIGHT_MEASURE_PERSON_REF + "=" + personId +
                " and " + WEIGHT_MEASURE_DATE + ">=" + date + " and " + WEIGHT_MEASURE_DATE + "<" + de;
        Cursor count  = db.rawQuery(req, null);
        if (! count.moveToFirst()) return 0;
        int res = count.getInt(0);
        count.close();
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
