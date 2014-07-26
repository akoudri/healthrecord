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
    public static final String SIZE_MEASURE_TABLE = "size_measure";
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

    public List<SizeMeasure> getPersonMeasuresWithDate(int personId, String date)
    {
        List<SizeMeasure> res = new ArrayList<SizeMeasure>();
        long d = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        long de = d + 86400000L;
        Cursor cursor = db.query(SIZE_MEASURE_TABLE, measureCols, SIZE_MEASURE_PERSON_REF + "=" + personId + " and " + SIZE_MEASURE_DATE + ">=" + d + " and "
                + SIZE_MEASURE_DATE + "<" + de, null, null, null, null);
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
        String req = "select count(*) from " + SIZE_MEASURE_TABLE + " where " + SIZE_MEASURE_PERSON_REF + "=" + personId;
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
        List<SizeMeasure> res = new ArrayList<SizeMeasure>();
        Cursor cursor = db.query(SIZE_MEASURE_TABLE, measureCols,
                SIZE_MEASURE_PERSON_REF + "=" + personId + " and " + SIZE_MEASURE_DATE + ">=" + ms + " and " + SIZE_MEASURE_DATE + "<" + me,
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
        for (SizeMeasure measure : res)
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
        String req = "select count(*) from " + SIZE_MEASURE_TABLE + " where " + SIZE_MEASURE_PERSON_REF + "=" + personId +
                " and " + SIZE_MEASURE_DATE + ">=" + date + " and " + SIZE_MEASURE_DATE + "<" + de;
        Cursor count  = db.rawQuery(req, null);
        if (! count.moveToFirst()) return 0;
        int res = count.getInt(0);
        count.close();
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
