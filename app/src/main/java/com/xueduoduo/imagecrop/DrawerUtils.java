package com.xueduoduo.imagecrop;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/1/28 17:08
 * @info:
 */
public class DrawerUtils {

    public static double getLength(float x, float y, float x1, float y1) {
        return Math.sqrt(Math.pow(x1 - 1, 2) + Math.pow(y1 - y, 2));
    }
}
