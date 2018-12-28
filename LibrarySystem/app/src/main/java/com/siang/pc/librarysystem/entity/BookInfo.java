package com.siang.pc.librarysystem.entity;

/**
 * Created by siang on 2018/5/20.
 * 消息类：表示发送或接受的消息
 */

public class BookInfo {

    private String Id;
    private String name;
    private String author;
    private String bookHave;
    private String bookBorrow;
    private int type;               //0借阅 1归还 2不可操作

    public BookInfo(String Id, String name, String author, String bookHave, String bookBorrow, int type) {     // 构造函数
        this.Id = Id;
        this.name = name;
        this.author = author;
        this.bookHave = bookHave;
        this.bookBorrow = bookBorrow;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getBookHave() {
        return bookHave;
    }

    public void setBookHave(String bookHave) {
        this.bookHave = bookHave;
    }

    public String getBookBorrow() {
        return bookBorrow;
    }

    public void setBookBorrow(String bookBorrow) {
        this.bookBorrow = bookBorrow;
    }
}
