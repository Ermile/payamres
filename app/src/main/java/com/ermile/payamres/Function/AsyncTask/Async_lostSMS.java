package com.ermile.payamres.Function.AsyncTask;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.ermile.payamres.Function.Database.DatabaseSMS;
import com.ermile.payamres.Static.av;

public class Async_lostSMS extends AsyncTask<String, Void , Void> {

    Context context;

    public Async_lostSMS(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        Log.d(av.TagAsync, "Start (Async_SentSmsSaved) \n");
    }

    @Override
    protected Void doInBackground(String... params) {
        for (String p : params) {
            String Localid = p;
            try {
                SQLiteDatabase smsDatabase = new DatabaseSMS(context).getWritableDatabase();
                String query = "UPDATE "+ DatabaseSMS.table_GetSMS +
                        " SET firstSendToServer = 'true' "+
                        "WHERE id = '" + Localid + "'"+
                        "AND firstSendToServer = 'false' "+
                        "AND serverID is null "
                        ;
                Log.i(av.tag_SendSMS, "Async_lostSMS \n Query: "+query);
                smsDatabase.execSQL(query);
                smsDatabase.close();


            } catch (Exception error) {
                Log.e(av.tag_SendSMS, " Async_lostSMS \n ERROR: " + error, null);
            }

        }
        return null;
    }
}