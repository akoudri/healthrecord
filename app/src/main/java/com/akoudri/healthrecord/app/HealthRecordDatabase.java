package com.akoudri.healthrecord.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.akoudri.healthrecord.data.PersonTable;
import com.akoudri.healthrecord.data.PersonTherapistTable;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranchTable;

/**
 * Created by Ali Koudri on 01/04/14.
 */
public class HealthRecordDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "health_record_db";
    public static final int DB_VERSION = 1;

    private PersonTable personTable;
    private TherapyBranchTable therapyBranchTable;
    private TherapistTable therapistTable;
    private PersonTherapistTable personTherapistTable;

    public HealthRecordDatabase(Context context)
    {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        personTable = new PersonTable(db);
        personTable.createPersonTable();
        therapyBranchTable = new TherapyBranchTable(db);
        therapyBranchTable.createTherapyBranchTable();
        therapistTable = new TherapistTable(db);
        therapistTable.createTherapistTable();
        personTherapistTable = new PersonTherapistTable(db);
        personTherapistTable.createPersonTherapistTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //FIXME: this first version simply removes the old tables
        db.execSQL("drop table if exists " + PersonTable.PERSON_TABLE );
        db.execSQL("drop table if exists " + TherapyBranchTable.THERAPYBRANCH_TABLE );
        db.execSQL("drop table if exists " + TherapistTable.THERAPIST_TABLE );
        db.execSQL("drop table if exists " + PersonTherapistTable.PERSON_THERAPIST_TABLE );
        onCreate(db);
    }
}
