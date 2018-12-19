package com.siang.pc.librarysystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.siang.pc.librarysystem.R;


public class AdminActivity extends AppCompatActivity {

    public AdminActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        findViews();
    }

    //通过findViewById将变量指向对应布局
    private void findViews() {
    }

    //单击管理书籍
    public void onControlBook(View view) {
        Intent intent = new Intent(this, SearchBookActivity.class);
        startActivity(intent);
    }

    //单击新增书籍
    public void onInsertBook(View view) {
        Intent intent = new Intent(this, SearchBookActivity.class);
        startActivity(intent);
    }

    //单击查询书籍
    public void onQueryBook(View view) {
        Intent intent = new Intent(this, SearchBookActivity.class);
        startActivity(intent);
    }

    //单击查询学生
    public void onQueryStudent(View view) {
        Intent intent = new Intent(this, SearchBookActivity.class);
        startActivity(intent);
    }

    //单击我的书籍
    public void onMyBook(View view) {
        Intent intent = new Intent(this, SearchBookActivity.class);
        startActivity(intent);
    }

    //单击我聊天水缸
    public void onChatRoom(View view) {
        Intent intent = new Intent(this, ChatRoomActivity.class);
        startActivity(intent);
    }

}