package com.akoudri.healthrecord.data;

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
    public static final String AILMENT_START_DATE = "startDate";
    public static final String AILMENT_END_DATE = "endDate";
    public static final String AILMENT_COMMENT = "comment";

    private String[] ailmentCols = {AILMENT_ID, AILMENT_ILLNESS_REF, AILMENT_START_DATE,
        AILMENT_END_DATE, AILMENT_COMMENT};

    public AilmentTable(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createAilmentTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + AILMENT_TABLE + " (");
        sb.append(AILMENT_ID + " integer primary key autoincrement,");
        sb.append(AILMENT_ILLNESS_REF + " integer not null,");
        sb.append(AILMENT_START_DATE + " text,");
        sb.append(AILMENT_END_DATE + " text,");
        sb.append(AILMENT_COMMENT + " text,");
        sb.append(" foreign key(" + AILMENT_ILLNESS_REF + ") references " + IllnessTable.ILLNESS_TABLE +
            "(" + IllnessTable.ILLNESS_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public void insertAilment(int illnessId, String startDate, String endDate, String comment)
    {
        //TODO
    }

}
