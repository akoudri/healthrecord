package com.akoudri.healthrecord.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.akoudri.healthrecord.data.AppointmentTable;
import com.akoudri.healthrecord.data.DeleteTherapistTrigger;
import com.akoudri.healthrecord.data.DrugTable;
import com.akoudri.healthrecord.data.IllnessTable;
import com.akoudri.healthrecord.data.MeasureTable;
import com.akoudri.healthrecord.data.MedicationTable;
import com.akoudri.healthrecord.data.PersonTable;
import com.akoudri.healthrecord.data.PersonTherapistTable;
import com.akoudri.healthrecord.data.RemovePersonTrigger;
import com.akoudri.healthrecord.data.RemoveTherapistFromPersonTrigger;
import com.akoudri.healthrecord.data.RemoveTreatmentTrigger;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranchTable;
import com.akoudri.healthrecord.data.TreatmentTable;

/**
 * Created by Ali Koudri on 01/04/14.
 */
public class HealthRecordDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "health_record_db";
    public static final int DB_VERSION = 1;

    private PersonTable personTable;
    private TherapyBranchTable therapyBranchTable;
    private IllnessTable illnessTable;
    //private AilmentTable ailmentTable;
    private TreatmentTable treatmentTable;
    private MedicationTable medicationTable;
    private TherapistTable therapistTable;
    private PersonTherapistTable personTherapistTable;
    private RemoveTherapistFromPersonTrigger removeTherapistFromPersonTrigger;
    private AppointmentTable appointmentTable;
    private DrugTable drugTable;
    private MeasureTable measureTable;
    private RemovePersonTrigger removePersonTrigger;
    private DeleteTherapistTrigger deleteTherapistTrigger;
    //private RemoveAilmentTrigger removeAilmentTrigger;
    private RemoveTreatmentTrigger removeTreatmentTrigger;

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
        //ailmentTable = new AilmentTable(db);
        //ailmentTable.createAilmentTable();
        therapistTable = new TherapistTable(db);
        therapistTable.createTherapistTable();
        personTherapistTable = new PersonTherapistTable(db);
        personTherapistTable.createPersonTherapistTable();
        removeTherapistFromPersonTrigger = new RemoveTherapistFromPersonTrigger(db);
        removeTherapistFromPersonTrigger.createRemoveTherapistFromPersonTrigger();
        appointmentTable = new AppointmentTable(db);
        appointmentTable.createAppointmentTable();
        treatmentTable = new TreatmentTable(db);
        treatmentTable.createTreatmentTable();
        medicationTable = new MedicationTable(db);
        medicationTable.createMedicationTable();
        drugTable = new DrugTable(db);
        drugTable.createDrugTable();
        measureTable = new MeasureTable(db);
        measureTable.createMeasureTable();
        removePersonTrigger = new RemovePersonTrigger(db);
        removePersonTrigger.createRemovePersonTrigger();
        deleteTherapistTrigger = new DeleteTherapistTrigger(db);
        deleteTherapistTrigger.createDeleteTherapistTrigger();
        //removeAilmentTrigger = new RemoveAilmentTrigger(db);
        //removeAilmentTrigger.createRemoveAilmentTrigger();
        removeTreatmentTrigger = new RemoveTreatmentTrigger(db);
        removeTreatmentTrigger.createRemoveTreatmentTrigger();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //FIXME: this first version simply removes the old tables
        db.execSQL("drop trigger if exists " + RemoveTreatmentTrigger.REMOVE_TREATMENT_TRIG);
        //db.execSQL("drop trigger if exists " + RemoveAilmentTrigger.REMOVE_AILMENT_TRIG);
        db.execSQL("drop trigger if exists " + DeleteTherapistTrigger.DELETE_THERAPIST_TRIG);
        db.execSQL("drop trigger if exists " + RemovePersonTrigger.REMOVE_PERSON_TRIG);
        db.execSQL("drop trigger if exists " + RemoveTherapistFromPersonTrigger.REMOVE_THERAPIST_FROM_PERSON_TRIG);
        db.execSQL("drop table if exists " + MeasureTable.MEASURE_TABLE);
        db.execSQL("drop table if exists " + DrugTable.DRUG_TABLE);
        db.execSQL("drop table if exists " + AppointmentTable.APPT_TABLE);
        db.execSQL("drop table if exists " + PersonTherapistTable.PERSON_THERAPIST_TABLE);
        db.execSQL("drop table if exists " + TherapistTable.THERAPIST_TABLE);
        //db.execSQL("drop table if exists " + AilmentTable.AILMENT_TABLE);
        db.execSQL("drop table if exists " + IllnessTable.ILLNESS_TABLE);
        db.execSQL("drop table if exists " + MedicationTable.MEDICATION_TABLE);
        db.execSQL("drop table if exists " + TreatmentTable.TREATMENT_TABLE);
        db.execSQL("drop table if exists " + TherapyBranchTable.THERAPYBRANCH_TABLE);
        db.execSQL("drop table if exists " + PersonTable.PERSON_TABLE);
        onCreate(db);
    }
}
