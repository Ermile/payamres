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
import java.util.Date;

public class IncomingSms extends BroadcastReceiver {
    private static String TAG = "IncomingSms";

    public void onReceive(final Context context, Intent intent) {
        final SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Log.i(TAG, "onReceive -Start");
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String numberSMS,textSMS,timeSMS,idSMS,userDataSMS;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                        numberSMS = msgs[i].getOriginatingAddress();
                        textSMS = msgs[i].getMessageBody();
                        timeSMS = DateFormat.getDateTimeInstance().format(new Date());
                        idSMS = msgs[i]+"";
                        userDataSMS = msgs[i].getUserData()+"";

                        Log.i(av.tag_GetSMS, "Receive SMS - info SMS " +
                                "\n number: "+numberSMS+
                                "\n Massage: "+textSMS+
                                "\n Time: "+timeSMS+
                                "\n SMS-ID: "+idSMS+
                                "\n UserData: "+userDataSMS +
                                "\n ---------------------------------Finish--------------------------------------");

                        /*Add SMS To Database*/
                        smsDatabase.execSQL(insertToGetSMS(numberSMS,textSMS,timeSMS,idSMS,userDataSMS,"false",null));
                        Log.i(av.tagQuery, "Query SQL: INSERT > "+insertToGetSMS(numberSMS,textSMS,timeSMS,idSMS,userDataSMS,"false",null));

                        /*Log Database*/
                        Cursor infoDatabaseSMS = smsDatabase.rawQuery("SELECT * FROM "+DatabaseSMS.table_GetSMS, null);
                        while (infoDatabaseSMS.moveToNext()) {
                            int id = infoDatabaseSMS.getInt(infoDatabaseSMS.getColumnIndex("id")) ;
                            String number = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("number")) ;
                            String text = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("text")) ;
                            String isSend = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("isSendToServer")) ;
                            String serverID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("serverID")) ;
                            Log.d(av.tag_GetSMS, "Dtabase: "+id+" - number: "+number +" | text: "+text +" | isSendToServer | ServerID: "+isSend +" | "+serverID);
                        }
                    }
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