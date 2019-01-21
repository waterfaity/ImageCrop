package com.xueduoduo.imagecrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    private BitmapDrawer mBitmapDrawer;
    private Paint mPaint;


    public ImageCropView(Context context) {
        super(context, null);
    }

    public ImageCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        test();
        mLineDrawer = new LineDrawer(context.getResources().getDisplayMetrics());
        mLineDrawer.setOnLineChangeListener(this);
        mLineDrawer.setBounds(new RectF(50, 50, 300, 300));
        mBitmapDrawer = new BitmapDrawer();
    }

    private void test() {
        mPaint = new Paint();
        mPaint.setTextSize(30);
        mPaint.setColor(Color.RED);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed)
            mLineDrawer.setMaxRect(right - left, bottom - top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mLineDrawer.draw(canvas);
        canvas.drawText(mLineDrawer.toString(), 10, 25, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean touchMovePath = mLineDrawer.isTouchMovePath(event);
        Log.i(TAG, "onTouchEvent: " + touchMovePath + "  " + event.getAction());
        return true;
    }

    @Override
    public void onLineChange(LineDrawer lineDrawer) {
        invalidate();
    }
}
