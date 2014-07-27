package com.akoudri.healthrecord.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ali Koudri on 01/07/14.
 */
public class RemovePersonTrigger {

    public static final String REMOVE_PERSON_TRIG = "remove_person_trig";

    private SQLiteDatabase db;

    public RemovePersonTrigger(SQLiteDatabase db) {
        this.db = db;
    }

    public void createRemovePersonTrigger()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create trigger " + REMOVE_PERSON_TRIG + " ");
        sb.append("before delete on " + PersonTable.PERSON_TABLE + " ");
        sb.append("begin ");
        sb.append("delete from " + AppointmentTable.APPT_TABLE + " ");
        sb.append("where ");
        sb.append(AppointmentTable.APPT_PERSON_REF + "=" + "old." + PersonTable.PERSON_ID);
        sb.append(";");
        sb.append("delete from " + PersonTherapistTable.PERSON_THERAPIST_TABLE + " ");
        sb.append("where ");
        sb.append(PersonTherapistTable.PT_PERSON_REF + "=" + "old." + PersonTable.PERSON_ID);
        sb.append(";");
        sb.append("delete from " + AilmentTable.AILMENT_TABLE + " ");
        sb.append("where ");
        sb.append(AilmentTable.AILMENT_PERSON_REF + "=" + "old." + PersonTable.PERSON_ID);
        sb.append(";");
        sb.append("delete from " + WeightMeasureTable.WEIGHT_MEASURE_TABLE + " ");
        sb.append("where ");
        sb.append(WeightMeasureTable.WEIGHT_MEASURE_PERSON_REF + "=" + "old." + PersonTable.PERSON_ID);
        sb.append(";");
        sb.append("delete from " + SizeMeasureTable.SIZE_MEASURE_TABLE + " ");
        sb.append("where ");
        sb.append(SizeMeasureTable.SIZE_MEASURE_PERSON_REF + "=" + "old." + PersonTable.PERSON_ID);
        sb.append(";");
        sb.append("delete from " + TemperatureMeasureTable.TEMPERATURE_MEASURE_TABLE + " ");
        sb.append("where ");
        sb.append(TemperatureMeasureTable.TEMPERATURE_MEASURE_PERSON_REF + "=" + "old." + PersonTable.PERSON_ID);
        sb.append(";");
        sb.append("delete from " + CranialPerimeterMeasureTable.CP_MEASURE_TABLE + " ");
        sb.append("where ");
        sb.append(CranialPerimeterMeasureTable.CP_MEASURE_PERSON_REF + "=" + "old." + PersonTable.PERSON_ID);
        sb.append(";");
        sb.append("delete from " + GlucoseMeasureTable.GLUCOSE_MEASURE_TABLE + " ");
        sb.append("where ");
        sb.append(GlucoseMeasureTable.GLUCOSE_MEASURE_PERSON_REF + "=" + "old." + PersonTable.PERSON_ID);
        sb.append(";");
        sb.append("delete from " + HeartMeasureTable.HEART_MEASURE_TABLE + " ");
        sb.append("where ");
        sb.append(HeartMeasureTable.HEART_MEASURE_PERSON_REF + "=" + "old." + PersonTable.PERSON_ID);
        sb.append(";");
        sb.append("end;");
        db.execSQL(sb.toString());
    }

}
