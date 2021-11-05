package com.example.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.login.BasicActivity;
import com.example.login.MemberInfo;
import com.example.login.R;
import com.example.login.RoomDTO;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.login.Util.showToast;

public class RoomChatStartActivity extends BasicActivity {

    private EditText room_name, user_edit;
    private Button user_next;
    private ListView room_list;
    private String nickname;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText nicknameText;
    private FirebaseUser user;
    private ArrayList<String> room;


    private FirebaseAuth mAuth; //파이어베이스 인증



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_room_chat_start);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        user  = FirebaseAuth.getInstance().getCurrentUser();
        //아이디text에 로그인 아이디 넣기
        nicknameText = (EditText)findViewById(R.id.user_edit);
//아이디 EditText 읽기전용으로 만들기
        nicknameText.setFocusable(false);
        nicknameText.setClickable(false);

        findNickName();


        ArrayList<String> room= new ArrayList<String>();

        databaseReference = FirebaseDatabase.getInstance().getReference();


        room_name = (EditText) findViewById(R.id.room_name);
        user_next = (Button) findViewById(R.id.user_next);
        room_list = (ListView) findViewById(R.id.room_list);
        // 리스트 어댑터 생성 및 세팅
        final ArrayAdapter<String> adapter

                = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);
        room_list.setAdapter(adapter);

        // 데이터 받아오기 및 어댑터 데이터 추가 및 삭제 등..리스너 관리
        databaseReference.child("rooms").child("room").addChildEventListener(new ChildEventListener() {
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
                room_name.setText(room.get(position ));
            }
        });

        user_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (room_name.getText().toString().equals("")) {
                    showToast(RoomChatStartActivity.this, "방 이름을 입력하세요.");
                }else{
                    roomCheck(room_name.getText().toString());

                }
            }
        });

        //showRoomList();

    }

    private void roomCheck(String roomName){
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        //nickname= ((EditText)findViewById(R.id.nicknameEditText)).getText().toString();

        databaseReference.child("rooms").child("room").child(roomName).child("roomName")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String value = snapshot.getValue(String.class);
                        if(value!=null){
                            Intent intent = new Intent(RoomChatStartActivity.this, RoomHomeActivity.class);
                            intent.putExtra("roomName", roomName);
                            databaseReference.child("rooms").child("member").child(room_name.getText().toString())
                                    .child(user.getUid()).setValue(user.getUid());
                            databaseReference.child("myRoom").child(user.getUid()).child(room_name.getText().toString())
                                    .setValue(room_name.getText().toString());
                            startActivity(intent);
                            //databaseReference.child("rooms").child(room_name.getText().toString()).push().setValue(room);
                        }else{
                            RoomDTO room = new RoomDTO();
                            room.setRoomName(room_name.getText().toString());
                            room.setMember(user.getUid());
                            //storeUploader(room_name.getText().toString());
                            databaseReference.child("rooms").child("room").child(room_name.getText().toString()).setValue(room);
                            //회원 방을 관리하기 위하여
                            databaseReference.child("rooms").child("member").child(room_name.getText().toString())
                                    .child(user.getUid()).setValue(user.getUid());
                            databaseReference.child("myRoom").child(user.getUid()).child(room_name.getText().toString())
                                    .setValue(room_name.getText().toString());
                            Intent intent = new Intent(RoomChatStartActivity.this, RoomHomeActivity.class);
                            intent.putExtra("roomName", roomName);
                            //intent.putExtra("userName", nickname);
                            startActivity(intent);

                            //signUp();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {
                    }
                });
    }



    private void findNickName(){

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    nicknameText.setText(document.getData().get("nickname").toString());
                    Log.e("로그","닉네임"+nickname);


                } else {

                }
            }
        });

    }




    private void showRoomList() {


    }
}