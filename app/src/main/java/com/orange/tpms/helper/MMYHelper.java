package com.orange.tpms.helper;

import android.content.Context;
import android.util.Log;

import com.orange.tpms.bean.MMYItemBean;
import com.orange.tpms.lib.api.MMy;
import com.orange.tpms.utils.AssetsUtils;

import java.io.File;
import java.util.ArrayList;

import bean.mmy.MMyBean;

public class MMYHelper extends BaseHelper {

    /**
     * 获取车的Makes
     * @param context 上下文
     */
    public void GetCarMakes(Context context) {
        preRequestNext();
        new Thread(() -> {
            String assetFocusPath = "carlogo/focus";
            String assetDefaultPath = "carlogo/default";
            String pushSeperator = "_push";
            ArrayList<MMYItemBean> mmyArray = new ArrayList<>();
            String[] logoArray = AssetsUtils.getfilesFromAssets(context,assetFocusPath);
            for(String pushFileName : logoArray){
                String normalFileName = pushFileName.replace(pushSeperator,"");
                String normalPath = assetDefaultPath+File.separator +normalFileName;
                String selectPath = assetFocusPath+File.separator +pushFileName;
                mmyArray.add(new MMYItemBean(normalFileName,selectPath,normalPath));
            }
            getCarMakesNext(mmyArray);
            finishRequestNext();
        }).start();
    }

    /**
     * 通过车的Make获取Model
     * @param context 上下文
     * @param make 车的Make
     */
    public void getCarModels(Context context, String make) {
        preRequestNext();
        new Thread(() -> {
            String name = MMy.getMakeWithLogoFileName(getFileNameNoEx(make));
            ArrayList<MMyBean> arrayList = MMy.getMMyWithMake(context, name);
            getCarModelNext(arrayList);
            finishRequestNext();
        }).start();
    }

    /**
     * 通过MMyBean获取车的年份
     * @param mmyBean MMyBean
     */
    public void getCarYear(MMyBean mmyBean) {
        preRequestNext();
        new Thread(() -> {
            ArrayList<MMyBean> arrayList = new ArrayList<>();
            arrayList.add(mmyBean);
            getCarYearNext(arrayList);
            finishRequestNext();
        }).start();
    }

    /**
     * 获取不带扩展名的文件名
     * @param filename 文件名
     */
    private String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /* *********************************  读取车的 make logo  ************************************** */

    private OnGetCarMakesListener onGetCarMakesListener;

    public void getCarMakesNext(ArrayList<MMYItemBean> arrayList) {
        boolean isEmpty = arrayList == null || arrayList.size() == 0;
        if (onGetCarMakesListener != null) {
            runMainThread(() -> onGetCarMakesListener.onGetCarMakes(isEmpty, arrayList));
        }
    }

    public void setOnGetCarMakesListener(OnGetCarMakesListener onGetCarMakesListener) {
        this.onGetCarMakesListener = onGetCarMakesListener;
    }

    public interface OnGetCarMakesListener {
        void onGetCarMakes(boolean isEmpty, ArrayList<MMYItemBean> arrayList);
    }

    /* *********************************  读取车的 model  ************************************** */

    private OnGetCarModelListener onGetCarModelListener;

    public void getCarModelNext(ArrayList<MMyBean> arrayList) {
        boolean isEmpty = arrayList == null || arrayList.size() == 0;
        if (onGetCarModelListener != null) {
            runMainThread(() -> onGetCarModelListener.onGetCarModel(isEmpty, arrayList));
        }
    }

    public void setOnGetCarModelListener(OnGetCarModelListener onGetCarModelListener) {
        this.onGetCarModelListener = onGetCarModelListener;
    }

    public interface OnGetCarModelListener {
        void onGetCarModel(boolean isEmpty, ArrayList<MMyBean> arrayList);
    }

    /* *********************************  读取车的 year  ************************************** */

    private OnGetCarYearListener onGetCarYearListener;

    public void getCarYearNext(ArrayList<MMyBean> arrayList) {
        boolean isEmpty = arrayList == null || arrayList.size() == 0;
        if (onGetCarYearListener != null) {
            runMainThread(() -> onGetCarYearListener.onGetCarYear(isEmpty, arrayList));
        }
    }

    public void setOnGetCarYearListener(OnGetCarYearListener onGetCarYearListener) {
        this.onGetCarYearListener = onGetCarYearListener;
    }

    public interface OnGetCarYearListener {
        void onGetCarYear(boolean isEmpty, ArrayList<MMyBean> arrayList);
    }
}
