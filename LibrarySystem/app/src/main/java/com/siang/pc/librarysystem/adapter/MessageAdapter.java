package com.siang.pc.librarysystem.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import java.util.List;

import com.siang.pc.librarysystem.R;
import com.siang.pc.librarysystem.entity.ChatMessage;

/**
 * Created by siang on 2018/5/20.
 * 聊天窗口显示信息的Adapter，对象为Message
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<ChatMessage> chatMessageList;       //view的数据来源，Message中定义了消息内容和来源

    // 创建viewholder子类，用于暂存recyclerview选项的view，以便重复利用
    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView leftUsername;
        TextView rightUsername;

        // 构造viewHolder,其中view表示父类的布局，用其获取子项元素
        public ViewHolder(View view) {
            super(view);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);        //R为指向res下layout的java
            rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            leftMsg = (TextView) view.findViewById(R.id.left_message);
            rightMsg = (TextView) view.findViewById(R.id.right_message);
            leftUsername = (TextView) view.findViewById(R.id.left_username);
            rightUsername = (TextView) view.findViewById(R.id.right_username);
        }
    }

    // 构造MessageAdapter，传入消息列表
    public MessageAdapter(List<ChatMessage> listItem) {
        chatMessageList = listItem;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
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
        ChatMessage chatMessage = chatMessageList.get(position);
        if (chatMessage.getType() == ChatMessage.TYPE_RECEIVED) {
            // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(chatMessage.getContent());
            holder.leftUsername.setText(chatMessage.getUsername());
        } else if (chatMessage.getType() == ChatMessage.TYPE_SENT) {
            // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightMsg.setText(chatMessage.getContent());
            holder.rightUsername.setText(chatMessage.getUsername());
        }
    }

    //提供reclclerview选项总数
    @Override
    public int getItemCount() {
        return chatMessageList != null ? chatMessageList.size() : 0;
    }
}
