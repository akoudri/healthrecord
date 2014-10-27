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
 * Created by Ali Koudri on 15/06/14.
 */
public class DrugTable {

    private SQLiteDatabase db;
    private Crypto crypto;

    //Table
    public static final String DRUG_TABLE = "drug";
    public static final String DRUG_ID = "_id";
    public static final String DRUG_NAME = "name";

    private String[] drugCols = {DRUG_ID, DRUG_NAME};

    public DrugTable(SQLiteDatabase db, Crypto crypto)
    {
        this.db = db;
        this.crypto = crypto;
    }

    public void createDrugTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + DRUG_TABLE + " (");
        sb.append(DRUG_ID + " integer primary key autoincrement,");
        sb.append(DRUG_NAME + " text not null unique");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertDrug(String name)
    {
        ContentValues values = new ContentValues();
        try {
            values.put(DRUG_NAME, crypto.armorEncrypt(name.getBytes()));
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
        return db.insert(DRUG_TABLE, null, values);
    }

    public List<Drug> getAllDrugs()
    {
        List<Drug> res = new ArrayList<Drug>();
        Cursor cursor = db.query(DRUG_TABLE, drugCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToDrug(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public Drug getDrugWithId(int drugId)
    {
        Cursor cursor = db.query(DRUG_TABLE, drugCols,
                DRUG_ID + "=" + drugId, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToDrug(cursor);
        return null;
    }

    public int getDrugId(String name)
    {

        Cursor cursor = null;
        try {
            cursor = db.query(DRUG_TABLE, drugCols, DRUG_NAME + "='" + crypto.armorEncrypt(name.getBytes()) + "'", null, null, null, null);
            if (cursor.moveToFirst())
                return  cursor.getInt(0);
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
        return -1;
    }

    private Drug cursorToDrug(Cursor cursor)
    {
        Drug d = new Drug();
        d.setId(cursor.getInt(0));
        try {
            d.setName(crypto.armorDecrypt(cursor.getString(1)));
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
        return d;
    }

}
