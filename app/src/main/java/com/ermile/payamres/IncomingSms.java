package com.ermile.payamres;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
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

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class IncomingSms extends BroadcastReceiver {

    DatabaseManager dbm;
    String smsappkey = "e2c998bbb48931f40a0f7d1cba53434f";
    String link_SendToServer = "https://khadije.com/fa/api/v6/smsapp/new";



    public static final String SMS_BUNDLE = "pdus";
    int db = 1;
    int db_puter = 1;
    String getSimSerialNumber = null;


    public void onReceive(final Context context, Intent intent) {

        /*Get Serial Number of SimCart*/
        TelephonyManager telemamanger = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            getSimSerialNumber = telemamanger.getSimSerialNumber();
            Log.i("smsais","\n serial : "+getSimSerialNumber);
        }
        final String brand = Build.BRAND;
        final String model = Build.MODEL;

        Bundle intentExtras = intent.getExtras();
        dbm = new DatabaseManager(context);

        /*Shared Preferences for info user*/
        final SharedPreferences save_user = context.getSharedPreferences("save_user", MODE_PRIVATE);
        SharedPreferences.Editor SaveUser_editor = save_user.edit();
        final Boolean getSMS_servic = save_user.getBoolean("getSMS_servic", false);
        final Boolean has_number = save_user.getBoolean("has_number", false);
        final String number_phone = save_user.getString("number_phone", null);
        if (!getSMS_servic){
            context.startService(new Intent(context, service.class));
            SaveUser_editor.putBoolean("getSMS_servic",true);
            SaveUser_editor.apply();
            Log.i("Timers", "IncomingSms : " + getSMS_servic);
        }

        /** Receive SMS */
        if (intentExtras != null) {
            Log.i("SSSPPP","intentExtras");
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);

            if (has_number && number_phone != null){
                for (int i = 0; i < sms.length; ++i) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                    final String smsFrom = smsMessage.getOriginatingAddress();
                    final String smsText = smsMessage.getMessageBody();
                    final String smsDate = DateFormat.getDateTimeInstance().format(new Date());
                    final String smsMessage_id = ""+smsMessage;
                    final String userdata = ""+smsMessage.getUserData();

                    Log.i("smsais","\n"+smsMessage_id +"\n"+userdata);

                    /*Get Count Data From DataBase*/
                    final int dbCount = dbm.personCount();

                    if (hasInternetConnection(context)){
                        /*Database is Full*/
                        if (dbCount != 0) {
                            db = 1;
                            db_puter = 1;
                            /*Send From DataBase To Server*/
                            Person iperson = new Person();
                            iperson.pNumber = smsFrom;
                            iperson.pMassage = smsText;
                            iperson.pTime = smsDate;
                            dbm.insertPerson(iperson);
                            setCount();
                            Log.i("amin","net ok > full > save data to database | "+dbm.personCount());

                            for (int p = 0 ; p <= dbCount; p++) {
                                /*Sending*/
                                StringRequest post_user_add = new StringRequest(Request.Method.POST, link_SendToServer,
                                        new Response.Listener<String>(){
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject mainObject = new JSONObject(response);
                                                    /*if sending from database is ok > Delete data from database*/
                                                    Boolean ok_send = mainObject.getBoolean("ok");
                                                    if (ok_send){
                                                        Person dperson = new Person();
                                                        dperson.pID = String.valueOf(db);
                                                        boolean del = dbm.deletePerson(dperson);
                                                        db++;
                                                        Log.i("amin","db row is | "+dperson.pID);

                                                        if (del) {
                                                            setCount();
                                                            Log.i("amin","net ok > full > clear database | "+dbm.personCount());
                                                        } else {
                                                            setCount();
                                                            Log.i("amin","net ok > full > NOT clear database | "+dbm.personCount());
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.i("amin","net ok > error > full > save data to database | "+dbm.personCount());
                                        dbm.removeAll();
                                    }
                                })
                                {
                                    @Override
                                    public Map<String, String> getHeaders()  {
                                        HashMap<String, String> headers = new HashMap<>();
                                        headers.put("smsappkey", smsappkey );
                                        headers.put("gateway", number_phone );
                                        setCount();
                                        Log.i("amin","net ok > full > send header | "+dbm.personCount());
                                        return headers;
                                    }
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> posting = new HashMap<>();
                                        final Person vPers = dbm.getPerson(String.valueOf(db_puter));
                                        posting.put("from", vPers.pNumber);
                                        posting.put("text", vPers.pMassage);
                                        posting.put("date", vPers.pTime);
                                        posting.put("brand", brand );
                                        posting.put("model", model );
                                        posting.put("simcart-serial", getSimSerialNumber );
                                        posting.put("smsMessage-id", smsMessage_id );
                                        posting.put("userdata", userdata );
                                        setCount();
                                        db_puter++;
                                        Log.i("amin","net ok > full > send body | "+dbm.personCount());
                                        return posting;
                                    }
                                };AppContoroler.getInstance().addToRequestQueue(post_user_add);
                            }

                        } else {
                            /*Send Direct SMS To Server*/
                            StringRequest post_user_add = new StringRequest(Request.Method.POST, link_SendToServer,
                                    new Response.Listener<String>(){
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject mainObject = new JSONObject(response);
                                                /*if sending from database is ok > Delete data from database*/
                                                Boolean ok_send = mainObject.getBoolean("ok");
                                                if (ok_send){
                                                    Log.i("amin","net ok > empty > Send Direct to server ");
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Person iperson = new Person();
                                    iperson.pNumber = smsFrom;
                                    iperson.pMassage = smsText;
                                    iperson.pTime = smsDate;
                                    dbm.insertPerson(iperson);
                                    setCount();
                                    Log.i("amin","net ok > error > empty > save data to database| "+dbm.personCount());
                                }
                            })
                            {
                                @Override
                                public Map<String, String> getHeaders()  {
                                    HashMap<String, String> headers = new HashMap<>();
                                    headers.put("smsappkey", smsappkey );
                                    headers.put("gateway", number_phone );
                                    setCount();
                                    Log.i("amin","net ok > empty > send header | "+dbm.personCount());
                                    return headers;
                                }
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> posting = new HashMap<>();
                                    posting.put("from", smsFrom);
                                    posting.put("text", smsText);
                                    posting.put("date", smsDate);
                                    posting.put("brand", brand );
                                    posting.put("model", model );
                                    posting.put("simcart-serial", getSimSerialNumber );
                                    posting.put("smsMessage-id", smsMessage_id );
                                    posting.put("userdata", userdata );
                                    setCount();
                                    Log.i("amin","net ok > empty > send body | "+dbm.personCount());
                                    return posting;
                                }
                            };AppContoroler.getInstance().addToRequestQueue(post_user_add);
                        }

                    }else {
                        Person iperson = new Person();
                        iperson.pNumber = smsFrom;
                        iperson.pMassage = smsText;
                        iperson.pTime = smsDate;
                        dbm.insertPerson(iperson);
                        setCount();
                        Log.i("amin","No net > save data to database | "+dbm.personCount());
                    }
                }
            }

        }

    }


    /*check InterNet Connection*/
    public static boolean hasInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected())
        {
            return true;
        }
        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected())
        {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected())
        {
            return true;
        }
        return false;
    }
    /*get count database*/
    public void setCount() {
        int sCount = dbm.personCount();

    }


}