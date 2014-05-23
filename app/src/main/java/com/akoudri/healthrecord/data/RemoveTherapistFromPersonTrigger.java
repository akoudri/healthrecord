package com.akoudri.healthrecord.data;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ali Koudri on 17/04/14.
 */
public class RemoveTherapistFromPersonTrigger {

    public static final String REMOVE_THERAPIST_FROM_PERSON_TRIG = "remove_therapist_from_person_trig";

    private SQLiteDatabase db;

    public RemoveTherapistFromPersonTrigger(SQLiteDatabase db)
    {
        this.db = db;
    }

    public void createRemoveTherapistFromPersonTrigger()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create trigger " + REMOVE_THERAPIST_FROM_PERSON_TRIG + " ");
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
