package com.ermile.payamres.Function.Database.DetabaseToJson.ItemsJson;

import java.util.List;

public class main_jsonObject {
    Boolean status;
    detail_jsonObject detail;
    List<smsnew_jsonArray> smsnew;
    List<smslost_jsonArray> lost;
    List<sentsms_jsonArray> sentsms;

    public main_jsonObject(Boolean status, detail_jsonObject detail) {
        this.status = status;
        this.detail = detail;
        this.smsnew = smsnew;
        this.lost = lost;
        this.sentsms = sentsms;
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

    public List<smslost_jsonArray> getSmslost() {
        return lost;
    }

    public void setSmslost(List<smslost_jsonArray> smslost) {
        this.lost = smslost;
    }

    public List<sentsms_jsonArray> getSentsms() {
        return sentsms;
    }

    public void setSentsms(List<sentsms_jsonArray> sentsms) {
        this.sentsms = sentsms;
    }
}
