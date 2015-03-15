package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.HealthRecordUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ali Koudri on 14/03/15.
 */
public class ReminderTable {

    private SQLiteDatabase db;

    //Table
    public static final String REMINDER_TABLE = "reminder";
    public static final String REMINDER_ID = "_id";
    public static final String REMINDER_PERSON_REF = "personId";
    public static final String REMINDER_DRUG_REF = "drugId";
    public static final String REMINDER_DATE = "date";
    public static final String REMINDER_COMMENT = "comment";


    private String[] reminderCols = {REMINDER_ID, REMINDER_PERSON_REF, REMINDER_DRUG_REF, REMINDER_DATE,
            REMINDER_COMMENT};

    public ReminderTable(SQLiteDatabase db) {
        this.db = db;
    }

    public void createReminderTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + REMINDER_TABLE + " (");
        sb.append(REMINDER_ID + " integer primary key autoincrement,");
        sb.append(REMINDER_PERSON_REF + " integer not null,");
        sb.append(REMINDER_DRUG_REF + " integer not null,");
        sb.append(REMINDER_DATE + " integer not null,");
        sb.append(REMINDER_COMMENT + " text,");
        sb.append(" foreign key(" + REMINDER_PERSON_REF + ") references " + PersonTable.PERSON_TABLE +
                "(" + PersonTable.PERSON_ID + "),");
        sb.append(" foreign key(" + REMINDER_DRUG_REF + ") references " + DrugTable.DRUG_TABLE +
                "(" + DrugTable.DRUG_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertReminder(int personId, int drugId, String date, String comment) {
        ContentValues values = new ContentValues();
        values.put(REMINDER_PERSON_REF, personId);
        values.put(REMINDER_DRUG_REF, drugId);
        long d = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        values.put(REMINDER_DATE, d);
        if (comment != null)
            values.put(REMINDER_COMMENT, comment);
        else
            values.putNull(REMINDER_COMMENT);
        return db.insert(REMINDER_TABLE, null, values);
    }

    public long insertReminder(Reminder reminder)
    {
        int personId = reminder.getPersonId();
        int drugId = reminder.getDrugId();
        String date = reminder.getDate();
        String comment = reminder.getComment();
        return insertReminder(personId, drugId, date, comment);
    }

    public boolean updateReminder(int reminderId, int personId, int drugId, String date, String comment) {
        ContentValues values = new ContentValues();
        values.put(REMINDER_PERSON_REF, personId);
        values.put(REMINDER_DRUG_REF, drugId);
        long d = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        values.put(REMINDER_DATE, d);
        if (comment != null)
            values.put(REMINDER_COMMENT, comment);
        else
            values.putNull(REMINDER_COMMENT);
        return db.update(REMINDER_TABLE, values, REMINDER_ID + "=" + reminderId, null) > 0;
    }

    public boolean updateReminder(Reminder reminder)
    {
        int reminderId = reminder.getId();
        int personId = reminder.getPersonId();
        int drugId = reminder.getDrugId();
        String date = reminder.getDate();
        String comment = reminder.getComment();
        return updateReminder(reminderId, personId, drugId, date, comment);
    }

    public Reminder getReminderWithId(int id) {
        Cursor cursor = db.query(REMINDER_TABLE, reminderCols, REMINDER_ID + "=" + id, null, null, null, null);
        if (cursor.moveToFirst())
            return  cursorToReminder(cursor);
        return null;
    }

    public List<Reminder> getDayRemindersForPerson(int personId, String date)
    {
        long ms = HealthRecordUtils.stringToCalendar(date).getTimeInMillis();
        long me = ms + 86400000L;//24h in ms
        List<Reminder> res = new ArrayList<Reminder>();
        Cursor cursor = db.query(REMINDER_TABLE, reminderCols,
                REMINDER_PERSON_REF + "=" + personId + " and " + REMINDER_DATE + ">=" + ms + " and " +
                        REMINDER_DATE + "<" + me,
                null, null, null, REMINDER_DATE + " ASC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToReminder(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public boolean removeMedicWithId(int medicId)
    {
        return db.delete(REMINDER_TABLE, REMINDER_ID + "=" + medicId, null) > 0;
    }

    public int[] getMonthRemindersForPerson(int personId, Calendar cal)
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
        List<Reminder> res = new ArrayList<Reminder>();
        Cursor cursor = db.query(REMINDER_TABLE, reminderCols,
                REMINDER_PERSON_REF + "=" + personId + " and " + REMINDER_DATE + ">=" + ms + " and " + REMINDER_DATE + "<" + me,
                null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToReminder(cursor));
            cursor.moveToNext();
        }
        int[] reminders = new int[max];
        for (int i = 0; i < max; i++)
            reminders[i] = 0;
        for (Reminder reminder : res)
        {
            long d = HealthRecordUtils.stringToCalendar(reminder.getDate()).getTimeInMillis() - cal.getTimeInMillis();
            int i = (int)(d/86400000L);
            reminders[i] ++;
        }
        return reminders;
    }

    private Reminder cursorToReminder(Cursor cursor) {
        Reminder reminder = new Reminder();
        reminder.setId(cursor.getInt(0));
        reminder.setPersonId(cursor.getInt(1));
        reminder.setDrugId(cursor.getInt(2));
        long d = cursor.getLong(3);
        reminder.setDate(HealthRecordUtils.millisToDatestring(d));
        reminder.setComment((cursor.isNull(4)) ? null : cursor.getString(4));
        return reminder;
    }

}