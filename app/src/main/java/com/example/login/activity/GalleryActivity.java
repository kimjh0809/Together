package com.example.login.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.BasicActivity;
import com.example.login.R;
import com.example.login.adapter.GalleryAdapter;

import java.util.ArrayList;

import static com.example.login.Util.GALLERY_IMAGE;
import static com.example.login.Util.GALLERY_VIDEO;
import static com.example.login.Util.INTENT_MEDIA;
import static com.example.login.Util.showToast;

public class GalleryActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        if (ContextCompat.checkSelfPermission(GalleryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GalleryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);
            if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {

                showToast(GalleryActivity.this,getResources().getString(R.string.please_grant_permission));
            }
        } else {
            recycleInit();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recycleInit();
                } else {
                    finish();
                    showToast(GalleryActivity.this,getResources().getString(R.string.please_grant_permission));
                }
        }
    }

    private void recycleInit() {
        final int numberOfColumns = 3;

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        //갤러리 어댑터로
        RecyclerView.Adapter mAdapter = new GalleryAdapter(this, getImagesPath(this));
        recyclerView.setAdapter(mAdapter);

    }

    public ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;

        // static 없어야 사용 가능..
        //동영상인지 이미지인지 값 가져오기
        Intent intent = getIntent();
        String[] projection;

        final int media=intent.getIntExtra(INTENT_MEDIA,GALLERY_IMAGE);

        if (media == GALLERY_VIDEO) {
            uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        } else {
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            projection = new String[]{MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        }


        String orderBy = MediaStore.Video.Media.DATE_TAKEN;

        cursor = activity.getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            PathOfImage = ((Cursor) cursor).getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }


}
