package com.ermile.payamres.Function.Database;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class DatabaseSMS extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "PayamresDatabase.db";

    /*Table Get SMS*/
    public static String table_GetSMS = "GetSMS";
    public static String getSMS_localID ="id";
    public static String getSMS_number ="number";
    public static String getSMS_text ="text";
    public static String getSMS_date ="date";
    public static String getSMS_smsID ="smsID";
    public static String getSMS_userData ="userData";
    public static String getSMS_isSendToServer ="isSendToServer";
    public static String getSMS_firstSendToServer ="firstSendToServer";
    public static String getSMS_serverID ="serverID";


    /*Table Send SMS*/
    public static String table_SendSMS = "SendSMS";
    public static String sendSMS_localID ="id";
    public static String sendSMS_toNumber ="toNumber";
    public static String sendSMS_text ="text";
    public static String sendSMS_date ="date";
    public static String sendSMS_smsID ="smsID";
    public static String sendSMS_isSendToUser ="isSendToUser";
    public static String sendSMS_isSendToServer ="isSendToServer";
    public static String sendSMS_serverID ="serverID";

    public static final int DATABASE_VERSION = 5;

    public DatabaseSMS(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade();
    }





}