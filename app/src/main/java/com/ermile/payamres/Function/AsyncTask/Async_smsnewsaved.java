package com.ermile.payamres.Function.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ermile.payamres.Function.Database.Update.GetSMS_update;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_smsnewsaved;
import com.ermile.payamres.Static.av;

public class Async_smsnewsaved extends AsyncTask<item_smsnewsaved, Void , Void> {

    Context context;

    public Async_smsnewsaved(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        Log.d(av.TagAsync, "Start (Async_smsnewsaved) \n");
    }

    @Override
    protected Void doInBackground(item_smsnewsaved... params) {
        for (item_smsnewsaved p : params) {
            try {
                String Localid = p.getLocalid();
                String Smsid = p.getSmsid();
                String Serverid = p.getServerid();

                GetSMS_update.serverID(context, Localid, Smsid, Serverid);
                Log.d(av.tag_GetSMS, "Async_smsnewsaved (doInBackground): "+ Localid + " | " + Smsid+ " | " + Serverid );

            } catch (Exception error) {
                Log.e(av.tag_GetSMS, "Async_smsnewsaved (doInBackground): " + error, null);
            }

        }
        return null;
    }
}
