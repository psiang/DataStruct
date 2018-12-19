package com.siang.pc.librarysystem.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.siang.pc.librarysystem.R;


public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etPasswordConfirm;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
    }

    //通过findViewById将变量指向对应布局
    private void findViews() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etPasswordConfirm);
    }

    //单击注册
    public void onRegister(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String password_confirm = etPasswordConfirm.getText().toString();
        //两次密码输入一致
        if (password.equals(password_confirm)) {
            /*Intent intent = new Intent(this, ChatRoomActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("username",username);          //传递用户名
            intent.putExtras(bundle);
            startActivity(intent);*/
        }
        else {
            etPasswordConfirm.setError(getResources().getString(R.string.passwordConfirmError));
            return;
        }
    }
}