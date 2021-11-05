package com.example.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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


public class MemberInitActivity extends BasicActivity {


    private ImageView profileImageView;
    private String profilePath;
    private FirebaseUser user;
    private String nickname;
    private RelativeLayout loaderLayout;
    private RelativeLayout buttonBackgroundLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        //로딩화면
        loaderLayout = findViewById(R.id.loaderLayout);

        //프로필 이미지 클릭했을때 넘어가기
        profileImageView = findViewById(R.id.profileImageView);

        buttonBackgroundLayout = findViewById(R.id.buttonBackgroundLayout);
        buttonBackgroundLayout.setOnClickListener(onClickListener);

        profileImageView.setOnClickListener(onClickListener);

        //회원정보 다 입력하고서 확인 버튼
        findViewById(R.id.checkButton).setOnClickListener(onClickListener);

        //프로필 이미지 촬영 클릭
        findViewById(R.id.profilePicture).setOnClickListener(onClickListener);
        //프로필 이미지 갤러리 클릭
        findViewById(R.id.gallery).setOnClickListener(onClickListener);

        Intent intent = getIntent();
        nickname = intent.getStringExtra("nickname");
        Log.e("로그","닉네임"+nickname);

        //아이디text에 로그인 아이디 넣기
        EditText text = (EditText)findViewById(R.id.nicknameEditText);
        text.setText(nickname);

        //아이디 EditText 읽기전용으로 만들기
        text.setFocusable(false);
        text.setClickable(false);


    }

    //뒤로가기 앱종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

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

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                //회원 정보 입력 완료 버튼
                case R.id.checkButton:
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


            }
        }
    };


    private void storageUploader() {

        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String phoneNumber = ((EditText) findViewById(R.id.phoneNumberEditText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();
        final String stateMsg = ((EditText) findViewById(R.id.stateMsgEditText)).getText().toString();


        if (name.length() > 0 && phoneNumber.length() > 9 && address.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainImagesRef = storageRef.child("users/" + user.getUid() + "/profileImage.jpg");

            if (profilePath == null) {
                // 회원정보에 대한 커스텀 객체!!!!
                MemberInfo memberInfo = new MemberInfo(name, phoneNumber, nickname,address,stateMsg);
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
                                MemberInfo memberInfo = new MemberInfo(name, phoneNumber, nickname, address, downloadUri.toString(),
                                        stateMsg);
                                storeUploader(memberInfo);

                            } else {
                                showToast(MemberInitActivity.this,"회원정보를 보내는데 실패하였습니다.");
                            }
                        }
                    });

                } catch (FileNotFoundException e) {

                }
            }
        } else {
            showToast(MemberInitActivity.this,"회원정보를 입력해주세요.");
        }
    }

    private void storeUploader(MemberInfo memberInfo) {
        //파이어베이서파이어스토어 초기화
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid()).set(memberInfo).addOnSuccessListener(aVoid -> {
            loaderLayout.setVisibility(View.GONE);
            myStartActivity(MainActivity.class);
            showToast(MemberInitActivity.this,"회원정보 등록을 성공하였습니다.");
            finish();

        })
                .addOnFailureListener(e -> {
                    loaderLayout.setVisibility(View.GONE);
                    showToast(MemberInitActivity.this,"회원정보 등록에 실패하였습니다.");

                });

    }


    // 카메라로 가기...
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }


}
