package com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask;

public class item_IsSendToUser {
    String  smsid,
            localid,
            serverid,
            isSendToUser;

    public item_IsSendToUser(String smsid, String localid, String serverid, String isSendToUser) {
        this.smsid = smsid;
        this.localid = localid;
        this.serverid = serverid;
        this.isSendToUser = isSendToUser;
    }

    public String getSmsid() {
        return smsid;
    }

    public void setSmsid(String smsid) {
        this.smsid = smsid;
    }

    public String getLocalid() {
        return localid;
    }

    public void setLocalid(String localid) {
        this.localid = localid;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public String getIsSendToUser() {
        return isSendToUser;
    }

    public void setIsSendToUser(String isSendToUser) {
        this.isSendToUser = isSendToUser;
    }
}
