package com.ermile.payamres.Function.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ermile.payamres.Function.Database.Update.GetSMS_update;
import com.ermile.payamres.Function.Database.Update.SendSMS_update;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_sentsmssaved;
import com.ermile.payamres.Static.av;

public class Async_sentsmssaved extends AsyncTask<item_sentsmssaved, Void , Void> {

    Context context;

    public Async_sentsmssaved(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        Log.d(av.TagAsync, "Start (Async_SentSmsSaved) \n");
    }

    @Override
    protected Void doInBackground(item_sentsmssaved... params) {
        for (item_sentsmssaved p : params) {
            String Localid = p.getLocalid();
            String Smsid = p.getSmsid();
            String Serverid = p.getServerid();
            String isSendToServer = p.getStatus();
            try {
                SendSMS_update update = new SendSMS_update(context);
                update.SendToServer(context, Localid, Smsid, Serverid,isSendToServer);
                Log.d(av.TagAsync, "Async_SentSmsSaved (doInBackground): "+ Localid + " | " + Smsid+ " | " + Serverid );

            } catch (Exception error) {
                Log.e(av.TagAsync, "Async_SentSmsSaved (doInBackground): " + error, null);
            }

        }
        return null;
    }
}
