package com.ermile.payamres.Function.DetabaseToJson.ItemsJson;

import java.util.List;

public class main_jsonObject {
    boolean status;
    detail_jsonObject detail;
    List<smsnew_jsonArray> smsNew;
    List<sentsms_jsonArray> smsSent;

    public main_jsonObject(boolean status, detail_jsonObject detail) {
        this.status = status;
        this.detail = detail;
        this.smsNew = smsNew;
        this.smsSent = smsSent;
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

    public List<smsnew_jsonArray> getSmsNew() {
        return smsNew;
    }

    public void setSmsNew(List<smsnew_jsonArray> smsNew) {
        this.smsNew = smsNew;
    }

    public List<sentsms_jsonArray> getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(List<sentsms_jsonArray> smsSent) {
        this.smsSent = smsSent;
    }
}
