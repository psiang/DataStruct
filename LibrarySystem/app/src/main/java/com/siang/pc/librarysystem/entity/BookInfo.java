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

    public BookInfo(String Id, String name, String author, String bookHave, String bookBorrow) {     // 构造函数
        this.Id = Id;
        this.name = name;
        this.author = author;
        this.bookHave = bookHave;
        this.bookBorrow = bookBorrow;
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
