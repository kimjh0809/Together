package com.example.login.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.CalendarInfo;
import com.example.login.R;

import java.util.List;

import static android.app.PendingIntent.getActivity;


public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {


    private List<CalendarInfo> calendarList;
    private String name;
private Activity activity;



    public CalendarAdapter(Activity activity,List<CalendarInfo> chatData, String name){
        //MainActivity.java에서 받은 데이터들을 저장
        calendarList = chatData;
        this.name = name;
        this.activity=activity;

    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nameText;
        public TextView contentsText;




        public View rootView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);


            nameText = itemView.findViewById(R.id.nameText);
            contentsText = itemView.findViewById(R.id.contentsText);
            //msgLinear = itemView.findViewById(R.id.msgLinear);


            rootView = itemView;


            itemView.setEnabled(true);
            itemView.setClickable(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }
    }




    @NonNull
    @Override
    public CalendarAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        //inflation 과정
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar,parent,false);


        MyViewHolder myViewHolder = new MyViewHolder(linearLayout);




        return myViewHolder;
    }


    //각 뷰의 기능 설정
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.MyViewHolder holder, int position) {


        CalendarInfo calendarInfo = calendarList.get(position);


        holder.nameText.setText(calendarInfo.getWriter());
        //holder.nameText.setTextSize(2,18);

        holder.contentsText.setText(calendarInfo.getContent());
      //  holder.timeText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(chat.getDate()));




    }


    //메시지아이템 갯수세기
    @Override
    public int getItemCount() {
        return calendarList == null ? 0: calendarList.size();
    }


    //메시지아이템의 추가 및 적용
    public void addChat(CalendarInfo calendar){
        calendarList.add(calendar);
        notifyItemInserted(calendarList.size()-1);
    }
    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.modifyPost:
                       // myStartActivity(WritePostActivity.class, mDataset.get(position));
                        return true;
                    case R.id.deletePost:
                       // firebaseHelper.storageDelete(mDataset.get(position));

                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }

}
