package com.akoudri.healthrecord.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.akoudri.healthrecord.data.AilmentTable;
import com.akoudri.healthrecord.data.AppointmentTable;
import com.akoudri.healthrecord.data.CranialPerimeterMeasureTable;
import com.akoudri.healthrecord.data.DrugTable;
import com.akoudri.healthrecord.data.GlucoseMeasureTable;
import com.akoudri.healthrecord.data.HeartMeasureTable;
import com.akoudri.healthrecord.data.IllnessTable;
import com.akoudri.healthrecord.data.MeasureView;
import com.akoudri.healthrecord.data.MedicalObservationTable;
import com.akoudri.healthrecord.data.MedicationTable;
import com.akoudri.healthrecord.data.PersonTable;
import com.akoudri.healthrecord.data.PersonTherapistTable;
import com.akoudri.healthrecord.data.RemoveAilmentTrigger;
import com.akoudri.healthrecord.data.RemovePersonTrigger;
import com.akoudri.healthrecord.data.SizeMeasureTable;
import com.akoudri.healthrecord.data.TemperatureMeasureTable;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranchTable;
import com.akoudri.healthrecord.data.WeightMeasureTable;

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
    private MedicationTable medicationTable;
    private TherapistTable therapistTable;
    private PersonTherapistTable personTherapistTable;
    private AppointmentTable appointmentTable;
    private DrugTable drugTable;
    private WeightMeasureTable weightMeasureTable;
    private SizeMeasureTable sizeMeasureTable;
    private TemperatureMeasureTable tempMeasureTable;
    private CranialPerimeterMeasureTable cpMeasureTable;
    private GlucoseMeasureTable glucoseMeasureTable;
    private HeartMeasureTable heartMeasureTable;
    private MedicalObservationTable medicalObservationTable;
    private MeasureView measureView;
    private RemovePersonTrigger removePersonTrigger;
    private RemoveAilmentTrigger removeAilmentTrigger;

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
        therapistTable = new TherapistTable(db);
        therapistTable.createTherapistTable();
        personTherapistTable = new PersonTherapistTable(db);
        personTherapistTable.createPersonTherapistTable();
        appointmentTable = new AppointmentTable(db);
        appointmentTable.createAppointmentTable();
        ailmentTable = new AilmentTable(db);
        ailmentTable.createTreatmentTable();
        medicationTable = new MedicationTable(db);
        medicationTable.createMedicationTable();
        drugTable = new DrugTable(db);
        drugTable.createDrugTable();
        weightMeasureTable = new WeightMeasureTable(db);
        weightMeasureTable.createMeasureTable();
        sizeMeasureTable = new SizeMeasureTable(db);
        sizeMeasureTable.createMeasureTable();
        tempMeasureTable = new TemperatureMeasureTable(db);
        tempMeasureTable.createMeasureTable();
        cpMeasureTable = new CranialPerimeterMeasureTable(db);
        cpMeasureTable.createMeasureTable();
        glucoseMeasureTable = new GlucoseMeasureTable(db);
        glucoseMeasureTable.createMeasureTable();
        heartMeasureTable = new HeartMeasureTable(db);
        heartMeasureTable.createMeasureTable();
        medicalObservationTable = new MedicalObservationTable(db);
        medicalObservationTable.createMedicalObservationTable();
        measureView = new MeasureView(db);
        measureView.createMeasureView();
        removePersonTrigger = new RemovePersonTrigger(db);
        removePersonTrigger.createRemovePersonTrigger();
        removeAilmentTrigger = new RemoveAilmentTrigger(db);
        removeAilmentTrigger.createRemoveTreatmentTrigger();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO when necessary
    }
}
