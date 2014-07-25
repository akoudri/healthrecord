package com.akoudri.healthrecord.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Koudri on 25/07/14.
 */
public class MeasureView {

    public static final String MEASURE_VIEW = "measure_view";
    public static final String MEASURE_ID = "_id";
    public static final String MEASURE_PERSON_REF = "personId";
    public static final String MEASURE_DATE = "date";
    public static final String MEASURE_VALUE = "value";
    public static final String MEASURE_TYPE = "type";

    private String[] measureCols = {MEASURE_ID, MEASURE_PERSON_REF, MEASURE_DATE, MEASURE_VALUE, MEASURE_TYPE};

    private SQLiteDatabase db;

    public MeasureView(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createMeasureView()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create view " + MEASURE_VIEW + " as ");
        //Weight
        sb.append("select " + WeightMeasureTable.WEIGHT_MEASURE_ID + " as " + MEASURE_ID + ",");
        sb.append(WeightMeasureTable.WEIGHT_MEASURE_PERSON_REF + " as " + MEASURE_PERSON_REF + ",");
        sb.append(WeightMeasureTable.WEIGHT_MEASURE_DATE + " as " + MEASURE_DATE + ",");
        sb.append(WeightMeasureTable.WEIGHT_MEASURE_VALUE + " || ' kg'" +" as " + MEASURE_VALUE + ",");
        sb.append(AbstractMeasure.WEIGHT_MEASURE_TYPE + " as " + MEASURE_TYPE + " from " + WeightMeasureTable.WEIGHT_MEASURE_TABLE);
        sb.append(" union ");
        //Size
        sb.append("select " + SizeMeasureTable.SIZE_MEASURE_ID + " as " + MEASURE_ID + ",");
        sb.append(SizeMeasureTable.SIZE_MEASURE_PERSON_REF + " as " + MEASURE_PERSON_REF + ",");
        sb.append(SizeMeasureTable.SIZE_MEASURE_DATE + " as " + MEASURE_DATE + ",");
        sb.append(SizeMeasureTable.SIZE_MEASURE_VALUE + " || ' cm'" + " as " + MEASURE_VALUE + ",");
        sb.append(AbstractMeasure.SIZE_MEASURE_TYPE + " as " + MEASURE_TYPE + " from " + SizeMeasureTable.SIZE_MEASURE_TABLE);
        sb.append(" union ");
        //Temperature
        sb.append("select " + TemperatureMeasureTable.TEMPERATURE_MEASURE_ID + " as " + MEASURE_ID + ",");
        sb.append(TemperatureMeasureTable.TEMPERATURE_MEASURE_PERSON_REF + " as " + MEASURE_PERSON_REF + ",");
        sb.append(TemperatureMeasureTable.TEMPERATURE_MEASURE_DATE + " as " + MEASURE_DATE + ",");
        sb.append(TemperatureMeasureTable.TEMPERATURE_MEASURE_VALUE + " || ' °C'" + " as " + MEASURE_VALUE + ",");
        sb.append(AbstractMeasure.TEMPERATURE_MEASURE_TYPE + " as " + MEASURE_TYPE + " from " + TemperatureMeasureTable.TEMPERATURE_MEASURE_TABLE);
        sb.append(" union ");
        //Cranial Perimeter
        sb.append("select " + CranialPerimeterMeasureTable.CP_MEASURE_ID + " as " + MEASURE_ID + ",");
        sb.append(CranialPerimeterMeasureTable.CP_MEASURE_PERSON_REF + " as " + MEASURE_PERSON_REF + ",");
        sb.append(CranialPerimeterMeasureTable.CP_MEASURE_DATE + " as " + MEASURE_DATE + ",");
        sb.append(CranialPerimeterMeasureTable.CP_MEASURE_VALUE + " || ' cm'" + " as " + MEASURE_VALUE + ",");
        sb.append(AbstractMeasure.CP_MEASURE_TYPE + " as " + MEASURE_TYPE + " from " + CranialPerimeterMeasureTable.CP_MEASURE_TABLE);
        sb.append(" union ");
        //Glucose
        sb.append("select " + GlucoseMeasureTable.GLUCOSE_MEASURE_ID + " as " + MEASURE_ID + ",");
        sb.append(GlucoseMeasureTable.GLUCOSE_MEASURE_PERSON_REF + " as " + MEASURE_PERSON_REF + ",");
        sb.append(GlucoseMeasureTable.GLUCOSE_MEASURE_DATE + " as " + MEASURE_DATE + ",");
        sb.append(GlucoseMeasureTable.GLUCOSE_MEASURE_VALUE + " as " + MEASURE_VALUE + ",");
        sb.append(AbstractMeasure.GLUCOSE_MEASURE_TYPE + " || ' g/l'" + " as " + MEASURE_TYPE + " from " + GlucoseMeasureTable.GLUCOSE_MEASURE_TABLE);
        sb.append(" union ");
        //Heart
        sb.append("select " + HeartMeasureTable.HEART_MEASURE_ID + " as " + MEASURE_ID + ",");
        sb.append(HeartMeasureTable.HEART_MEASURE_PERSON_REF + " as " + MEASURE_PERSON_REF + ",");
        sb.append(HeartMeasureTable.HEART_MEASURE_DATE + " as " + MEASURE_DATE + ",");
        sb.append(HeartMeasureTable.HEART_MEASURE_DIASTOLIC + " || '/' || " + HeartMeasureTable.HEART_MEASURE_SYSTOLIC + " || ' - ' || " + HeartMeasureTable.HEART_MEASURE_HEARTBEAT + " as " + MEASURE_VALUE + ",");
        sb.append(AbstractMeasure.HEART_MEASURE_TYPE + " as " + MEASURE_TYPE + " from " + HeartMeasureTable.HEART_MEASURE_TABLE);
        sb.append(";");
        db.execSQL(sb.toString());
    }

    public List<AbstractMeasure> getPersonMeasuresWithDate(int personId, String date)
    {
        List<AbstractMeasure> res = new ArrayList<AbstractMeasure>();
        long d = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        long de = d + 86400000L;
        Cursor cursor = db.query(MEASURE_VIEW, measureCols, MEASURE_PERSON_REF + "=" + personId + " and " + MEASURE_DATE + ">=" + d + " and "
                + MEASURE_DATE + "<" + de, null, null, null, MEASURE_DATE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToMeasure(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    private AbstractMeasure cursorToMeasure(Cursor cursor)
    {
        AbstractMeasure measure = new AbstractMeasure();
        measure.setId(cursor.getInt(0));
        measure.setPersonId(cursor.getInt(1));
        String[] d = HealthRecordUtils.millisToLongdatestring(cursor.getLong(2)).split("/");
        String date = String.format("%s/%s/%s", d[0], d[1], d[2]);
        String hour = String.format("%s:%s", d[3], d[4]);
        measure.setDate(date);
        measure.setHour(hour);
        measure.setValueString(cursor.getString(3) + " @ " + hour);
        measure.setType(cursor.getInt(4));
        return measure;
    }

}
