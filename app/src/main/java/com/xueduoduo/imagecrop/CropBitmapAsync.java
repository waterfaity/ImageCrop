package com.xueduoduo.imagecrop;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

public class CropBitmapAsync {
    public static void getCropBitmap(final Bitmap.CompressFormat format, final Drawable drawable, final RectF lineRect, final RectF bitmapRect, final OnCropBitmapListener onCropBitmapListener) {
        if (onCropBitmapListener!=null)onCropBitmapListener.onCropStart();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                //剪切bitmap
                Bitmap bitmap = cropBitmap(drawable, lineRect, bitmapRect);
                if (bitmap == null) {
                    if (onCropBitmapListener != null)
                        onCropBitmapListener.onCropError("剪切bitmap失败!");
                } else {
                    //压缩图片
                    //保存图片
                    try {
                        File savePath = FileUtils.getSavePath(Bitmap.CompressFormat.PNG == format ? "png" : "jpg");
                        try {
                            FileUtils.saveBitmap(format, bitmap, savePath);
                            return savePath.toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (onCropBitmapListener != null)
                                onCropBitmapListener.onCropError("保存图片失败");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (onCropBitmapListener != null)
                            onCropBitmapListener.onCropError("创建保存文件失败!");
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String savePath) {
                super.onPostExecute(savePath);
                if (onCropBitmapListener!=null)onCropBitmapListener.onCropSuccess(savePath);
            }
        }.execute();
    }

    public static Bitmap cropBitmap(Drawable drawable, RectF lineRect, RectF bitmapRect) {
        if (drawable instanceof BitmapDrawable) {
            //获取bitmap
            BitmapDrawable bitmapDrawer = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawer.getBitmap();
            //bitmap的宽高
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();

            //缩放后的bitmap在view中的宽高
            float tempBitmapWidth = bitmapRect.width();
            float tempBitmapHeight = bitmapRect.height();

            //计算剪切位置在缩放的尺寸中的占比  然后得到对应真实图片的尺寸
            int cropLeft = (int) ((lineRect.left - bitmapRect.left) / tempBitmapWidth * bitmapWidth);
            int cropTop = (int) ((lineRect.top - bitmapRect.top) / tempBitmapHeight * bitmapHeight);
            int cropWidth = (int) (lineRect.width() / tempBitmapWidth * bitmapWidth);
            int cropHeight = (int) (lineRect.height() / tempBitmapHeight * bitmapHeight);
            if (cropLeft<0)cropLeft=0;
            if (cropTop<0)cropTop=0;
            if (cropWidth+cropLeft>bitmapWidth)cropWidth=bitmapWidth-cropLeft;
            if (cropHeight+cropTop>bitmapHeight)cropHeight=bitmapHeight-cropTop;
            return Bitmap.createBitmap(bitmap, cropLeft, cropTop, cropWidth, cropHeight);
        }
        return null;
    }
}
