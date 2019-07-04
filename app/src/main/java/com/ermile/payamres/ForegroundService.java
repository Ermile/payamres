package com.ermile.payamres;

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
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ermile.payamres.network.AppContoroler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ForegroundService extends Service {
    /*Static Value*/
    private static String TAG = "ForegroundService";
    String smsappkey = "e2c998bbb48931f40a0f7d1cba53434f";
    String link_dashboard = "https://khadije.com/api/v6/smsapp/dashboard";
    String link_LastSMS = "https://khadije.com/api/v6/smsapp/notsent";
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


    /*Handler 30sec*/
    boolean powerServic = false;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run LastSMSSending ,"+" power Service is : "+powerServic);
            if (powerServic){
                /*Set Value Notify*/
                GetDashbord();
                /*Run Send SMS*/
                LastSMSSending(getBaseContext());
                handler.postDelayed(runnable, 30000);
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

    /*Last SMS for Sending*/
    public void LastSMSSending(final Context context_LastSMSSending){
        /*Get Number Phone */
        final SharedPreferences save_user = context_LastSMSSending.getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final Boolean has_number = save_user.getBoolean("has_number", false);
        final String number_phone = save_user.getString("number_phone", null);
        if (has_number && number_phone != null){
            StringRequest post_LastSMSSending = new StringRequest(Request.Method.POST, link_LastSMS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                /*if sending from database is ok > Delete data from database*/
                                Boolean ok_dashboard = mainObject.getBoolean("ok");
                                if (ok_dashboard) {
                                    if (!mainObject.isNull("result")){
                                        JSONArray result = mainObject.getJSONArray("result");
                                        for (int newM = 0; newM <= result.length(); newM++) {
                                            JSONObject getsms_Forsend = result.getJSONObject(newM);
                                            id_smsForSend = null;
                                            if(!getsms_Forsend.isNull("id")){
                                                id_smsForSend = getsms_Forsend.getString("id");
                                                String smsto = getsms_Forsend.getString("fromnumber");
                                                String sms_text = getsms_Forsend.getString("answertext");

                                                try {
                                                    SmsManager smsManager = SmsManager.getDefault();
                                                    smsManager.sendTextMessage(smsto, null, sms_text, null, null);
                                                    Log.i(TAG , "last sms > ok true > send sms");
                                                    SMS_Sent(id_smsForSend);
                                                    Log.i(TAG ,"id is "+id_smsForSend);

                                                } catch (Exception e) {
                                                    Log.i(TAG ,"No Send last sms"+"\n"+smsto+"\n"+sms_text);
                                                }
                                            }else {
                                                id_smsForSend = null;
                                            }



                                        }
                                    }else {
                                        NewSMSSending(context_LastSMSSending);
                                        Log.i(TAG , "last sms > no sms for send :) > Check new sms");
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG , "last sms > error");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> lastsms_headers = new HashMap<>();
                    lastsms_headers.put("smsappkey", smsappkey);
                    lastsms_headers.put("gateway", number_phone);
                    Log.i(TAG , "Send Header");
                    return lastsms_headers;
                }
            };
            AppContoroler.getInstance().addToRequestQueue(post_LastSMSSending);
        }

    }

    /*New SMS for Sending*/
    public void NewSMSSending(final Context context_NewSMSSending){
        /*Get Number Phone */
        final SharedPreferences save_user = context_NewSMSSending.getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final Boolean has_number = save_user.getBoolean("has_number", false);
        final String number_phone = save_user.getString("number_phone", null);
        if (has_number && number_phone != null){
            StringRequest post_NewSMSSending = new StringRequest(Request.Method.POST, link_newSMS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                /*if sending from database is ok > Delete data from database*/
                                Boolean ok_dashboard = mainObject.getBoolean("ok");
                                if (ok_dashboard) {
                                    if (!mainObject.isNull("result")){
                                        JSONArray result = mainObject.getJSONArray("result");
                                        for (int newM = 0; newM <= result.length(); newM++) {
                                            JSONObject getsms_Forsend = result.getJSONObject(newM);
                                            id_smsForSend = null;
                                            if(!getsms_Forsend.isNull("id")){
                                                id_smsForSend = getsms_Forsend.getString("id");
                                                String smsto = getsms_Forsend.getString("fromnumber");
                                                String sms_text = getsms_Forsend.getString("answertext");
                                                try {
                                                    SmsManager smsManager = SmsManager.getDefault();
                                                    smsManager.sendTextMessage(smsto, null, sms_text, null, null);
                                                    Log.i(TAG , "last sms > ok true > send sms");
                                                    SMS_Sent(id_smsForSend);
                                                    Log.i(TAG ,"id is "+id_smsForSend);

                                                } catch (Exception e) {
                                                    Log.i(TAG ,"No Send");
                                                }
                                            }
                                        }
                                    }else {
                                        Log.i(TAG , "new sms > no sms for send :)");
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG , "new sms > error");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> newsms_headers = new HashMap<>();
                    newsms_headers.put("smsappkey", smsappkey);
                    newsms_headers.put("gateway", number_phone);
                    Log.i(TAG , "Send Header");
                    return newsms_headers;
                }
            };
            AppContoroler.getInstance().addToRequestQueue(post_NewSMSSending);
        }

    }

    /*SMS Sent*/
    public void SMS_Sent(final String id_smsForSend){
        /*Get Number Phone */
        final SharedPreferences save_user = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final Boolean has_number = save_user.getBoolean("has_number", false);
        final String number_phone = save_user.getString("number_phone", null);
        if (has_number && number_phone != null){
            StringRequest post_user_add = new StringRequest(Request.Method.POST, link_smsIsSent,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                /*if sending from database is ok > Delete data from database*/
                                Boolean ok_sent = mainObject.getBoolean("ok");
                                if (ok_sent){
                                    Log.i(TAG ,"SMS Sent | "+id_smsForSend);
                                }else {
                                    Log.i(TAG ,"SMS NOT Sent | "+id_smsForSend);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG , "sms sent > error");
                }
            })
            {
                @Override
                public Map<String, String> getHeaders()  {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("smsappkey", smsappkey );
                    headers.put("gateway", number_phone );
                    Log.i(TAG , "Send Header");
                    return headers;
                }
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> posting = new HashMap<>();
                    posting.put("smsid", id_smsForSend);
                    Log.i(TAG ,"Send Parametr id | "+id_smsForSend);
                    return posting;
                }
            };AppContoroler.getInstance().addToRequestQueue(post_user_add);
        }

    }

    /*Get Dashbord*/
    public void GetDashbord(){
        /*Get Number Phone */
        final SharedPreferences save_user = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final SharedPreferences.Editor SaveUser_editor = save_user.edit();
        final String number_phone = save_user.getString("number_phone", null);
        /*Json*/
        StringRequest post_user_add = new StringRequest(Request.Method.POST, link_dashboard,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject mainObject = new JSONObject(response);
                            /*if sending from database is ok > Delete data from database*/
                            Boolean ok_dashboard = mainObject.getBoolean("ok");
                            if (ok_dashboard){
                                JSONObject result = mainObject.getJSONObject("result");
                                final Boolean status = result.getBoolean("status");

                                JSONObject day = result.getJSONObject("day");
                                if (!day.isNull("send")){
                                    day_send = day.getString("send");
                                    SaveUser_editor.putString("save_Ds",day_send);
                                    SaveUser_editor.apply();
                                }
                                if (!day.isNull("receive")){
                                    day_receive = day.getString("receive");
                                    SaveUser_editor.putString("save_Dr",day_receive);
                                    SaveUser_editor.apply();
                                }
                                if (!day.isNull("date")){
                                    day_date = day.getString("date");
                                    SaveUser_editor.putString("save_date",day_date);
                                    SaveUser_editor.apply();
                                }

                                /*Update Notify Text*/
                                builder .setContentTitle(day_date)
                                        .setContentText(payamres_string + day_send+ " ارسال "+" - " + day_receive + " دریافت " )
                                        .setWhen(Calendar.getInstance().getTimeInMillis() )
                                ;
                                notificationManager.notify(100, builder.build());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                day_send = " قطع ارتباط!";
                /*Update Notify Text*/
                builder .setContentTitle(day_date)
                        .setContentText(day_send);
                notificationManager.notify(100, builder.build());

            }
        })
        {
            @Override
            public Map<String, String> getHeaders()  {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("smsappkey", smsappkey );
                headers.put("gateway", number_phone );
                return headers;
            }
        };AppContoroler.getInstance().addToRequestQueue(post_user_add);
    }
}