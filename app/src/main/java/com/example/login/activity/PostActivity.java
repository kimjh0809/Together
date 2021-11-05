package com.example.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.bumptech.glide.Glide;
import com.example.login.BasicActivity;
import com.example.login.FirebaseHelper;
import com.example.login.PostInfo;
import com.example.login.R;
import com.example.login.listener.OnPostListener;
import com.example.login.view.ContentsItemView;
import com.example.login.view.ReadContentsView;

import static com.example.login.Util.INTENT_PATH;
import static com.example.login.Util.showToast;


public class PostActivity extends BasicActivity {
    private PostInfo postInfo;
    private FirebaseHelper firebaseHelper;
    private ReadContentsView readContentsView;
    private LinearLayout contentsLayout;
    private String nickname;
    private String roomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        nickname=intent.getStringExtra("nickname");
        contentsLayout = findViewById(R.id.contentsLayout);
        readContentsView = findViewById(R.id.readContentsView);

        firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.setOnPostListener(onPostListener);
        uiUpdate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    postInfo = (PostInfo)data.getSerializableExtra("postInfo");
                    contentsLayout.removeAllViews();
                    uiUpdate();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deletePost:
                if(!postInfo.getPublisher().toString().equals(nickname)){
                    showToast(this,"작성자가 아닙니다");
                }
                else{
                    firebaseHelper.storageDelete(postInfo);
                    return true;}

                return true;
            case R.id.modifyPost:
                Log.e("로그","postInfo.getPublisher()"+postInfo.getPublisher());
                Log.e("로그","nickname"+nickname);

                if(!postInfo.getPublisher().toString().equals(nickname)){
                    showToast(this,"작성자가 아닙니다");
                }
                else{
                myStartActivity(WritePostActivity.class, postInfo);
                return true;}
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            Log.e("로그 ","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그 ","수정 성공");
        }
    };

    private void uiUpdate(){
        setToolbarTitle(postInfo.getTitle());
        readContentsView.setPostInfo(postInfo);
    }

    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", postInfo);
        startActivityForResult(intent, 0);
    }
}
