package com.ermile.payamres.Function.Database.DetabaseToJson;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ermile.payamres.Function.Database.DetabaseToJson.ItemsJson.detail_jsonObject;
import com.ermile.payamres.Function.Database.DetabaseToJson.ItemsJson.main_jsonObject;
import com.ermile.payamres.Function.Database.DetabaseToJson.ItemsJson.sentsms_jsonArray;
import com.ermile.payamres.Function.Database.DetabaseToJson.ItemsJson.smslost_jsonArray;
import com.ermile.payamres.Function.Database.DetabaseToJson.ItemsJson.smsnew_jsonArray;
import com.ermile.payamres.Function.Database.DatabaseSMS;
import com.ermile.payamres.Function.SaveDataUser.SaveManager;
import com.ermile.payamres.Static.av;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ProducerJSON {
    Context context;

    public ProducerJSON(Context context) {
        this.context = context;
    }

    public String Producer(Context context){
        try {
            /* Get Number & Status */
            final String number_phone = SaveManager.get(context).get_Number().get(SaveManager.numberPhone);
            boolean statusServer = SaveManager.get(context).get_Status().get(SaveManager.status_server);
            detail_jsonObject detail_Json = new detail_jsonObject(number_phone);
            main_jsonObject mJson = new main_jsonObject(statusServer,detail_Json);
            GetSMS_Send(context,mJson);

            Gson gson = new Gson();
            Log.i(av.pTag, "ProducerJSON.class > "+gson.toJson(mJson));
            return gson.toJson(mJson);

        }catch (Exception error){
            Log.e(av.pTag, "Producer Error: "+error,null );
            return "Producer Error: "+error;
        }
    }

    private void GetSMS_Send(Context context,main_jsonObject mainJsonObject){
        try {
            String SimSerialNumber = null;
            TelephonyManager telemamanger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                SimSerialNumber = telemamanger.getSimSerialNumber();
            }
            final String brand = Build.BRAND;
            final String model = Build.MODEL;

            List<smsnew_jsonArray> smsNew = new ArrayList<>();
            List<smslost_jsonArray> smslost = new ArrayList<>();

            SQLiteDatabase smsDatabase = new DatabaseSMS(context).getReadableDatabase();
            Cursor infoDatabaseSMS = smsDatabase.rawQuery("SELECT * FROM "+DatabaseSMS.table_GetSMS + " WHERE "
                    +DatabaseSMS.getSMS_serverID+" is null AND "
                    +DatabaseSMS.getSMS_isSendToServer+ " = 'false' AND "
                    +DatabaseSMS.getSMS_firstSendToServer +"= 'false'", null);

            if (infoDatabaseSMS.getCount() == 0){
                GetSMS_lost(context,mainJsonObject);
                mainJsonObject.setSmsnew(smsNew);
                infoDatabaseSMS.close();
                smsDatabase.close();
            }else {
                mainJsonObject.setSmslost(smslost);
                while (infoDatabaseSMS.moveToNext()) {
                    String id,number,text,date,smsID,userData,isSendToServer,serverID,MD5 = null;
                    id = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_localID)) ;
                    number = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_number)) ;
                    text = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_text)) ;
                    date = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_date)) ;
                    smsID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_smsID)) ;
                    userData = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_userData)) ;
                    MD5 = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_MD5)) ;
                    isSendToServer = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_isSendToServer)) ;
                    serverID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_serverID)) ;

                    String query = "UPDATE GetSMS SET firstSendToServer = 'true' WHERE id = "+id+" ";
                    SQLiteDatabase smsDatabase_lost = new DatabaseSMS(context).getWritableDatabase();
                    smsDatabase_lost.execSQL(query);
                    smsDatabase_lost.close();
                    Log.d(av.iTag, "query------------------------------------------>\n\n: "+query);

                    smsNew.add(new smsnew_jsonArray(id,number,text,date,smsID,userData,MD5,brand,model,SimSerialNumber));
                    mainJsonObject.setSmsnew(smsNew);

                }
                smsDatabase.close();
                infoDatabaseSMS.close();
            }
            SendSMS_Sent(context,mainJsonObject);
        }catch (Exception error){
            Log.e(av.pTag, "Get SMS Send from database: "+error,null);
        }

    }

    private void  GetSMS_lost(Context context,main_jsonObject mainJsonObject){
        try {
            String SimSerialNumber = null;
            TelephonyManager telemamanger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                SimSerialNumber = telemamanger.getSimSerialNumber();
            }
            final String brand = Build.BRAND;
            final String model = Build.MODEL;

            List<smslost_jsonArray> smslost = new ArrayList<>();

            SQLiteDatabase smsDatabase = new DatabaseSMS(context).getReadableDatabase();
            Cursor infoDatabaseSMS = smsDatabase.rawQuery("SELECT * FROM "+DatabaseSMS.table_GetSMS + " WHERE "+DatabaseSMS.getSMS_serverID+ " is null AND "
                    +DatabaseSMS.getSMS_firstSendToServer +"= 'true'", null);

            if (infoDatabaseSMS.getCount() == 0){
                mainJsonObject.setSmslost(smslost);
                infoDatabaseSMS.close();
                smsDatabase.close();
            }else {
                while (infoDatabaseSMS.moveToNext()) {
                    String id,number,text,date,smsID,userData,isSendToServer,serverID,MD5 = null;
                    id = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_localID)) ;
                    number = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_number)) ;
                    text = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_text)) ;
                    date = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_date)) ;
                    smsID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_smsID)) ;
                    userData = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_userData)) ;
                    MD5 = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_MD5)) ;
                    isSendToServer = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_isSendToServer)) ;
                    serverID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_serverID)) ;

                    smslost.add(new smslost_jsonArray(id,number,text,date,smsID,userData,MD5,brand,model,SimSerialNumber));
                    mainJsonObject.setSmslost(smslost);

                }
                smsDatabase.close();
                infoDatabaseSMS.close();
            }
            SendSMS_Sent(context,mainJsonObject);
        }catch (Exception error){
            Log.e(av.pTag, "Get SMS Send from database: "+error,null);
        }

    }

    private void SendSMS_Sent (Context context,main_jsonObject mainJsonObject){
        try {
            List<sentsms_jsonArray> smsSent = new ArrayList<>();

            SQLiteDatabase smsDatabase = new DatabaseSMS(context).getReadableDatabase();
            Cursor infoDatabaseSMS = smsDatabase.rawQuery(
                    "SELECT * FROM "+DatabaseSMS.table_SendSMS+" WHERE "
                    +DatabaseSMS.sendSMS_isSendToUser +" = 'true' "
                    + "AND "+DatabaseSMS.sendSMS_isSendToServer +" = 'false'", null);

            if (infoDatabaseSMS.getCount() == 0){
                mainJsonObject.setSentsms(smsSent);
                infoDatabaseSMS.close();
                smsDatabase.close();
            }else {
                while (infoDatabaseSMS.moveToNext()) {
                    String id,smsID,serverID = null;
                    id = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.sendSMS_localID)) ;
                    smsID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.sendSMS_smsID)) ;
                    serverID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.sendSMS_serverID)) ;

                    smsSent.add(new sentsms_jsonArray(id,smsID,serverID));
                    mainJsonObject.setSentsms(smsSent);

                    Log.d(av.pTag, "json crated > smsSent[] "+ id +" | "+ smsID +" | "+ serverID );
                }
                smsDatabase.close();
                infoDatabaseSMS.close();
            }
        }catch (Exception error){
            Log.e(av.pTag, "GetSMS_Sent from database : "+error,null );
        }


    }
}
