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
            SharedPreferences save_user = context.getSharedPreferences("save_user", MODE_PRIVATE);
            /* Get Number & Status */
            final String number_phone = SaveManager.get(context).get_Number().get(SaveManager.numberPhone);
            boolean statusServer = save_user.getBoolean("status",true);
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

            SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
            Cursor infoDatabaseSMS = smsDatabase.rawQuery("SELECT * FROM "+DatabaseSMS.table_GetSMS + " WHERE "+DatabaseSMS.getSMS_isSendToServer+ " = 'false' ", null);

            if (infoDatabaseSMS.getCount() == 0){
                mainJsonObject.setSmsnew(smsNew);
            }else {
                while (infoDatabaseSMS.moveToNext()) {
                    String id,number,text,date,smsID,userData,isSendToServer,serverID = null;
                    id = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_localID)) ;
                    number = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_number)) ;
                    text = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_text)) ;
                    date = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_date)) ;
                    smsID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_smsID)) ;
                    userData = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_userData)) ;
                    isSendToServer = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_isSendToServer)) ;
                    serverID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.getSMS_serverID)) ;

                    smsNew.add(new smsnew_jsonArray(id,number,text,date,smsID,userData,brand,model,SimSerialNumber));
                    mainJsonObject.setSmsnew(smsNew);

                }
                smsDatabase.close();
            }
            GetSMS_Sent(context,mainJsonObject);
        }catch (Exception error){
            Log.e(av.pTag, "Get SMS Send from database: "+error,null);
        }

    }

    private void GetSMS_Sent (Context context,main_jsonObject mainJsonObject){
        try {
            List<sentsms_jsonArray> smsSent = new ArrayList<>();

            SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
            Cursor infoDatabaseSMS = smsDatabase.rawQuery(
                    "SELECT * FROM "+DatabaseSMS.table_SendSMS+" WHERE "
                    +DatabaseSMS.sendSMS_isSendToUser +" = 'true' "
                    + "AND "+DatabaseSMS.sendSMS_isSendToServer +" = 'false'", null);

            if (infoDatabaseSMS.getCount() == 0){
                mainJsonObject.setSentsms(smsSent);
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
            }
        }catch (Exception error){
            Log.e(av.pTag, "GetSMS_Sent from database : "+error,null );
        }


    }
}
