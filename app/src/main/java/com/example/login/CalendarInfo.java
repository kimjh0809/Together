package com.example.login;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.io.File;

public class CalendarInfo {
    private String date;
    private String writer;
    private int year;
    private int month;
    private int day;
    private String content;
    private String room;
    private CalendarDay cd;




    public CalendarInfo(String room,String date,String writer,int year, int month, int day, String content, CalendarDay cd){
        this.date=date;
        this.writer=writer;
        this.year=year;
        this.month=month;
        this.day=day;
        this.content=content;
        this.room=room;
        this.cd=cd;

    }
    public CalendarInfo(){

    }
    public String getDate(){
        return this.date;
    }

    public void setDate(){
        this.date=date;
    }
    public String getRoom(){
        return this.room;
    }
    public void setRoom(){
        this.room=room;
    }
    public String getWriter(){
        return this.writer;
    }
    public void setWriter(){
        this.writer=writer;
    }

    public int getYear(){
        return this.year;
    }
    public void setYear(){
        this.year=year;
    }

    public int getMonth(){ return this.month; }
    public void setMonth(){
        this.month=month;
    }

    public int getDay(){
        return this.day;
    }
    public void setDay(){
        this.day=day;
    }

    public String getContent(){
        return this.content;
    }
    public void setContent(){
        this.content=content;
    }

    public CalendarDay getCd() { return this.cd; }
    public void setCd(CalendarDay cd) { this.cd = cd; }
}
