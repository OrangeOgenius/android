package com.orange.tpms.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

public class AssetsUtils {

    /**
     * 获取assets目录下的图片转成Drawable
     *
     * @param context  上下文
     * @param fileName Assets文件完整路径
     * @return 流数据
     */
    public static Drawable getDrawableFromAssets(Context context, String fileName) {
        Drawable drawable = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            drawable = Drawable.createFromStream(is, "srcName");
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * 获取assets目录下的图片
     *
     * @param context  上下文
     * @param fileName 文件名
     * @return 流数据
     */
    public static Bitmap getImageFromAssetsFile(Context context, String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 获取所有文件
     *
     * @param path 指定目录
     * @return 文件路径列表
     */
    public static String[] getfilesFromAssets(Context context, String path) {
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }
}
