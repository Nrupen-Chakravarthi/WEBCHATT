package com.example.webchat.Model;

public class LoginResponse {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public  LoginResponse(String token, String username){
        this.token = token;
        this.username = username;
    }
}
