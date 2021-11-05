package com.example.login.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.login.BasicActivity;
import com.example.login.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.login.Util.showToast;

public class LoginActivity extends BasicActivity {

    private FirebaseAuth mAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private String email;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        // myStartActivity(MemberInitActivity.class);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Login");


        findViewById(R.id.loginButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoSignUpButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoPasswordResetButton).setOnClickListener(onClickListener);
    }


    private void updateUI(FirebaseUser currentUser) {
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //회원가입 처리 시작
            switch (v.getId()) {
                case R.id.loginButton:
                    login();
                    break;
                case R.id.gotoSignUpButton:
                    myStartActivity(SignUpActivity.class);
                    break;
                case R.id.gotoPasswordResetButton:
                    myStartActivity(PasswordResetActivity.class);
                    break;
            }
        }
    };

    private void login() {

        email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordEditText)).getText().toString();


        if (email.length() > 0 && password.length() > 0) {
            //로딩화면
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            //로딩화면 띄우기
            loaderLayout.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //로딩화면 가리기
                            loaderLayout.setVisibility(View.GONE);

                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();
                                showToast(LoginActivity.this,"로그인에 성공하였습니다.");


                                startMainActivity();
                            } else {
                                if (task.getException() != null) {
                                    showToast(LoginActivity.this,"로그인 실패");

                                }
                            }
                        }
                    });


        } else {
            showToast(LoginActivity.this,"이메일 또는 비밀번호를 입력해 주세요.");

        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        //로그인하고서도 뒤로 가면 앱 꺼지기
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(LoginActivity.this, c);
        //뒤로 가면 앱 꺼지기
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}