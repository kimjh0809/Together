package com.example.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.login.BasicActivity;
import com.example.login.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends BasicActivity {
    //확인 태그
    private static final String TAG = "MemberInitActivity";

    private String email;

    private ImageView profileImgView;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setToolbarTitle("123123");

        //현재 사용자 아이디(이메일) 불러오기
        user  = FirebaseAuth.getInstance().getCurrentUser();


        //현재 아이디 저장
        email = user.getEmail();

        profileImgView=findViewById(R.id.profile);
        profileImgView.setOnClickListener(onClickListener);

        profileLoad();

        //만약에 현재유저가 null이면 회원가입창 실행
        if(user ==null){
            myStartActivity(SignUpActivity.class);
        }else{
            //파이어베이서파이어스토어 초기화
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                        } else {
                            myStartActivity(MemberInitActivity.class);
                        }
                    }
                } else { }
            });

        }



        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
        findViewById(R.id.roomButton).setOnClickListener(onClickListener);
        findViewById(R.id.goMyPage).setOnClickListener(onClickListener);


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

                //방 들어가기
                case R.id.roomButton:

                    startRoomStartActivity();
                    break;
                //마이페이지 가기
                case R.id.goMyPage:

                    myStartActivity(MyPageActivity.class);
                    break;

            }
        }
    };



    private  void profileLoad(){
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            if(document.getData().get("photoUri") != null){
                                Glide.with(getApplicationContext()).load(document.getData().get("photoUri")).centerCrop().override(500).into(profileImgView);
                            }


                        } else {

                        }
                    }
                } else {

                }
            }
        });
    }


    private void  startRoomStartActivity(){

        Intent intent=new Intent(this, RoomActivity.class);

        startActivity(intent);
    }

    private void  myStartActivity(Class c){
        Intent intent=new Intent(this, c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}