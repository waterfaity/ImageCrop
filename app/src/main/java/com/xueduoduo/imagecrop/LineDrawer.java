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
    //最大边界
    private RectF mMaxRect;
    //移动边界
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
    //移动旧坐标
    private float lastX;
    private float lastY;

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
        minWidth = (int) (mDisplayMetrics.density * 40);
        minHeight = minWidth;
        circleRadius = (int) (mDensity * 3);
        touchCircleRadius = (int) (mDensity * 13);
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
        mAreaPath.reset();
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
                lastX = event.getX();
                lastY = event.getY();
                onLineChangeListener.onLineChange(this);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMoving) {
                    //移动处理
                    handleMove(event.getX(), event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                isMoving = false;
                onLineChangeListener.onLineChange(this);
                break;
        }
        return isMoving;

    }

    private void handleMove(float x, float y) {
        float left = mRectF.left;
        float top = mRectF.top;
        float right = mRectF.right;
        float bottom = mRectF.bottom;
        //1-8 判断边缘(动1-2边)
        //9 :all  判断移向的边缘(四边都要移动)

        switch (moveStyle) {
            case LEFT:
                left = getLeft(x, left, right);
                break;
            case TOP:
                top = getTop(y, top, bottom);
                break;
            case RIGHT:
                right = getRight(x, left, right);
                break;
            case BOTTOM:
                bottom = getBottom(y, top, bottom);
                break;
            case LT:
                top = getTop(y, top, bottom);
                left = getLeft(x, left, right);
                break;
            case RT:
                top = getTop(y, top, bottom);
                right = getRight(x, left, right);
                break;
            case LB:
                left = getLeft(x, left, right);
                bottom = getBottom(y, top, bottom);
                break;
            case RB:
                right = getRight(x, left, right);
                bottom = getBottom(y, top, bottom);
                break;
            case ALL:
                //判断
                float withTemp = right - left;
                float heightTemp = bottom - top;
                left += (x - lastX);
                right += (x - lastX);
                if (x < lastX && left < touchCircleRadius) {
                    //到达左侧
                    left = touchCircleRadius;
                    right = touchCircleRadius + withTemp;
                } else if (x > lastX && right > mMaxRect.right - touchCircleRadius) {
                    //到达右侧
                    right = mMaxRect.right - touchCircleRadius;
                    left = right - withTemp;
                }
                top += (y - lastY);
                bottom += (y - lastY);

                if (y < lastY && top < touchCircleRadius) {
                    //到达左侧
                    top = touchCircleRadius;
                    bottom = touchCircleRadius + heightTemp;
                } else if (y > lastY && bottom > mMaxRect.bottom - touchCircleRadius) {
                    //到达右侧
                    bottom = mMaxRect.bottom - touchCircleRadius;
                    top = bottom - heightTemp;
                }
                break;
        }
        mRectF.set(left, top, right, bottom);
        lastX = x;
        lastY = y;
        onLineChangeListener.onLineChange(this);
    }

    private float getTop(float y, float top, float bottom) {

        top += (y - lastY);
        if (top < touchCircleRadius) top = touchCircleRadius;
        if (bottom - top < minHeight) {
            top = bottom - minHeight;
        }
        return top;
    }

    private float getBottom(float y, float top, float bottom) {
        bottom += (y - lastY);
        if (bottom > mMaxRect.bottom - touchCircleRadius)
            bottom = mMaxRect.bottom - touchCircleRadius;
        if (bottom - top < minHeight) {
            bottom = top + minHeight;
        }
        return bottom;
    }

    private float getLeft(float x, float left, float right) {
        left += (x - lastX);
        if (left < touchCircleRadius) left = touchCircleRadius;
        if (right - left < minWidth) {
            left = right - minWidth;
        }
        return left;
    }

    private float getRight(float x, float left, float right) {
        right += (x - lastX);
        if (right > mMaxRect.right - touchCircleRadius) right = mMaxRect.right - touchCircleRadius;
        if (right - left < minWidth) {
            right = left + minWidth;
        }
        return right;
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
            //上边
            return MoveStyle.TOP;
        } else if (isInLineHor(mRectF.bottom, y)) {
            //下边
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

    public void setMaxRect(int width, int height) {
        if (mMaxRect == null) {
            mMaxRect = new RectF();
        }
        mMaxRect.set(0, 0, width, height);
    }

    public interface OnLineChangeListener {
        void onLineChange(LineDrawer lineDrawer);
    }


    public enum MoveStyle {
        NO(0),
        LEFT(1),//左边界
        TOP(2),//上
        RIGHT(3),//右
        BOTTOM(4),//下
        //左上 右上 左下 右下
        LT(5),
        RT(6),
        LB(7),
        RB(8),
        ALL(9);//整体移动

        MoveStyle(int ni) {
            nativeInt = ni;
        }

        final int nativeInt;
    }

    @Override
    public String toString() {
        return "moving: " + isMoving + " ; bounds: (" + (int) mRectF.left + "," + (int) mRectF.top + "," + (int) mRectF.right + "," + (int) mRectF.bottom + ") ; x: " + (int) lastX + " y: " + (int) lastY;
    }
}
