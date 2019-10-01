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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class IncomingSms extends BroadcastReceiver {
    private static String TAG = "IncomingSms";

    public void onReceive(final Context context, Intent intent) {


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

                    String textMD5 = numberSMS+textSMS+idSMS+userDataSMS+textSMS;

                    /*Add SMS To Database*/
                    SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
                    smsDatabase.execSQL(insertToGetSMS(numberSMS,textSMS,timeSMS,idSMS,userDataSMS,"false","false",md5(textMD5)));
                    smsDatabase.close();
                    Log.i(av.tagQuery, "Query SQL: INSERT > "+insertToGetSMS(numberSMS,textSMS,timeSMS,idSMS,userDataSMS,"false","false",md5(textMD5)));
                }catch(Exception e){
                    Log.e(av.tag_GetSMS, "onReceive: -Error  \n"+ e,null );
                }
            }
        }
    }

    private static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null ;
        }
    }

    private String insertToGetSMS (String number,String text,String date,String smsID,String userData,String isSendToServer,String firstSendServer,String MD5){
        return "INSERT INTO "+ DatabaseSMS.table_GetSMS
                        + "("+ DatabaseSMS.getSMS_number +","
                        + DatabaseSMS.getSMS_text + ","
                        + DatabaseSMS.getSMS_date + ","
                        + DatabaseSMS.getSMS_smsID + ","
                        + DatabaseSMS.getSMS_userData + ","
                        + DatabaseSMS.getSMS_isSendToServer + ","
                        + "firstSendToServer" + ","
                        + "MD5" + ")"

                        + "Values (" +
                        "'"+number+"'," +
                        " '"+text+"'," +
                        " '"+date+"'," +
                        " '"+smsID+"'," +
                        " '"+userData+"'," +
                        " '"+isSendToServer+"'," +
                        " '"+firstSendServer+"'," +
                        " '"+MD5+"' )";

    }
}