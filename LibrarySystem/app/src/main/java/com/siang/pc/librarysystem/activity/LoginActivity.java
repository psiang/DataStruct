package com.siang.pc.librarysystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.siang.pc.librarysystem.R;


public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;

    public LoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
    }

    //通过findViewById将变量指向对应布局
    private void findViews() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
    }

    //单击登录
    public void onLogin(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        //密码正确跳转至聊天页面
        if (password.matches("admin")) {
            Intent intent = new Intent(this, AdminActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("username",username);          //传递用户名
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else {
            etPassword.setError(getResources().getString(R.string.passwordError));
            return;
        }
    }

    //单击注册
    public void onRegister(View view) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
    }
}