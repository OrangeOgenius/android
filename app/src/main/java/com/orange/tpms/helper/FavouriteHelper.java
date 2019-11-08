package com.orange.tpms.helper;

import android.content.Context;

import com.orange.tpms.bean.MMYItemBean;
import com.orange.tpms.lib.api.MMy;
import com.orange.tpms.utils.AssetsUtils;

import java.io.File;
import java.util.ArrayList;

import bean.mmy.MMyBean;

public class FavouriteHelper extends BaseHelper {

    /**
     * 获取我的最爱
     * @param context 上下文
     */
    public void getFavourite(Context context) {
        preRequestNext();
        ArrayList<MMyBean> arrayList = MMy.getMyMMy(context);
        getFavouriteNext(arrayList);
        finishRequestNext();
    }

    /**
     * 获取我的最爱
     * @param context 上下文
     */
    public void getFavouriteSetting(Context context) {
        preRequestNext();
        ArrayList<MMyBean> arrayList = MMy.getMyMMy(context);
        arrayList.add(new MMyBean());
        arrayList.add(new MMyBean());
        arrayList.add(new MMyBean());
        getFavouriteNext(arrayList);
        finishRequestNext();
    }

    /**
     * 获取我的最爱
     * @param context 上下文
     * @param mMyBean 删除我的最爱
     */
    public void deleteFavourite(Context context,MMyBean mMyBean){
        MMy.rmMyLikeMMy(context, mMyBean.getMake_id());
        getFavouriteSetting(context);
    }

    /* *********************************  读取我的最爱  ************************************** */

    private OnGetFavouriteListener onGetFavouriteListener;

    public void getFavouriteNext(ArrayList<MMyBean> arrayList) {
        boolean isEmpty = arrayList == null || arrayList.size() == 0;
        if (onGetFavouriteListener != null) {
            runMainThread(() -> onGetFavouriteListener.onGetFavourite(isEmpty, arrayList));
        }
    }

    public void setOnGetFavouriteListener(OnGetFavouriteListener onGetFavouriteListener) {
        this.onGetFavouriteListener = onGetFavouriteListener;
    }

    public interface OnGetFavouriteListener {
        void onGetFavourite(boolean isEmpty, ArrayList<MMyBean> arrayList);
    }
}
