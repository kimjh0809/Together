package com.example.login;

/**
 * 사용자 계정 정보 모델 클래스
 */
public class UserAccount {
    private String idToken;
    private String emailId;
    private String pwd;
    private String nickname;

    public UserAccount() {}

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getEmailId() {return emailId;}

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname= nickname;
    }
}
