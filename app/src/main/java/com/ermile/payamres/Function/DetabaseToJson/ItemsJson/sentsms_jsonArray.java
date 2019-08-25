package com.ermile.payamres.Function.DetabaseToJson.ItemsJson;

public class sentsms_jsonArray {
    String localid,smsid,serverid;

    public sentsms_jsonArray(String localid, String smsid, String serverid) {
        this.localid = localid;
        this.smsid = smsid;
        this.serverid = serverid;
    }

    public String getLocalid() {
        return localid;
    }

    public void setLocalid(String localid) {
        this.localid = localid;
    }

    public String getSmsid() {
        return smsid;
    }

    public void setSmsid(String smsid) {
        this.smsid = smsid;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }
}

