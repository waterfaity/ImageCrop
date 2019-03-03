package com.xueduoduo.imagecrop;


public interface OnCropBitmapListener {
    void onCropSuccess(String bitmapPath);

    void onCropError(String errMsg);

    void onCropStart();
}
