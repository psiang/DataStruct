package com.siang.pc.librarysystem.entity;

/**
 * Created by siang on 2018/5/20.
 * 消息类：表示发送或接受的消息
 */

public class MyBookInfo {

    private String Id;
    private String name;
    private String author;
    private String startTime;
    private String endTime;

    public MyBookInfo(String Id, String name, String author, String startTime, String endTime) {     // 构造函数
        this.Id = Id;
        this.name = name;
        this.author = author;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
