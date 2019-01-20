package com.xueduoduo.imagecrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/1/20 16:00
 * @info:
 */
public class ImageCropView extends AppCompatImageView implements LineDrawer.OnLineChangeListener {
    private static final String TAG = "imageCropView";
    private LineDrawer mLineDrawer;

    public ImageCropView(Context context) {
        super(context, null);
    }

    public ImageCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mLineDrawer = new LineDrawer(context.getResources().getDisplayMetrics());
        mLineDrawer.setOnLineChangeListener(this);
        mLineDrawer.setBounds(new RectF(50, 50, 300, 300));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mLineDrawer.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        Log.i(TAG, "onTouchEvent: " + event.getAction());
        boolean touchMovePath = mLineDrawer.isTouchMovePath(event);
        Log.i(TAG, "onTouchEvent: " + touchMovePath);
        return true;
    }

    @Override
    public void onLineChange(LineDrawer lineDrawer) {
        invalidate();
    }
}
