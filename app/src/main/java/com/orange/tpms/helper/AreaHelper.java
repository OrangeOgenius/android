package com.orange.tpms.helper;

import android.content.Context;
import android.content.res.Resources;

import com.orange.tpms.R;
import com.orange.tpms.lib.db.share.SettingShare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AreaHelper extends BaseHelper {

    private List<String> areaList = new ArrayList<>();//区域选择列表

    public AreaHelper(Context context){
        Resources res =context.getResources();
        String[] areaArray=res.getStringArray(R.array.area);
        if(areaArray.length>0){
            areaList.addAll(Arrays.asList(areaArray));
        }
    }

    /**
     * 读取锁定列表
     */
    public void getArea(Context context){
        SettingShare.AreaUnitEnum[] areaArray = SettingShare.AreaUnitEnum.values();
        SettingShare.AreaUnitEnum areaUnitEnum= SettingShare.getArea(context);
        for(int i=0;i<areaArray.length;i++){
            if(areaUnitEnum == areaArray[i]){
                getAreaNext(i,areaList);
            }
        }
    }

    /**
     * 设置锁定位置
     */
    public void setArea(Context context,int index){
        SettingShare.AreaUnitEnum[] areaArray = SettingShare.AreaUnitEnum.values();
        if(areaArray.length > index){
            SettingShare.setArea(context,areaArray[index]);
        }
    }

    /* *********************************  获取区域列表  ************************************** */

    private OnGetareaListener onGetareaListener;

    public void getAreaNext(int select,List<String> arrayList) {
        if (onGetareaListener != null) {
            runMainThread(() -> onGetareaListener.onGetArea(select, arrayList));
        }
    }

    public void setOnGetAreaListener(OnGetareaListener onGetareaListener) {
        this.onGetareaListener = onGetareaListener;
    }

    public interface OnGetareaListener {
        void onGetArea(int select, List<String> arrayList);
    }
}
