package com.siang.pc.librarysystem.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.siang.pc.librarysystem.adapter.MessageAdapter;
import com.siang.pc.librarysystem.entity.ChatMessage;
import com.siang.pc.librarysystem.helper.ChatRecordSQLiteOpenHelper;
import com.siang.pc.librarysystem.R;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.net.Socket;

/**
 * Created by siang on 2018/5/21.
 */

public class ChatRoomActivity extends AppCompatActivity {
    private List<ChatMessage> msgList;                                   // 消息列表
    private EditText etMessage;                                          // 输入框
    private RecyclerView msgRecyclerView;                               // recyclerview块，滚动显示消息
    private MessageAdapter adapter;                                      // msgRcyclerview的adapter，设置内容
    private Socket socket;                                               //Socket类
    private String username;                                             //当前用户名称
    private final static int NOTIFICATION_ID = 0;                     //通知信息ID
    private NotificationManager notificationManager;                   //通知信息管理器
    private ChatRecordSQLiteOpenHelper helper;                           //聊天记录SQLite辅助器

    public static final int ChatMessage_From_Server = 1;      // Message类收到消息的what

    public ChatRoomActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        if (helper == null) {
            helper = new ChatRecordSQLiteOpenHelper(ChatRoomActivity.this);
        }
        msgList = helper.getAllChatMessage();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        username = getUsername();

        findViews();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager); // 将msgRecyclerView赋予Listview样式
        adapter = new MessageAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);    // 为msgRecyclerView设置一个adapter


        listenSeverMessage();
        // 更新列表
        adapter.notifyDataSetChanged();
        // 将RecyclerView定位到最后一行
        msgRecyclerView.scrollToPosition(msgList.size() - 1);

    }

    protected  void onStart() {
        super.onStart();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.close();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //通过findViewById将变量指向对应布局
    private void findViews() {
        etMessage = (EditText) findViewById(R.id.etMessage);
        msgRecyclerView = (RecyclerView) findViewById(R.id.messageRecycleView);
    }

    //获取用户名
    private String getUsername() {
        Bundle bundle = getIntent().getExtras();
        return bundle.getString("username","");
    }

    //连接并监听服务器
    public void listenSeverMessage() {
        final Handler handler = new ChatMsgHandler();   //创建Handler类传递数据给主线程
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

    //点击发送按钮触发
    public void onSentMessage(View view) {
        final String data = etMessage.getText().toString().trim();
        // 清空输入框中的内容
        etMessage.setText(null);
        if (data.isEmpty()) {
            //若输入为空则Toast出不能为空的提示
            Toast toast = Toast.makeText(getApplicationContext(), R.string.inputEmpty, Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //向服务器端用输出流输出消息
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(("Message" + "//" + socket.getLocalPort() + "//" + username + "//" + data).getBytes("utf-8"));
                        //输出流的消息在客户端存在缓冲区等待缓冲区满，只有flush清除缓冲区强制发送出去
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    //创建标题栏菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflater使用options_menu的item项目
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    //设置标题栏菜单选项
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 如果点击了删除全部
            case R.id.deleteMessage:
                //清除消息列表中所有元素
                msgList.clear();
                //更新RecyclerView
                adapter.notifyDataSetChanged();
                helper.deleteAllChatMessage();;
                break;
            case R.id.showOnlineNumber:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //向服务器端用输出流输出消息
                            OutputStream outputStream = socket.getOutputStream();
                            outputStream.write(("OnlineNumber" + "//" + socket.getLocalPort()).getBytes("utf-8"));
                            //输出流的消息在客户端存在缓冲区等待缓冲区满，只有flush清除缓冲区强制发送出去
                            outputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    /**
     * 构建handler子类，将数据传递给主线程UI使用
     * Messege类中，what为自定义类型，obj为传递的数据对象
    */
    private class ChatMsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ChatMessage_From_Server) {
                int localPort = socket.getLocalPort();
                String[] split = ((String) msg.obj).split("//");
                //判断消息来源是否为本身
                //0---消息类型  1---端口号  2---用户名   3--消息内容
                if (split[0].equals("Message")) {
                    if (split[1].equals(localPort + "")) {
                        ChatMessage chatmsg = new ChatMessage(split[2], split[3], ChatMessage.TYPE_SENT);
                        System.out.println("收到自己消息");
                        helper.insertChatMessage(chatmsg);
                        msgList.add(chatmsg);
                    } else {
                        ChatMessage chatmsg = new ChatMessage(split[2], split[3], ChatMessage.TYPE_RECEIVED);
                        System.out.println("收到他人消息");
                        helper.insertChatMessage(chatmsg);
                        msgList.add(chatmsg);

                        //设置通知信
                        // 消息样式
                        Intent intent = new Intent(ChatRoomActivity.this, ChatRoomActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(ChatRoomActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Notification notification = new NotificationCompat.Builder( ChatRoomActivity.this,"default")
                                .setContentTitle(split[2])
                                .setContentText(split[3])
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent)
                                .build();
                        //显示通知信息
                        notificationManager.notify(NOTIFICATION_ID, notification);
                    }

                    System.out.println("消息数目："+msgList.size());
                    // 当有新消息时，更新列表最后的位置上的数据可以调用
                    adapter.notifyItemInserted(msgList.size() - 1);
                    // 将RecyclerView定位到最后一行
                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                }
                else if (split[0].equals("OnlineNumber")) {
                    String text = "当前人数为" + split[1] + "人";
                    Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }
    }
}
