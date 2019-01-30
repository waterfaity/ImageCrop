package com.xueduoduo.imagecrop;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/1/21 11:32
 * @info:
 */
public class BitmapDrawer implements ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = "BitmapDrawer";
    private float lastX, lastY;
    private ImageView imageView;
    private boolean isDrag;//拖动
    private boolean isZoom;//缩放
    private GestureDetector gestureDetector;//手势  双击放大/缩小
    private ScaleGestureDetector scaleGestureDetector;//手势  双指放大
    private int mLastPointCount;
    private float[] matrixValues = new float[9];

    private float BIGGER = 4;
    private float SMALLER;
    private float currentScale;


    public BitmapDrawer(ImageView imageView) {
        this.imageView = imageView;
        gestureDetector = new GestureDetector(imageView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Log.i(TAG, "onDoubleTap: ");
                return true;
            }
        });
        scaleGestureDetector = new ScaleGestureDetector(imageView.getContext(), this);
    }

    private OnDrawerChangeListener onDrawerChangeListener;

    public boolean isCanMove(MotionEvent event) {
        //双击事件进行关联
        if (gestureDetector.onTouchEvent(event)) {
            //如果是双击的话就直接不向下执行了

            return true;
        }
        //求平均点
        int x = 0, y = 0;
        int pointCount = event.getPointerCount();
        for (int i = 0; i < pointCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointCount;
        y = y / pointCount;
        if (event.getPointerCount() == 2) {
            //双指缩放
            scaleGestureDetector.onTouchEvent(event);
        }
        if (mLastPointCount != pointCount) {
            //手指数改变 重记 防止错误
            lastX = x;
            lastY = y;
        }
        mLastPointCount = pointCount;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                lastY = event.getY();
                isDrag = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                //所有的touch
                isDrag = false;
                isZoom = false;
                break;


            case MotionEvent.ACTION_MOVE:
                move(x, y);
                break;

        }
        lastX = x;
        lastY = y;
        return isDrag || isZoom;
    }

    private void move(int x, int y) {
        if (isDrag && !isZoom) {
            RectF matrixRectF = getMatrixRectF();

            float dx = x - lastX;
            float dy = y - lastY;
            if (matrixRectF.width() < imageView.getWidth()) {
                dx = 0;
            } else {
                if (dx > 0) {
                    //右移
                    if (matrixRectF.left + dx > 0) {
                        //左侧超界
                        dx = -matrixRectF.left;
                    }
                } else if (dx < 0) {
                    //左移
                    if (matrixRectF.right + dx < imageView.getWidth()) {
                        dx = imageView.getWidth() - matrixRectF.right;
                    }
                }
            }
            if (matrixRectF.height() < imageView.getHeight()) {
                dy = 0;
            } else {
                if (dy > 0) {
                    //下移
                    if (matrixRectF.top + dy > 0) {
                        //上侧超界
                        dy = -matrixRectF.top;
                    }
                } else if (dy < 0) {
                    //上移
                    if (matrixRectF.bottom + dy < imageView.getHeight()) {
                        dy = imageView.getHeight() - matrixRectF.bottom;
                    }
                }
            }
            //单指移动
            Matrix imageMatrix = imageView.getImageMatrix();
            imageMatrix.postTranslate(dx, dy);
            onDrawerChangeListener.onBitmapChange(this);
        }
    }


    public void setOnDrawerChangeListener(OnDrawerChangeListener onLineChangeListener) {
        this.onDrawerChangeListener = onLineChangeListener;
    }


    /**
     * 缩放中
     *
     * @param detector
     * @return
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        Matrix matrix = imageView.getImageMatrix();
        currentScale = getScale();
        if (SMALLER == 0) SMALLER = currentScale;
        float scaleFactor = detector.getScaleFactor();
        //缩小  放大
        if ((currentScale > SMALLER && scaleFactor < 1.0F) || (currentScale < BIGGER && scaleFactor > 1.0F)) {
            if (currentScale * scaleFactor < SMALLER) {
                scaleFactor = SMALLER / currentScale;
            }
            if (currentScale * scaleFactor > BIGGER) {
                scaleFactor = BIGGER / currentScale;
            }
            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            checkBorderAndCenterWhenScale();
            onDrawerChangeListener.onBitmapChange(this);
        }
        return true;
    }

    /**
     * 在缩放时，进行图片显示范围的控制
     */
    private void checkBorderAndCenterWhenScale() {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = imageView.getWidth();
        int height = imageView.getHeight();

        // 如果宽或高大于屏幕，则控制范围
        if (rect.width() >= width) {
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() < width) {
            deltaX = width * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() < height) {
            deltaY = height * 0.5f - rect.bottom + 0.5f * rect.height();
        }
        Log.e(TAG, "deltaX = " + deltaX + " , deltaY = " + deltaY);

        imageView.getImageMatrix().postTranslate(deltaX, deltaY);

    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = imageView.getImageMatrix();
        RectF rect = new RectF();
        Drawable d = imageView.getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 缩放开始
     *
     * @param detector
     * @return
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        isZoom = true;
        return true;
    }

    /**
     * 缩放结束
     *
     * @param detector
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        isZoom = false;
    }

    public interface OnDrawerChangeListener {
        void onBitmapChange(BitmapDrawer bitmapDrawer);
    }

    /**
     * 获得当前的缩放比例
     *
     * @return
     */
    private float getScale() {
        imageView.getImageMatrix().getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    private class AutoScaleRunnable implements Runnable {

        private final float targetScale;
        private final float y;
        private final float x;

        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.targetScale = targetScale;
            this.x = x;
            this.y = y;
        }

        @Override
        public void run() {

        }
    }
}
