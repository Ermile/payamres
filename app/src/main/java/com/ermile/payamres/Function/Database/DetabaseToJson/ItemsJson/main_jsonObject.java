package com.ermile.payamres.Function.Database.DetabaseToJson.ItemsJson;

import java.util.List;

public class main_jsonObject {
    Boolean status;
    detail_jsonObject detail;
    List<smsnew_jsonArray> smsnew;
    List<sentsms_jsonArray> sentsms;
    List<lostSMS_jsonArray> lostsms;

    public main_jsonObject(Boolean status, detail_jsonObject detail) {
        this.status = status;
        this.detail = detail;
        this.smsnew = smsnew;
        this.sentsms = sentsms;
        this.lostsms = lostsms;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public detail_jsonObject getDetail() {
        return detail;
    }

    public void setDetail(detail_jsonObject detail) {
        this.detail = detail;
    }

    public List<smsnew_jsonArray> getSmsnew() {
        return smsnew;
    }

    public void setSmsnew(List<smsnew_jsonArray> smsnew) {
        this.smsnew = smsnew;
    }

    public List<sentsms_jsonArray> getSentsms() {
        return sentsms;
    }

    public void setSentsms(List<sentsms_jsonArray> sentsms) {
        this.sentsms = sentsms;
    }

    public List<lostSMS_jsonArray> getLostsms() {
        return lostsms;
    }

    public void setLostsms(List<lostSMS_jsonArray> lostsms) {
        this.lostsms = lostsms;
    }
}
