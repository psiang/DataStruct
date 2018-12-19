package com.siang.pc.librarysystem.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.siang.pc.librarysystem.R;
import com.siang.pc.librarysystem.entity.BookInfo;

import java.util.List;

/**
 * Created by siang on 2018/5/20.
 * 聊天窗口显示信息的Adapter，对象为Message
 */

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

    private List<BookInfo> bookList;       //view的数据来源

    // 创建viewholder子类，用于暂存recyclerview选项的view，以便重复利用
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView bookName;
        TextView bookAuthor;
        TextView bookId;
        TextView bookHave;
        TextView bookBorrow;

        // 构造viewHolder,其中view表示父类的布局，用其获取子项元素
        public ViewHolder(View view) {
            super(view);
            bookName = (TextView) view.findViewById(R.id.book_name);        //R为指向res下layout的java
            bookAuthor = (TextView) view.findViewById(R.id.book_author);        //R为指向res下layout的java
            bookId = (TextView) view.findViewById(R.id.book_id);        //R为指向res下layout的java
            bookHave = (TextView) view.findViewById(R.id.book_have);        //R为指向res下layout的java
            bookBorrow = (TextView) view.findViewById(R.id.book_borrow);        //R为指向res下layout的java
        }
    }

    // 构造MessageAdapter，传入列表
    public BookListAdapter(List<BookInfo> listItem) {
        bookList = listItem;
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
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item, parent, false);
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookInfo book = bookList.get(position);
        holder.bookName.setText(book.getName());
        holder.bookAuthor.setText(book.getAuthor());
        holder.bookId.setText(book.getId());
        holder.bookHave.setText(book.getBookHave());
        holder.bookBorrow.setText(book.getBookBorrow());
    }

    //提供reclclerview选项总数
    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }
}
