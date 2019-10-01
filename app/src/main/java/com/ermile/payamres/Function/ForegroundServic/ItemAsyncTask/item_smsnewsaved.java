package com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask;

public class item_smsnewsaved {

    String  smsid,
            localid,
            md5,
            serverid;

    public item_smsnewsaved(String smsid, String localid, String md5, String serverid) {
        this.smsid = smsid;
        this.localid = localid;
        this.md5 = md5;
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

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }
}
