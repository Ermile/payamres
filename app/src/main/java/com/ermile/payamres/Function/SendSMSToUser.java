package com.ermile.payamres.Function;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ermile.payamres.Function.AsyncTask.Async_SendDevice;
import com.ermile.payamres.Function.Database.DatabaseSMS;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.itemSendDevice;
import com.ermile.payamres.Static.av;

public class SendSMSToUser {

    Context context;

    public SendSMSToUser(Context context) {
        this.context = context;

        SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
        Cursor getSendSMS = smsDatabase.rawQuery("SELECT * FROM "+DatabaseSMS.table_SendSMS + " WHERE "+DatabaseSMS.sendSMS_isSendToUser+ " = 'false' ", null);
        Log.i(av.tag_SendSMS, "B 4- Get Table 'SendSMS' --> if (isSendToUser = false)");
        while (getSendSMS.moveToNext()){
            String id,number,massage,smsID,serverID;
            id = getSendSMS.getString(getSendSMS.getColumnIndex(DatabaseSMS.sendSMS_localID)) ;
            number = getSendSMS.getString(getSendSMS.getColumnIndex(DatabaseSMS.sendSMS_toNumber)) ;
            massage = getSendSMS.getString(getSendSMS.getColumnIndex(DatabaseSMS.sendSMS_text)) ;
            smsID = getSendSMS.getString(getSendSMS.getColumnIndex(DatabaseSMS.sendSMS_smsID)) ;
            serverID = getSendSMS.getString(getSendSMS.getColumnIndex(DatabaseSMS.sendSMS_serverID)) ;

            itemSendDevice itemSend_Device= new itemSendDevice(id,number,massage,smsID,serverID);
            Log.i(av.tag_SendSMS, "B 5- Start Async Send SMS To User \n"
                    +"  "+id+"- "+number+" | "+massage.replace("\n"," ")+" | "+smsID+" | "+serverID);
            new Async_SendDevice(context).execute(itemSend_Device);

        }
    }


}
