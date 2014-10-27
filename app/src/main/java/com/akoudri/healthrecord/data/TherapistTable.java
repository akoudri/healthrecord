package com.akoudri.healthrecord.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.akoudri.healthrecord.utils.Crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Ali Koudri on 12/04/14.
 */
public class TherapistTable {

    private SQLiteDatabase db;
    private Crypto crypto;

    //Table
    public static final String THERAPIST_TABLE = "therapist";
    public static final String THERAPIST_ID = "_id";
    public static final String THERAPIST_NAME = "name";
    public static final String THERAPIST_PHONENUMBER = "phoneNumber";
    public static final String THERAPIST_CELLPHONENUMBER = "cellPhoneNumber";
    public static final String THERAPIST_EMAIL = "email";
    public static final String THERAPIST_BRANCHID = "branchId";

    private String[] therapistCols = {THERAPIST_ID, THERAPIST_NAME,
            THERAPIST_PHONENUMBER, THERAPIST_CELLPHONENUMBER, THERAPIST_EMAIL, THERAPIST_BRANCHID};

    public TherapistTable(SQLiteDatabase db, Crypto crypto)
    {
        this.db = db;
        this.crypto = crypto;
    }

    public void createTherapistTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + THERAPIST_TABLE + " (");
        sb.append(THERAPIST_ID + " integer primary key autoincrement,");
        sb.append(THERAPIST_NAME + " text not null unique,");
        sb.append(THERAPIST_PHONENUMBER + " text,");
        sb.append(THERAPIST_CELLPHONENUMBER + " text,");
        sb.append(THERAPIST_EMAIL + " text,");
        sb.append(THERAPIST_BRANCHID + " integer not null,");
        sb.append("foreign key(" + THERAPIST_BRANCHID + ") references "
                + TherapyBranchTable.THERAPYBRANCH_TABLE + "(" + TherapyBranchTable.THERAPYBRANCH_ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertTherapist(String name, String phoneNumber, String cellPhoneNumber, String email, int branchId)
    {
        ContentValues values = new ContentValues();
        try {
            values.put(THERAPIST_NAME, crypto.armorEncrypt(name.getBytes()));
            if (phoneNumber != null) values.put(THERAPIST_PHONENUMBER, crypto.armorEncrypt(phoneNumber.getBytes()));
            if (cellPhoneNumber != null) values.put(THERAPIST_CELLPHONENUMBER, crypto.armorEncrypt(cellPhoneNumber.getBytes()));
            if (email != null) values.put(THERAPIST_EMAIL, crypto.armorEncrypt(email.getBytes()));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        values.put(THERAPIST_BRANCHID, branchId);
        return db.insert(THERAPIST_TABLE, null, values);
    }

    public boolean updateTherapist(int therapistId, String name, String phoneNumber, String cellPhoneNumber, String email, int branchId)
    {
        ContentValues values = new ContentValues();

        try {
            values.put(THERAPIST_NAME, crypto.armorEncrypt(name.getBytes()));
            if (phoneNumber != null) values.put(THERAPIST_PHONENUMBER, crypto.armorEncrypt(phoneNumber.getBytes()));
            else values.putNull(THERAPIST_PHONENUMBER);
            if (cellPhoneNumber != null) values.put(THERAPIST_CELLPHONENUMBER, crypto.armorEncrypt(cellPhoneNumber.getBytes()));
            else values.putNull(THERAPIST_CELLPHONENUMBER);
            if (email != null) values.put(THERAPIST_EMAIL, crypto.armorEncrypt(email.getBytes()));
            else values.putNull(THERAPIST_EMAIL);
            values.put(THERAPIST_BRANCHID, branchId);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return db.update(THERAPIST_TABLE, values, THERAPIST_ID + "=" + therapistId, null) > 0;
    }

    public List<Therapist> getAllTherapists()
    {
        List<Therapist> res = new ArrayList<Therapist>();
        Cursor cursor = db.query(THERAPIST_TABLE, therapistCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToTherapist(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public Therapist getTherapistWithId(int therapistId)
    {
        Cursor cursor = db.query(THERAPIST_TABLE, therapistCols,
                THERAPIST_ID + "=" + therapistId, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToTherapist(cursor);
        return null;
    }

    public Therapist getTherapistWithName(String name)
    {
        Cursor cursor = null;
        try {
            cursor = db.query(THERAPIST_TABLE, therapistCols,
                    THERAPIST_NAME + "='" + crypto.armorEncrypt(name.getBytes()) + "'", null, null, null, null);
            if (cursor.moveToFirst())
                return cursorToTherapist(cursor);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Therapist> getTherapistsWithBranchId(int branchId)
    {
        List<Therapist> res = new ArrayList<Therapist>();
        Cursor cursor = db.query(THERAPIST_TABLE, therapistCols, THERAPIST_BRANCHID + "=" + branchId,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToTherapist(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    private Therapist cursorToTherapist(Cursor cursor)
    {
        Therapist e = new Therapist();
        e.setId(cursor.getInt(0));
        try {
            e.setName((cursor.isNull(1))?null:crypto.armorDecrypt(cursor.getString(1)));
            e.setPhoneNumber((cursor.isNull(2))?null:crypto.armorDecrypt(cursor.getString(2)));
            e.setCellPhoneNumber((cursor.isNull(3))?null:crypto.armorDecrypt(cursor.getString(3)));
            e.setEmail((cursor.isNull(4))?null:crypto.armorDecrypt(cursor.getString(4)));
            e.setBranchId(cursor.getInt(5));
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (NoSuchPaddingException e1) {
            e1.printStackTrace();
        } catch (IllegalBlockSizeException e1) {
            e1.printStackTrace();
        } catch (BadPaddingException e1) {
            e1.printStackTrace();
        } catch (InvalidAlgorithmParameterException e1) {
            e1.printStackTrace();
        }
        return e;
    }

}
