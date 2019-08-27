package com.ermile.payamres.Function.ForegroundServic.ItemAsyncTask;

public class item_queue {

    String  id,
            fromnumber,
            togateway,
            fromgateway,
            text,
            tonumber,
            user_id,
            date,
            datecreated,
            datemodified,
            uniquecode,
            receivestatus,
            sendstatus,
            amount,
            answertext,
            group_id,
            recommend_id;


    public item_queue(String id, String fromnumber, String togateway, String fromgateway, String text, String tonumber, String user_id, String date, String datecreated, String datemodified, String uniquecode, String receivestatus, String sendstatus, String amount, String answertext, String group_id, String recommend_id) {
        this.id = id;
        this.fromnumber = fromnumber;
        this.togateway = togateway;
        this.fromgateway = fromgateway;
        this.text = text;
        this.tonumber = tonumber;
        this.user_id = user_id;
        this.date = date;
        this.datecreated = datecreated;
        this.datemodified = datemodified;
        this.uniquecode = uniquecode;
        this.receivestatus = receivestatus;
        this.sendstatus = sendstatus;
        this.amount = amount;
        this.answertext = answertext;
        this.group_id = group_id;
        this.recommend_id = recommend_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromnumber() {
        return fromnumber;
    }

    public void setFromnumber(String fromnumber) {
        this.fromnumber = fromnumber;
    }

    public String getTogateway() {
        return togateway;
    }

    public void setTogateway(String togateway) {
        this.togateway = togateway;
    }

    public String getFromgateway() {
        return fromgateway;
    }

    public void setFromgateway(String fromgateway) {
        this.fromgateway = fromgateway;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTonumber() {
        return tonumber;
    }

    public void setTonumber(String tonumber) {
        this.tonumber = tonumber;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }

    public String getDatemodified() {
        return datemodified;
    }

    public void setDatemodified(String datemodified) {
        this.datemodified = datemodified;
    }

    public String getUniquecode() {
        return uniquecode;
    }

    public void setUniquecode(String uniquecode) {
        this.uniquecode = uniquecode;
    }

    public String getReceivestatus() {
        return receivestatus;
    }

    public void setReceivestatus(String receivestatus) {
        this.receivestatus = receivestatus;
    }

    public String getSendstatus() {
        return sendstatus;
    }

    public void setSendstatus(String sendstatus) {
        this.sendstatus = sendstatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAnswertext() {
        return answertext;
    }

    public void setAnswertext(String answertext) {
        this.answertext = answertext;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getRecommend_id() {
        return recommend_id;
    }

    public void setRecommend_id(String recommend_id) {
        this.recommend_id = recommend_id;
    }
}
