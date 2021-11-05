package com.example.login.listener;


import com.example.login.PostInfo;

public interface OnPostListener {
    void onDelete(PostInfo postInfo);
    void onModify();

}
