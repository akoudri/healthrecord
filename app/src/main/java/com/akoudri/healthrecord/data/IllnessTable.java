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
 * Created by Ali Koudri on 10/04/14.
 */
public class IllnessTable {

    private SQLiteDatabase db;
    private Crypto crypto;

    //Table
    public static final String ILLNESS_TABLE = "illness";
    public static final String ILLNESS_ID = "_id";
    public static final String ILLNESS_NAME = "name";

    private String[] illnessCols = {ILLNESS_ID, ILLNESS_NAME};

    public IllnessTable(SQLiteDatabase db, Crypto crypto)
    {
        this.db = db;
        this.crypto = crypto;
    }

    public void createIllnessTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + ILLNESS_TABLE + " (");
        sb.append(ILLNESS_ID + " integer primary key autoincrement,");
        sb.append(ILLNESS_NAME + " text not null unique");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertIllness(String name)
    {
        ContentValues values = new ContentValues();
        try {
            values.put(ILLNESS_NAME, crypto.armorEncrypt(name.getBytes()));
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
        return db.insert(ILLNESS_TABLE, null, values);
    }

    public List<Illness> getAllIllnesses()
    {
        List<Illness> res = new ArrayList<Illness>();
        Cursor cursor = db.query(ILLNESS_TABLE, illnessCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToIllness(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public Illness getIllnessWithId(int illnessId)
    {
        Cursor cursor = db.query(ILLNESS_TABLE, illnessCols,
                ILLNESS_ID + "=" + illnessId, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToIllness(cursor);
        return null;
    }

    public int getIllnessId(String illnessName)
    {
        Cursor cursor = null;
        try {
            cursor = db.query(ILLNESS_TABLE, illnessCols,
                    ILLNESS_NAME + "='" + crypto.armorEncrypt(illnessName.getBytes()) + "'", null, null, null, null);
            if (cursor.moveToFirst())
                return cursor.getInt(0);
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
        return -1; //illness name not found
    }

    private Illness cursorToIllness(Cursor cursor)
    {
        Illness illness = new Illness();
        illness.setId(cursor.getInt(0));
        try {
            illness.setName(crypto.armorDecrypt(cursor.getString(1)));
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
        return illness;
    }

}
