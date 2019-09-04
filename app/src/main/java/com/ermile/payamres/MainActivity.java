package com.ermile.payamres;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ermile.payamres.Function.Database.Delete.GetSMS_delete;
import com.ermile.payamres.Function.Database.Delete.SendSMS_delete;
import com.ermile.payamres.Function.ForegroundServic.ForegroundService;
import com.ermile.payamres.Function.SaveDataUser.SaveManager;
import com.ermile.payamres.Function.Status;
import com.ermile.payamres.Function.syncSMS;
import com.ermile.payamres.Static.av;
import com.ermile.payamres.Static.prival;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

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


    private void setText(){
        txv_versionNumber = findViewById(R.id.txv_versionNumber);
        tv_numberphone = findViewById(R.id.tv_numberphone);
        tv_todayR = findViewById(R.id.tv_todayR);
        tv_todayS = findViewById(R.id.tv_todayS);
        tv_weekR = findViewById(R.id.tv_weekR);
        tv_weekS = findViewById(R.id.tv_weekS);
        tv_monthR = findViewById(R.id.tv_monthR);
        tv_monthS = findViewById(R.id.tv_monthS);
        tv_allR = findViewById(R.id.tv_allR);
        tv_allS = findViewById(R.id.tv_allS);

        status_CheckBox = findViewById(R.id.status_CheckBox);
        GIFs = findViewById(R.id.GIFs);

        /*Set Version number*/
        try {
            tv_numberphone.setText(SaveManager.get(getApplicationContext()).get_Number().get(SaveManager.numberPhone));
            tv_todayR.setText(SaveManager.get(getApplicationContext()).get_Day().get(SaveManager.day_Receive));
            tv_todayS.setText(SaveManager.get(getApplicationContext()).get_Day().get(SaveManager.day_Send));

            tv_weekR.setText(SaveManager.get(getApplicationContext()).get_week().get(SaveManager.week_Receive));
            tv_weekS.setText(SaveManager.get(getApplicationContext()).get_week().get(SaveManager.week_Send));

            tv_monthR.setText(SaveManager.get(getApplicationContext()).get_month().get(SaveManager.month_Receive));
            tv_monthS.setText(SaveManager.get(getApplicationContext()).get_month().get(SaveManager.month_Send));

            tv_allR.setText(SaveManager.get(getApplicationContext()).get_all().get(SaveManager.all_Receive));
            tv_allS.setText(SaveManager.get(getApplicationContext()).get_all().get(SaveManager.all_Send));

            boolean STATUS_SAVED = SaveManager.get(getApplicationContext()).get_Status().get(SaveManager.status_server);
            if (STATUS_SAVED){
                status_CheckBox.setChecked(true);
                servic_smsAI =true;
                ((GifDrawable)GIFs.getDrawable()).start();
            }else {
                status_CheckBox.setChecked(false);
                servic_smsAI = false;
                ((GifDrawable)GIFs.getDrawable()).stop();
            }

            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                versionAPP = pInfo.versionName;
                txv_versionNumber.setText(pInfo.versionName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Log.e(TAG, "setText: ",e );
            }
        }catch (Exception error){
            Log.e(TAG, "setText: ", error);
        }


    }

    public void CheckBox(){
        status_CheckBox = findViewById(R.id.status_CheckBox);
        GIFs = findViewById(R.id.GIFs);
        status_CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasInternetConnection()){
                    if (servic_smsAI){
                        servic_smsAI = false;
                        status_CheckBox.setChecked(false);
                        ((GifDrawable)GIFs.getDrawable()).stop();
                        new Status().sendToServer(getApplicationContext(),servic_smsAI);
                    }else {
                        servic_smsAI = true;
                        ((GifDrawable)GIFs.getDrawable()).start();
                        status_CheckBox.setChecked(true);
                        new Status().sendToServer(getApplicationContext(),servic_smsAI);
                    }
                }else {
                    status_CheckBox.setChecked(servic_smsAI);
                }

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        setText();

    }


    @Override
    protected void onResume() {
        super.onResume();
        setText();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setText();
        CheckBox();

        smsPermission_isOK();
        /*First Open app*/
        String number = SaveManager.get(getApplicationContext()).get_Number().get(SaveManager.numberPhone);
        boolean firstOpen = SaveManager.get(getApplicationContext()).get_Info().get(SaveManager.firstOpen);
        if (number == null && !firstOpen){
            SendSMS_Tester();
            SAVE_NUMBER();
        }else {
            if (hasInternetConnection()){
                setText();
                startService();
            }
        }




        /*Set Direction RTL*/
        Layout_ActivityMain = findViewById(R.id.Layout_ActivityMain);
        ViewCompat.setLayoutDirection(Layout_ActivityMain,ViewCompat.LAYOUT_DIRECTION_RTL);
        /*Refresh*/
        Refresh_json = findViewById(R.id.Refresh_json);
        Refresh_json.setRefreshing(false);
        Refresh_json.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    setText();
                    Refresh_json.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        new SendSMS_delete().delete30DayAgo(getApplicationContext());
        new GetSMS_delete().delete30DayAgo(getApplicationContext());
        Log.d(TAG, "Delete 30 Day Ago ");


    }


    /*check Permission for SMS*/
    public Boolean smsPermission_isOK(){
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
            return false;

        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED )
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {
                            Manifest.permission.READ_PHONE_STATE
                    }, 1);
            Log.i(TAG,"request-user: permission SMS ");
            return false;

        }
        else{
            Log.i(TAG,"permission SMS is true!");
            return true;
        }
    }

    /*Set Number*/
    public void SAVE_NUMBER() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_savenumber, null);
        final EditText edt_number = view.findViewById(R.id.edt_number);
        builder.setView(view);
        builder.setCancelable(false);


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SaveManager.get(getApplicationContext()).save_Number(edt_number.getText().toString());
                Log.i(TAG,""+edt_number.getText().toString());
                finish();
                startActivity(getIntent());
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void SendSMS_Tester(){
        try {
            PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(av.phone_evazzadeh, null, "Payamres "+pInfo.versionName+"\n"+model, null, null);
            SaveManager.get(getApplicationContext()).save_Info(true);
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


    /*Check Internet Connection*/
    private boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
        alertDialog("قطع ارتباط","اینترنت خود را بررسی کنید!","باشه",false);

        return false;
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
                        finish();
                        startActivity(getIntent());
                    }
                });
        builderSingle.setCancelable(Cancelable);
        builderSingle.show();
    }

    public void SnacBar(View view, String Title, String Buttone, final Intent intent,Integer Duration){
        Snackbar snackbar = Snackbar.make(view, Title, Snackbar.LENGTH_INDEFINITE)
                .setAction(Buttone, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(intent);
                    }
                });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.setDuration(Duration);
        snackbar.show();
    }


    public void GetSmsFromDevice(){
        Uri myMessage = Uri.parse("content://sms/");
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(myMessage, new String[] { "_id", "address", "date", "body","read" },null, null, null);
        while (c.moveToNext()){
            String Number = c.getString(c.getColumnIndexOrThrow("address")).toString();
            String ReadStatus = c.getString(c.getColumnIndex("read"));
            String Body = c.getString(c.getColumnIndexOrThrow("body")).toString();

            Log.d("SmsDataBaseDevice", "Number: "+Number+"\n ReadStatus: "+ReadStatus+"\n Body: "+Body);
        }
        c.close();
    }

}
