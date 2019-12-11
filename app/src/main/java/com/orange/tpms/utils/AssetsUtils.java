package com.orange.tpms.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import com.orange.blelibrary.blelibrary.BleActivity;

import java.io.File;
import java.io.FileOutputStream;
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
    public static void copyFilesFassets(BleActivity context, String oldPath, String newPath) {
        try {
            String fileNames[] = context.getAssets().list(oldPath);//获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {//如果是目录
                File file = new File(newPath);
                file.mkdirs();//如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    copyFilesFassets(context,oldPath + "/" + fileName,newPath+"/"+fileName);
                }
            } else {//如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount=0;
                while((byteCount=is.read(buffer))!=-1) {//循环从输入流读取 buffer字节
                    fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //如果捕捉到错误则通知UI线程
        }
    }

}
