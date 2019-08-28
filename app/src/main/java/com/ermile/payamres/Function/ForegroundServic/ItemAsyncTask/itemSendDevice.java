package com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask;

import android.content.Context;

public class itemSendDevice {
    String localID,Number,Massage,smsID,serverID;

    public itemSendDevice(String localID, String number, String massage, String smsID, String serverID) {
        this.localID = localID;
        Number = number;
        Massage = massage;
        this.smsID = smsID;
        this.serverID = serverID;
    }

    public String getLocalID() {
        return localID;
    }

    public void setLocalID(String localID) {
        this.localID = localID;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getMassage() {
        return Massage;
    }

    public void setMassage(String massage) {
        Massage = massage;
    }

    public String getSmsID() {
        return smsID;
    }

    public void setSmsID(String smsID) {
        this.smsID = smsID;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }
}
