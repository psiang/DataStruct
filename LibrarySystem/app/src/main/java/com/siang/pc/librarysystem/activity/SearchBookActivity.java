package com.siang.pc.librarysystem.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.siang.pc.librarysystem.R;
import com.siang.pc.librarysystem.adapter.BookListAdapter;
import com.siang.pc.librarysystem.adapter.MessageAdapter;
import com.siang.pc.librarysystem.entity.BookInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SearchBookActivity extends AppCompatActivity {
    private List<BookInfo> bookList;                                   // 消息列表
    private EditText etSearch;
    private Socket socket;
    private String username = "pp";
    private String data = "hallo world";
    private BookListAdapter adapter;                                      // msgRcyclerview的adapter，设置内容
    private RecyclerView msgRecyclerView;                               // recyclerview块，滚动显示消息

    public SearchBookActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_book);
        findViews();

        bookList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager); // 将msgRecyclerView赋予Listview样式
        adapter = new BookListAdapter(bookList);
        msgRecyclerView.setAdapter(adapter);    // 为msgRecyclerView设置一个adapter

        listenSeverMessage();
    }

    //通过findViewById将变量指向对应布局
    private void findViews() {
        etSearch = (EditText) findViewById(R.id.etSearch);
        msgRecyclerView = (RecyclerView) findViewById(R.id.bookRecycleView);
    }

    //单击注册
    public void onSearch(View view) {
        String search = etSearch.getText().toString();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //向服务器端用输出流输出消息
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(("Query_B" + "//" + socket.getLocalPort() + "//" + username + "//" + data).getBytes("utf-8"));
                    //输出流的消息在客户端存在缓冲区等待缓冲区满，只有flush清除缓冲区强制发送出去
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    protected  void onStart() {
        super.onStart();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            Toast toast = Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT);
            toast.show();
            String[] split = ((String) msg.obj).split("//");
            bookList.clear();
            int number = Integer.parseInt(split[2]), j = 3;
            for (int i = 1; i <= number; i++, j += 5) {
                BookInfo book = new BookInfo(split[j], split[j + 1], split[j + 2], split[j + 3], split[j + 4]);
                bookList.add(book);
            }
            // 当有新消息时，更新列表最后的位置上的数据可以调用
            adapter.notifyItemInserted(bookList.size() - 1);
        }
    }
}