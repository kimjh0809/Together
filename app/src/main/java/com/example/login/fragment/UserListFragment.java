package com.example.login.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.MemberInfo;
import com.example.login.PostInfo;
import com.example.login.R;
import com.example.login.activity.WritePostActivity;
import com.example.login.adapter.PostAdapter;
import com.example.login.adapter.UserListAdapter;
import com.example.login.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class UserListFragment extends Fragment {

    private String roomName;
    private String userUId;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private UserListAdapter userListAdapter;

    private ArrayList<MemberInfo> memberList;



    public UserListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user_list, container, false);

        /*
        //현재  방 저장
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");*/



        //현재 사용자 아이디(이메일) 불러오기
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userUId = firebaseUser.getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();

        memberList = new ArrayList<>();
        //포스트 어댑터로
        userListAdapter = new UserListAdapter(getActivity(), memberList);



        final RecyclerView recyclerView = view.findViewById(R.id.userRecycleView);



        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userListAdapter);


        postUpdate();


        return view;
    }

    @Override
    public   void onPause(){
        super.onPause();
    }




    private void postUpdate() {

       // Date date=memberList.size()==0 || clear? new Date(): memberList.get(memberList.size()-1).getCreatedAt();

        CollectionReference collectionReference = firebaseFirestore.collection("users");
        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                                memberList.clear();


                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d(TAG, document.getId() + " => " + document.getData());

                                memberList.add(new MemberInfo(
                                        document.getData().get("name").toString(),
                                        document.getData().get("phoneNumber").toString(),
                                        document.getData().get("nickname").toString(),
                                        document.getData().get("address").toString(),
                                        document.getData().get("photoUri")==null? null :
                                                document.getData().get("photoUri").toString(),
                                        document.getData().get("stateMsg").toString()
                                        ));
                                Log.e("로그","포스트리스트 사이즈"+memberList.size());
                            }
                            userListAdapter.notifyDataSetChanged();

                        } else {
                        }

                    }
                });
    }


    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);

        intent.putExtra("roomName", roomName);
        startActivityForResult(intent,0);
    }

}