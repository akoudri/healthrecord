package com.akoudri.healthrecord.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.data.AilmentTable;
import com.akoudri.healthrecord.data.AppointmentTable;
import com.akoudri.healthrecord.data.DrugTable;
import com.akoudri.healthrecord.data.IllnessTable;
import com.akoudri.healthrecord.data.MeasureTable;
import com.akoudri.healthrecord.data.MedicationTable;
import com.akoudri.healthrecord.data.PersonTable;
import com.akoudri.healthrecord.data.PersonTherapistTable;
import com.akoudri.healthrecord.data.TherapistTable;
import com.akoudri.healthrecord.data.TherapyBranchTable;

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
    private MedicationTable medicationTable;
    private DrugTable drugTable;
    private MeasureTable measureTable;

    private boolean isOpened = false;

    private static HealthRecordDataSource instance;

    public static HealthRecordDataSource getInstance(Context context)
    {
        if (instance == null)
            instance = new HealthRecordDataSource(context);
        return instance;
    }

    private HealthRecordDataSource(Context context)
    {
        dbHelper = new HealthRecordDatabase(context);
    }

    public void open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
        isOpened = true;
    }

    public void close()
    {
        if (!isOpened) return;
        dbHelper.close();
        personTable = null;
        therapyBranchTable = null;
        illnessTable = null;
        therapistTable = null;
        personTherapistTable = null;
        appointmentTable = null;
        ailmentTable = null;
        medicationTable = null;
        drugTable = null;
        measureTable = null;
        isOpened = false;
    }

    public PersonTable getPersonTable()
    {
        if (!isOpened) return null;
        if (personTable == null)
            personTable = new PersonTable(db);
        return personTable;
    }

    public TherapyBranchTable getTherapyBranchTable() {
        if (!isOpened) return null;
        if (therapyBranchTable == null)
            therapyBranchTable = new TherapyBranchTable(db);
        return therapyBranchTable;
    }

    public IllnessTable getIllnessTable()
    {
        if (!isOpened) return null;
        if (illnessTable == null)
            illnessTable = new IllnessTable(db);
        return illnessTable;
    }

    public TherapistTable getTherapistTable() {
        if (!isOpened) return null;
        if (therapistTable == null)
            therapistTable = new TherapistTable(db);
        return therapistTable;
    }

    public PersonTherapistTable getPersonTherapistTable() {
        if (!isOpened) return null;
            personTherapistTable = new PersonTherapistTable(db);
        return personTherapistTable;
    }

    public AppointmentTable getAppointmentTable()
    {
        if (!isOpened) return null;
        if (appointmentTable == null)
            appointmentTable = new AppointmentTable(db);
        return appointmentTable;
    }

    public AilmentTable getAilmentTable() {
        if (!isOpened) return null;
        if (ailmentTable == null)
            ailmentTable = new AilmentTable(db);
        return ailmentTable;
    }

    public MedicationTable getMedicationTable() {
        if (!isOpened) return null;
        if (medicationTable == null)
            medicationTable = new MedicationTable(db);
        return medicationTable;
    }

    public DrugTable getDrugTable()
    {
        if (!isOpened) return null;
        if (drugTable == null)
            drugTable = new DrugTable(db);
        return drugTable;
    }

    public MeasureTable getMeasureTable()
    {
        if (!isOpened) return null;
        if (measureTable == null)
            measureTable = new MeasureTable(db);
        return measureTable;
    }
}
