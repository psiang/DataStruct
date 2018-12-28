package com.siang.pc.librarysystem.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.siang.pc.librarysystem.R;
import com.siang.pc.librarysystem.adapter.BookListAdapter;
import com.siang.pc.librarysystem.adapter.ManagerBookListAdapter;
import com.siang.pc.librarysystem.entity.BookInfo;
import com.siang.pc.librarysystem.entity.MyBookInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ManagerBookActivity extends AppCompatActivity {
    private List<BookInfo> bookList;                                   // 消息列表
    private EditText etSearch;
    private Socket socket;
    private ManagerBookListAdapter adapter;                                      // msgRcyclerview的adapter，设置内容
    private RecyclerView msgRecyclerView;                               // recyclerview块，滚动显示消息
    private RadioGroup rgWay;
    private String check;
    private String username;
    private String way = "2";
    private String searchString = "";

    public ManagerBookActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_book);
        check = getResources().getString(R.string.AuthorofBook);
        username = getUsername();
        findViews();

        bookList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager); // 将msgRecyclerView赋予Listview样式
        adapter = new ManagerBookListAdapter(bookList, this);
        msgRecyclerView.setAdapter(adapter);    // 为msgRecyclerView设置一个adapter

        listenSeverMessage();
    }

    //通过findViewById将变量指向对应布局
    private void findViews() {
        etSearch = (EditText) findViewById(R.id.etSearch);
        msgRecyclerView = (RecyclerView) findViewById(R.id.bookRecycleView);
        rgWay = (RadioGroup) findViewById(R.id.radioGroup);

        rgWay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(i);
                check = radioButton.getText().toString();
            }
        });
    }

    //单击搜索
    public void onSearch(View view) {
        final String search = etSearch.getText().toString();
        if (search.equals("")) {
            String text = getResources().getString(R.string.Error_Empty);
            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (check.equals(getResources().getString(R.string.IDofBook))) {
                            way = "0";
                        }
                        else if (check.equals(getResources().getString(R.string.NameofBook))) {
                            way = "1";
                        }
                        else way = "2";
                        searchString = search;
                        //向服务器端用输出流输出消息
                        OutputStream outputStream = socket.getOutputStream();
                        outputStream.write(("Query_B" + "//" + socket.getLocalPort() + "//" + way + "//" + etSearch.getText().toString() + "//" + username).getBytes("utf-8"));
                        //输出流的消息在客户端存在缓冲区等待缓冲区满，只有flush清除缓冲区强制发送出去
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
                    adapter.setSocket(socket);
                    adapter.setUsername(getUsername());
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
            String[] split = data.split("//");
            if (split[0].equals("Book") && split[1].equals("0")) {
                adapter.notifyItemRangeRemoved(0, bookList.size());
                bookList.clear();
                adapter.setWay(way);
                adapter.setSearch(searchString);
                List<MyBookInfo> myBookList =  new ArrayList<>();
                int number_myborrow = Integer.parseInt(split[2]), j = 3;
                for (int i = 1; i <= number_myborrow; i++, j += 5) {
                    MyBookInfo book = new MyBookInfo(split[j], split[j + 1], split[j + 2], split[j + 3], split[j + 4]);
                    myBookList.add(book);
                }
                int number_book = Integer.parseInt(split[j]);
                j += 1;
                for (int i = 1; i <= number_book && j + 4 < split.length; i++, j += 5) {
                    int type, total, borrow;
                    total = Integer.parseInt(split[j + 3].replace("/",""));
                    borrow = Integer.parseInt(split[j + 4].replace("/",""));
                    if (total == borrow)
                        type = 2;
                    else
                        type = 0;
                    for (int k = 0; k < myBookList.size(); k++)
                        if (myBookList.get(k).getId().equals(split[j])) {
                            type = 1;
                            break;
                        }
                    BookInfo book = new BookInfo(split[j], split[j + 1], split[j + 2], split[j + 3].replace("/",""), split[j + 4].replace("/",""), type);
                    bookList.add(book);
                }
                // 当有新消息时，更新列表最后的位置上的数据可以调用
                adapter.notifyItemInserted(bookList.size() - 1);
            }
            else if (split[0].equals("Book_next") && split[1].equals("0")) {
                System.out.println(data);
                List<MyBookInfo> myBookList =  new ArrayList<>();
                int number_myborrow = Integer.parseInt(split[2]), j = 3;
                for (int i = 1; i <= number_myborrow; i++, j += 5) {
                    MyBookInfo book = new MyBookInfo(split[j], split[j + 1], split[j + 2], split[j + 3], split[j + 4]);
                    myBookList.add(book);
                }
                int number_book = Integer.parseInt(split[j]);
                j += 1;
                for (int i = 1; i <= number_book && j + 4 < split.length; i++, j += 5) {
                    int type, total, borrow;
                    total = Integer.parseInt(split[j + 3].replace("/",""));
                    borrow = Integer.parseInt(split[j + 4].replace("/",""));
                    if (total == borrow)
                        type = 2;
                    else
                        type = 0;
                    for (int k = 0; k < myBookList.size(); k++)
                        if (myBookList.get(k).getId().equals(split[j])) {
                            type = 1;
                            break;
                        }
                    BookInfo book = new BookInfo(split[j], split[j + 1], split[j + 2], split[j + 3].replace("/",""), split[j + 4].replace("/",""), type);
                    bookList.add(book);
                }
                // 当有新消息时，更新列表最后的位置上的数据可以调用
                adapter.notifyItemInserted(bookList.size() - 1);
            }
            else if(split[0].equals("Info_Insert") && split[1].equals("0")) {
                String text = getResources().getString(R.string.Info_Insert0);
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
                String bookName = split[2];
                int pos = -1;
                for(int i = 0; i < bookList.size(); i++)
                    if (bookList.get(i).getName().equals(bookName)) {
                        pos = i;
                        break;
                    }
                int have_num = Integer.parseInt(bookList.get(pos).getBookHave()) + Integer.parseInt(split[3]);
                bookList.get(pos).setBookHave(String.valueOf(have_num));
                adapter.notifyItemChanged(pos);
            }
            else if (split[0].equals("Error_Query")) {
                adapter.notifyItemRangeRemoved(0, bookList.size());
                bookList.clear();
                String text = getResources().getString(R.string.Error_Query);
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
            else if (split[0].equals("Error_Query_next")) {
                String text = getResources().getString(R.string.Error_Query_next);
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
            else if(split[0].equals("Info_Insert") && split[1].equals("1")) {
                String text = getResources().getString(R.string.Info_Insert1);
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
                String bookName = split[2];
                int pos = -1;
                for(int i = 0; i < bookList.size(); i++)
                    if (bookList.get(i).getName().equals(bookName)) {
                        pos = i;
                        break;
                    }
                int have_num = Integer.parseInt(bookList.get(pos).getBookHave()) - Integer.parseInt(split[3]);
                bookList.get(pos).setBookHave(String.valueOf(have_num));
                adapter.notifyItemChanged(pos);
            }
            else if (split[0].equals("Error_Insert1")) {
                String text = getResources().getString(R.string.Error_Insert1);
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
            else if (split[0].equals("Error_DeleteB")) {
                String text = getResources().getString(R.string.Error_DeleteB);
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
            }
            else if (split[0].equals("Info_DeleteB")) {
                String text = getResources().getString(R.string.Info_DeleteB);
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.show();
                String bookName = split[2];
                int pos = -1;
                for(int i = 0; i < bookList.size(); i++)
                    if (bookList.get(i).getName().equals(bookName)) {
                        pos = i;
                        break;
                    }
                bookList.remove(pos);
                adapter.notifyItemRemoved(pos);
            }
        }
    }

    //获取用户名
    private String getUsername() {
        Bundle bundle = getIntent().getExtras();
        return bundle.getString("username","");
    }
}