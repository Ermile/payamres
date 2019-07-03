package com.ermile.payamres;

import android.app.Notification;
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
import android.telephony.SmsManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ermile.payamres.network.AppContoroler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForegroundService extends Service {
    String smsappkey = "e2c998bbb48931f40a0f7d1cba53434f";
    String link_LastSMS = "https://khadije.com/api/v6/smsapp/notsent";
    String link_newSMS = "https://khadije.com/api/v6/smsapp/queue";
    String link_smsIsSent = "https://khadije.com/api/v6/smsapp/sent";
    String id_smsForSend = null;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static String TAG = "ForegroundService";

    boolean powerServic = false;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "run LastSMSSending ,"+" power Service is : "+powerServic);
            if (powerServic){
                LastSMSSending(getBaseContext());
                handler.postDelayed(runnable, 10000); //100 ms you should do it 4000
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Payamres is Running..")
                .setContentText("Send: 10")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

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
}