package com.xueduoduo.imagecrop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;

public class ImageCropActivity extends AppCompatActivity {

    private ImageCropView mCropView;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CompressBean compressBean=new CompressBean();
        compressBean.setHeight(300);
        compressBean.setWidth(300);
        compressBean.setMaxHeight(300);
        compressBean.setMaxWidth(300);
        compressBean.setRatio(CompressBean.RATIO_1_1);
        compressBean.setMaxSize(300);
        setContentView(R.layout.activity_main);
        mCropView = findViewById(R.id.crop_view);
        mCropView.setImageResource(R.mipmap.img_1);
        mCropView.setCompressBean(compressBean);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("完成").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mCropView.startCrop(new OnCropBitmapListener() {
            @Override
            public void onCropSuccess(String bitmapPath) {
                if (loadingDialog != null) loadingDialog.dismiss();
                Intent intent = new Intent(ImageCropActivity.this, ShowBitmapActivity.class);
                intent.putExtra("path", bitmapPath);
                startActivity(intent);
            }

            @Override
            public void onCropError(String errMsg) {
                if (loadingDialog != null) loadingDialog.dismiss();
            }

            @Override
            public void onCropStart() {
                if (loadingDialog == null)
                    loadingDialog = new LoadingDialog(ImageCropActivity.this);
                loadingDialog.show();
            }
        });


        return super.onOptionsItemSelected(item);
    }
}
