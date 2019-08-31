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
        String query = "UPDATE "+ DatabaseSMS.table_SendSMS +
                " SET isSendToServer = " + " ' " + isSendToServer +" ' "+
                "WHERE id = ' " + localID + " ' " +
                "AND smsID = ' " + smsID + "' " +
                "AND serverID = ' " + serverID + "' ";
        Log.i(av.tag_SendSMS, "11- SendSMS_update Send To Server OK \n Query: "+query);

        smsDatabase.execSQL(query);
        smsDatabase.close();
    }

    public void SendToUser(Context context,String localID,String smsID,String serverID,String isSendToUser){

        SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
        String query = "UPDATE "+ DatabaseSMS.table_SendSMS +
                " SET isSendToUser = " + "'" + isSendToUser +"'"+
                " WHERE id = '" + localID + "'" +
                " AND smsID = '" + smsID + "'" +
                " AND serverID = '" + serverID + "'";
        Log.i(av.tag_SendSMS, "B 7- Update isSendToUser = true \n Query is: "+query);

        smsDatabase.execSQL(query);
        smsDatabase.close();
    }
}
