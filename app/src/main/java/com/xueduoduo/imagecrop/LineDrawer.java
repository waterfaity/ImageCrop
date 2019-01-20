package com.xueduoduo.imagecrop;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/1/20 16:53
 * @info:
 */
public class LineDrawer {
    private final DisplayMetrics mDisplayMetrics;//
    private float mDensity;
    //边界
    private RectF mRectF;
    //线
    private Paint mLinePaint;
    //矩形 边角
    private Paint mAreaPaint;
    private Path mAreaPath;

    //最小宽高
    private int minWidth, minHeight;
    //边缘线宽
    private int mLineWidthOutside;
    //内部线宽
    private int mLineWidthInside;
    //矩形最小宽
    private int mPartMinWidth;
    //圆半径
    private int circleRadius;
    private int touchCircleRadius;

    //移动中
    private boolean isMoving;
    private MoveStyle moveStyle;
    private OnLineChangeListener onLineChangeListener;

    public void setOnLineChangeListener(OnLineChangeListener onLineChangeListener) {
        this.onLineChangeListener = onLineChangeListener;
    }

    public LineDrawer(DisplayMetrics displayMetrics) {
        this.mDisplayMetrics = displayMetrics;
        mDensity = mDisplayMetrics.density;
        initPaint();
        initSize();

    }

    private void initPaint() {
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLUE);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mAreaPaint = new Paint();
        mAreaPaint.setColor(Color.BLUE);
        mAreaPath = new Path();
    }

    /**
     * 宽 = 外部线*2 + 内部线*2 + 矩形()*3
     */
    private void initSize() {
        mLineWidthInside = (int) (1 * mDisplayMetrics.density);
        mLineWidthOutside = (int) (2 * mDisplayMetrics.density);
        mPartMinWidth = (int) (8 * mDisplayMetrics.density);
        //最小宽高
        minWidth = (int) (mDisplayMetrics.density * 11);
        minHeight = minWidth;
        circleRadius = (int) (mDensity * 2);
        touchCircleRadius = (int) (mDensity * 5);
    }

    public void setBounds(RectF rectF) {
        this.mRectF = rectF;
    }

    public void draw(Canvas canvas) {
        if (canvas == null || mRectF == null || mRectF.width() <= 0 || mRectF.height() <= 0) return;
        //画边界
        mLinePaint.setStrokeWidth(mLineWidthOutside);
        canvas.drawRect(mRectF.left + mDensity, mRectF.top + mDensity, mRectF.right - mDensity, mRectF.bottom - mDensity, mLinePaint);
        //画内部线条
        mLinePaint.setStrokeWidth(mLineWidthInside);

        //1 横
        float verMean = mRectF.height() / 3F;
        canvas.drawLine(mRectF.left, mRectF.top + verMean, mRectF.right, mRectF.top + verMean, mLinePaint);
        canvas.drawLine(mRectF.left, mRectF.bottom - verMean, mRectF.right, mRectF.bottom - verMean, mLinePaint);
        //2 竖
        float horMean = mRectF.width() / 3F;
        canvas.drawLine(mRectF.left + horMean, mRectF.top, mRectF.left + horMean, mRectF.bottom, mLinePaint);
        canvas.drawLine(mRectF.right - horMean, mRectF.top, mRectF.right - horMean, mRectF.bottom, mLinePaint);
        //画中间交叉圆 左上 右上 左下 右下
        mAreaPath.addCircle(mRectF.left + horMean, mRectF.top + verMean, circleRadius, Path.Direction.CW);
        mAreaPath.addCircle(mRectF.right - horMean, mRectF.top + verMean, circleRadius, Path.Direction.CW);
        mAreaPath.addCircle(mRectF.left + horMean, mRectF.bottom - verMean, circleRadius, Path.Direction.CW);
        mAreaPath.addCircle(mRectF.right - horMean, mRectF.bottom - verMean, circleRadius, Path.Direction.CW);
        canvas.drawPath(mAreaPath, mAreaPaint);
    }

    /**
     * 是否按在课移动的区域
     *
     * @param event
     */
    public boolean isTouchMovePath(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMoving = false;
                //按下处理
                //1.判断是否在圆上
                if (MoveStyle.NO != (moveStyle = isInCircle(event.getX(), event.getY()))) {
                    isMoving = true;
                }
                //2.判断是否在 四角
                if (!isMoving && MoveStyle.NO != (moveStyle = isInCornerOrLine(event.getX(), event.getY()))) {
                    isMoving = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMoving) {
                    //移动处理
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return isMoving;

    }

    /**
     * 边角或则边线
     *
     * @param x
     * @param y
     * @return
     */
    private MoveStyle isInCornerOrLine(float x, float y) {


        if (isInLineVer(mRectF.left, x)) {
            //左边线
            if (isInLineHor(mRectF.top, y)) {
                return MoveStyle.LT;
            } else if (isInLineHor(mRectF.bottom, y)) {
                return MoveStyle.LB;
            } else return MoveStyle.LEFT;
        } else if (isInLineVer(mRectF.right, x)) {
            //右边线
            if (isInLineHor(mRectF.top, y)) {
                return MoveStyle.RT;
            } else if (isInLineHor(mRectF.bottom, y)) {
                return MoveStyle.RB;
            } else return MoveStyle.RIGHT;
        } else if (isInLineHor(mRectF.top, y)) {
            return MoveStyle.TOP;
        } else if (isInLineHor(mRectF.bottom, y)) {
            return MoveStyle.BOTTOM;
        }
        return MoveStyle.NO;
    }

    /**
     * @param y  目标线
     * @param y1
     * @return
     */
    private boolean isInLineHor(float y, float y1) {
        return y1 >= y - touchCircleRadius && y1 <= y + touchCircleRadius;
    }

    /**
     * @param x
     * @param x1
     * @return
     */
    private boolean isInLineVer(float x, float x1) {
        return x1 >= x - touchCircleRadius && x1 <= x + touchCircleRadius;

    }

    private MoveStyle isInCircle(float x, float y) {
        //1 横
        float verMean = mRectF.height() / 3F;
        float horMean = mRectF.width() / 3F;

        float x1 = mRectF.left + horMean;
        float x2 = mRectF.right - horMean;
        float y1 = mRectF.top + verMean;
        float y2 = mRectF.bottom - verMean;
        //左上 右上 左下 右下
        if (isInCircleSub(x1, y1, x, y) ||
                isInCircleSub(x1, y2, x, y) ||
                isInCircleSub(x2, y1, x, y) ||
                isInCircleSub(x2, y2, x, y)) {
            return MoveStyle.ALL;
        } else {
            return MoveStyle.NO;
        }
    }


    /**
     * 是否在圆上
     *
     * @param x
     * @param y
     * @param xTemp
     * @param yTemp
     * @return
     */
    private boolean isInCircleSub(float x, float y, float xTemp, float yTemp) {
        return xTemp >= (x - touchCircleRadius) && xTemp <= (x + touchCircleRadius) && yTemp >= (y - touchCircleRadius) && yTemp <= (y + touchCircleRadius);
    }

    public interface OnLineChangeListener {
        void onLineChange(LineDrawer lineDrawer);
    }


    public enum MoveStyle {
        NO(0),
        ALL(1),//整体移动

        LEFT(2),//左边界
        TOP(3),//上
        RIGHT(4),//右
        BOTTOM(5),//下
        //左上 右上 左下 右下
        LT(5),
        RT(6),
        LB(7),
        RB(8);

        MoveStyle(int ni) {
            nativeInt = ni;
        }

        final int nativeInt;
    }

}
