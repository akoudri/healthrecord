package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ali Koudri on 28/05/14.
 */
public class AilmentTable {

    private SQLiteDatabase db;

    //Table
    public static final String AILMENT_TABLE = "ailment";
    public static final String AILMENT_ID = "_id";
    public static final String AILMENT_ILLNESS_REF = "illnessId";
    public static final String AILMENT_CHRONIC = "isChronic";
    public static final String AILMENT_START_DATE = "startDate";
    public static final String AILMENT_END_DATE = "endDate";
    public static final String AILMENT_COMMENT = "comment";

    private String[] ailmentCols = {AILMENT_ID, AILMENT_ILLNESS_REF, AILMENT_CHRONIC, AILMENT_START_DATE,
            AILMENT_END_DATE, AILMENT_COMMENT};

    public AilmentTable(SQLiteDatabase db) {
        this.db = db;
    }

    public void createAilmentTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + AILMENT_TABLE + " (");
        sb.append(AILMENT_ID + " integer primary key autoincrement,");
        sb.append(AILMENT_ILLNESS_REF + " integer not null,");
        //TODO: default value to 0 for isChronic
        sb.append(AILMENT_CHRONIC + " integer not null,");
        sb.append(AILMENT_START_DATE + " text,");
        sb.append(AILMENT_END_DATE + " text,");
        sb.append(AILMENT_COMMENT + " text,");
        sb.append(" foreign key(" + AILMENT_ILLNESS_REF + ") references " + IllnessTable.ILLNESS_TABLE +
                "(" + IllnessTable.ILLNESS_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertAilment(int illnessId, int isChronic, String startDate, String endDate, String comment) {
        ContentValues values = new ContentValues();
        values.put(AILMENT_ILLNESS_REF, illnessId);
        values.put(AILMENT_CHRONIC, isChronic);
        values.put(AILMENT_START_DATE, startDate);
        values.put(AILMENT_END_DATE, endDate);
        values.put(AILMENT_COMMENT, comment);
        return db.insert(AILMENT_TABLE, null, values);
    }

    public boolean updateAilment(int ailmentId, int illnessId, int isChronic, String startDate, String endDate, String comment) {
        ContentValues values = new ContentValues();
        values.put(AILMENT_ILLNESS_REF, illnessId);
        values.put(AILMENT_CHRONIC, isChronic);
        values.put(AILMENT_START_DATE, startDate);
        values.put(AILMENT_END_DATE, endDate);
        values.put(AILMENT_COMMENT, comment);
        return db.update(AILMENT_TABLE, values, AILMENT_ID + "=" + ailmentId, null) > 0;
    }

    public Ailment getAilmentWith(int id) {
        return null;
    }

    private Ailment cursorToAppointment(Cursor cursor) {
        Ailment ailment = new Ailment();
        ailment.setId(cursor.getInt(0));
        ailment.setIllnessId(cursor.getInt(1));
        //FIXME: here
        ailment.setChronic(cursor.getInt(2) == 0);
        return ailment;
    }

}