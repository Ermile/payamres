package com.ermile.payamres.CrateJSON;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ermile.payamres.CrateJSON.items.detail_jsonObject;
import com.ermile.payamres.CrateJSON.items.main_jsonObject;
import com.ermile.payamres.CrateJSON.items.sentsms_jsonArray;
import com.ermile.payamres.CrateJSON.items.smsnew_jsonArray;
import com.ermile.payamres.DatabaseSMS;
import com.ermile.payamres.save_user;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class json_crated {


    Context context;

    public json_crated(Context context) {
        this.context = context;
        JsonApp(context);
    }

    public String JsonApp(Context context){
        detail_jsonObject detail_Json = new detail_jsonObject("09195191378");
        main_jsonObject mJson = new main_jsonObject("true",detail_Json);
        GetSMS_Send(context,mJson);

        Gson gson = new Gson();
        Log.i(save_user.pTag, "JSON > "+gson.toJson(mJson));
        return gson.toJson(mJson);
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
                mainJsonObject.setSmsNew(smsNew);

//            Log.d(save_user.pTag, "json_crated > smsNew[] "+ id +" | "+ number +" | "+ text +" | "+ date +" | "+ smsID +" | "+ userData +" | "+ brand +" | "+ model +" | "+ SimSerialNumber );
            }
            smsDatabase.close();
            GetSMS_Sent(context,mainJsonObject);
        }catch (Exception error){
            Log.e(save_user.pTag, "Get SMS Send from database: "+error,null);
        }

    }

    private void GetSMS_Sent (Context context,main_jsonObject mainJsonObject){
        try {
            List<sentsms_jsonArray> smsSent = new ArrayList<>();

            SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
            Cursor infoDatabaseSMS = smsDatabase.rawQuery("SELECT * FROM "+DatabaseSMS.table_SendSMS+" WHERE "+DatabaseSMS.sendSMS_isSendToUser +" = 'true' ", null);
            while (infoDatabaseSMS.moveToNext()) {
                String id,smsID,serverID = null;
                id = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.sendSMS_localID)) ;
                smsID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.sendSMS_smsID)) ;
                serverID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex(DatabaseSMS.sendSMS_serverID)) ;

                smsSent.add(new sentsms_jsonArray(id,smsID,serverID));
                mainJsonObject.setSmsSent(smsSent);

                Log.d(save_user.pTag, "json_crated > smsSent[] "+ id +" | "+ smsID +" | "+ serverID );
            }
            smsDatabase.close();
        }catch (Exception error){
            Log.e(save_user.pTag, "GetSMS_Sent from database : "+error,null );
        }


    }
}
