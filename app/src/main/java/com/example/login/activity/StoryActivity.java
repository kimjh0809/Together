package com.example.login.activity;



import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.login.BasicActivity;


import com.example.login.R;

import com.example.login.fragment.HomeFragment;
import com.example.login.fragment.MemberInfoFragment;
import com.example.login.fragment.UserListFragment;


import com.google.android.material.bottomnavigation.BottomNavigationView;


public class StoryActivity extends BasicActivity {

    private String roomName;
    private String nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        //setToolbarTitle(getResources().getString(R.string.app_name));
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");
        nickname=intent.getStringExtra("nickname");
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                init();
                break;
        }
    }

    private void init(){



            HomeFragment homeFragment = new HomeFragment(nickname,roomName);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.holder, homeFragment)
                    .commit();

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            HomeFragment homeFragment = new HomeFragment(nickname,roomName);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.holder, homeFragment)
                                    .commit();
                            return true;
                        case R.id.myInfo:
                            MemberInfoFragment memberInfoFragment = new MemberInfoFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.holder, memberInfoFragment)
                                    .commit();
                            return true;
                        case R.id.userList:
                            UserListFragment userListFragment = new UserListFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.holder, userListFragment)
                                    .commit();
                            return true;
                    }
                    return false;
                }
            });

    }


}