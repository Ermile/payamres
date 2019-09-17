package com.ermile.payamres.Function;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.ermile.payamres.Function.Database.DatabaseSMS;
import com.ermile.payamres.Static.av;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IncomingSms extends BroadcastReceiver {
    private static String TAG = "IncomingSms";

    public void onReceive(final Context context, Intent intent) {
        final SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Log.i(TAG, "onReceive -Start");
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String numberSMS = null
                    ,textSMS = ""
                    ,timeSMS = null
                    ,idSMS = null
                    ,userDataSMS = null
                    ,md5 = null;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        if (i == 0){
                            numberSMS = msgs[i].getOriginatingAddress();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            timeSMS = simpleDateFormat.format(new Date());
                            idSMS = msgs[i]+"";
                            userDataSMS = msgs[i].getUserData()+"";
                        }
                        textSMS = textSMS + msgs[i].getMessageBody();


                    }
                    /*Add SMS To Database*/
                    smsDatabase.execSQL(insertToGetSMS(numberSMS,textSMS,timeSMS,idSMS,userDataSMS,"false",null));
                    Log.i(av.tagQuery, "Query SQL: INSERT > "+insertToGetSMS(numberSMS,textSMS,timeSMS,idSMS,userDataSMS,"false",null));
                    smsDatabase.close();
                }catch(Exception e){
                    Log.e(av.tag_GetSMS, "onReceive: -Error  \n"+ e,null );
                }
            }
        }
    }


    private String insertToGetSMS (String number,String text,String date,String smsID,String userData,String isSendToServer,String serverID){
        String getSMS_insert =
                "INSERT INTO "+ DatabaseSMS.table_GetSMS
                        + "("+ DatabaseSMS.getSMS_number +","
                        + DatabaseSMS.getSMS_text + ","
                        + DatabaseSMS.getSMS_date + ","
                        + DatabaseSMS.getSMS_smsID + ","
                        + DatabaseSMS.getSMS_userData + ","
                        + DatabaseSMS.getSMS_isSendToServer + ","
                        + DatabaseSMS.getSMS_serverID + ")"

                        + "Values (" +
                        "'"+number+"'," +
                        " '"+text+"'," +
                        " '"+date+"'," +
                        " '"+smsID+"'," +
                        " '"+userData+"'," +
                        " '"+isSendToServer+"'," +
                        " '"+serverID+"' )";

        return getSMS_insert;

    }
}