package com.ermile.payamres.Function.ForegroundServic;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ermile.payamres.Function.AsyncTask.Async_queue;
import com.ermile.payamres.Function.AsyncTask.Async_sentsmssaved;
import com.ermile.payamres.Function.AsyncTask.Async_smsnewsaved;
import com.ermile.payamres.Function.Database.DetabaseToJson.ProducerJSON;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_queue;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_sentsmssaved;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_smsnewsaved;
import com.ermile.payamres.Function.SaveDataUser.SaveManager;
import com.ermile.payamres.Static.prival;
import com.ermile.payamres.Function.SendSMSToUser;
import com.ermile.payamres.MainActivity;
import com.ermile.payamres.R;
import com.ermile.payamres.Static.Network.AppContoroler;
import com.ermile.payamres.Static.av;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ForegroundService extends Service {
    /*Static Value*/
    private static String TAG = "ForegroundService";
    String link_newSMS = "https://khadije.com/api/v6/smsapp/queue";
    String link_smsIsSent = "https://khadije.com/api/v6/smsapp/sent";
    String id_smsForSend = null;

    /*Value Static Notify*/
    String payamres_string = " پیامرس ";
    String day_send = "قطع ارتباط!";
    String day_receive = "";
    String day_date = "";
    /*Notification Static Value*/
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(day_date)
            .setContentText(day_send+ " ارسال "+" - " + day_receive + " دریافت " )
            .setContentInfo("ارمایل")
            .setSmallIcon(R.drawable.logo_xml);
    NotificationManagerCompat notificationManager ;


    /*Handler 10sec*/
    boolean powerServic = false;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run LastSMSSending ,"+" power Service is : "+powerServic);
            if (powerServic){
                /*Run Send SMS*/
                SyncSmsToServer(getApplicationContext());
                handler.postDelayed(runnable, 60000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        final SharedPreferences save_user = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        day_send = save_user.getString("save_Ds", "");
        day_receive = save_user.getString("save_Dr", "");
        day_date = save_user.getString("save_date", "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*Crate Notification in start*/
        createNotificationChannel();
        /*Intent go to app by toch*/
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        /*get notificationManager*/
        notificationManager = NotificationManagerCompat.from(getApplicationContext());
        /*Set Intent go to app by toch*/
        builder.setContentIntent(pendingIntent);
        notificationManager.notify(100, builder.build());
        startForeground(100, builder.build());

        /*Handler Starter */
        if (!powerServic){
            powerServic = true;
            handler.postDelayed(runnable, 0);
            Log.d(TAG, "handler.postDelayed "+" --> "+powerServic);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*Stop Handler in onDestroy*/
        powerServic = false;
        handler.removeCallbacks(runnable);
        Log.e(TAG,"onDestroy"+" --> "+powerServic);

        Intent goToMain = new Intent(this,MainActivity.class);
        startActivity(goToMain);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
            Log.i(TAG,"createNotificationChannel");
        }
    }


    private void SyncSmsToServer(final Context context){
        final String textJsonDatabaseSMS = new ProducerJSON(context).Producer(context);
        /*Get Number Phone */
        final String number_phone = SaveManager.get(context).get_Number().get(SaveManager.numberPhone);
        if (number_phone != null){
            StringRequest post_NewSMSSending = new StringRequest(Request.Method.POST, av.url_Sync,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d(av.jsonPost, "ForegroundService: "+response.replace("\n-               ",""));
                                JSONObject mainObject = new JSONObject(response);
                                JSONObject result = mainObject.getJSONObject("result");
                                if (!result.isNull("status")){
                                    Boolean status = result.getBoolean("status");
                                    if (status){

                                    }else {

                                    }
                                }

                                JSONObject dashboard = result.getJSONObject("dashboard");
                                /*Set for Nofit Forgrund*/
                                if (!dashboard.isNull("day")){
                                    JSONObject day = dashboard.getJSONObject("day");
                                    String sendTody = day.getString("send");
                                    String receiveTody = day.getString("receive");
                                    SaveManager.get(context).save_Day(receiveTody,sendTody);
                                    /*Set ForgroundServic*/
                                    String dateTody = day.getString("date");
                                    updateNotifForground(dateTody,sendTody,receiveTody);
                                }

                                if (!dashboard.isNull("week")){
                                    JSONObject week = dashboard.getJSONObject("week");
                                    String week_send = week.getString("send");
                                    String week_receive = week.getString("receive");
                                    SaveManager.get(context).save_week(week_receive,week_send);
                                }

                                if (!dashboard.isNull("month")){
                                    JSONObject month = dashboard.getJSONObject("month");
                                    String month_send = month.getString("send");
                                    String month_receive = month.getString("receive");
                                    SaveManager.get(context).save_month(month_receive,month_send);
                                }


                                if (!dashboard.isNull("total")){
                                    JSONObject total = dashboard.getJSONObject("total");
                                    String all_send = total.getString("send");
                                    String all_receive = total.getString("receive");
                                    SaveManager.get(context).save_all(all_receive,all_send);
                                }






                                /* Update GetSMS */
                                if(!result.isNull("smsnewsaved")){
                                    JSONArray smsNewSaved = result.getJSONArray("smsnewsaved");
                                    for (int i = 0; i < smsNewSaved.length(); i++) {
                                        String localID,smsID,ServerID = null;
                                        JSONObject objectArray =  smsNewSaved.getJSONObject(i);
                                        if (!objectArray.isNull("localid") ||
                                                !objectArray.isNull("smsid")||
                                                !objectArray.isNull("serverid"))
                                        {
                                            localID = objectArray.getString("localid");
                                            smsID = objectArray.getString("smsid");
                                            ServerID = objectArray.getString("serverid");

                                            item_smsnewsaved param_smsnewsaved = new item_smsnewsaved(smsID,localID,ServerID);
                                            Log.i(av.tag_GetSMS, "1- smsnewsaved : "+ localID+" | "+smsID+" | "+ServerID);
                                            new Async_smsnewsaved(context).execute(param_smsnewsaved);
                                        }
                                    }
                                }

                                /* New SMS */
                                if (!result.isNull("queue")){
                                    JSONArray queue = result.getJSONArray("queue");
                                    for (int i = 0; i < queue.length(); i++) {
                                        String toNumber,text,ServerID = null;
                                        JSONObject objectArray =  queue.getJSONObject(i);

                                        if (!objectArray.isNull("togateway") ||
                                                !objectArray.isNull("answertext")||
                                                !objectArray.isNull("id"))
                                        {
                                            toNumber = objectArray.getString("togateway") ;
                                            text = objectArray.getString("answertext");
                                            ServerID = objectArray.getString("id");
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                            String date = simpleDateFormat.format(new Date());
                                            item_queue param_itemQueu = new item_queue(ServerID,"",toNumber,"",text,"","",date,"","","","","","","","","");
                                            Log.i(av.tag_SendSMS, "A 1- (queue) Start Async Save SmsNew To 'Table SendSMS' > "+i+" \n "
                                                    +"ServerID: "+ServerID+" toNumber: "+toNumber+" Massage: "+text.replace("\n"," ")+" | date: "+date);
                                            new Async_queue(context).execute(param_itemQueu);

                                        }
                                    }
                                }else if (!result.isNull("notsent")){
                                    JSONArray queue = result.getJSONArray("notsent");
                                    for (int i = 0; i < queue.length(); i++) {
                                        String toNumber,text,ServerID = null;
                                        JSONObject objectArray =  queue.getJSONObject(i);

                                        if (!objectArray.isNull("togateway") ||
                                                !objectArray.isNull("answertext")||
                                                !objectArray.isNull("id"))
                                        {
                                            toNumber = objectArray.getString("togateway") ;
                                            text = objectArray.getString("answertext");
                                            ServerID = objectArray.getString("id");
                                            item_queue param_itemQueu = new item_queue(ServerID,"",toNumber,"",text,"","","","","","","","","","","","");
                                            Log.i(av.tag_SendSMS, "A 1- (noSent) Start Async Save SmsNew To 'Table SendSMS' > "+i+" \n "
                                                    +"ServerID: "+ServerID+" toNumber: "+toNumber+" Massage: "+text.replace("\n"," "));
                                            new Async_queue(context).execute(param_itemQueu);
                                        }
                                    }
                                }

                                /* Update SendSMS */
                                if (!result.isNull("sentsmssaved")){
                                    JSONArray sentSmsSaved = result.getJSONArray("sentsmssaved");
                                    for (int i = 0; i < sentSmsSaved.length(); i++) {
                                        String smsid,localid,serverid,status_sent;
                                        JSONObject objectArray =  sentSmsSaved.getJSONObject(i);

                                        if (!objectArray.isNull("smsid") ||
                                                !objectArray.isNull("localid") ||
                                                !objectArray.isNull("serverid") ||
                                                !objectArray.isNull("status"))
                                        {
                                            smsid = objectArray.getString("smsid");
                                            localid = objectArray.getString("localid") ;
                                            serverid = objectArray.getString("serverid") ;
                                            status_sent = objectArray.getString("status") ;

                                            item_sentsmssaved param_SentSmsSaved = new item_sentsmssaved(smsid,localid,serverid,"true");
                                            new Async_sentsmssaved(context).execute(param_SentSmsSaved);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG , "new sms > error");
                }
            }) {

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("smsappkey", prival.keyapp);
                    header.put("gateway", number_phone);
                    Log.d(TAG , "Send Header");
                    return header;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    byte[] body = new byte[0];
                    try {
                        body = textJsonDatabaseSMS.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e(av.iTag, "Unable to gets bytes from JSON", e.fillInStackTrace());
                    }
                    return body;
                }

                @Override
                public String getBodyContentType() {
                    Log.d(av.iTag, "getBodyContentType: "+ textJsonDatabaseSMS);
                    return "application/json";
                }
            };
            AppContoroler.getInstance().addToRequestQueue(post_NewSMSSending);
        }

        new SendSMSToUser(context).execute("1");
    }





    private void updateNotifForground(String dayDesc,String SendSMS, String ReceiveSMS){
        /*Update Notify Text*/
        builder .setContentTitle(dayDesc)
                .setContentText(payamres_string + SendSMS+ " ارسال "+" - " + ReceiveSMS + " دریافت " )
                .setWhen(Calendar.getInstance().getTimeInMillis() )
        ;
        notificationManager.notify(100, builder.build());
    }


}