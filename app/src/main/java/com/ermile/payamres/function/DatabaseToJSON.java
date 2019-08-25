package com.ermile.payamres.function;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ermile.payamres.DatabaseSMS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseToJSON {
    public final String TAG = this.getClass().getSimpleName();

    Context context;

    public DatabaseToJSON(Context context) {
        this.context = context;
    }


    public String crateGetSMS_JSON (Context contexts){

        Object jsonObjectMAIN = new Object();
        ArrayList<HashMap<String, String>> offlineList = new ArrayList<HashMap<String, String>>();


        String selectQuery = "SELECT  * FROM manyofflineCheckouts ";

        SQLiteDatabase smsDatabase = new DatabaseSMS(contexts).getWritableDatabase();

        HashMap<String, HashMap<String, String>> hashMap = new HashMap<String, HashMap<String, String>>();

        Cursor infoDatabaseSMS = smsDatabase.rawQuery("SELECT * FROM "+DatabaseSMS.table_GetSMS, null);
        while (infoDatabaseSMS.moveToNext()) {
            int id = infoDatabaseSMS.getInt(infoDatabaseSMS.getColumnIndex("id")) ;
            String number = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("number")) ;
            String text = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("text")) ;
            String isSend = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("isSendToServer")) ;
            String serverID = infoDatabaseSMS.getString(infoDatabaseSMS.getColumnIndex("serverID")) ;
//            Log.d(TAG, "Dtabase: "+id+"--> number: "+number +" | text: "+text +" | isSendToServer | ServerID: "+isSend +" | "+serverID);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put( "id",id+"");
            map.put( "number",number);
            hashMap.put("mekane",map);
            offlineList.add(map);
        }
        smsDatabase.close();
        Gson gson = new GsonBuilder().create();

        return gson.toJson(offlineList);
    }
}
