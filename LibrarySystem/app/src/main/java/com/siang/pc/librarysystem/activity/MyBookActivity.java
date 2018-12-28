package com.siang.pc.librarysystem.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.siang.pc.librarysystem.R;
import com.siang.pc.librarysystem.adapter.MyBookListAdapter;
import com.siang.pc.librarysystem.entity.MyBookInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyBookActivity extends AppCompatActivity {
    private List<MyBookInfo> mybookList;                                   // 消息列表
    private TextView tvNumber;
    private Socket socket;
    private String username;
    private MyBookListAdapter adapter;                                      // msgRcyclerview的adapter，设置内容
    private RecyclerView msgRecyclerView;                               // recyclerview块，滚动显示消息

    public MyBookActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybook);
        username = getUsername();
        findViews();

        mybookList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager); // 将msgRecyclerView赋予Listview样式
        adapter = new MyBookListAdapter(mybookList, this);
        msgRecyclerView.setAdapter(adapter);    // 为msgRecyclerView设置一个adapter

        listenSeverMessage();
    }

    //通过findViewById将变量指向对应布局
    private void findViews() {
        msgRecyclerView = (RecyclerView) findViewById(R.id.bookRecycleView);
        tvNumber = (TextView) findViewById(R.id.number);
    }

    public void searchMyBook() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //向服务器端用输出流输出消息
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(("Query_B" + "//" + socket.getLocalPort() + "//3//" + username + "//0//0").getBytes("utf-8"));
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
                    adapter.setSocket(socket);
                    adapter.setUsername(username);
                    searchMyBook();
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

    //获取用户名
    private String getUsername() {
        Bundle bundle = getIntent().getExtras();
        return bundle.getString("username","");
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
            String[] split = data.split("//");
            if (split[0].equals("Book") && split[1].equals("1")) {
                adapter.notifyItemRangeRemoved(0, mybookList.size());
                mybookList.clear();
                if (split[2].equals(username))
                    adapter.setSelf(0);                 //0为是自己  1为是他人
                else
                    adapter.setSelf(1);
                int number = Integer.parseInt(split[3]), j = 4;
                tvNumber.setText(split[3]);
                for (int i = 1; i <= number; i++, j += 5) {
                    MyBookInfo book = new MyBookInfo(split[j], split[j + 1], split[j + 2], split[j + 3], split[j + 4]);
                    mybookList.add(book);
                }
                // 当有新消息时，更新列表最后的位置上的数据可以调用
                adapter.notifyItemInserted(mybookList.size() - 1);
            }
            else if(split[0].equals("Info_Return")) {
                String text = getResources().getString(R.string.Info_Return);
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
                String bookName = split[2];
                int pos = -1;
                for(int i = 0; i < mybookList.size(); i++)
                    if (mybookList.get(i).getName().equals(bookName)) {
                        pos = i;
                        break;
                    }
                mybookList.remove(pos);
                tvNumber.setText(String.valueOf(Integer.parseInt((String)tvNumber.getText()) - 1));
                adapter.notifyItemRemoved(pos);
            }
        }
    }
}