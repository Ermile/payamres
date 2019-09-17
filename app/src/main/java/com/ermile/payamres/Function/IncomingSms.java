package com.ermile.payamres.Function;

import android.annotation.SuppressLint;
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
import java.util.Arrays;
import java.util.Date;

public class IncomingSms extends BroadcastReceiver {
    private static String TAG = "IncomingSms";

    public void onReceive(final Context context, Intent intent) {
        final SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Log.i(TAG, "onReceive -Start");
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];

                    String  numberSMS = null,
                            timeSMS = null,
                            idSMS = null,
                            timeJAVA= null,
                            userDataSMS = null,
                            textForMD5 = null,
                            textSMS = "";

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    timeSMS = simpleDateFormat.format(new Date());


                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);

                        if (i == 0){
                            numberSMS = msgs[i].getOriginatingAddress();
                            idSMS = msgs[i]+"";
                            userDataSMS = String.valueOf(msgs[i].getUserData());
                            timeJAVA = String.valueOf(msgs[i].getTimestampMillis());
                        }

                        textSMS = textSMS + msgs[i].getMessageBody();

                    }
                    textForMD5= idSMS+numberSMS+timeSMS+textSMS+userDataSMS+timeJAVA;
                    /*Add SMS To Database*/
                    smsDatabase.execSQL(insertToGetSMS(numberSMS,textSMS,timeSMS,idSMS,userDataSMS,"false","false",convertStringToMD5(textForMD5)));
                    smsDatabase.close();
                    Log.i(av.iTags, "- MD5: "+convertStringToMD5(textForMD5) +
                            "\nID: "+textForMD5+
                            "\n text: "+textSMS+
                            "\n ---------------------------------Finish--------------------------------------");
                    Log.i(av.iTags, "- Query: "+insertToGetSMS(numberSMS,textSMS,timeSMS,idSMS,userDataSMS,"false","false",convertStringToMD5(textForMD5))+
                            "\n ---------------------------------Query--------------------------------------");

                }catch(Exception e){
                    Log.e(av.tag_GetSMS, "onReceive: -Error  \n"+ e,null );
                }
            }
        }
    }


    private String insertToGetSMS (String number,String text,String date,String smsID,String userData,String isSendToServer ,String firstSendToServer,String MD5){
        return "INSERT INTO "+ DatabaseSMS.table_GetSMS
                + "("+ DatabaseSMS.getSMS_number +","
                + DatabaseSMS.getSMS_text + ","
                + DatabaseSMS.getSMS_date + ","
                + DatabaseSMS.getSMS_smsID + ","
                + DatabaseSMS.getSMS_userData + ","
                + DatabaseSMS.getSMS_isSendToServer +","
                + DatabaseSMS.getSMS_firstSendToServer +","
                + DatabaseSMS.getSMS_MD5 +")"

                + "Values (" +
                " '"+number+"' ," +
                " '"+text+"'   ," +
                " '"+date+"'   ," +
                " '"+smsID+"'  ," +
                " '"+userData+"' ," +
                " '"+isSendToServer+ "'    ,"+
                " '"+firstSendToServer+ "' ,"+
                " '"+MD5+"' )";

    }

    public static String convertStringToMD5(final String s) {
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
        }
        return null;
    }
}