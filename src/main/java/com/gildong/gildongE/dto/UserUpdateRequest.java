package com.gildong.gildongE.dto;

public class UserUpdateRequest {
    private String userName;
    private String password;

    public UserUpdateRequest() {}

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}
}
