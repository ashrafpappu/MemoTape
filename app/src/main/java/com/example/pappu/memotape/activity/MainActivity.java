package com.example.pappu.memotape.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.pappu.memotape.R;
import com.example.pappu.memotape.Utility.AppPermission;
import com.example.pappu.memotape.Utility.AppUtils;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private AppPermission appPermission;
    private  String Tag="MainActivity";
    private ImageButton cameraButton,galleryButton,editButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.hideSystemUI(this);
        setContentView(R.layout.activity_main);
        cameraButton = (ImageButton)findViewById(R.id.camera);
        galleryButton = (ImageButton)findViewById(R.id.gallery);
        editButton = (ImageButton)findViewById(R.id.edit);
        editButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        appPermission = new AppPermission();

        int currentapiVersion = Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.M) {
            appPermission.verifyPermissions(this);
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.camera:
                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.gallery:
                Intent intentgallery = new Intent(this, AlbumFragmentActvity.class);
                startActivity(intentgallery);
                break;
            case R.id.edit:
                Intent intentgalleryedit = new Intent(this, AlbumFragmentActvity.class);
                startActivity(intentgalleryedit);
                break;
        }
    }
}
