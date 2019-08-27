package com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask;

public class item_smsnewsaved {

    String  smsid,
            localid,
            serverid;

    public item_smsnewsaved(String smsid, String localid, String serverid) {
        this.smsid = smsid;
        this.localid = localid;
        this.serverid = serverid;
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
}
