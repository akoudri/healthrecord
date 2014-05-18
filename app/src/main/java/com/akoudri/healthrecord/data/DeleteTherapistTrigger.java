package com.akoudri.healthrecord.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ali Koudri on 17/04/14.
 */
public class DeleteTherapistTrigger {

    public static final String DELETE_THERAPIST_TRIGGER = "delete_therapist_trig";

    private SQLiteDatabase db;

    public DeleteTherapistTrigger(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createDeleteTherapistTrigger()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create trigger " + DELETE_THERAPIST_TRIGGER + " ");
        sb.append("after delete on " + PersonTherapistTable.PERSON_THERAPIST_TABLE + " ");
        sb.append("when (");
        sb.append("select count(*) from " + PersonTherapistTable.PERSON_THERAPIST_TABLE +
            " where " + PersonTherapistTable.PT_THERAPIST_REF + " = old." +
                PersonTherapistTable.PT_THERAPIST_REF);
        sb.append(") = 0 ");
        sb.append("begin ");
        sb.append("delete from " + TherapistTable.THERAPIST_TABLE + " ");
        sb.append("where ");
        sb.append(TherapistTable.THERAPIST_ID + "=" + "old." + PersonTherapistTable.PT_THERAPIST_REF);
        sb.append(";");
        sb.append("end;");
        db.execSQL(sb.toString());
    }
}
