package com.ermile.payamres.Function;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

import com.ermile.payamres.Function.Database.DatabaseSMS;
import com.ermile.payamres.Function.Database.Update.SendSMS_update;
import com.ermile.payamres.Static.av;

import java.util.Random;

public class SendSMSToUser extends AsyncTask<String, Void , Void> {

    Context context;

    public SendSMSToUser(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Void doInBackground(String... params) {
        for (String p : params) {
            SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
            String query =  "SELECT * FROM "+DatabaseSMS.table_SendSMS
                    + " WHERE "+DatabaseSMS.sendSMS_isSendToUser+ " = 'false' limit 10 ";
            Cursor getSendSMS = smsDatabase.rawQuery(query, null);
            Log.i(av.tag_SendSMS, "B 4- Get Table 'SendSMS' count: "+getSendSMS.getCount()
                    +" --> if (isSendToUser = false)"
                    +"\n Query: "+query);
            while (getSendSMS.moveToNext()){
                String id,number,massage,smsID,serverID;
                id = getSendSMS.getString(getSendSMS.getColumnIndex(DatabaseSMS.sendSMS_localID)) ;
                number = getSendSMS.getString(getSendSMS.getColumnIndex(DatabaseSMS.sendSMS_toNumber)) ;
                massage = getSendSMS.getString(getSendSMS.getColumnIndex(DatabaseSMS.sendSMS_text)) ;
                smsID = getSendSMS.getString(getSendSMS.getColumnIndex(DatabaseSMS.sendSMS_smsID)) ;
                serverID = getSendSMS.getString(getSendSMS.getColumnIndex(DatabaseSMS.sendSMS_serverID)) ;
                try {
                    int timeSleep = new Random().nextInt((av.max_SendSMS - av.min_SendSMS))+ av.min_SendSMS;
                    Thread.sleep(timeSleep);
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null,  massage, null, null);
                    Log.i(av.tag_SendSMS, "B 5- Send SMS To User \n"
                            +"number: "+number +" | Massage: "+massage.replace("\n"," ")+" Sleep Code "+timeSleep+" MilSec");

                    SendSMS_update.SendToUser(context,id,smsID,serverID,"true");
                    Log.i(av.tag_SendSMS, "B 6- start Function Update for (Update Table SendSMS 'isSendToUser = true') \n"
                            +"Id: "+id+" | Smsid: "+smsID+" | Serverid: "+serverID+" | isSendToUser: 'true' ");

                } catch (Exception e) {
                    SendSMS_update.SendToUser(context,id,smsID,serverID,"false");
                    Log.e(av.tag_SendSMS, "B 5- Send SMS To User \n ERROR > "+e);
                }

            }
        }
        return null;
    }

}
