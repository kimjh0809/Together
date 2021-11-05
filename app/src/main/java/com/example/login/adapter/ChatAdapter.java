package com.example.login.adapter;

import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;


import com.example.login.ChatDTO;
import com.example.login.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {


    private List<ChatDTO> chatList;
    private String name;




    public ChatAdapter(List<ChatDTO> chatData, String name){
        //MainActivity.java에서 받은 데이터들을 저장
        chatList = chatData;
        this.name = name;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nameText;
        public TextView msgText;
        public LinearLayout msgLinear;
        public TextView timeText;


        public View rootView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            timeText=itemView.findViewById(R.id.timeText);
            nameText = itemView.findViewById(R.id.nameText);
            msgText = itemView.findViewById(R.id.msgText);
            msgLinear = itemView.findViewById(R.id.msgLinear);


            rootView = itemView;


            itemView.setEnabled(true);
            itemView.setClickable(true);
        }
    }




    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        //inflation 과정
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);


        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);


        return myViewHolder;
    }


    //각 뷰의 기능 설정
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {


        ChatDTO chat = chatList.get(position);


        holder.nameText.setText(chat.getName());
        holder.nameText.setTextSize(2,18);

        holder.msgText.setText(chat.getMsg());
        holder.timeText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(chat.getDate()));




        if(chat.getName().equals(this.name)){
            //사용자가 저장된 이름과 같을 시 오른쪽으로 정렬
            holder.nameText.setText("");
            holder.nameText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.msgText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            holder.timeText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

            holder.msgLinear.setGravity(Gravity.RIGHT);
        } else {
            //아닐 시 왼쪽 정렬
            holder.nameText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.msgText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            holder.timeText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);

            holder.msgLinear.setGravity(Gravity.LEFT);
        }




    }


    //메시지아이템 갯수세기
    @Override
    public int getItemCount() {
        return chatList == null ? 0: chatList.size();
    }


    //메시지아이템의 추가 및 적용
    public void addChat(ChatDTO chat){
        chatList.add(chat);
        notifyItemInserted(chatList.size()-1);
    }
}
