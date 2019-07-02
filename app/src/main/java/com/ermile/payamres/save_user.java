package com.ermile.payamres;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class save_user extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        Boolean getSMS_servic = prefs.getBoolean("getSMS_servic",false);
        Boolean has_number = prefs.getBoolean("has_number",false);
        String number_phone = prefs.getString("number_phone", null);

    }
}
