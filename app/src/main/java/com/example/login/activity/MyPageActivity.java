package com.example.login.activity;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.login.BasicActivity;
import com.example.login.MemberInfo;
import com.example.login.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.login.Util.INTENT_PATH;
import static com.example.login.Util.showToast;

public class MyPageActivity extends BasicActivity {

    private ImageView profileImageView;

    private  FirebaseUser user;
    private String profilePath;
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스

    private EditText nameText;
    private EditText nicknameText;
    private EditText phoneText;
    private EditText stateMsgText;
    private RelativeLayout loaderLayout;
    private EditText addressText;
    private RelativeLayout buttonBackgroundLayout;
    private String photoUrl;


    private String nickname;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        //현재 사용자 아이디(이메일) 불러오기
        user  = FirebaseAuth.getInstance().getCurrentUser();
        profileLoad();

        nameText = (EditText)findViewById(R.id.nameText);
        phoneText = (EditText)findViewById(R.id.phoneNumberText);
        addressText = (EditText)findViewById(R.id.addressText);
        nicknameText = (EditText)findViewById(R.id.nicknameText);
        stateMsgText = (EditText)findViewById(R.id.stateMsgText);
        //아이디 EditText 읽기전용으로 만들기
        nicknameText.setFocusable(false);
        nicknameText.setClickable(false);
        //리얼타임데이터베이스 초기화화
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Login");

//로그아웃
        findViewById(R.id.logoutButton2).setOnClickListener(onClickListener);
        //로딩화면
        loaderLayout = findViewById(R.id.loaderLayout);

        //프로필 이미지 클릭했을때 넘어가기
        profileImageView = findViewById(R.id.profileImageView);

        buttonBackgroundLayout = findViewById(R.id.buttonBackgroundLayout);
        buttonBackgroundLayout.setOnClickListener(onClickListener);
        findViewById(R.id.logoutButton2).setOnClickListener(onClickListener);
        profileImageView.setOnClickListener(onClickListener);

        //회원정보 다 입력하고서 확인 버튼
        findViewById(R.id.profileChangeButton).setOnClickListener(onClickListener);

        //프로필 이미지 촬영 클릭
        findViewById(R.id.profilePicture).setOnClickListener(onClickListener);
        //프로필 이미지 갤러리 클릭
        findViewById(R.id.gallery).setOnClickListener(onClickListener);








    }




    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //회원가입 처리 시작
            switch (v.getId()){
                //회원 정보 입력 완료 버튼
                case R.id.profileChangeButton:
                    storageUploader();
                    break;
                // 프로필 사진 눌렀을 때 카드 뷰가 보이게(또는 안보이게)
                case R.id.profileImageView:
                    buttonBackgroundLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.buttonBackgroundLayout:
                    buttonBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.profilePicture:
                    myStartActivity(CameraActivity.class);
                    break;
                case R.id.gallery:
                    myStartActivity(GalleryActivity.class);
                    break;
                case R.id.logoutButton2:
                    //로그아웃하기
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(LoginActivity.class);

                    break;

            }
        }
    };

    // 결과값 가져오기
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (resultCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra(INTENT_PATH);
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageView);
                    buttonBackgroundLayout.setVisibility(View.GONE);
                }
                break;
            }
        }
    }





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
                                Glide.with(getApplicationContext()).load(document.getData().get("photoUri")).centerCrop().override(500).into(profileImageView);
                                photoUrl=document.getData().get("photoUri").toString();
                            }
                            nickname=document.getData().get("nickname").toString();
                            nicknameText.setText(document.getData().get("nickname").toString());
                            nameText.setText(document.getData().get("name").toString());
                            phoneText.setText(document.getData().get("phoneNumber").toString());
                            addressText.setText(document.getData().get("address").toString());
                            stateMsgText.setText(document.getData().get("stateMsg").toString());

                        } else {

                        }
                    }
                } else {

                }
            }
        });
    }

    private void storageUploader() {

        final String name = ((EditText) findViewById(R.id.nameText)).getText().toString();
        final String phoneNumber = ((EditText) findViewById(R.id.phoneNumberText)).getText().toString();
        final String stateMsg = ((EditText) findViewById(R.id.stateMsgText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressText)).getText().toString();


        if (name.length() > 0 && phoneNumber.length() > 9 && address.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();


            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            if (profilePath == null) {
                // 회원정보에 대한 커스텀 객체!!!!
                MemberInfo memberInfo = new MemberInfo(name, phoneNumber, nickname,address,photoUrl,stateMsg);
                storeUploader(memberInfo);
            } else {
                try {
                    InputStream stream = new FileInputStream(new File(profilePath));

                    UploadTask uploadTask = mountainImagesRef.putStream(stream);
                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            return mountainImagesRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                //프로필 사진 URI
                                Uri downloadUri = task.getResult();

                                // 회원정보에 대한 커스텀 객체!!!!
                                MemberInfo memberInfo = new MemberInfo(name, phoneNumber, nickname, address, downloadUri.toString(),stateMsg);
                                storeUploader(memberInfo);

                            } else {
                                showToast(MyPageActivity.this,"회원정보 수정에 실패하였습니다.");
                            }
                        }
                    });

                } catch (FileNotFoundException e) {

                }
            }
        } else {
            showToast(this,"회원정보를 입력해주세요.");
        }
    }

    private void storeUploader(MemberInfo memberInfo) {
        //파이어베이서파이어스토어 초기화
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid()).set(memberInfo).addOnSuccessListener(aVoid -> {
            loaderLayout.setVisibility(View.GONE);
            profileLoad();;
            showToast(this,"회원정보 변경을 성공하였습니다.");
            myStartActivity(MainActivity.class);

        })
                .addOnFailureListener(e -> {
                    loaderLayout.setVisibility(View.GONE);
                    showToast(this,"회원정보 변경에 실패하였습니다.");

                });

    }



    // 카메라로 가기...
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }




}
