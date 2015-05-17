package com.akoudri.healthrecord.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.akoudri.healthrecord.data.AilmentTable;
import com.akoudri.healthrecord.data.AppointmentTable;
import com.akoudri.healthrecord.data.CholesterolMeasureTable;
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
import com.akoudri.healthrecord.data.ReminderTable;
import com.akoudri.healthrecord.data.RemoveAilmentTrigger;
import com.akoudri.healthrecord.data.RemovePersonTrigger;
import com.akoudri.healthrecord.data.SizeMeasureTable;
import com.akoudri.healthrecord.data.TemperatureMeasureTable;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranchTable;
import com.akoudri.healthrecord.data.WeightMeasureTable;
import com.akoudri.healthrecord.utils.Crypto;

/**
 * Created by Ali Koudri on 01/04/14.
 */
public class HealthRecordDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "health_record_db";
    public static final int DB_VERSION = 3;

    private Crypto crypto;

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
    private CholesterolMeasureTable cholesterolMeasureTable;
    private MedicalObservationTable medicalObservationTable;
    private ReminderTable reminderTable;
    private MeasureView measureView;
    private RemovePersonTrigger removePersonTrigger;
    private RemoveAilmentTrigger removeAilmentTrigger;

    public HealthRecordDatabase(Context context, Crypto crypto)
    {
        super(context, DB_NAME, null, DB_VERSION);
        this.crypto = crypto;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        personTable = new PersonTable(db, crypto);
        personTable.createPersonTable();
        therapyBranchTable = new TherapyBranchTable(db, crypto);
        therapyBranchTable.createTherapyBranchTable();
        illnessTable = new IllnessTable(db, crypto);
        illnessTable.createIllnessTable();
        therapistTable = new TherapistTable(db, crypto);
        therapistTable.createTherapistTable();
        personTherapistTable = new PersonTherapistTable(db);
        personTherapistTable.createPersonTherapistTable();
        appointmentTable = new AppointmentTable(db, crypto);
        appointmentTable.createAppointmentTable();
        ailmentTable = new AilmentTable(db);
        ailmentTable.createTreatmentTable();
        medicationTable = new MedicationTable(db);
        medicationTable.createMedicationTable();
        drugTable = new DrugTable(db, crypto);
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
        cholesterolMeasureTable = new CholesterolMeasureTable(db);
        cholesterolMeasureTable.createMeasureTable();
        medicalObservationTable = new MedicalObservationTable(db, crypto);
        medicalObservationTable.createMedicalObservationTable();
        reminderTable = new ReminderTable(db);
        reminderTable.createReminderTable();
        measureView = new MeasureView(db);
        measureView.createMeasureView();
        removePersonTrigger = new RemovePersonTrigger(db);
        removePersonTrigger.createRemovePersonTrigger();
        removeAilmentTrigger = new RemoveAilmentTrigger(db);
        removeAilmentTrigger.createRemoveTreatmentTrigger();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2)
        {
            medicationTable.updateV2();
        }
        if (oldVersion < 3)
        {
            reminderTable = new ReminderTable(db);
            reminderTable.createReminderTable();
            cholesterolMeasureTable = new CholesterolMeasureTable(db);
            cholesterolMeasureTable.createMeasureTable();
            removePersonTrigger.updateVersion();
            measureView.updateVersion();
        }
    }
}
