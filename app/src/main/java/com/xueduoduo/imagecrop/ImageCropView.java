package com.xueduoduo.imagecrop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

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
    private boolean isImgMove;


    public ImageCropView(Context context) {
        super(context, null);
    }

    public ImageCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        test();
        mLineDrawer = new LineDrawer(context.getResources().getDisplayMetrics().density);
        mLineDrawer.setOnLineChangeListener(this);
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
        if (changed) {
            float minRadius = Math.min(right - left, bottom - top) / 4;
            float centerX = (right - left) / 2;
            float centerY = (bottom - top) / 2;
            mLineDrawer.setBounds(centerX - minRadius, centerY - minRadius, centerX + minRadius, centerY + minRadius);
            mLineDrawer.setMaxRect(right - left, bottom - top);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mLineDrawer.draw(canvas);
        canvas.drawText(mLineDrawer.toString(), 10, 25, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isLineMove = false;
        if (!isImgMove) {
            isLineMove = mLineDrawer.isCanMove(event);
        }
        if (!isLineMove)
            isImgMove = mBitmapDrawer.isCanMove(event);
        return isLineMove || isImgMove;
    }

    @Override
    public void onLineChange(LineDrawer lineDrawer) {
        invalidate();
    }
}
