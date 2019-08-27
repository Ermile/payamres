package com.ermile.payamres.Function.AsyncTask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.ermile.payamres.Function.Database.Update.GetSMS_update;
import com.ermile.payamres.Function.Database.insert.SnedSMS_insert;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_queue;
import com.ermile.payamres.Static.av;

public class Async_queue extends AsyncTask<item_queue, Void , Void> {

    Context context;

    public Async_queue(Context context) {
        this.context = context;
    }


    @Override
    protected void onPreExecute() {
        Log.d(av.TagAsync, "Start (Async_queue) \n");
    }

    @Override
    protected Void doInBackground(item_queue... params) {
        for (item_queue p : params) {
            try {
                String id = p.getId() ;
                String fromnumber = p.getFromnumber() ;
                String togateway = p.getTogateway() ;
                String fromgateway = p.getFromgateway() ;
                String text = p.getText() ;
                String tonumber = p.getTonumber() ;
                String user_id = p.getUser_id() ;
                String date = p.getDate() ;
                String datecreated = p.getDatecreated() ;
                String datemodified = p.getDatemodified() ;
                String uniquecode = p.getUniquecode() ;
                String receivestatus = p.getReceivestatus() ;
                String sendstatus = p.getSendstatus() ;
                String amount = p.getAmount() ;
                String answertext = p.getAnswertext() ;
                String group_id = p.getGroup_id() ;
                String recommend_id = p.getRecommend_id() ;

                SnedSMS_insert insert = new SnedSMS_insert(context);
                insert.insertToSendSMS(context,togateway,text,null,"false","false",id);
                Log.d(av.TagAsync, "Async_queue (doInBackground): "+ togateway + " | " + text + " | " + id );

            } catch (Exception error) {
                Log.e(av.TagAsync, "Async_queue (doInBackground): " + error, null);
            }

        }
        return null;
    }
}
