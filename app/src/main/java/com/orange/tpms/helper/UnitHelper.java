package com.orange.tpms.helper;

import android.content.Context;
import android.content.res.Resources;
import com.orange.tpms.R;
import com.orange.tpms.lib.db.share.SettingShare;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnitHelper extends BaseHelper {

    private List<String> tempList = new ArrayList<>();//温度单位列表
    private List<String> tireList  = new ArrayList<>();//压力单位列表
    private List<String> numeralList  = new ArrayList<>();//数字单位列表

    public UnitHelper(Context context){
        Resources res =context.getResources();
        String[] tempArray=res.getStringArray(R.array.unite_temp);
        if(tempArray.length > 0){
            tempList.addAll(Arrays.asList(tempArray));
        }
        String[] numeralArray = res.getStringArray(R.array.unite_numeral);
        if(numeralArray.length > 0){
            numeralList.addAll(Arrays.asList(numeralArray));
        }
        String[] tireArray = res.getStringArray(R.array.unite_tire);
        if(tireArray.length > 0){
            tireList.addAll(Arrays.asList(tireArray));
        }
    }

    /**
     * 读取单位
     * @param context 上下文
     */
    public void getUnit(Context context){
        SettingShare.Unit unit = SettingShare.getUnit(context);
        SettingShare.TemperatureUnitEnum[] tempArray = SettingShare.TemperatureUnitEnum.values();
        SettingShare.TirePressureUnitEnum[] tireArray = SettingShare.TirePressureUnitEnum.values();
        SettingShare.NumeralSystemUnitEnum[] numeralArray = SettingShare.NumeralSystemUnitEnum.values();
        if(tempArray.length > 0 ){
            for(int i = 0;i<tempArray.length;i++){
                if(unit.temperatureUnit == tempArray[i]){
                    getTempNext(i,tempList);
                }
            }
        }
        if(tireArray.length>0){//读取温度的单位
            for(int i = 0;i<tireArray.length;i++){
                if(unit.tirePressureUnit == tireArray[i]){
                    getPressureNext(i,tireList);
                }
            }
        }
        if(numeralArray.length>0){//读取数字的单位
            for(int i = 0;i<numeralArray.length;i++){
                if(unit.numeralSystemUnit == numeralArray[i]){
                    getNumeralNext(i,numeralList);
                }
            }
        }
    }

    /**
     * 设置温度的单位
     * @param context 上下文
     */
    public void setTemp(Context context,int index){
        SettingShare.TemperatureUnitEnum[] tempArray = SettingShare.TemperatureUnitEnum.values();
        if(tempArray.length > index){
            SettingShare.setTemperatureUnit(context,tempArray[index]);
        }
    }

    /**
     * 设置压力的单位
     * @param context 上下文
     */
    public void setPressure(Context context,int index){
        SettingShare.TirePressureUnitEnum[] tireArray = SettingShare.TirePressureUnitEnum.values();
        if(tireArray.length > index){
            SettingShare.setTirePressureUnit(context,tireArray[index]);
        }
    }

    /**
     * 设置数字的单位
     * @param context 上下文
     */
    public void setNumeral(Context context,int index){
        SettingShare.NumeralSystemUnitEnum[] numeralArray = SettingShare.NumeralSystemUnitEnum.values();
        if(numeralArray.length > index){
            SettingShare.setNumeralSystemUnit(context,numeralArray[index]);
        }
    }

    /* *********************************  获取温度单位  ************************************** */

    private OnGetTempListener onGetTempListener;

    public void getTempNext(int select,List<String> arrayList) {
        if(arrayList == null || arrayList.size() <= select){
            return;
        }
        if (onGetTempListener != null) {
            runMainThread(() -> onGetTempListener.onGetTemp(select, arrayList));
        }
    }

    public void setOnGetTempListener(OnGetTempListener onGetTempListener) {
        this.onGetTempListener = onGetTempListener;
    }

    public interface OnGetTempListener {
        void onGetTemp(int select, List<String> arrayList);
    }

    /* *********************************  获取压力单位  ************************************** */

    private OnGetPressureListener onGetPressureListener;

    public void getPressureNext(int select,List<String> arrayList) {
        if(arrayList == null || arrayList.size() <= select){
            return;
        }
        if (onGetPressureListener != null) {
            runMainThread(() -> onGetPressureListener.onGetPressure(select, arrayList));
        }
    }

    public void setOnGetPressureListener(OnGetPressureListener onGetPressureListener) {
        this.onGetPressureListener = onGetPressureListener;
    }

    public interface OnGetPressureListener {
        void onGetPressure(int select, List<String> arrayList);
    }

    /* *********************************  获取数字单位  ************************************** */

    private OnGetNumeralListener onGetNumeralListener;

    public void getNumeralNext(int select,List<String> arrayList) {
        if(arrayList == null || arrayList.size() <= select){
            return;
        }
        if (onGetNumeralListener != null) {
            runMainThread(() -> onGetNumeralListener.onGetNumeral(select, arrayList));
        }
    }

    public void setOnGetNumeralListener(OnGetNumeralListener onGetNumeralListener) {
        this.onGetNumeralListener = onGetNumeralListener;
    }

    public interface OnGetNumeralListener {
        void onGetNumeral(int select, List<String> arrayList);
    }
}


