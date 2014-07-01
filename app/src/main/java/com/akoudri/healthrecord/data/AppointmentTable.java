package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali Koudri on 18/05/14.
 */
public class AppointmentTable {

    private SQLiteDatabase db;

    //Table
    public static final String APPT_TABLE = "appointment";
    public static final String APPT_ID = "_id";
    public static final String APPT_PERSON_REF = "personId";
    public static final String APPT_THERAPIST_REF = "therapistId";
    public static final String APPT_DATE = "date";
    public static final String APPT_HOUR = "hour";
    public static final String APPT_COMMENT = "comment";

    private String[] AppointmentCols = {APPT_ID, APPT_PERSON_REF, APPT_THERAPIST_REF,
            APPT_DATE, APPT_HOUR, APPT_COMMENT};

    public AppointmentTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createAppointmentTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + APPT_TABLE + " (");
        sb.append(APPT_ID + " integer primary key autoincrement,");
        sb.append(APPT_PERSON_REF + " integer not null,");
        sb.append(APPT_THERAPIST_REF + " integer not null,");
        sb.append(APPT_DATE + " text not null,");
        sb.append(APPT_HOUR + " text not null,");
        sb.append(APPT_COMMENT + " text,");
        sb.append(" foreign key(" + APPT_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + "),");
        sb.append(" foreign key(" + APPT_THERAPIST_REF + ") references " + TherapistTable.THERAPIST_TABLE +
                "(" + TherapistTable.THERAPIST_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertAppointment(int personId, int therapistId, String date, String hour, String comment)
    {
        ContentValues values = new ContentValues();
        values.put(APPT_PERSON_REF, personId);
        values.put(APPT_THERAPIST_REF, therapistId);
        values.put(APPT_DATE, date);
        values.put(APPT_HOUR, hour);
        if (comment != null)
            values.put(APPT_COMMENT, comment);
        return db.insert(APPT_TABLE, null, values);
    }

    public boolean updateAppointment(int apptId, int therapistId, String date, String hour, String comment)
    {
        ContentValues values = new ContentValues();
        values.put(APPT_THERAPIST_REF, therapistId);
        values.put(APPT_DATE, date);
        values.put(APPT_HOUR, hour);
        if (comment != null)
            values.put(APPT_COMMENT, comment);
        return db.update(APPT_TABLE, values, APPT_ID + "=" + apptId, null) > 0;
    }

    public List<Appointment> getDayAppointmentsForPerson(int personId, String date)
    {
        List<Appointment> res = new ArrayList<Appointment>();
        Cursor cursor = db.query(APPT_TABLE, AppointmentCols,
                APPT_PERSON_REF + "=" + personId + " and " + APPT_DATE + "=?",
                new String[] {date}, null, null, APPT_HOUR + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToAppointment(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public Appointment getAppointmentWithId(int apptId)
    {
        Cursor cursor = db.query(APPT_TABLE, AppointmentCols,
                APPT_ID + "=" + apptId, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToAppointment(cursor);
        return null;
    }

    public boolean removeAppointmentWithId(int apptId)
    {
        return db.delete(APPT_TABLE, APPT_ID + "=" + apptId, null) > 0;
    }

    private Appointment cursorToAppointment(Cursor cursor)
    {
        Appointment appt = new Appointment();
        appt.setId(cursor.getInt(0));
        appt.setPerson(cursor.getInt(1));
        appt.setTherapist(cursor.getInt(2));
        appt.setDate(cursor.getString(3));
        appt.setHour(cursor.getString(4));
        appt.setComment((cursor.isNull(5))?null:cursor.getString(5));
        return appt;
    }

}
