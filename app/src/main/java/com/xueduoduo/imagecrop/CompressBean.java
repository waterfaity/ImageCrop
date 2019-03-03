package com.xueduoduo.imagecrop;

import java.io.Serializable;

public class CompressBean implements Serializable {

    private static final long serialVersionUID=20190303221512L;
    public static final float RATIO_4_3=4/3F;
    public static final float RATIO_1_1=1F;
    public static final float RATIO_16_9=16/9F;

    private int width;//确定宽高
    private int height;

    private int maxSize;//图片占空间大小  KB

    private int maxWidth;//剪切后 压缩到最宽
    private int maxHeight;
    private float ratio;//宽高比 针对

    public int getWidth() {
        return width;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }
}
