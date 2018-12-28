package com.siang.pc.librarysystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.siang.pc.librarysystem.R;


public class NormalActivity extends AppCompatActivity {
    TextView tvGreet;

    public NormalActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal);
        findViews();
    }

    //通过findViewById将变量指向对应布局
    private void findViews() {
        tvGreet = (TextView) findViewById(R.id.tvGreet2);
        tvGreet.setText(getUsername());
    }

    //单击查询书籍
    public void onQueryBook(View view) {
        Intent intent = new Intent(this, SearchBookActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username",getUsername());          //传递用户名
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //单击我的书籍
    public void onMyBook(View view) {
        Intent intent = new Intent(this, MyBookActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username",getUsername());          //传递用户名
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //单击聊天水缸
    public void onChatRoom(View view) {
        Intent intent = new Intent(this, ChatRoomActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("username",getUsername());          //传递用户名
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //获取用户名
    private String getUsername() {
        Bundle bundle = getIntent().getExtras();
        return bundle.getString("username","");
    }
}