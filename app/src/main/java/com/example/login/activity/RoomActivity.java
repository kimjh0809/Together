package com.example.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.login.BasicActivity;
import com.example.login.PostInfo;
import com.example.login.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class RoomActivity extends BasicActivity {

    private FirebaseUser user;

    private DatabaseReference mDatabase;
    private ArrayList<String> roomList;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<String> room;
    private ListView room_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_room);

        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference();
        user  = FirebaseAuth.getInstance().getCurrentUser();


        ArrayList<String> room= new ArrayList<String>();


        //room_name = (EditText) findViewById(R.id.room_name);

        room_list = (ListView) findViewById(R.id.recyclerView2);
        // 리스트 어댑터 생성 및 세팅
        final ArrayAdapter<String> adapter

                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        room_list.setAdapter(adapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        mDatabase.child("myRoom").child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.e("LOG", "dataSnapshot.getKey() : " + dataSnapshot.getKey());
                room.add(dataSnapshot.getKey());
                adapter.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


        room_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               String roomName=room.get(position);
                myStartActivity(RoomHomeActivity.class,roomName.toString());

            }
        });
///

        findViewById(R.id.floatingActionButton).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.floatingActionButton:
                    myStartActivity(RoomChatStartActivity.class);
                    break;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        //postUpdate(false);

    }

    @Override
    protected  void onPause(){
        super.onPause();

    }
    private void showRoomList() {
        // 리스트 어댑터 생성 및 세팅
       // final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout., android.R.id.text1);


    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(RoomActivity.this, c);


        startActivity(intent);
    }
    private void myStartActivity(Class c, String roomName) {
        Intent intent = new Intent(RoomActivity.this, c);

        intent.putExtra("roomName", roomName);
        startActivity(intent);
    }

    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(RoomActivity.this, c);
        //회원 아이디 아님
        intent.putExtra("postInfo", postInfo);
        //intent.putExtra("roomName", roomName);
        startActivity(intent);
    }


}
