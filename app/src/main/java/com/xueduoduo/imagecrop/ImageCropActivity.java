package com.xueduoduo.imagecrop;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ImageCropActivity extends AppCompatActivity {

    private ImageCropView mCropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCropView=findViewById(R.id.crop_view);
    }
}
