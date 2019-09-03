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
import com.ermile.payamres.Function.syncSMS;
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
                new syncSMS().SyncSmsToServer(getApplicationContext());
                handler.postDelayed(runnable, 60000);
            }
        }
    };


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








    public void updateNotifForground(String dayDesc,String SendSMS, String ReceiveSMS){
        /*Update Notify Text*/
        builder .setContentTitle(dayDesc)
                .setContentText(payamres_string + SendSMS+ " ارسال "+" - " + ReceiveSMS + " دریافت " )
                .setWhen(Calendar.getInstance().getTimeInMillis() )
        ;
        notificationManager.notify(100, builder.build());
    }


}