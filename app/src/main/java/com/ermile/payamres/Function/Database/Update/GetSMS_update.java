package com.ermile.payamres.Function.Database.Update;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ermile.payamres.Function.Database.DatabaseSMS;
import com.ermile.payamres.Static.av;

public class GetSMS_update {

    Context context;

    public GetSMS_update(Context context) {
        this.context = context;
    }


    public void serverID(Context context ,String localID, String smsID, String serverID){
        Log.d(av.TagUpdateDatabase, "GetSMS_update serverID:  "+localID +" | "+smsID + " | "+serverID);

        SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
        String query = "UPDATE "+ DatabaseSMS.table_GetSMS +
                " SET isSendToServer = 'true' , serverID = ' " + serverID + " ' " +
                "WHERE id = ' " + localID + " ' " +
                "AND smsID = ' " + smsID + "' ";
        Log.i(av.TagUpdateDatabase, "GetSMS_update serverID: "+query);

        smsDatabase.execSQL(query);
        smsDatabase.close();

    }


}
