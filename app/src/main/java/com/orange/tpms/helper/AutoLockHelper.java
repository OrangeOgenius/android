package com.orange.tpms.helper;

import android.content.Context;
import android.content.res.Resources;

import com.orange.tpms.R;
import com.orange.tpms.lib.db.share.SettingShare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoLockHelper extends BaseHelper {

    private List<String> autoLockList = new ArrayList<>();//自动锁屏列表

    public AutoLockHelper(Context context){
        Resources res =context.getResources();
        String[] autoLockArray=res.getStringArray(R.array.auto_lock);
        if(autoLockArray.length>0){
            autoLockList.addAll(Arrays.asList(autoLockArray));
        }
    }

    /**
     * 读取锁定列表
     */
    public void getAutoLock(Context context){
        SettingShare.TimeOutUnitEnum[] autoTimeArray = SettingShare.TimeOutUnitEnum.values();
        SettingShare.TimeOutUnitEnum timeOutUnitEnum= SettingShare.getAutoLockTimeout(context);
        for(int i=0;i<autoTimeArray.length;i++){
            if(timeOutUnitEnum == autoTimeArray[i]){
                getAutoLockNext(i,autoLockList);
            }
        }
    }

    /**
     * 设置锁定位置
     */
    public void setAutoLock(Context context,int index){
        SettingShare.TimeOutUnitEnum[] autoTimeArray = SettingShare.TimeOutUnitEnum.values();
        if(autoTimeArray.length > index){
            SettingShare.setAutoLockTimeout(context,autoTimeArray[index]);
        }
    }

    /* *********************************  获取自动锁定列表  ************************************** */

    private OnGetAutoLockListener onGetAutoLockListener;

    public void getAutoLockNext(int select,List<String> arrayList) {
        if (onGetAutoLockListener != null) {
            runMainThread(() -> onGetAutoLockListener.onGetAutoLock(select, arrayList));
        }
    }

    public void setOnGetAutoLockListener(OnGetAutoLockListener onGetAutoLockListener) {
        this.onGetAutoLockListener = onGetAutoLockListener;
    }

    public interface OnGetAutoLockListener {
        void onGetAutoLock(int select, List<String> arrayList);
    }
}
