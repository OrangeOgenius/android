package com.orange.tpms.helper;

import android.content.Context;
import android.content.res.Resources;

import com.de.rocket.Rocket;
import com.orange.tpms.R;
import com.orange.tpms.bean.LanguageBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LanguageHelper extends BaseHelper {

    /**
     * 支持语言的枚举类
     */
    public enum SURPORT_LANGUAGE {
        ENGLISH(1, Locale.ENGLISH),
        CHINA(2, Locale.CHINA),
        TAIWAN(3, Locale.TAIWAN),
        ITALIANO(4, Locale.ITALIAN),
        DEUTSCH(5, Locale.GERMAN);

        int language;
        Locale locale;

        SURPORT_LANGUAGE(int language, Locale locale) {
            this.language = language;
            this.locale = locale;
        }

        public int toInt() {
            return this.language;
        }

        public Locale toLocale() {
            return this.locale;
        }
    }

    private List<LanguageBean> languageList = new ArrayList<>();//支持语言列表

    public LanguageHelper(Context context){
        Resources res =context.getResources();
        String[] languageStringArray=res.getStringArray(R.array.language);
        SURPORT_LANGUAGE[] languageEnumArray = SURPORT_LANGUAGE.values();
        if(languageStringArray.length>0 && languageStringArray.length == languageEnumArray.length){
            for(int i=0;i<languageEnumArray.length;i++){
                languageList.add(new LanguageBean(languageStringArray[i],languageEnumArray[i]));
            }
        }
    }

    /**
     * 读取语言列表
     */
    public void getLanguage(Context context){
        Locale locale = Rocket.getSaveLocale(context);
        if(languageList!=null){
            for(int i=0;i<languageList.size();i++){
                if(locale.equals(languageList.get(i).getSurportLanguage().toLocale())){
                    getLanguageNext(i,languageList);
                }
            }
        }
    }

    /**
     * 设置选中语言位置
     */
    public void setLanguage(Context context,int index){
        if(languageList != null && languageList.size()-1 >= index){
            Rocket.setSaveLocale(context, languageList.get(index).getSurportLanguage().toLocale());
        }
    }

    /* *********************************  获取自动锁定列表  ************************************** */

    private OnGetlanguageListener onGetlanguageListener;

    public void getLanguageNext(int select,List<LanguageBean> arrayList) {
        if (onGetlanguageListener != null) {
            runMainThread(() -> onGetlanguageListener.onGetlanguage(select, arrayList));
        }
    }

    public void setOnGetlanguageListener(OnGetlanguageListener onGetlanguageListener) {
        this.onGetlanguageListener = onGetlanguageListener;
    }

    public interface OnGetlanguageListener {
        void onGetlanguage(int select, List<LanguageBean> arrayList);
    }
}
