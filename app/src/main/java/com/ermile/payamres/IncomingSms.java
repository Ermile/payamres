package com.ermile.payamres;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

public class IncomingSms extends BroadcastReceiver {
    private static String TAG = "IncomingSms";

    public void onReceive(final Context context, Intent intent) {
        final SQLiteDatabase smsDatabase = new sql_SmsDatabase(context).getWritableDatabase();

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Log.d(TAG, "onReceive -Start");
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

                        Log.i(TAG, "Receive SMS - info SMS " +
                                "\n number: "+numberSMS+
                                "\n Massage: "+textSMS+
                                "\n Time: "+timeSMS+
                                "\n SMS-ID: "+idSMS+
                                "\n UserData: "+userDataSMS +
                                "\n ---------------------------------Finish--------------------------------------");

                        /*Add SMS To Database*/
                        String addSmsToDatabase =
                                "INSERT INTO smsTable (number,text,time,massageId,massageData,isSend) " +
                                "Values (" +
                                        "'"+numberSMS+"'," +
                                        " '"+textSMS+"'," +
                                        " '"+timeSMS+"'," +
                                        " '"+idSMS+"'," +
                                        " '"+userDataSMS+"'," +
                                        " 'false')";
                        smsDatabase.execSQL(addSmsToDatabase);

                        /*Log Database*/
                        Cursor infoDatabaseSMS = smsDatabase.rawQuery("SELECT * FROM smsTable", null);
                        while (infoDatabaseSMS.moveToNext()) {
                            int id = infoDatabaseSMS.getInt(infoDatabaseSMS.getColumnIndex("id")) ;
                            String number = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("number")) ;
                            String text = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("text")) ;
                            String isSend = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("isSend")) ;
                            Log.i(TAG, "Dtabase: "+id+" - number: "+number +" | text: "+text +" | isSend: "+isSend);
                        }
                    }
                }catch(Exception e){
                    Log.e(TAG, "onReceive: -Error  \n"+ e,null );
                }
            }
        }
    }
}