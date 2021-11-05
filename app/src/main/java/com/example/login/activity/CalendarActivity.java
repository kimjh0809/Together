package com.example.login.activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.BasicActivity;
import com.example.login.CalendarInfo;
import com.example.login.R;
import com.example.login.adapter.CalendarAdapter;
import com.example.login.decorator.EventDecorator;
import com.example.login.decorator.OneDayDecorator;
import com.example.login.decorator.SaturdayDecorator;
import com.example.login.decorator.SundayDecorator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.login.Util.showToast;

public class CalendarActivity extends BasicActivity {

    private DatabaseReference mDatabase;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private String fname=null;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private MaterialCalendarView materialCalendarView;
    private CalendarDay date1;
    public Button save_Btn;
    public TextView diaryTextView;
    public EditText contextEditText;

    private ArrayAdapter<String> adapter;
    public ArrayList<String> listItem;
    private int year1;
    private int month1;
    private int day1;
    private FirebaseUser user;
    private String roomName;
    private String nickname;
    private List<CalendarInfo> calendarList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mDatabase = FirebaseDatabase.getInstance().getReference("calendar");

        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        nickname=intent.getStringExtra("nickname");
        setToolbarTitle(roomName);
        Log.e("룸네임",""+roomName);


        materialCalendarView=(MaterialCalendarView) findViewById(R.id.calendarView);

        year1=materialCalendarView.getCurrentDate().getYear();
        month1=materialCalendarView.getCurrentDate().getMonth() +1;
        day1=materialCalendarView.getCurrentDate().getDay();
        calendarUploader(year1,month1,day1);

        diaryTextView=findViewById(R.id.diaryTextView);
        save_Btn=findViewById(R.id.save_Btn);


        contextEditText=findViewById(R.id.contextEditText);

        materialCalendarView.addDecorators(
                new SundayDecorator(), new SaturdayDecorator(), oneDayDecorator);

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                calendarUploader(date.getYear(),date.getMonth()+1,date.getDay());
            }
        });


        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                year1 = date.getYear();
                month1= date.getMonth()+ 1;
                day1 = date.getDay();
                date1=date;
                diaryTextView.setVisibility(View.VISIBLE);
                save_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);
                // listView1.setVisibility(View.VISIBLE);
                // listView1.setVisibility(View.VISIBLE);
                diaryTextView.setText(String.format("%d / %d / %d",year1,month1,day1));
                contextEditText.setText("");


                checkDay(year1,month1,day1);
            }
        });


        save_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDiary(fname,year1,month1,day1);
                save_Btn.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.VISIBLE);

                contextEditText.setText("");


            }
        });
    }
    public void calendarUploader(int year, int month, int day){
        mDatabase = FirebaseDatabase.getInstance().getReference("calendar");
        year1 = year;
        month1= month;
        day1 = day;

        fname=year1+"-"+(month1)+"-"+day1;
        for(int i=1;i <=31; i++){
            fname=year1+"-"+(month1)+"-"+i;
            mDatabase.child("check").child(roomName).child(fname).child("cd")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            CalendarDay calendarDay = snapshot.getValue(CalendarDay.class);
                            Log.e("calendar",""+calendarDay);
                            materialCalendarView.addDecorator(new EventDecorator(Color.RED, Collections.singleton(calendarDay)));

                        }

                        @Override
                        public void onCancelled(@NonNull  DatabaseError error) {
                        }
                    });
        }
    }



    public void checkDay(int cYear,int cMonth,int cDay){

        fname=cYear+"-"+(cMonth)+"-"+cDay;//저장할 파일 이름설정
        FileInputStream fis=null;//FileStream fis 변수
        final CalendarInfo calendar = new CalendarInfo();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        try{
            //리사이클러뷰에 어댑터 적용
            recyclerView = findViewById(R.id.calRecycleView);
            recyclerView.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

            calendarList = new ArrayList<>();
            RecyclerView.Adapter adapter2;
            adapter2 = new CalendarAdapter(this,calendarList, nickname);
            recyclerView.setAdapter(adapter2);

            mDatabase = FirebaseDatabase.getInstance().getReference("calendar");

            //데이터들을 추가, 변경, 제거, 이동, 취소
            mDatabase.child(roomName).child(fname).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    //어댑터에 DTO추가
                    CalendarInfo calendarInfo= snapshot.getValue(CalendarInfo.class);
                    ((CalendarAdapter)adapter2).addChat(calendarInfo);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay){
        FileOutputStream fos=null;

        try{
            fos=openFileOutput(readDay,MODE_NO_LOCALIZED_COLLATORS);
            String content="";
            fos.write((content).getBytes());
            fos.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay,int year, int month, int dayOfMonth){
        FileOutputStream fos=null;
/*
//파이어베이서파이어스토어 초기화
FirebaseFirestore db = FirebaseFirestore.getInstance();
// 회원정보에 대한 커스텀 객체!!!!*/

        try{
            mDatabase = FirebaseDatabase.getInstance().getReference("calendar");
            //materialCalendarView.addDecorator(new EventDecorator(Color.RED, Collections.singleton(date1)));
            CalendarInfo calendar = new CalendarInfo(
                    roomName,readDay,nickname, year,month,dayOfMonth,
                    contextEditText.getText().toString(),date1);
            mDatabase.child(roomName).child(readDay).push().setValue(calendar);
            mDatabase.child("check").child(roomName).child(readDay).setValue(calendar);





        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void startToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}