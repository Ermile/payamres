package com.ermile.payamres.Function.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

import com.ermile.payamres.Function.Database.Update.SendSMS_update;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.itemSendDevice;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_IsSendToUser;
import com.ermile.payamres.Static.av;

public class Async_SendToUser extends AsyncTask<item_IsSendToUser, Void , Void> {

    Context context;

    public Async_SendToUser(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        Log.d(av.TagAsync, "Start (Async_queue) \n");
    }

    @Override
    protected Void doInBackground(item_IsSendToUser... params) {
        for (item_IsSendToUser p : params) {
            String Localid = p.getLocalid();
            String Smsid = p.getSmsid();
            String Serverid = p.getServerid();
            String isSendToUser = p.getIsSendToUser();
            SendSMS_update update = new SendSMS_update(context);
            try {
                update.SendToUser(context,Localid,Smsid,Serverid,isSendToUser);
                Log.i(av.tag_SendSMS, "B 8- start Function Update for (Update Table SendSMS 'isSendToUser = true') \n"
                        +"Id: "+Localid+" | Smsid: "+Smsid+" | Serverid: "+Serverid+" | isSendToUser: "+isSendToUser);
            } catch (Exception e) {
                update.SendToUser(context,Localid,Smsid,Serverid,"false");
                Log.e(av.tag_SendSMS, "B 8- ERROR Function Update for (Update Table SendSMS 'isSendToUser = false') \n ERROR > "+e);
            }

        }
        return null;
    }

}