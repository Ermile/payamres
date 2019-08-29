package com.ermile.payamres.Function.AsyncTask;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.util.Log;

import com.ermile.payamres.Function.Database.insert.SnedSMS_insert;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.itemSendDevice;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_IsSendToUser;
import com.ermile.payamres.Static.av;

import java.util.Random;

public class Async_SendDevice extends AsyncTask<itemSendDevice, Void , Void> {

    Context context;

    public Async_SendDevice(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        Log.d(av.TagAsync, "Start (Async_queue) \n");
    }

    @Override
    protected Void doInBackground(itemSendDevice... params) {
        for (itemSendDevice p : params) {
            String localID = p.getLocalID();
            String number = p.getNumber();
            String massage = p.getMassage();
            String smsID = p.getSmsID();
            String serverID = p.getServerID();
            try {
                Random random = new Random();
                int timeSleep = random.nextInt(1000-5000);
                Log.d(av.iTag, "SendDevice SMS > .sleep ("+timeSleep+") sec");
                Thread.sleep(timeSleep);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number, null,  massage, null, null);
                item_IsSendToUser itemIsSendToUser = new item_IsSendToUser(smsID,localID,serverID,"true");
                new Async_SendToUser(context).execute(itemIsSendToUser);
                Log.i(av.TagAsync , "SendSMS ToNumber: "+number +" Massage: "+massage);
            } catch (Exception e) {
                item_IsSendToUser itemIsSendToUser = new item_IsSendToUser(smsID,localID,serverID,"false");
                new Async_SendToUser(context).execute(itemIsSendToUser);
                Log.i(av.TagAsync, "No Send");
            }

        }
        return null;
    }

}
