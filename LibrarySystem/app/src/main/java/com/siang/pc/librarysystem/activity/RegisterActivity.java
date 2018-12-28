package com.siang.pc.librarysystem.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.siang.pc.librarysystem.R;
import com.siang.pc.librarysystem.entity.BookInfo;
import com.siang.pc.librarysystem.entity.MyBookInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etPassword, etPasswordConfirm;
    Socket socket;

    public RegisterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        listenSeverMessage();
    }

    //通过findViewById将变量指向对应布局
    private void findViews() {
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordConfirm = (EditText) findViewById(R.id.etConfirmPassword);
    }

    //单击注册
    public void onRegister(View view) {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        String password_confirm = etPasswordConfirm.getText().toString();
        //两次密码输入一致
        if (password.equals(password_confirm)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //向服务器端用输出流输出消息
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(("Register_S" + "//" + socket.getLocalPort() + "//0//" + username + "//" + password).getBytes("utf-8"));
                        //输出流的消息在客户端存在缓冲区等待缓冲区满，只有flush清除缓冲区强制发送出去
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else {
            etPasswordConfirm.setError(getResources().getString(R.string.passwordConfirmError));
            return;
        }
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
            String data = ((String) msg.obj);
            String[] split = data.split("//");
            if (split[0].equals("Info_Register")) {
                if (split[1].equals("true")) {
                    String text = getResources().getString(R.string.Info_Register);
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    etUsername.setError(getResources().getString(R.string.usernameError));
                }
            }
        }
    }
}