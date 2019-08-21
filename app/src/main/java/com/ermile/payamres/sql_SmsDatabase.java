package com.ermile.payamres;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class sql_SmsDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "smsDatabase.db";
    String TableName = "smsTable";
    String number ="number";
    String  text ="text";
    String time ="time";
    String massageId ="massageId";
    String massageData ="massageData";
    String isSend ="isSend";

    public static final int DATABASE_VERSION = 2;

    public sql_SmsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }
}