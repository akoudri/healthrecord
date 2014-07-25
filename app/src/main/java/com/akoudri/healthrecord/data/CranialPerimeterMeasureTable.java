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

    //zero values are considered as null
    public boolean updateMeasureWithDate(int personId, String date, String hour, int cranialPerimeter)
    {
        ContentValues values = new ContentValues();
        values.put(CP_MEASURE_VALUE, cranialPerimeter);
        long d = HealthRecordUtils.datehourToCalendar(date, hour).getTimeInMillis();
        return db.update(CP_MEASURE_TABLE, values, CP_MEASURE_PERSON_REF + "=" + personId + " and " + CP_MEASURE_DATE + "=" + d, null) > 0;
    }

    public List<CranialPerimeterMeasure> getPersonMeasuresWithDate(int personId, String date)
    {
        List<CranialPerimeterMeasure> res = new ArrayList<CranialPerimeterMeasure>();
        long d = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        long de = d + 86400000L;
        Cursor cursor = db.query(CP_MEASURE_TABLE, measureCols, CP_MEASURE_PERSON_REF + "=" + personId + " and " + CP_MEASURE_DATE + ">=" + d + " and "
                + CP_MEASURE_DATE + "<" + de, null, null, null, null);
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
        String req = "select count(*) from " + CP_MEASURE_TABLE + " where " + CP_MEASURE_PERSON_REF + "=" + personId;
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
        List<CranialPerimeterMeasure> res = new ArrayList<CranialPerimeterMeasure>();
        Cursor cursor = db.query(CP_MEASURE_TABLE, measureCols,
                CP_MEASURE_PERSON_REF + "=" + personId + " and " + CP_MEASURE_DATE + ">=" + ms + " and " + CP_MEASURE_DATE + "<" + me,
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
        for (CranialPerimeterMeasure measure : res)
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
        String req = "select count(*) from " + CP_MEASURE_TABLE + " where " + CP_MEASURE_PERSON_REF + "=" + personId +
                " and " + CP_MEASURE_DATE + ">=" + date + " and " + CP_MEASURE_DATE + "<" + de;
        Cursor count  = db.rawQuery(req, null);
        if (! count.moveToFirst()) return 0;
        int res = count.getInt(0);
        count.close();
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
