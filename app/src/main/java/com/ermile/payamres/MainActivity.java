package com.ermile.payamres;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
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
import com.ermile.payamres.CrateJSON.json_crated;
import com.ermile.payamres.item.arraySchool;
import com.ermile.payamres.item.school;
import com.ermile.payamres.item.techer;
import com.ermile.payamres.network.AppContoroler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {
    String TAG = "MainActivity";
    String model = Build.MODEL;

    /*sms App Key For API*/
    String smsappkey = prival.keyapp;
    String versionAPP= null;

    /*On & Off Seystem*/
    boolean servic_smsAI = false;

    /*My value*/
    RelativeLayout Layout_ActivityMain;
    CheckBox status_CheckBox;
    TextView tv_numberphone,tv_todayR,tv_todayS,tv_weekR,tv_weekS,tv_monthR,tv_monthS,tv_allR,tv_allS,txv_versionNumber;
    GifImageView GIFs;
    SwipeRefreshLayout Refresh_json;

    String noNull = null;
    String day_send ,day_receive ,week_send , week_receive ,month_send , month_receive ,total_send ,total_receive;


    @Override
    protected void onStart() {
        super.onStart();
        /*Set Version number*/
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            versionAPP = pInfo.versionName;
            final SharedPreferences save_user = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
            day_send = save_user.getString("save_Ds", "");
            day_receive = save_user.getString("save_Dr", "");

            week_send = save_user.getString("save_Ws", "");
            week_receive = save_user.getString("save_Wr", "");

            month_send = save_user.getString("save_Ms", "");
            month_receive = save_user.getString("save_Mr", "");

            total_send = save_user.getString("save_As", "");
            total_receive = save_user.getString("save_Ar", "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionAPP = "last version";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        json_crated JsonCrated = new json_crated(getApplicationContext());
        String JsonDatabase = JsonCrated.JsonApp(getApplicationContext());

        alertDialog("Json: ",JsonDatabase,"ok",false);


        /*techer testgson = new techer("amin","09195191387");
        school schoolGson = new school("MalekZadeh","Mele",testgson);
        List<arraySchool> arraySchools = new ArrayList<>();
        for (int i = 0; i <100; i++) {
            arraySchools.add(new arraySchool(i + "", "amin " + i));
            schoolGson.setArraySchoolList(arraySchools);
        }
        Gson gson = new Gson();
        Log.i("DatabaseToJSON", ""+gson.toJson(schoolGson));*/


        /*Get save_user*/
        final SharedPreferences save_user = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final SharedPreferences.Editor SaveUser_editor = save_user.edit();
        final boolean first_open = save_user.getBoolean("first_open", true);
        final boolean has_number = save_user.getBoolean("has_number", false);
        /*Get Number Phone */
        final String number_phone = save_user.getString("number_phone", null);

        /*First Open app*/
        if (first_open && has_number){
            SaveUser_editor.putBoolean("first_open",false);
            SaveUser_editor.apply();
            SendSMS_Tester();
        }
        if (!has_number || number_phone == null){
            SAVE_NUMBER();
        }
        if (has_number){
            /*Start Services*/
            startService();
        }



        /*Get Permission for SMS and ID Simcart*/
        smsPermission_isOK();

        /*My Value*/
        Layout_ActivityMain = findViewById(R.id.Layout_ActivityMain);
        status_CheckBox = findViewById(R.id.status_CheckBox);
        txv_versionNumber = findViewById(R.id.txv_versionNumber);
        txv_versionNumber.setText(versionAPP);
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
        /*Set Version number*/
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            txv_versionNumber.setText(pInfo.versionName);
            versionAPP = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            txv_versionNumber.setText("12.0.0");
        }
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
            Log.i(TAG,"request-user: permission SMS ");

        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {
                            Manifest.permission.READ_PHONE_STATE
                    }, 1);
            Log.i(TAG,"request-user: permission SMS ");

        }
        else{
            Log.i(TAG,"permission SMS is true!");
        }
    }

    /*Off and On system*/
    public void SET_STATUS(){
        /*Get Number Phone */
        final SharedPreferences save_userSHP = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final Boolean has_number = save_userSHP.getBoolean("has_number", false);
        final String number_phone = save_userSHP.getString("number_phone", null);
        if (has_number && number_phone != null){
            StringRequest post_user_add = new StringRequest(Request.Method.POST, save_user.link_status,
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
                Log.i(TAG,""+edt_number.getText().toString());
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
        final SharedPreferences save_userSHP = getApplicationContext().getSharedPreferences("save_user", MODE_PRIVATE);
        final SharedPreferences.Editor SaveUser_editor = save_userSHP.edit();
        final Boolean has_number = save_userSHP.getBoolean("has_number", false);
        final String number_phone = save_userSHP.getString("number_phone", null);
        /*Json*/
        StringRequest post_user_add = new StringRequest(Request.Method.POST, save_user.link_dashboard,
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
                                day_send = day.getString("send");
                                day_receive = day.getString("receive");

                                JSONObject week = result.getJSONObject("week");
                                week_send = week.getString("send");
                                week_receive = week.getString("receive");

                                JSONObject month = result.getJSONObject("month");
                                month_send = month.getString("send");
                                month_receive = month.getString("receive");

                                JSONObject total = result.getJSONObject("total");
                                total_send = total.getString("send");
                                total_receive = total.getString("receive");

                                /*Save Dashboard*/
                                SaveUser_editor.putString("save_Ds",day_send);
                                SaveUser_editor.putString("save_Dr",day_receive);
                                /*Week*/
                                SaveUser_editor.putString("save_Ws",week_send);
                                SaveUser_editor.putString("save_Wr",week_receive);
                                /*Month*/
                                SaveUser_editor.putString("save_Ms",month_send);
                                SaveUser_editor.putString("save_Mr",month_receive);
                                /*All*/
                                SaveUser_editor.putString("save_As",total_send);
                                SaveUser_editor.putString("save_Ar",total_receive);
                                SaveUser_editor.apply();

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
                if (has_number){
                    tv_numberphone.setText(number_phone);
                }else {
                    tv_numberphone.setText("No Number");
                }
                tv_allS.setText(total_send);
                tv_allR.setText(total_receive);

                tv_todayR.setText(day_receive);
                tv_todayS.setText(day_send);

                tv_weekR.setText(week_receive);
                tv_weekS.setText(week_send);

                tv_monthR.setText(month_receive);
                tv_monthS.setText(month_send);
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

    private void SendSMS_Tester(){
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(save_user.phone_evazzadeh, null, "Payamres "+pInfo.versionName+"\n"+model, null, null);
            Log.i(TAG , "SendSMS_Tester for phone_evazzadeh");
        } catch (Exception e) {
            Log.i(TAG, "No Send");
        }
    }


    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");

        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }


    /** Oder Method (No Used)*/
    private void alertDialog(String title, final String desc, String btnTitle, boolean Cancelable){
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        /*Title*/
        builderSingle.setTitle(title);
        /*Message*/
        builderSingle.setMessage(desc);
        /*Button*/
        builderSingle.setPositiveButton(btnTitle,
                /*Open Url*/
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_TEXT,desc);
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "The title");
                        startActivity(Intent.createChooser(shareIntent, "Share..."));
                    }
                });
        builderSingle.setCancelable(Cancelable);
        builderSingle.show();
    }

}
