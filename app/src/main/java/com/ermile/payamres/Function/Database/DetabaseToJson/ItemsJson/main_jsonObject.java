package com.ermile.payamres.Function.Database.DetabaseToJson.ItemsJson;

import java.util.List;

public class main_jsonObject {
    boolean status;
    detail_jsonObject detail;
    List<smsnew_jsonArray> smsnew;
    List<sentsms_jsonArray> sentsms;

    public main_jsonObject(boolean status, detail_jsonObject detail) {
        this.status = status;
        this.detail = detail;
        this.smsnew = smsnew;
        this.sentsms = sentsms;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
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
}
