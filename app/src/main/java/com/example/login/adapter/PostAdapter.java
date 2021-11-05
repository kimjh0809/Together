package com.example.login.adapter;

import android.app.Activity;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;



import com.example.login.FirebaseHelper;
import com.example.login.PostInfo;
import com.example.login.R;

import com.example.login.activity.PostActivity;

import com.example.login.activity.WritePostActivity;
import com.example.login.listener.OnPostListener;
import com.example.login.view.ReadContentsView;
import com.google.android.exoplayer2.SimpleExoPlayer;



import java.util.ArrayList;

import static com.example.login.Util.showToast;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {


    private ArrayList<PostInfo> mDataset;
    private Activity activity;
    private FirebaseHelper firebaseHelper;
    private final int MORE_INDEX=2;
    private ArrayList<ArrayList<SimpleExoPlayer>> playerArrayListArrayList = new ArrayList<>();
    private String nickname;
    private String roomName;



    static class PostViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        PostViewHolder(CardView v) {
            super(v);
            cardView = v;

        }
    }

    public PostAdapter(Activity activity, ArrayList<PostInfo> myDataset, String nickname,String roomName) {
        this.mDataset = myDataset;
        this.activity = activity;
        this.nickname=nickname;
        this.roomName=roomName;

        firebaseHelper = new FirebaseHelper(activity);

    }

    public void setOnPostListener(OnPostListener onPostListener){

        firebaseHelper.setOnPostListener(onPostListener);
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
                .inflate(R.layout.item_post, parent, false);

        final PostViewHolder postViewHolder = new PostViewHolder(cardView);


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PostActivity.class);
                //회원 아이디 아님
                intent.putExtra("postInfo", mDataset.get(postViewHolder.getAdapterPosition()));
                intent.putExtra("nickname",nickname);
                intent.putExtra("roomName",roomName);
               // intent.putExtra("roomName", roomName);
                activity.startActivity(intent);

            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, postViewHolder.getAdapterPosition());
            }
        });

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostViewHolder holder, int position) {

        CardView cardView = holder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.titleTextView);

        PostInfo postInfo=mDataset.get(position);
        titleTextView.setText(postInfo.getTitle());

        ReadContentsView readContentsView= cardView.findViewById(R.id.readContentsView);

        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);


        if(contentsLayout.getTag() == null ||!contentsLayout.getTag().equals(postInfo)){

            contentsLayout.setTag(postInfo);
            contentsLayout.removeAllViews();

            readContentsView.setMoreIndex(MORE_INDEX);
            readContentsView.setPostInfo(postInfo);

            ArrayList<SimpleExoPlayer> playerArrayList=readContentsView.getPlayerArrayList();
            if(playerArrayList!= null){
            playerArrayListArrayList.add( playerArrayList);
            }


        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                PostInfo postInfo=mDataset.get(position);
                switch (item.getItemId()) {
                    case R.id.modifyPost:
                        if(!postInfo.getPublisher().equals(nickname)){
                            showToast(activity,"작성자가 아닙니다.");
                        }else{
                        myStartActivity(WritePostActivity.class, mDataset.get(position));
                        return true;}
                    case R.id.deletePost:
                        if(!postInfo.getPublisher().equals(nickname)){
                            showToast(activity,"작성자가 아닙니다.");
                        }else{
                            firebaseHelper.storageDelete(mDataset.get(position));

                            return true;}
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }



    private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(activity, c);
        //회원 아이디 아님
        intent.putExtra("postInfo", postInfo);
        intent.putExtra("nickname",nickname);
        intent.putExtra("roomName",roomName);
        //intent.putExtra("roomName", roomName);
        activity.startActivity(intent);
    }

    public void playerStop(){
        for(int i=0; i<playerArrayListArrayList.size();i++){
            ArrayList<SimpleExoPlayer> playerArrayList=playerArrayListArrayList.get(i);
            for(int ii=0; ii<playerArrayList.size();ii++ ){
                SimpleExoPlayer player = playerArrayList.get(ii);
                if(player.getPlayWhenReady()){
                    player.setPlayWhenReady(false);
                }

            }
        }
    }

}