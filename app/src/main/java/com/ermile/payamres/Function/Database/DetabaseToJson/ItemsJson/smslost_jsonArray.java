package com.ermile.payamres.Function.Database.DetabaseToJson.ItemsJson;

public class smslost_jsonArray {
    String localid,from,text,date,smsid,userdata,MD5;
    String brand,model,simcartSerial;

    public smslost_jsonArray(String localid, String from, String text, String date, String smsid, String userdata, String MD5, String brand, String model, String simcartSerial) {
        this.localid = localid;
        this.from = from;
        this.text = text;
        this.date = date;
        this.smsid = smsid;
        this.userdata = userdata;
        this.MD5 = MD5;
        this.brand = brand;
        this.model = model;
        this.simcartSerial = simcartSerial;
    }

    public String getLocalid() {
        return localid;
    }

    public void setLocalid(String localid) {
        this.localid = localid;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSmsid() {
        return smsid;
    }

    public void setSmsid(String smsid) {
        this.smsid = smsid;
    }

    public String getUserdata() {
        return userdata;
    }

    public void setUserdata(String userdata) {
        this.userdata = userdata;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSimcartSerial() {
        return simcartSerial;
    }

    public void setSimcartSerial(String simcartSerial) {
        this.simcartSerial = simcartSerial;
    }
}
