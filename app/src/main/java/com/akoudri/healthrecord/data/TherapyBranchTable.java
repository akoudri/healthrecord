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
public class TherapyBranchTable {

    private SQLiteDatabase db;
    private Crypto crypto;

    //Table
    public static final String THERAPYBRANCH_TABLE = "therapybranch";
    public static final String THERAPYBRANCH_ID = "_id";
    public static final String THERAPYBRANCH_NAME = "name";

    private String[] therapybranchCols = {THERAPYBRANCH_ID, THERAPYBRANCH_NAME};

    public TherapyBranchTable(SQLiteDatabase db, Crypto crypto)
    {
        this.db = db;
        this.crypto = crypto;
    }

    public void createTherapyBranchTable()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + THERAPYBRANCH_TABLE + " (");
        sb.append(THERAPYBRANCH_ID + " integer primary key autoincrement,");
        sb.append(THERAPYBRANCH_NAME + " text not null unique");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public long insertTherapyBranch(String name)
    {
        ContentValues values = new ContentValues();
        try {
            values.put(THERAPYBRANCH_NAME, crypto.armorEncrypt(name.getBytes()));
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
        return db.insert(THERAPYBRANCH_TABLE, null, values);
    }

    public List<TherapyBranch> getAllBranches()
    {
        List<TherapyBranch> res = new ArrayList<TherapyBranch>();
        Cursor cursor = db.query(THERAPYBRANCH_TABLE, therapybranchCols,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast())
        {
            res.add(cursorToTherapyBranch(cursor));
            cursor.moveToNext();
        }
        return res;
    }

    public TherapyBranch getBranchWithId(int branchId)
    {
        Cursor cursor = db.query(THERAPYBRANCH_TABLE, therapybranchCols,
                THERAPYBRANCH_ID + "=" + branchId, null, null, null, null);
        if (cursor.moveToFirst())
            return cursorToTherapyBranch(cursor);
        return null;
    }

    public int getBranchId(String branchName)
    {
        Cursor cursor = null;
        try {
            cursor = db.query(THERAPYBRANCH_TABLE, therapybranchCols,
                    THERAPYBRANCH_NAME + "='" + crypto.armorEncrypt(branchName.getBytes()) + "'", null, null, null, null);
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
        return -1; //branch name not found
    }

    private TherapyBranch cursorToTherapyBranch(Cursor cursor)
    {
        TherapyBranch tb = new TherapyBranch();
        tb.setId(cursor.getInt(0));
        try {
            tb.setName(crypto.armorDecrypt(cursor.getString(1)));
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
        return tb;
    }

}
