package com.akoudri.healthrecord.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ali Koudri on 23/05/14.
 */
public class DeleteTherapistTrigger {

    public static final String DELETE_THERAPIST_TRIG = "delete_therapist_trig";

    private SQLiteDatabase db;

    public DeleteTherapistTrigger(SQLiteDatabase db)
    {
        this.db = db;
    }

    //FIXME: do not remove corresponding treatment, only set therapist to null
    public void createDeleteTherapistTrigger()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create trigger " + DELETE_THERAPIST_TRIG + " ");
        sb.append("before delete on " + TherapistTable.THERAPIST_TABLE + " ");
        sb.append("begin ");
        sb.append("delete from " + AppointmentTable.APPT_TABLE + " ");
        sb.append("where ");
        sb.append(AppointmentTable.APPT_THERAPIST_REF + "=" + "old." + TherapistTable.THERAPIST_ID);
        sb.append(";");
        sb.append("delete from " + PersonTherapistTable.PERSON_THERAPIST_TABLE + " ");
        sb.append("where ");
        sb.append(PersonTherapistTable.PT_THERAPIST_REF + "=" + "old." + TherapistTable.THERAPIST_ID);
        sb.append(";");
        sb.append("delete from " + AilmentTable.AILMENT_TABLE + " ");
        sb.append("where ");
        sb.append(AilmentTable.AILMENT_THERAPIST_REF + "=" + "old." + TherapistTable.THERAPIST_ID);
        sb.append(";");
        sb.append("end;");
        db.execSQL(sb.toString());
    }

}
