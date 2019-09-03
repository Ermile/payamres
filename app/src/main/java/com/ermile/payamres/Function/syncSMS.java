package com.ermile.payamres.Function;

import android.content.Context;
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
import com.ermile.payamres.Function.ForegroundServic.ForegroundService;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_queue;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_sentsmssaved;
import com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask.item_smsnewsaved;
import com.ermile.payamres.Function.SaveDataUser.SaveManager;
import com.ermile.payamres.Static.Network.AppContoroler;
import com.ermile.payamres.Static.av;
import com.ermile.payamres.Static.prival;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class syncSMS {
    public void SyncSmsToServer(final Context context){
        final String textJsonDatabaseSMS = new ProducerJSON(context).Producer(context);
        /*Get Number Phone */
        final String number_phone = SaveManager.get(context).get_Number().get(SaveManager.numberPhone);
        if (number_phone != null){
            StringRequest post_NewSMSSending = new StringRequest(Request.Method.POST, av.url_Sync,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.d(av.jsonPost, "ForegroundService: "+response.replace("\n-               ",""));
                                JSONObject mainObject = new JSONObject(response);
                                JSONObject result = mainObject.getJSONObject("result");
                                if (!result.isNull("status")){
                                    SaveManager.get(context).save_Status(result.getBoolean("status"));
                                }

                                JSONObject dashboard = result.getJSONObject("dashboard");
                                /*Set for Nofit Forgrund*/
                                String sendTody,receiveTody,dateTody;
                                if (!dashboard.isNull("day")){
                                    JSONObject day = dashboard.getJSONObject("day");
                                    sendTody = day.getString("send");
                                    receiveTody = day.getString("receive");
                                    SaveManager.get(context).save_Day(receiveTody,sendTody);
                                    /*Set ForgroundServic*/
                                    dateTody = day.getString("date");
                                }

                                if (!dashboard.isNull("week")){
                                    JSONObject week = dashboard.getJSONObject("week");
                                    String week_send = week.getString("send");
                                    String week_receive = week.getString("receive");
                                    SaveManager.get(context).save_week(week_receive,week_send);
                                }

                                if (!dashboard.isNull("month")){
                                    JSONObject month = dashboard.getJSONObject("month");
                                    String month_send = month.getString("send");
                                    String month_receive = month.getString("receive");
                                    SaveManager.get(context).save_month(month_receive,month_send);
                                }


                                if (!dashboard.isNull("total")){
                                    JSONObject total = dashboard.getJSONObject("total");
                                    String all_send = total.getString("send");
                                    String all_receive = total.getString("receive");
                                    SaveManager.get(context).save_all(all_receive,all_send);
                                }






                                /* Update GetSMS */
                                if(!result.isNull("smsnewsaved")){
                                    JSONArray smsNewSaved = result.getJSONArray("smsnewsaved");
                                    for (int i = 0; i < smsNewSaved.length(); i++) {
                                        String localID,smsID,ServerID = null;
                                        JSONObject objectArray =  smsNewSaved.getJSONObject(i);
                                        if (!objectArray.isNull("localid") ||
                                                !objectArray.isNull("smsid")||
                                                !objectArray.isNull("serverid"))
                                        {
                                            localID = objectArray.getString("localid");
                                            smsID = objectArray.getString("smsid");
                                            ServerID = objectArray.getString("serverid");

                                            item_smsnewsaved param_smsnewsaved = new item_smsnewsaved(smsID,localID,ServerID);
                                            Log.i(av.tag_GetSMS, "1- smsnewsaved : "+ localID+" | "+smsID+" | "+ServerID);
                                            new Async_smsnewsaved(context).execute(param_smsnewsaved);
                                        }
                                    }
                                }

                                /* New SMS */
                                if (!result.isNull("queue")){
                                    JSONArray queue = result.getJSONArray("queue");
                                    for (int i = 0; i < queue.length(); i++) {
                                        String toNumber,text,ServerID = null;
                                        JSONObject objectArray =  queue.getJSONObject(i);

                                        if (!objectArray.isNull("togateway") ||
                                                !objectArray.isNull("answertext")||
                                                !objectArray.isNull("id"))
                                        {
                                            toNumber = objectArray.getString("togateway") ;
                                            text = objectArray.getString("answertext");
                                            ServerID = objectArray.getString("id");
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                            String date = simpleDateFormat.format(new Date());
                                            item_queue param_itemQueu = new item_queue(ServerID,"",toNumber,"",text,"","",date,"","","","","","","","","");
                                            Log.i(av.tag_SendSMS, "A 1- (queue) Start Async Save SmsNew To 'Table SendSMS' > "+i+" \n "
                                                    +"ServerID: "+ServerID+" toNumber: "+toNumber+" Massage: "+text.replace("\n"," ")+" | date: "+date);
                                            new Async_queue(context).execute(param_itemQueu);

                                        }
                                    }
                                }else if (!result.isNull("notsent")){
                                    JSONArray queue = result.getJSONArray("notsent");
                                    for (int i = 0; i < queue.length(); i++) {
                                        String toNumber,text,ServerID = null;
                                        JSONObject objectArray =  queue.getJSONObject(i);

                                        if (!objectArray.isNull("togateway") ||
                                                !objectArray.isNull("answertext")||
                                                !objectArray.isNull("id"))
                                        {
                                            toNumber = objectArray.getString("togateway") ;
                                            text = objectArray.getString("answertext");
                                            ServerID = objectArray.getString("id");
                                            item_queue param_itemQueu = new item_queue(ServerID,"",toNumber,"",text,"","","","","","","","","","","","");
                                            Log.i(av.tag_SendSMS, "A 1- (noSent) Start Async Save SmsNew To 'Table SendSMS' > "+i+" \n "
                                                    +"ServerID: "+ServerID+" toNumber: "+toNumber+" Massage: "+text.replace("\n"," "));
                                            new Async_queue(context).execute(param_itemQueu);
                                        }
                                    }
                                }

                                /* Update SendSMS */
                                if (!result.isNull("sentsmssaved")){
                                    JSONArray sentSmsSaved = result.getJSONArray("sentsmssaved");
                                    for (int i = 0; i < sentSmsSaved.length(); i++) {
                                        String smsid,localid,serverid,status_sent;
                                        JSONObject objectArray =  sentSmsSaved.getJSONObject(i);

                                        if (!objectArray.isNull("smsid") ||
                                                !objectArray.isNull("localid") ||
                                                !objectArray.isNull("serverid") ||
                                                !objectArray.isNull("status"))
                                        {
                                            smsid = objectArray.getString("smsid");
                                            localid = objectArray.getString("localid") ;
                                            serverid = objectArray.getString("serverid") ;
                                            status_sent = objectArray.getString("status") ;

                                            item_sentsmssaved param_SentSmsSaved = new item_sentsmssaved(smsid,localid,serverid,"true");
                                            new Async_sentsmssaved(context).execute(param_SentSmsSaved);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(av.pTag , "new sms > error");
                }
            }) {

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("smsappkey", prival.keyapp);
                    header.put("gateway", number_phone);
                    Log.d(av.pTag , "Send Header");
                    return header;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    byte[] body = new byte[0];
                    try {
                        body = textJsonDatabaseSMS.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e(av.iTag, "Unable to gets bytes from JSON", e.fillInStackTrace());
                    }
                    return body;
                }

                @Override
                public String getBodyContentType() {
                    Log.d(av.iTag, "getBodyContentType: "+ textJsonDatabaseSMS);
                    return "application/json";
                }
            };
            AppContoroler.getInstance().addToRequestQueue(post_NewSMSSending);
        }
    }
}
