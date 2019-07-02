package com.ermile.payamres;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class SendSMS extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /*Shared Preferences for info user*/
        final SharedPreferences save_user = context.getSharedPreferences("save_user", MODE_PRIVATE);
        final SharedPreferences.Editor SaveUser_editor = save_user.edit();
        final Boolean on_servic = save_user.getBoolean("on_servic", false);

        if (intent.getAction() !=null) {
            Intent i = new Intent(context, MainActivity.class);
            context.startActivity(i);
        }
    }

}