package com.orange.tpms.utils;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.View;

/**
 * 图像工具类
 */
public class ImageUtil {

    /**
     * 背景灰化
     */
    public static void toBagroundGrey(View view){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        view.getBackground().setColorFilter(filter);
    }
}
