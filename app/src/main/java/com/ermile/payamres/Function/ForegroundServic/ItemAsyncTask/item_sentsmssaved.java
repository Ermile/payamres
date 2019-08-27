package com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask;

public class item_sentsmssaved {

    String  smsid,
            localid,
            serverid,
            status;

    public item_sentsmssaved(String smsid, String localid, String serverid, String status) {
        this.smsid = smsid;
        this.localid = localid;
        this.serverid = serverid;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
