package com.example.login;

import java.io.Serializable;

public class MemberInfo implements Serializable {

    private String name;
    private String phoneNumber;
    private String nickname;
    private String address;
    private String photoUri;
    private String stateMsg;

    public MemberInfo(String name, String phoneNumber, String nickname, String address,String photoUri,String stateMsg){
        this.name=name;
        this.phoneNumber=phoneNumber;
        this.nickname=nickname;
        this.address=address;
        this.photoUri=photoUri;
        this.stateMsg=stateMsg;
    }

    public MemberInfo(String name, String phoneNumber, String nickname, String address,String stateMsg){
        this.name=name;
        this.phoneNumber=phoneNumber;
        this.nickname=nickname;
        this.address=address;
        this.stateMsg=stateMsg;
    }
    public MemberInfo( ){

    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getStateMsg(){
        return this.stateMsg;
    }
    public void setStateMsg(String stateMsg){
        this.stateMsg=stateMsg;
    }

    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }

    public String getnickname(){
        return this.nickname;
    }
    public void setnickname(String nickname){
        this.nickname=nickname;
    }

    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address=address;
    }

    public String getPhotoUri(){
        return this.photoUri;
    }
    public void setPhotoUri(String photoUri){
        this.photoUri=photoUri;
    }
}

