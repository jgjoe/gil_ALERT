package com.gildong.gildongE.dto;

import com.gildong.gildongE.model.AuthProvider;

public class UserRegisterRequest {
    private String userName;
    private String loginId;
    private String password;
    private AuthProvider provider;

    public UserRegisterRequest() {}

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getLoginId() {
        return loginId;
    }
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public AuthProvider getProvider() {
        return provider;
    }
    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }
}
