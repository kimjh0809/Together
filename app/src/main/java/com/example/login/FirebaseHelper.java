package com.example.login;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.login.listener.OnPostListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.example.login.Util.isStorageURI;
import static com.example.login.Util.showToast;
import static com.example.login.Util.storageUrlToName;

public class FirebaseHelper {
    private Activity activity;
    private int successCount;
    private OnPostListener onPostListener;

    public FirebaseHelper(Activity activity){
        this.activity=activity;
    }

    public  void  setOnPostListener(OnPostListener onPostListener){
        this.onPostListener=onPostListener;
    }
    public void storageDelete(final PostInfo postInfo){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final String id = postInfo.getId();
        ArrayList<String> contentsList = postInfo.getContents();
        for (int i = 0; i < contentsList.size(); i++) {
            String contents = contentsList.get(i);

            if (isStorageURI(contents)) {
                successCount++;
                String[] list = contents.split("\\?");
                String[] list2 = list[0].split("posts");
                String name = list2[list2.length-1].replace("%2F","/");
                Log.e("로그",""+name);
                StorageReference desertRef = storageRef.child("posts"+name);
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        successCount--;
                        storeDelete(id, postInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToast(activity, "Error");
                    }
                });
            }
        }
        storeDelete(id, postInfo);
    }

   private void storeDelete(final String id,final PostInfo postInfo) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if (successCount == 0) {
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(activity,"게시글을 삭제하였습니다.");
                            onPostListener.onDelete(postInfo);
                           // postUpdate();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(activity,"게시글을 삭제하지 못하였습니다.");

                        }
                    });
        }
    }
}
