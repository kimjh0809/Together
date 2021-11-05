package com.example.login.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login.MemberInfo;
import com.example.login.R;
import com.example.login.activity.MemberInitActivity;
import com.example.login.adapter.UserListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MemberInfoFragment extends Fragment {


    private FirebaseUser firebaseUser;




    public MemberInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
      
        /*
        //현재  방 저장
        Intent intent = getIntent();
        roomName = intent.getStringExtra("roomName");*/

        //현재 사용자 아이디(이메일) 불러오기

        View view=inflater.inflate(R.layout.fragment_member_info, container, false);
        final ImageView profileImageView =view.findViewById(R.id.profileImageView);
        final TextView nameTextView=view.findViewById(R.id.name);
        final TextView nicknameTextView=view.findViewById(R.id.nickname);
        final TextView stateMsgTextView=view.findViewById(R.id.stateMsg);

        final TextView phoneTextView=view.findViewById(R.id.phoneNumber);
        final TextView addressTextView=view.findViewById(R.id.address);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            if(document.getData().get("photoUri") != null){
                                Glide.with(getActivity()).load(document.getData().get("photoUri")).centerCrop().override(500).into(profileImageView);
                            }
                            nameTextView.setText(document.getData().get("name").toString());
                            nicknameTextView.setText(document.getData().get("nickname").toString());
                            phoneTextView.setText(document.getData().get("phoneNumber").toString());
                            stateMsgTextView.setText(document.getData().get("stateMsg").toString());
                            addressTextView.setText(document.getData().get("address").toString());

                        } else {

                        }
                    }
                } else {

                }
            }
        });

        return view;
    }

    @Override
    public   void onPause(){
        super.onPause();
    }



}