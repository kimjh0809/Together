package com.example.login.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.login.FirebaseHelper;
import com.example.login.MemberInfo;
import com.example.login.PostInfo;
import com.example.login.R;
import com.example.login.activity.PostActivity;
import com.example.login.activity.WritePostActivity;
import com.example.login.listener.OnPostListener;
import com.example.login.view.ReadContentsView;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.PostViewHolder> {


    private ArrayList<MemberInfo> mDataset;
    private Activity activity;



    static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        PostViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public UserListAdapter(Activity activity, ArrayList<MemberInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_list, parent, false);

        final PostViewHolder postViewHolder = new PostViewHolder(cardView);


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {

        CardView cardView = holder.cardView;
        ImageView photoImageView=cardView.findViewById(R.id.photoImageView);
        TextView nameTextView = cardView.findViewById(R.id.nameTextView);
        TextView addressTextView = cardView.findViewById(R.id.addressTextView);


        MemberInfo memberInfo=mDataset.get(position);
        if(mDataset.get(position).getPhotoUri() != null){
            Glide.with(activity).load(mDataset.get(position).getPhotoUri()).centerCrop().override(500).into(photoImageView);
        }

        nameTextView.setText(memberInfo.getnickname());
        addressTextView.setText(memberInfo.getStateMsg());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }



}