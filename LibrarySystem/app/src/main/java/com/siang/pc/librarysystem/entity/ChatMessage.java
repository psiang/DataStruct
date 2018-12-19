package com.siang.pc.librarysystem.entity;

/**
 * Created by siang on 2018/5/20.
 * 消息类：表示发送或接受的消息
 */

public class ChatMessage {
    public static final int TYPE_RECEIVED = 0;  // 接收消息
    public static final int TYPE_SENT = 1;      // 发送消息

    private String username;                       // 消息用户
    private String content;                       // 消息内容
    private int type;                             // 消息来源

    public ChatMessage(String username, String content, int type) {     // 构造函数
        this.username = username;
        this.content = content;
        this.type = type;
    }

    public static int getTypeReceived() {
        return TYPE_RECEIVED;
    }

    public static int getTypeSent() {
        return TYPE_SENT;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
