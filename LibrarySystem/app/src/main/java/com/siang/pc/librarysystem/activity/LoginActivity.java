package com.siang.pc.librarysystem.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.siang.pc.librarysystem.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    Socket socket;
    Intent intentVIP;
    Intent intent;

    public LoginActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();
        intentVIP = new Intent(this, AdminActivity.class);
        intent = new Intent(this, NormalActivity.class);
        listenSeverMessage();
    }

    //通过findViewById将变量指向对应布局
    private void findViews() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
    }

    //单击登录
    public void onLogin(View view) {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //向服务器端用输出流输出消息
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(("Login_S" + "//" + socket.getLocalPort() + "//0//" + username + "//" + password + "//0").getBytes("utf-8"));
                    //输出流的消息在客户端存在缓冲区等待缓冲区满，只有flush清除缓冲区强制发送出去
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //单击注册
    public void onRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //单击重置密码
    public void onChangePassword(View view) {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    //连接并监听服务器
    public void listenSeverMessage() {
        final Handler handler = new SeverMsgHandler();   //创建Handler类传递数据给主线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //socket构造函数中会先尝试连接，未连接则阻塞
                    System.out.println("尝试连接。。");
                    socket = new Socket("140.143.209.173", 2222);
                    System.out.println("连接成功！");
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    //inputStream.read()在文件结束返回-1，未收到消息为阻塞状态，所以当未收到服务器消息时一直阻塞在此
                    while ((len = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, len);
                        // 将收到的数据发到主线程中
                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = data;
                        handler.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 构建handler子类，将数据传递给主线程UI使用
     * Messege类中，what为自定义类型，obj为传递的数据对象
     */
    private class SeverMsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final String username = etUsername.getText().toString();
            String data = ((String) msg.obj);
            String[] split = data.split("//");
            if (split[0].equals("Info_Login")) {
                //密码正确跳转至聊天页面
                if (split[1].matches("true")) {
                    if (username.equals("root") || username.equals("admin")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("username",username);          //传递用户名
                        intentVIP.putExtras(bundle);
                        startActivity(intentVIP);
                    }
                    else{
                        Bundle bundle = new Bundle();
                        bundle.putString("username",username);          //传递用户名
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                else {
                    etPassword.setError(getResources().getString(R.string.passwordError));
                    return;
                }
            }
        }
    }
}