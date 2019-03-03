package com.xueduoduo.imagecrop;

import android.content.Context;
import android.graphics.Bitmap;
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
public class ImageCropView extends AppCompatImageView implements LineDrawer.OnLineChangeListener, BitmapDrawer.OnDrawerChangeListener {
    private static final String TAG = "imageCropView";
    private LineDrawer mLineDrawer;//框
    private BitmapDrawer mBitmapDrawer;//图片
    private Paint mPaint;//画笔
    private boolean isImgMove;//图片是否移动中
    private CompressBean compressBean;


    public ImageCropView(Context context) {
        super(context, null);
    }

    public ImageCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        test();
        mLineDrawer = new LineDrawer(context.getResources().getDisplayMetrics().density);
        mLineDrawer.setOnLineChangeListener(this);
        mBitmapDrawer = new BitmapDrawer(this);
        mBitmapDrawer.setOnDrawerChangeListener(this);
        mLineDrawer.freshBitmapRect(mBitmapDrawer.getBitmapRect());
        mBitmapDrawer.freshLineRect(mLineDrawer.getLineRect());
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
            //整个布局的1/4     边界是布局的短边的1/2  并居中
            float minRadius = Math.min(right - left, bottom - top) / 4;
            float centerX = (right - left) / 2;
            float centerY = (bottom - top) / 2;

            if (compressBean!=null){

            }else {

            }

            mLineDrawer.setBounds(centerX - minRadius, centerY - minRadius, centerX + minRadius, centerY + minRadius);
            mLineDrawer.setMaxRect(right - left, bottom - top);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        mBitmapDrawer.draw(canvas);
        mLineDrawer.draw(canvas);
        canvas.drawText(mLineDrawer.toString(), 10, 25, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //线是否在移动
        boolean isLineMove = false;
        //移动线框 注意 : 不可让边框大于图片的边距
        if (!isImgMove) {
            //刷新图片边距
            if (event.getAction()==MotionEvent.ACTION_DOWN){
                mLineDrawer.freshBitmapRect(mBitmapDrawer.getBitmapRect());
            }
            isLineMove = mLineDrawer.isCanMove(event);
        }
        //移动图片
        if (!isLineMove) {
            //刷新剪切框边界
            if (event.getAction()==MotionEvent.ACTION_DOWN){
                mBitmapDrawer.freshLineRect(mLineDrawer.getLineRect());
            }
            mBitmapDrawer.freshLineRect(mLineDrawer.getLineRect());
            isImgMove = mBitmapDrawer.isCanMove(event);
        }
        return isLineMove || isImgMove;
    }

    public void startCrop(OnCropBitmapListener onCropBitmapListener){
          CropBitmapAsync.getCropBitmap(Bitmap.CompressFormat.JPEG,getDrawable(),mLineDrawer.getLineRect(),mBitmapDrawer.getBitmapRect(),onCropBitmapListener);
    }

    /**
     * 线改变
     *
     * @param lineDrawer
     */
    @Override
    public void onLineChange(LineDrawer lineDrawer) {
        invalidate();
    }

    @Override
    public void onBitmapChange(BitmapDrawer bitmapDrawer) {
        invalidate();
    }

    public void setCompressBean(CompressBean compressBean) {
        this.compressBean=compressBean;
        mLineDrawer.setRatio(compressBean.getRatio());
    }
}
