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
import com.example.login.UserAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.login.Util.INTENT_MEDIA;
import static com.example.login.Util.showToast;

public class SignUpActivity extends BasicActivity {

    private FirebaseAuth mAuth; //파이어베이스 인증
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스

    private String email;
    private String password ;
    private String passwordCheck;
    private String nickname;
    private RelativeLayout loaderLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        loaderLayout = findViewById(R.id.loaderLayout);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Login");


        findViewById(R.id.signUpButton).setOnClickListener(onClickListener);
        findViewById(R.id.gotoLoginButton).setOnClickListener(onClickListener);
    }


    // 회원가입화면에서 뒤로가면 꺼지기
    @Override public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }



    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //회원가입 처리 시작
            switch (v.getId()){
                case R.id.signUpButton:
                    nicknameCheck();
                    break;
                case R.id.gotoLoginButton:
                    myStartActivity(LoginActivity.class);
                    break;
            }
        }
    };

    private void nicknameCheck(){

        email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        nickname= ((EditText)findViewById(R.id.nicknameEditText)).getText().toString();

        mDatabaseRef.child("nickname").child(nickname).child("nickname")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String value = snapshot.getValue(String.class);

                        if(value!=null){
                            showToast(SignUpActivity.this,"이미 존재하는 닉네임 입니다.");
                        }else{

                            signUp();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull  DatabaseError error) {
                    }
                });
    }

    public void signUp() {
        email = ((EditText)findViewById(R.id.emailEditText)).getText().toString();
        password = ((EditText)findViewById(R.id.passwordEditText)).getText().toString();
        passwordCheck = ((EditText)findViewById(R.id.passwordCheckEditText)).getText().toString();
        nickname = ((EditText)findViewById(R.id.nicknameEditText)).getText().toString();

        if(email.length() >0 && password.length()>0) {
            if(password.equals(passwordCheck)){
                loaderLayout.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                //로딩화면 가리기
                                loaderLayout.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    UserAccount account = new UserAccount();
                                    account.setIdToken(user.getUid());
                                    account.setEmailId(user.getEmail());
                                    account.setNickname(nickname);
                                    account.setPwd(password);

                                    mDatabaseRef.child("nickname").child(nickname.toString()).setValue(account);
                                    //mDatabaseRef.child("nicknameCheck").child(user.getUid()).setValue(nickname.toString());
                                    showToast(SignUpActivity.this,"회원가입에 성공하였습니다.");


                                    //메인페이지로 간다.(로그인 완료)
                                    myStartActivity(MemberInitActivity.class,nickname);
                                    finish();

                                } else {
                                    if(task.getException() != null){
                                        Toast.makeText(SignUpActivity.this,task.getException().toString(),Toast.LENGTH_SHORT).show();
                                    }

                                    //UI
                                }
                            }
                        });
            }else{
                showToast(SignUpActivity.this,"비밀번호가 일치하지 않습니다.");

            }

        }else{
            showToast(SignUpActivity.this,"이메일 또는 비밀번호를 입력해 주세요.");
        }
    }
    //다른 화면으로 이동
    private void  myStartActivity(Class c,String nickname){
        Intent intent=new Intent(this, c);

        intent.putExtra("nickname", nickname);

        startActivity(intent);
    }
    private void  myStartActivity(Class c){
        Intent intent=new Intent(this, c);


        startActivity(intent);
    }

}
