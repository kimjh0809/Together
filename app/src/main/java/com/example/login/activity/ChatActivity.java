package com.example.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.BasicActivity;
import com.example.login.adapter.ChatAdapter;
import com.example.login.ChatDTO;
import com.example.login.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends BasicActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ChatDTO> chatList;
    //private String nickname = "익명1";


    private String roomName;
    private String nickname;

    private EditText chatText;
    private Button sendButton;

    private DatabaseReference myRef;
    private String map;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        user  = FirebaseAuth.getInstance().getCurrentUser();

        // 로그인 화면에서 받아온 채팅방 이름, 유저 이름 저장
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        nickname=intent.getStringExtra("nickname");
        setToolbarTitle(roomName);

        map = intent.getStringExtra("map");

        Log.e("로그","닉네임"+nickname);

        chatText = findViewById(R.id.chatText);
        sendButton = findViewById(R.id.sendButton);

        if(map!=null){
            chatText.setText(map);
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("chat");

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입력창에 메시지를 입력 후 버튼클릭했을 때
                String msg = chatText.getText().toString();

                if(msg != null){
                    ChatDTO chatDTO = new ChatDTO();
                    chatDTO.setName(nickname);
                    chatDTO.setMsg(msg);
                    chatDTO.setDate(new Date());

                    //메시지를 파이어베이스에 보냄.
                    myRef.child(roomName).push().setValue(chatDTO);

                    chatText.setText("");
                    recyclerView.scrollToPosition(adapter.getItemCount());
                }

            }
        });
        //리사이클러뷰에 어댑터 적용
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        chatList = new ArrayList<>();
        adapter = new ChatAdapter(chatList,nickname);
        recyclerView.setAdapter(adapter);



        //데이터들을 추가, 변경, 제거, 이동, 취소
        myRef.child(roomName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                //어댑터에 DTO추가
                ChatDTO chatDTO = snapshot.getValue(ChatDTO.class);
                ((ChatAdapter)adapter).addChat(chatDTO);
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
    }


}