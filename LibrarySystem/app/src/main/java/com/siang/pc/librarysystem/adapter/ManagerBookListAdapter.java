package com.siang.pc.librarysystem.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.siang.pc.librarysystem.R;
import com.siang.pc.librarysystem.entity.BookInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

/**
 * Created by siang on 2018/5/20.
 * 聊天窗口显示信息的Adapter，对象为Message
 */

public class ManagerBookListAdapter extends RecyclerView.Adapter<ManagerBookListAdapter.ViewHolder> {

    private List<BookInfo> bookList;       //view的数据来源
    private Socket socket;
    private Context context;
    private String username;
    private String way = "2";
    private String search = "";

    // 创建viewholder子类，用于暂存recyclerview选项的view，以便重复利用
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView bookName;
        TextView bookAuthor;
        TextView bookId;
        TextView bookHave;
        TextView bookBorrow;
        TextView btAdd;
        TextView btDec;
        EditText etAdd;
        EditText etDec;
        TextView btDelete;

        // 构造viewHolder,其中view表示父类的布局，用其获取子项元素
        ViewHolder(View view) {
            super(view);
            bookName = (TextView) view.findViewById(R.id.book_name);        //R为指向res下layout的java
            bookAuthor = (TextView) view.findViewById(R.id.book_author);
            bookId = (TextView) view.findViewById(R.id.book_id);
            bookHave = (TextView) view.findViewById(R.id.book_have);
            bookBorrow = (TextView) view.findViewById(R.id.book_borrow);
            btAdd = (TextView) view.findViewById(R.id.bt_add);
            btDec = (TextView) view.findViewById(R.id.bt_dec);
            btDelete = (TextView) view.findViewById(R.id.bt_delete);
            etAdd = (EditText) view.findViewById(R.id.ed_add);
            etDec = (EditText) view.findViewById(R.id.ed_dec);
        }
    }

    // 构造MessageAdapter，传入列表
    public ManagerBookListAdapter(List<BookInfo> listItem, Context ct) {
        bookList = listItem;
        context = ct;
    }

    public void setSocket(Socket soc) {
        socket = soc;
    }

    public void setUsername(String user) {
        username = user;
    }

    public void setWay(String w) {
        way = w;
    }

    public void setSearch(String s) {
        search = s;
    }

    /**
     * 创建 ViewHolder 通过LayoutInflater加载 RecycleView 子项的布局
     * 其中parent为RecyclerView,即需要把view放置处的父元素
     * viewtype为View的类型，可以根据这个类型判断去创建不同item的ViewHolder
     * 构建一个inflate，inflate的作用是加载XML文件到view中来操作，view并不会显示
     * 按照第一个参数的样式填充view，第二个参数为父对象，第三个参数为是否挂到父对象上，即add，因为adapter会自动调用挂上去，所以这里不用挂
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_book_list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 当要显示recyclerview时会调用此方法
     * position为要显示的数据的位置，根据position提供message对象给viewholder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
         if (holder.etAdd.getTag() instanceof TextWatcher){
            holder.etAdd.removeTextChangedListener ((TextWatcher) holder.etAdd.getTag());
        }
        if (holder.etDec.getTag() instanceof TextWatcher){
            holder.etDec.removeTextChangedListener ((TextWatcher) holder.etDec.getTag());
        }
        holder.etAdd.setText("");
        holder.etDec.setText("");
        TextWatcher watcherAdd = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        TextWatcher watcherDec = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        holder.etAdd.addTextChangedListener(watcherAdd);
        holder.etAdd.setTag(watcherAdd);
        holder.etDec.addTextChangedListener(watcherDec);
        holder.etDec.setTag(watcherDec);
        final BookInfo book = bookList.get(position);
        holder.bookName.setText(book.getName());
        holder.bookAuthor.setText(book.getAuthor());
        holder.bookId.setText(book.getId());
        holder.bookHave.setText(book.getBookHave());
        holder.bookBorrow.setText(book.getBookBorrow());
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //向服务器端用输出流输出消息
                            OutputStream outputStream = socket.getOutputStream();
                            outputStream.write(("Delete_B" + "//" + socket.getLocalPort() + "//" + "0" + "//" + book.getName()).getBytes("utf-8"));
                            //输出流的消息在客户端存在缓冲区等待缓冲区满，只有flush清除缓冲区强制发送出去
                            outputStream.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        holder.btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.etAdd.getText().toString().equals("")) {
                    String text = context.getResources().getString(R.string.Error_Empty);
                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //向服务器端用输出流输出消息
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(("Insert_B" + "//" + socket.getLocalPort() + "//" + "0" + "//" + book.getName() + "//" + holder.etAdd.getText().toString() + "//" + "0").getBytes("utf-8"));
                                //输出流的消息在客户端存在缓冲区等待缓冲区满，只有flush清除缓冲区强制发送出去
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        holder.btDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.etDec.getText().toString().equals("")) {
                    String text = context.getResources().getString(R.string.Error_Empty);
                    Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    toast.show();
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //向服务器端用输出流输出消息
                                OutputStream outputStream = socket.getOutputStream();
                                outputStream.write(("Insert_B" + "//" + socket.getLocalPort() + "//" + "1" + "//" + book.getName() + "//" + holder.etDec.getText().toString() + "//" + "0").getBytes("utf-8"));
                                //输出流的消息在客户端存在缓冲区等待缓冲区满，只有flush清除缓冲区强制发送出去
                                outputStream.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        if(position == getItemCount()-1 && !way.equals("0")){//已经到达列表的底部
            if (way.equals("1"))
                loadMoreData(search, book.getName());
            else
                loadMoreData(book.getName(), book.getAuthor());

        }
    }

    public void loadMoreData(final String name, final String author) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //向服务器端用输出流输出消息
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(("Query_B_next" + "//" + socket.getLocalPort() + "//" + way + "//" + name + "//" + username + "//" + author).getBytes("utf-8"));
                    //输出流的消息在客户端存在缓冲区等待缓冲区满，只有flush清除缓冲区强制发送出去
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //提供reclclerview选项总数
    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }
}
