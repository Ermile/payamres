package com.ermile.payamres.Function.SaveDataUser;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class save_user extends PreferenceActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Boolean first_open = prefs.getBoolean("first_open",true);
        Boolean getSMS_servic = prefs.getBoolean("getSMS_servic",false);
        Boolean has_number = prefs.getBoolean("has_number",false);
        String number_phone = prefs.getString("number_phone", null);

        String save_As = prefs.getString("save_As", "");
        String save_Ar = prefs.getString("save_Ar", "");

        String save_Ds = prefs.getString("save_Ds", "");
        String save_Dr = prefs.getString("save_Dr", "");

        String save_Ws = prefs.getString("save_Ws", "");
        String save_Wr = prefs.getString("save_Wr", "");

        String save_Ms = prefs.getString("save_Ms", "");
        String save_Mr = prefs.getString("save_Mr", "");

        String save_date = prefs.getString("save_date", "");

    }
}
