package com.akoudri.healthrecord.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.akoudri.healthrecord.data.AilmentTable;
import com.akoudri.healthrecord.data.AppointmentTable;
import com.akoudri.healthrecord.data.DeleteTherapistTrigger;
import com.akoudri.healthrecord.data.IllnessTable;
import com.akoudri.healthrecord.data.RemoveTherapistFromPersonTrigger;
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
    private IllnessTable illnessTable;
    private AilmentTable ailmentTable;
    private TherapistTable therapistTable;
    private PersonTherapistTable personTherapistTable;
    private RemoveTherapistFromPersonTrigger removeTherapistFromPersonTrigger;
    private AppointmentTable appointmentTable;
    private DeleteTherapistTrigger deleteTherapistTrigger;

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
        illnessTable = new IllnessTable(db);
        illnessTable.createIllnessTable();
        ailmentTable = new AilmentTable(db);
        ailmentTable.createAilmentTable();
        therapistTable = new TherapistTable(db);
        therapistTable.createTherapistTable();
        personTherapistTable = new PersonTherapistTable(db);
        personTherapistTable.createPersonTherapistTable();
        removeTherapistFromPersonTrigger = new RemoveTherapistFromPersonTrigger(db);
        removeTherapistFromPersonTrigger.createRemoveTherapistFromPersonTrigger();
        appointmentTable = new AppointmentTable(db);
        appointmentTable.createAppointmentTable();
        deleteTherapistTrigger = new DeleteTherapistTrigger(db);
        deleteTherapistTrigger.createDeleteTherapistTrigger();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //FIXME: this first version simply removes the old tables
        db.execSQL("drop trigger if exists " + DeleteTherapistTrigger.DELETE_THERAPIST_TRIG);
        db.execSQL("drop table if exists " + AppointmentTable.APPOINTMENT_TABLE);
        db.execSQL("drop trigger if exists " + RemoveTherapistFromPersonTrigger.REMOVE_THERAPIST_FROM_PERSON_TRIG);
        db.execSQL("drop table if exists " + PersonTherapistTable.PERSON_THERAPIST_TABLE);
        db.execSQL("drop table if exists " + TherapistTable.THERAPIST_TABLE);
        db.execSQL("drop table if exists " + AilmentTable.AILMENT_TABLE);
        db.execSQL("drop table if exists " + IllnessTable.ILLNESS_TABLE);
        db.execSQL("drop table if exists " + TherapyBranchTable.THERAPYBRANCH_TABLE);
        db.execSQL("drop table if exists " + PersonTable.PERSON_TABLE);
        onCreate(db);
    }
}
