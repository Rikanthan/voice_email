package com.example.i_email;

public class Sentbox {
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Sentbox(String msg, String receiver, String date, String time) {
        this.msg = msg;
        this.receiver = receiver;
        this.date = date;
        this.time = time;
    }

    String msg;
    String receiver;
    String date;
    String time;

    public Sentbox(){}
}
