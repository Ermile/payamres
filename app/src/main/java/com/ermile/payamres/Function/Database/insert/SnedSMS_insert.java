package com.ermile.payamres.Function.Database.insert;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ermile.payamres.Function.Database.DatabaseSMS;
import com.ermile.payamres.Static.av;

public class SnedSMS_insert {
    Context context;

    public SnedSMS_insert(Context context) {
        this.context = context;
    }

    public void insertToSendSMS (Context context, String toNumber, String text, String smsID, String isSendToUser, String isSendToServer, String serverID){
        Log.d(av.TagInsertDatabase, "insertToSendSMS: "+toNumber + " | " + text + " | " + smsID + " | " + isSendToUser + " | " + isSendToServer + " | " + serverID);

        SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
        String query = "INSERT INTO "
                + DatabaseSMS.table_SendSMS + "("
                + DatabaseSMS.sendSMS_toNumber +","
                + DatabaseSMS.sendSMS_text + ","
                + DatabaseSMS.sendSMS_smsID + ","
                + DatabaseSMS.sendSMS_isSendToUser + ","
                + DatabaseSMS.sendSMS_isSendToServer + ","
                + DatabaseSMS.getSMS_serverID + ")"
                + "Values ("
                + "'"+toNumber+"',"
                + " '"+text+"',"
                + " '"+smsID+"',"
                + " '"+isSendToUser+"',"
                + " '"+isSendToServer+"',"
                + " '"+serverID+"' )";
        Log.i(av.tag_SendSMS, "A 3- INSERT SMSNew To Table SendSMS \n Query: "+ query);

        smsDatabase.execSQL(query);
        smsDatabase.close();
    }
}
