package com.ermile.payamres.Function.Database.Update;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ermile.payamres.Function.Database.DatabaseSMS;
import com.ermile.payamres.Static.av;

public class SendSMS_update {
    Context context;

    public SendSMS_update(Context context) {
        this.context = context;
    }

    public void SendToServer(Context context,String localID,String smsID,String serverID,String isSendToServer){
        Log.d(av.TagUpdateDatabase, "SendSMS_update (isSendToServer):  "+localID +" | "+smsID + " | "+serverID + " | "+isSendToServer);

        SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
        String query = "UPDATE "+ DatabaseSMS.table_GetSMS +
                " SET isSendToServer = " + " ' " + isSendToServer +" ' "+
                "WHERE id = ' " + localID + " ' " +
                "AND smsID = ' " + smsID + "' " +
                "AND serverID = ' " + serverID + "' ";
        Log.i(av.TagUpdateDatabase, "SendSMS_update (isSendToServer): "+query);

        smsDatabase.execSQL(query);
        smsDatabase.close();
    }
}