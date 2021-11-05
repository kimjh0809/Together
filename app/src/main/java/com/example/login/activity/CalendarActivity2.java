package com.example.login.activity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.login.CalendarInfo;
import com.example.login.R;
import com.example.login.decorator.EventDecorator;
import com.example.login.decorator.OneDayDecorator;
import com.example.login.decorator.SaturdayDecorator;
import com.example.login.decorator.SundayDecorator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class CalendarActivity2 extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private final OneDayDecorator oneDayDecorator = new OneDayDecorator();

    private String fname=null;
    public String str=null;
    private MaterialCalendarView materialCalendarView;
    private CalendarDay date1;
    public Button save_Btn;
    public TextView diaryTextView,textView3;
    public EditText contextEditText;
    ListView listView1;
    private ArrayAdapter<String> adapter;
    public ArrayList<String> listItem;
    private int year1;
    private int month1;
    private int day1;
    private FirebaseUser user;
    private String roomName;
    private String nickname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar2);


        mDatabase = FirebaseDatabase.getInstance().getReference("calendar");

        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        roomName = intent.getStringExtra("nickname");

        materialCalendarView=(MaterialCalendarView) findViewById(R.id.calendarView);
        diaryTextView=findViewById(R.id.diaryTextView);
        save_Btn=findViewById(R.id.save_Btn);
        listView1 = findViewById(R.id.listView1);
        listItem = new ArrayList<String>();
        textView3=findViewById(R.id.textView3);
        contextEditText=findViewById(R.id.contextEditText);

        adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,listItem){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
// Get the Item from ListView
                View view = super.getView(position, convertView, parent);

// Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);

// Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.BLACK);

// Generate ListView Item using TextView
                return view;
            }};


        listView1.setAdapter(adapter);
        listView1.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        materialCalendarView.addDecorators(
                new SundayDecorator(), new SaturdayDecorator(), oneDayDecorator);



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
                listView1.setVisibility(View.VISIBLE);
                listView1.setVisibility(View.VISIBLE);
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
                listView1.setVisibility(View.VISIBLE);
                listItem.add(contextEditText.getText().toString());
                adapter.notifyDataSetChanged(); // 변경되었음을 어답터에 알려준다.
                contextEditText.setText("");
            }
        });
    }
    public void calendarUploader(){

    }

    public void showInputBox(String oldItem, final int index){
        final Dialog dialog=new Dialog(CalendarActivity2.this);
        dialog.setTitle("일정");
        dialog.setContentView(R.layout.input_box);
        TextView txtMessage=(TextView)dialog.findViewById(R.id.txtmessage);
        txtMessage.setText("내용을 입력하세요");
        txtMessage.setTextColor(Color.parseColor("#ff2222"));
        final EditText editText=(EditText)dialog.findViewById(R.id.txtinput);
        editText.setText(oldItem);
        Button btd=(Button)dialog.findViewById(R.id.btdone);
        Button btr=(Button)dialog.findViewById(R.id.btreomove);

        btr.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listView1.clearChoices();
                listItem.remove(index);
                adapter.notifyDataSetChanged();
                removeDiary(fname);
                dialog.dismiss();
            }
        });

        btd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listItem.set(index, editText.getText().toString());
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();

    }



    public void checkDay(int cYear,int cMonth,int cDay){

        fname=cYear+"-"+(cMonth)+""+"-"+cDay;//저장할 파일 이름설정
        FileInputStream fis=null;//FileStream fis 변수
        final CalendarInfo calendar = new CalendarInfo();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        try{
            mDatabase.child(roomName).child(fname)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            CalendarInfo calendarInfo = snapshot.getValue(CalendarInfo.class);


                            if(calendarInfo!=null){

                                String str= calendarInfo.getContent();
                                materialCalendarView.
                                        addDecorator(new EventDecorator(Color.RED, Collections.singleton(calendarInfo.getCd())));

                                contextEditText.setVisibility(View.VISIBLE);
                                listView1.setVisibility(View.VISIBLE);
                                listItem.add(contextEditText.getText().toString());

                                save_Btn.setVisibility(View.VISIBLE);
                            }else{


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });



            listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showInputBox(listItem.get(position),position);
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
            materialCalendarView.addDecorator(new EventDecorator(Color.RED, Collections.singleton(date1)));
            CalendarInfo calendar = new CalendarInfo(
                    roomName,readDay,user.getUid(), year,month,dayOfMonth, contextEditText.getText().toString(),date1);
            mDatabase.child(roomName).child(readDay).setValue(calendar);



        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void startToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}