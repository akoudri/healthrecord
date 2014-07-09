package com.akoudri.healthrecord.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ali Koudri on 01/07/14.
 */
public class RemoveAilmentTrigger {

    public static final String REMOVE_AILMENT_TRIG = "remove_ailment_trig";

    private SQLiteDatabase db;

    public RemoveAilmentTrigger(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createRemoveTreatmentTrigger()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create trigger " + REMOVE_AILMENT_TRIG + " ");
        sb.append("before delete on " + AilmentTable.AILMENT_TABLE + " ");
        sb.append("begin ");
        sb.append("delete from " + MedicationTable.MEDICATION_TABLE + " ");
        sb.append("where ");
        sb.append(MedicationTable.MEDICATION_AILMENT_REF + "=" + "old." + AilmentTable.AILMENT_ID);
        sb.append(";");
        sb.append("end;");
        db.execSQL(sb.toString());
    }

}
