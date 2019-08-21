package com.ermile.payamres;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DatabaseName = "myinfo.db";
    private static final int Version = 2;
    private static final String TableName = "tbl_person";
    private static final String dID = "id";
    private static final String dNumber = "Number";
    private static final String dMassage = "Massage";
    private static final String dIsSend = "IsSend";
    private static final String dTime = "time";

    public DatabaseManager(Context cnt) {

        super(cnt, DatabaseName, null, Version);
        Log.i("Mahdi", "Database Created!");

    }

    @Override
    public void onCreate(SQLiteDatabase cdb) {

        String cQuery = "CREATE TABLE "
                + TableName
                + " ( " + dID + " INTEGER PRIMARY KEY UNIQUE, "
                + dNumber + " TEXT, "
                + dMassage + " TEXT, "
                + dIsSend + " TEXT, "
                + dTime + " TEXT );";
        cdb.execSQL(cQuery);
        Log.i("Mahdi", "Table Created!");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertPerson(Person iprs) {

        SQLiteDatabase idb = this.getWritableDatabase();
        ContentValues icv = new ContentValues();
        icv.put(dID, iprs.pID);
        icv.put(dNumber, iprs.pNumber);
        icv.put(dMassage, iprs.pMassage);
        icv.put(dIsSend, iprs.pIsSend);
        icv.put(dTime, iprs.pTime);
        idb.insert(TableName, null, icv);
        idb.close();
        Log.i("Mahdi", "insertPerson Method");

    }

    public Person getPerson(String gID) {

        Person gPrs = new Person();
        SQLiteDatabase gdb = this.getReadableDatabase();
        String gQuery = "SELECT * FROM " + TableName + " WHERE " + dIsSend + "=" + "false";
        Cursor gCur = gdb.rawQuery(gQuery, null);
        if (gCur.moveToFirst()) {
            gPrs.pNumber = gCur.getString(gCur.getColumnIndex(dNumber));
            gPrs.pMassage = gCur.getString(gCur.getColumnIndex(dMassage));
            gPrs.pIsSend = gCur.getString(gCur.getColumnIndex(dIsSend));
            gPrs.pTime = gCur.getString(gCur.getColumnIndex(dTime));
        }
        Log.i("Mahdi", "getPerson Method");
        return gPrs;

    }


    public boolean deletePerson() {

        SQLiteDatabase ddb = this.getWritableDatabase();
        long dResult = ddb.delete(TableName, dIsSend + "true",null);

        Log.i("Mahdi", "deletePerson Method");

        if (dResult == 0)
            return false;
        else
            return true;

    }

    public int personCount() {

        String gQuery = "SELECT * FROM " + TableName;
        SQLiteDatabase gdb = this.getReadableDatabase();
        Cursor gCur = gdb.rawQuery(gQuery, null);
        int cResult = gCur.getCount();
        return cResult;

    }

}