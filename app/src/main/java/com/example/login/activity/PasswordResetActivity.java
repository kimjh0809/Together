package com.example.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.login.BasicActivity;
import com.example.login.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.login.Util.showToast;

public class PasswordResetActivity extends BasicActivity {

    private FirebaseAuth mAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Login");

        // 보내기 버튼 클릭!!!!!
        findViewById(R.id.sendButton).setOnClickListener(onClickListener);

    }


    private void updateUI(FirebaseUser currentUser) {
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //비밀번호 재설정 보내기 버튼
            switch (v.getId()) {
                case R.id.sendButton:
                    //보내기버튼 클릭했을 때
                    send();
                    break;

            }
        }
    };

    private void send() {

        email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();

        if (email.length() > 0) {

            //로딩화면
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            //로딩화면 띄우기
            loaderLayout.setVisibility(View.VISIBLE);

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //로딩화면 가리기
                            loaderLayout.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                showToast(PasswordResetActivity.this, "이메일을 보냈습니다.");

                            }
                        }
                    });


        } else {
           showToast(PasswordResetActivity.this, "이메일을 입력해주세요");
        }
    }




}
