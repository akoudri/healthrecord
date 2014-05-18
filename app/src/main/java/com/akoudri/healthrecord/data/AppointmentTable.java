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
    public static final String APPOINTMENT_TABLE = "appointment";
    public static final String APPOINTMENT_ID = "_id";
    public static final String APPT_PERSON_REF = "personId";
    public static final String APPT_THERAPIST_REF = "doctorId";
    public static final String APPOINTMENT_DATE = "date";
    public static final String APPOINTMENT_HOUR = "hour";
    public static final String APPOINTMENT_COMMENT = "comment";

    private String[] AppointmentCols = {APPOINTMENT_ID, APPT_THERAPIST_REF, APPT_PERSON_REF,
            APPOINTMENT_DATE, APPOINTMENT_HOUR, APPOINTMENT_COMMENT};

    public AppointmentTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createAppointmentTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + APPOINTMENT_TABLE + " (");
        sb.append(APPOINTMENT_ID + " integer primary key autoincrement,");
        sb.append(APPT_PERSON_REF + " integer not null,");
        sb.append(APPT_THERAPIST_REF + " integer not null,");
        sb.append(APPOINTMENT_DATE + " text not null,");
        sb.append(APPOINTMENT_HOUR + " text not null,");
        sb.append(APPOINTMENT_COMMENT + " text,");
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
        values.put(APPOINTMENT_DATE, date);
        values.put(APPOINTMENT_HOUR, hour);
        values.put(APPOINTMENT_COMMENT, comment);
        return db.insert(APPOINTMENT_TABLE, null, values);
    }

    /*public List<Appointment> getAllAppointments()
    {
        List<Appointment> res = new ArrayList<Appointment>();
        Cursor cursor = db.query(APPOINTMENT_TABLE, AppointmentCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToAppointment(cursor));
            cursor.moveToNext();
        }
        return res;
    }*/

    public List<Appointment> getDayAppointmentsForPerson(int personId, String date)
    {
        List<Appointment> res = new ArrayList<Appointment>();
        Cursor cursor = db.query(APPOINTMENT_TABLE, AppointmentCols,
                personId + "=" + APPT_PERSON_REF + " and " + APPOINTMENT_DATE + "=" + date,
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToAppointment(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    private Appointment cursorToAppointment(Cursor cursor)
    {
        Appointment appt = new Appointment();
        appt.setId(cursor.getInt(0));
        appt.setPerson(cursor.getInt(1));
        appt.setTherapist(cursor.getInt(2));
        appt.setDate(cursor.getString(3));
        appt.setHour(cursor.getString(4));
        appt.setComment(cursor.getString(5));
        return appt;
    }

}
