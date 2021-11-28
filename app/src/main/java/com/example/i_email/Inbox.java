package com.example.i_email;

public class Inbox {
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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



    public Inbox(String msg, String sender, String date, String time,String status) {
        this.msg = msg;
        this.sender = sender;
        this.date = date;
        this.time = time;
        this.status = status;
    }
    String msg;
    String sender;
    String date;
    String time;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;
    public Inbox(){}

}
