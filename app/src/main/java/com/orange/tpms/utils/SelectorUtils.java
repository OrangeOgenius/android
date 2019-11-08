package com.orange.tpms.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 动态设置 点击View的Selector的工具类。可以从本地添加 ，也可以从网络添加。
 */
public class SelectorUtils {

    /**
     * 从 drawable 获取图片 id 给 Imageview 添加 selector
     *
     * @param context  调用方法的 Activity
     * @param idNormal 默认图片的 id
     * @param idPress  点击图片的 id
     * @param view     点击的 view
     */
    public static void setSelectorFromDrawable(Context context, int idNormal, int idPress, View view) {
        StateListDrawable drawable = new StateListDrawable();
        Drawable normal = context.getResources().getDrawable(idNormal);
        Drawable press = context.getResources().getDrawable(idPress);
        drawable.addState(new int[]{android.R.attr.state_pressed}, press);
        drawable.addState(new int[]{android.R.attr.state_checked}, press);
        drawable.addState(new int[]{-android.R.attr.state_pressed}, normal);
        view.setBackground(drawable);
    }

    /**
     * 从 Assets 获取图片 给 Imageview 添加 selector
     *
     * @param normalPath 默认图片
     * @param selectPath 点击图片
     * @param view       点击的 view
     */
    public static void setSelectorFromAssets(Context context,String normalPath, String selectPath, View view) {
        new AsyncTask<Void, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(Void... params) {
                AssetManager am = context.getResources().getAssets();
                StateListDrawable drawable = new StateListDrawable();
                try {
                    InputStream normalInputString = am.open(normalPath);
                    InputStream selectlInputString = am.open(selectPath);
                    Drawable normal = Drawable.createFromStream(normalInputString, "normalName");
                    Drawable press = Drawable.createFromStream(selectlInputString, "selectName");
                    drawable.addState(new int[]{android.R.attr.state_pressed}, press);
                    drawable.addState(new int[]{android.R.attr.state_checked}, press);
                    drawable.addState(new int[]{-android.R.attr.state_pressed}, normal);
                    normalInputString.close();
                    selectlInputString.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return drawable;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);
                view.setBackground(drawable);
            }
        }.execute();
    }

    /**
     * 从 filePath 获取图片 给 Imageview 添加 selector
     *
     * @param normalPath 默认图片
     * @param selectPath 点击图片
     * @param view       点击的 view
     */
    public static void setSelectorFromPath(String normalPath, String selectPath, View view) {
        new AsyncTask<Void, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(Void... params) {
                StateListDrawable drawable = new StateListDrawable();
                Drawable normal = Drawable.createFromPath(normalPath);
                Drawable press = Drawable.createFromPath(selectPath);
                drawable.addState(new int[]{android.R.attr.state_pressed}, press);
                drawable.addState(new int[]{android.R.attr.state_checked}, press);
                drawable.addState(new int[]{-android.R.attr.state_pressed}, normal);
                return drawable;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);
                view.setBackground(drawable);
            }
        }.execute();
    }

    /**
     * 从网络获取图片 给 ImageView 设置 selector
     *
     * @param normalUrl 获取默认图片的链接
     * @param pressUrl  获取点击图片的链接
     * @param view      点击的 view
     */
    public static void setSeletorFromNet(String normalUrl, String pressUrl, View view) {
        new AsyncTask<Void, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(Void... params) {
                StateListDrawable drawable = null;
                try {
                    drawable = new StateListDrawable();
                    Drawable normal = Drawable.createFromStream(new URL(normalUrl).openStream(), "normalName");
                    Drawable press = Drawable.createFromStream(new URL(pressUrl).openStream(), "selectName");
                    drawable.addState(new int[]{android.R.attr.state_pressed}, press);
                    drawable.addState(new int[]{android.R.attr.state_checked}, press);
                    drawable.addState(new int[]{-android.R.attr.state_pressed}, normal);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return drawable;
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);
                view.setBackground(drawable);
            }
        }.execute();
    }
}
