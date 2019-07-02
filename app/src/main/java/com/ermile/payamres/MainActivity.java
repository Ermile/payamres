package com.ermile.payamres;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Telephony;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {

    public String phone_evazzadeh = "+989357269759";

    String link_LastSMS = "https://khadije.com/api/v6/smsapp/notsent";
    String link_newSMS = "https://khadije.com/api/v6/smsapp/queue";
    String link_smsIsSent = "https://khadije.com/api/v6/smsapp/sent";
    String id_smsForSend = null;

    /*sms App Key For API*/
    String smsappkey = "e2c998bbb48931f40a0f7d1cba53434f";

    /*On & Off Seystem*/
    boolean servic_smsAI = false;

    /*URL JSON*/
    String link_status = "https://khadije.com/api/v6/smsapp/status";
    String link_dashboard = "https://khadije.com/api/v6/smsapp/dashboard";

    /*My value*/
    RelativeLayout Layout_ActivityMain;
    CheckBox status_CheckBox;
    TextView tv_numberphone,tv_todayR,tv_todayS,tv_weekR,tv_weekS,tv_monthR,tv_monthS,tv_allR,tv_allS;
    GifImageView GIFs;
    SwipeRefreshLayout Refresh_json;

    String noNull = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*Get Number Phone */
        final SharedPreferences save_user = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        SharedPreferences.Editor SaveUser_editor = save_user.edit();
        final Boolean getSMS_servic = save_user.getBoolean("getSMS_servic", false);
        if (!getSMS_servic){
            startService(new Intent(this, service.class));
            SaveUser_editor.putBoolean("getSMS_servic",true);
            SaveUser_editor.apply();
            Log.i("Timers", "Destroyd : " + getSMS_servic);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LastSMSSending(this);

        send_testSMS();

        /*Get Number Phone */
        final SharedPreferences save_user = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final SharedPreferences.Editor SaveUser_editor = save_user.edit();
        final Boolean getSMS_servic = save_user.getBoolean("getSMS_servic", false);
        final Boolean has_number = save_user.getBoolean("has_number", false);
        final String number_phone = save_user.getString("number_phone", null);

        SaveUser_editor.putBoolean("getSMS_servic",false);
        SaveUser_editor.apply();
        stopService(new Intent(getApplicationContext(),service.class));
        Log.d("Timers", "servic stoped");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startService(new Intent(getApplicationContext(), service.class));
                SaveUser_editor.putBoolean("getSMS_servic",true);
                SaveUser_editor.apply();
                Log.d("Timers", "Refresh : " + getSMS_servic);
            }
        }, 500);

        if (!has_number || number_phone == null){
            SAVE_NUMBER();
        }



        /*Get Permission for SMS and ID Simcart*/
        smsPermission_isOK();

        /*My Value*/
        Layout_ActivityMain = findViewById(R.id.Layout_ActivityMain);
        status_CheckBox = findViewById(R.id.status_CheckBox);
        tv_numberphone = findViewById(R.id.tv_numberphone);
        tv_todayR = findViewById(R.id.tv_todayR);
        tv_todayS = findViewById(R.id.tv_todayS);
        tv_weekR = findViewById(R.id.tv_weekR);
        tv_weekS = findViewById(R.id.tv_weekS);
        tv_monthR = findViewById(R.id.tv_monthR);
        tv_monthS = findViewById(R.id.tv_monthS);
        tv_allR = findViewById(R.id.tv_allR);
        tv_allS = findViewById(R.id.tv_allS);
        GIFs = findViewById(R.id.GIFs);
        Refresh_json = findViewById(R.id.Refresh_json);
        /*Set Direction RTL*/
        ((GifDrawable)GIFs.getDrawable()).stop();
        ViewCompat.setLayoutDirection(Layout_ActivityMain,ViewCompat.LAYOUT_DIRECTION_RTL);


        Refresh_json.setRefreshing(true);
        Refresh_json.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Set_Title();
            }
        });

        status_CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (servic_smsAI){
                    status_CheckBox.setChecked(false);
                    ((GifDrawable)GIFs.getDrawable()).stop();
                    servic_smsAI = false;
                    SET_STATUS();
                }else {
                    servic_smsAI = true;
                    ((GifDrawable)GIFs.getDrawable()).start();
                    status_CheckBox.setChecked(true);
                    SET_STATUS();
                }
            }
        });

        Set_Title();




    }


    /*check Permission for SMS*/
    public void smsPermission_isOK(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_PHONE_STATE
                    }, 1);
            Log.i("amin","request-user: permission SMS ");

        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {
                            Manifest.permission.READ_PHONE_STATE
                    }, 1);
            Log.i("amin","request-user: permission SMS ");

        }
        else{
            Log.i("amin","permission SMS is true!");
        }
    }

    /*Off and On system*/
    public void SET_STATUS(){
        /*Get Number Phone */
        final SharedPreferences save_user = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final Boolean has_number = save_user.getBoolean("has_number", false);
        final String number_phone = save_user.getString("number_phone", null);
        if (has_number && number_phone != null){
            StringRequest post_user_add = new StringRequest(Request.Method.POST, link_status,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                /*if sending from database is ok > Delete data from database*/
                                Boolean ok_status = mainObject.getBoolean("ok");
                                if (ok_status){
                                    JSONArray msg = mainObject.getJSONArray("msg");
                                    for (int i = 0 ; i <=  msg.length() ; i++){
                                        JSONObject get_msg = msg.getJSONObject(i);
                                        String type = get_msg.getString("type");
                                        String text = get_msg.getString("text");
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> posting = new HashMap<>();
                    posting.put("status", Boolean.toString(servic_smsAI));
                    return posting;
                }
            };AppContoroler.getInstance().addToRequestQueue(post_user_add);
        }
    }

    /*Set Number*/
    public void SAVE_NUMBER() {
        /*Get and Save Number*/
        final SharedPreferences save_user = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final SharedPreferences.Editor SaveUser_editor = save_user.edit();
        final Boolean has_number = save_user.getBoolean("has_number", false);
        final String number_phone = save_user.getString("number_phone", null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_savenumber, null);
        final EditText edt_number = view.findViewById(R.id.edt_number);
        builder.setView(view);
        builder.setCancelable(false);


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveUser_editor.putBoolean("has_number",true);
                SaveUser_editor.putString("number_phone",edt_number.getText().toString());
                Log.i("amin",""+edt_number.getText().toString());
                SaveUser_editor.apply();
                finish();
                startActivity(getIntent());
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*Set title*/
    public void Set_Title(){
        status_CheckBox = findViewById(R.id.status_CheckBox);
        tv_numberphone = findViewById(R.id.tv_numberphone);
        tv_todayR = findViewById(R.id.tv_todayR);
        tv_todayS = findViewById(R.id.tv_todayS);
        tv_weekR = findViewById(R.id.tv_weekR);
        tv_weekS = findViewById(R.id.tv_weekS);
        tv_monthR = findViewById(R.id.tv_monthR);
        tv_monthS = findViewById(R.id.tv_monthS);
        tv_allR = findViewById(R.id.tv_allR);
        tv_allS = findViewById(R.id.tv_allS);
        GIFs = findViewById(R.id.GIFs);
        Refresh_json = findViewById(R.id.Refresh_json);
        /*Get Number Phone */
        final SharedPreferences save_user = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final Boolean has_number = save_user.getBoolean("has_number", false);
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

                                if (status){
                                    ((GifDrawable)GIFs.getDrawable()).start();
                                    status_CheckBox.setChecked(true);
                                    servic_smsAI = status;
                                }else {
                                    ((GifDrawable)GIFs.getDrawable()).stop();
                                    status_CheckBox.setChecked(false);
                                    servic_smsAI = status;
                                }

                                JSONObject day = result.getJSONObject("day");
                                final String day_send = day.getString("send");
                                final String day_receive = day.getString("receive");

                                JSONObject week = result.getJSONObject("week");
                                final String week_send = week.getString("send");
                                final String week_receive = week.getString("receive");

                                JSONObject month = result.getJSONObject("month");
                                final String month_send = month.getString("send");
                                final String month_receive = month.getString("receive");

                                JSONObject total = result.getJSONObject("total");
                                final String total_send = total.getString("send");
                                final String total_receive = total.getString("receive");

                                noNull = total_send+total_receive;
                                if (noNull != null){
                                    Refresh_json.setRefreshing(false);
                                }

                                tv_allS.setText(total_send);
                                tv_allR.setText(total_receive);

                                tv_todayR.setText(day_receive);
                                tv_todayS.setText(day_send);

                                tv_weekR.setText(week_receive);
                                tv_weekS.setText(week_send);

                                tv_monthR.setText(month_receive);
                                tv_monthS.setText(month_send);

                                if (has_number){
                                    tv_numberphone.setText(number_phone);
                                }else {
                                    tv_numberphone.setText("No Number");
                                }


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((GifDrawable)GIFs.getDrawable()).stop();
                Snackbar snackbar = Snackbar.make(Layout_ActivityMain,"Error Connection!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Refresh", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                                startActivity(getIntent());
                            }
                        });
                snackbar.setActionTextColor(Color.WHITE);
                View sbView = snackbar.getView();
                TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.setDuration(999999999);
                snackbar.show();
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
                                            id_smsForSend = getsms_Forsend.getString("id");
                                            String smsto = getsms_Forsend.getString("fromnumber");
                                            String sms_text = getsms_Forsend.getString("answertext");

                                            try {
                                                SmsManager smsManager = SmsManager.getDefault();
                                                smsManager.sendTextMessage(smsto, null, sms_text, null, null);
                                                Log.i("LastSMSSending", "last sms > ok true > send sms");
                                                SMS_Sent(context_LastSMSSending);
                                            } catch (Exception e) {
                                                Log.i("LastSMSSending_error","No Send last sms"+"\n"+smsto+"\n"+sms_text);
                                            }

                                        }
                                    }else {
                                        NewSMSSending(context_LastSMSSending);
                                        Log.i("LastSMSSending", "last sms > no sms for send :) > Check new sms");
                                    }


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("LastSMSSending", "last sms > error");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> lastsms_headers = new HashMap<>();
                    lastsms_headers.put("smsappkey", smsappkey);
                    lastsms_headers.put("gateway", number_phone);
                    Log.i("LastSMSSending", "Send Header");
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
                                            id_smsForSend = getsms_Forsend.getString("id");
                                            String smsto = getsms_Forsend.getString("fromnumber");
                                            String sms_text = getsms_Forsend.getString("answertext");

                                            try {
                                                SmsManager smsManager = SmsManager.getDefault();
                                                smsManager.sendTextMessage(smsto, null, sms_text, null, null);
                                                Log.i("NewSMSSending", "last sms > ok true > send sms");
                                                SMS_Sent(context_NewSMSSending);
                                            } catch (Exception e) {
                                                Log.i("NewSMSSending","No Send");
                                            }
                                        }
                                    }else {
                                        Log.i("NewSMSSending", "new sms > no sms for send :)");
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("amin", "new sms > error");
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> newsms_headers = new HashMap<>();
                    newsms_headers.put("smsappkey", smsappkey);
                    newsms_headers.put("gateway", number_phone);
                    Log.i("NewSMSSending", "Send Header");
                    return newsms_headers;
                }
            };
            AppContoroler.getInstance().addToRequestQueue(post_NewSMSSending);
        }

    }

    /*SMS Sent*/
    public void SMS_Sent(Context context_SMS_Sent){
        /*Get Number Phone */
        final SharedPreferences save_user = context_SMS_Sent.getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
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
                                    Log.i("SMS_Sent","SMS Sent | "+id_smsForSend);
                                }else {
                                    Log.i("SMS_Sent","SMS NOT Sent | "+id_smsForSend);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("SMS_Sent", "sms sent > error");
                }
            })
            {
                @Override
                public Map<String, String> getHeaders()  {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("smsappkey", smsappkey );
                    headers.put("gateway", number_phone );
                    Log.i("SMS_Sent", "Send Header");
                    return headers;
                }
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> posting = new HashMap<>();
                    posting.put("smsid", id_smsForSend);
                    Log.i("SMS_Sent","Send Parametr id | "+id_smsForSend);
                    return posting;
                }
            };AppContoroler.getInstance().addToRequestQueue(post_user_add);
        }

    }

    public void send_testSMS(){
        Log.i("send_testSMS", "started");
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone_evazzadeh, null, "Pyamres v11.0.0 Installed!", null, null);
            Log.i("send_testSMS", "sms sent");

        } catch (Exception e) {
            Log.e("error","send_testSMS no send");
        }
    }

}
