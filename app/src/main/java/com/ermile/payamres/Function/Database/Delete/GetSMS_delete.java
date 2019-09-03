package com.ermile.payamres.Function.Database.Delete;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ermile.payamres.Function.Database.DatabaseSMS;
import com.ermile.payamres.Static.av;

public class GetSMS_delete {

    public void delete30DayAgo(Context context){
        SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
        String query = "DELETE FROM "+DatabaseSMS.table_GetSMS+" WHERE date  <= date('now','-30 day') AND "+DatabaseSMS.getSMS_isSendToServer+" = 'true'";
        Log.d(av.tagQuery, "delete30DayAgo: GetSMS " + query);
        smsDatabase.execSQL(query);
        smsDatabase.close();

    }
}