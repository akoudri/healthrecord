package com.akoudri.healthrecord.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ali Koudri on 01/07/14.
 */
public class RemoveTreatmentTrigger {

    public static final String REMOVE_TREATMENT_TRIG = "remove_treatment_trig";

    private SQLiteDatabase db;

    public RemoveTreatmentTrigger(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createRemoveTreatmentTrigger()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create trigger " + REMOVE_TREATMENT_TRIG + " ");
        sb.append("before delete on " + TreatmentTable.TREATMENT_TABLE + " ");
        sb.append("begin ");
        sb.append("delete from " + MedicationTable.MEDICATION_TABLE + " ");
        sb.append("where ");
        sb.append(MedicationTable.MEDICATION_TREATMENT_REF + "=" + "old." + TreatmentTable.TREATMENT_ID);
        sb.append(";");
        sb.append("end;");
        db.execSQL(sb.toString());
    }

}
