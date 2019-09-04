package com.ermile.payamres.Function;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ermile.payamres.Function.SaveDataUser.SaveManager;
import com.ermile.payamres.Static.Network.AppContoroler;
import com.ermile.payamres.Static.av;
import com.ermile.payamres.Static.prival;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Status {

    public void sendToServer(final Context context, final Boolean status){
        final String number = SaveManager.get(context).get_Number().get(SaveManager.numberPhone);
        if (number != null){
            StringRequest post_user_add = new StringRequest(Request.Method.POST, av.link_status,
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
                                        SaveManager.get(context).save_Status(status);
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
                    headers.put("smsappkey", prival.keyapp );
                    headers.put("gateway", number );
                    return headers;
                }
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> posting = new HashMap<>();
                    posting.put("status", Boolean.toString(status));
                    return posting;
                }
            };AppContoroler.getInstance().addToRequestQueue(post_user_add);
        }
    }

}
