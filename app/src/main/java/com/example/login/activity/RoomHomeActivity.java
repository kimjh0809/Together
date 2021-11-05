package com.example.login.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.login.BasicActivity;
import com.example.login.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RoomHomeActivity extends BasicActivity {

    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private ImageView profileImgView;
    private String emailId;
    private String roomName;
    private String nickname;
    private FirebaseUser user;
    private TextView nicknameTextView;
    private TextView stateMsgTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_home);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference("room");
        profileImgView=findViewById(R.id.profileImgView);
        //현재  방 저장
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        setToolbarTitle(roomName);
        //현재 사용자 아이디(이메일) 불러오기
        user  = FirebaseAuth.getInstance().getCurrentUser();
        emailId = user.getUid();
        ImageLoad(emailId);

        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.goChatButton).setOnClickListener(onClickListener);
        findViewById(R.id.locationButton).setOnClickListener(onClickListener);
        findViewById(R.id.storyButton).setOnClickListener(onClickListener);
        findViewById(R.id.CalendarButton).setOnClickListener(onClickListener);
        findViewById(R.id.myPageButton).setOnClickListener(onClickListener);
        nicknameTextView=(TextView)findViewById(R.id.nickname);
        stateMsgTextView=(TextView)findViewById(R.id.stateMsg);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            nicknameTextView.setText(document.getData().get("nickname").toString());
                            if(document.getData().get("stateMsg").toString() !=null){
                                stateMsgTextView.setText(document.getData().get("stateMsg").toString());}

                        } else { } } } else { } }});
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.logoutButton:
                    //로그아웃하기
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(LoginActivity.class);

                    break;
                case R.id.goChatButton:

                    myStartActivity(ChatActivity.class);
                    break;

                case R.id.locationButton:
                    myStartActivity(LocationActivity.class);
                    break;

                case R.id.storyButton:
                    myStartActivity(StoryActivity.class);
                    break;

                case R.id.CalendarButton:
                    myStartActivity(CalendarActivity.class);
                    break;

                case R.id.myPageButton:
                    myStartActivity(MyPageActivity.class);

            }
        }
    };

    public void setName(final String nickname){
        this.nickname = nickname;
    }
    public void nickNameCheck(Class c){
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            Intent intent = new Intent(RoomHomeActivity.this, c);
                            //intent.putExtra("userName", emailId);
                            intent.putExtra("roomName", roomName);
                            intent.putExtra("nickname",document.getData().get("nickname").toString());
                            startActivity(intent);

                        } else { } } } else { } }});

    }
    private void myStartActivity(Class c) {
        nickNameCheck(c);

    }


    //사진 로드
    private void ImageLoad(String user){
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://login-221d9.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child("users/"+user+"/profileImage.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //이미지 로드 성공시

                Glide.with(getApplicationContext())
                        .load(uri).centerCrop().override(500)
                        .into(profileImgView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시

            }
        });
    }


}