package com.ermile.payamres.Function.Database.Select;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ermile.payamres.Function.Database.DatabaseSMS;
import com.ermile.payamres.Static.av;

public class SendSMS_Select {
    Context context;
    public SendSMS_Select(Context context) {
        this.context = context;
    }

    public boolean getServerID(Context context,String ServerID){
        SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
        Cursor getID = smsDatabase.rawQuery(
                " SELECT "+DatabaseSMS.sendSMS_serverID
                        +" FROM "+DatabaseSMS.table_SendSMS
                        + " WHERE "+DatabaseSMS.sendSMS_serverID
                        + " = '"+ServerID+ "' ",null);
        Log.d(av.tagQuery, "query getServerID (SendSMS): "+getID);
        while (getID.moveToNext()){
           String id = getID.getString(getID.getColumnIndex(DatabaseSMS.sendSMS_serverID)) ;
            Log.d(av.pTag, "getServerID (SendSMS): "+id);
            return false;
        }
        return true;
    }


}
