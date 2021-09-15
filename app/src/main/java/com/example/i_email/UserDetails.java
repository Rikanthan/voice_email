package com.example.i_email;

public class UserDetails {
    public UserDetails(String username, String email, String phoneNo, String passCode) {
        this.username = username;
        this.email = email;
        this.phoneNo = phoneNo;
        this.passCode = passCode;
    }

    public UserDetails() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }


    String username;
    String email;
    String phoneNo;

    public String getPassCode() {
        return passCode;
    }

    public void setPassCode(String passCode) {
        this.passCode = passCode;
    }

    String passCode;


}
