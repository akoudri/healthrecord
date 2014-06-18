package com.akoudri.healthrecord.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.data.AilmentTable;
import com.akoudri.healthrecord.data.AppointmentTable;
import com.akoudri.healthrecord.data.DrugTable;
import com.akoudri.healthrecord.data.IllnessTable;
import com.akoudri.healthrecord.data.MedicationTable;
import com.akoudri.healthrecord.data.PersonTable;
import com.akoudri.healthrecord.data.PersonTherapistTable;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranchTable;
import com.akoudri.healthrecord.data.TreatmentTable;

import java.sql.SQLException;

/**
 * Created by Ali Koudri on 01/04/14.
 */
public class HealthRecordDataSource {

    private SQLiteDatabase db;
    private HealthRecordDatabase dbHelper;
    private PersonTable personTable;
    private TherapyBranchTable therapyBranchTable;
    private IllnessTable illnessTable;
    private TherapistTable therapistTable;
    private PersonTherapistTable personTherapistTable;
    private AppointmentTable appointmentTable;
    private AilmentTable ailmentTable;
    private TreatmentTable treatmentTable;
    private MedicationTable medicationTable;
    private DrugTable drugTable;

    public HealthRecordDataSource(Context context)
    {
        dbHelper = new HealthRecordDatabase(context);
    }

    public void open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        personTable = new PersonTable(db);
        therapyBranchTable = new TherapyBranchTable(db);
        illnessTable = new IllnessTable(db);
        therapistTable = new TherapistTable(db);
        personTherapistTable = new PersonTherapistTable(db);
        appointmentTable = new AppointmentTable(db);
        ailmentTable = new AilmentTable(db);
        treatmentTable = new TreatmentTable(db);
        medicationTable = new MedicationTable(db);
        drugTable = new DrugTable(db);
    }

    public void close()
    {
        dbHelper.close();
    }

    public PersonTable getPersonTable()
    {
        return personTable;
    }

    public TherapyBranchTable getTherapyBranchTable() {
        return therapyBranchTable;
    }

    public IllnessTable getIllnessTable()
    {
        return illnessTable;
    }

    public TherapistTable getTherapistTable() {
        return therapistTable;
    }

    public PersonTherapistTable getPersonTherapistTable() {
        return personTherapistTable;
    }

    public AppointmentTable getAppointmentTable()
    {
        return appointmentTable;
    }

    public AilmentTable getAilmentTable()
    {
        return ailmentTable;
    }

    public TreatmentTable getTreatmentTable() {
        return treatmentTable;
    }

    public MedicationTable getMedicationTable() {
        return medicationTable;
    }

    public DrugTable getDrugTable()
    {
        return drugTable;
    }
}
