package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.Calendar;
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
    public static final String APPT_DATE = "date"; //contains also hour and min
    public static final String APPT_COMMENT = "comment";

    private String[] AppointmentCols = {APPT_ID, APPT_PERSON_REF, APPT_THERAPIST_REF,
            APPT_DATE, APPT_COMMENT};

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
        sb.append(APPT_DATE + " integer not null,");
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
        Calendar c = HealthRecordUtils.datehourToCalendar(date, hour);
        ContentValues values = new ContentValues();
        values.put(APPT_PERSON_REF, personId);
        values.put(APPT_THERAPIST_REF, therapistId);
        values.put(APPT_DATE, c.getTimeInMillis());
        if (comment != null)
            values.put(APPT_COMMENT, comment);
        return db.insert(APPT_TABLE, null, values);
    }

    public boolean updateAppointment(int apptId, int therapistId, String date, String hour, String comment)
    {
        Calendar c = HealthRecordUtils.datehourToCalendar(date, hour);
        ContentValues values = new ContentValues();
        values.put(APPT_THERAPIST_REF, therapistId);
        values.put(APPT_DATE, c.getTimeInMillis());
        if (comment != null)
            values.put(APPT_COMMENT, comment);
        else
            values.putNull(APPT_COMMENT);
        return db.update(APPT_TABLE, values, APPT_ID + "=" + apptId, null) > 0;
    }

    public List<Appointment> getDayAppointmentsForPerson(int personId, String date)
    {
        long ms = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        long me = ms + 86400000L;//24h in ms
        List<Appointment> res = new ArrayList<Appointment>();
        Cursor cursor = db.query(APPT_TABLE, AppointmentCols,
                APPT_PERSON_REF + "=" + personId + " and " + APPT_DATE + ">=" + ms + " and " + APPT_DATE + "<" + me,
                null, null, null, APPT_DATE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToAppointment(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public int[] getMonthAppointmentsForPerson(int personId, Calendar cal)
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
        List<Appointment> res = new ArrayList<Appointment>();
        Cursor cursor = db.query(APPT_TABLE, AppointmentCols,
                APPT_PERSON_REF + "=" + personId + " and " + APPT_DATE + ">=" + ms + " and " + APPT_DATE + "<" + me,
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToAppointment(cursor));
            cursor.moveToNext();
        }
        int[] appts = new int[max];
        for (int i = 0; i < max; i++)
            appts[i] = 0;
        for (Appointment appt : res)
        {
            long d = HealthRecordUtils.stringToCalendar(appt.getDate()).getTimeInMillis() - cal.getTimeInMillis();
            int i = (int)(d/86400000L);
            appts[i] ++;
        }
        return appts;
    }

    public int countAppointmentsForDay(int personId, long date)
    {
        long me = date + 86400000L;//24h in ms
        String req = "select count(*) from " + APPT_TABLE + " where " + APPT_PERSON_REF + "=" + personId + " and " + APPT_DATE + ">=" + date + " and " + APPT_DATE + "<" + me;
        Cursor count = db.rawQuery(req, null);
        if (!count.moveToFirst())
            return 0;
        int res = count.getInt(0);
        count.close();
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
        appt.setPersonId(cursor.getInt(1));
        appt.setTherapistId(cursor.getInt(2));
        String[] d = HealthRecordUtils.millisToLongdatestring(cursor.getLong(3)).split("/");
        String date = String.format("%s/%s/%s", d[0], d[1], d[2]);
        String hour = String.format("%s:%s", d[3], d[4]);
        appt.setDate(date);
        appt.setHour(hour);
        appt.setComment((cursor.isNull(4))?null:cursor.getString(4));
        return appt;
    }

}
