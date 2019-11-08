package com.orange.tpms.helper;

import android.content.Context;
import android.util.Log;

import com.orange.tpms.lib.api.Server;
import com.orange.tpms.lib.db.tb.ZoneTable;

import java.util.ArrayList;
import java.util.List;

import bean.normal.CityBean;
import bean.server.req.RegisterBeanReq;

public class RegisterHelper extends BaseHelper {

    private ArrayList<CityBean> countriesList = new ArrayList<>();
    private ArrayList<CityBean> provincesList = new ArrayList<>();
    private ArrayList<CityBean> citiesList = new ArrayList<>();

    /**
     * 注册
     * @param context 上下文
     * @param registerBeanReq 注册信息
     */
    public void register(Context context,RegisterBeanReq registerBeanReq) {
        if(registerBeanReq != null){
            preRequestNext();
            Server server = new Server(context);
            server.registerNewAccount(registerBeanReq, respond -> {
                int status = respond.getStatus();
                String message = respond.getMessage();
                if(status == 200){
                    //请求成功
                    onRegisterSuccessNext();
                }else {
                    //请求失败
                    onRegisterFailedNext("注册失败 "+message);
                }
                finishRequestNext();
            });
        }
    }

    /**
     * 读取国家列表
     * @param context 上下文
     */
    public void getCountries(Context context){
        new Thread(() -> {
            ArrayList<CityBean> contonentList = ZoneTable.getContinent(context);// 洲列表
            for (int i=0;i<contonentList.size();i++) {
                ArrayList<CityBean> countryList = ZoneTable.getCountry(context, contonentList.get(i).getId());
                if(countryList != null){
                    countriesList.addAll(countryList);
                }
            }
            if(countriesList.size() >0){
                List<String> countries = new ArrayList<>();
                for (int i=0;i<countriesList.size();i++) {
                    countries.add(countriesList.get(i).getName());
                }
                getCountriesNext(countries);
                getProvinces(context,0);
            }
        }).start();
    }

    /**
     * 读取省份列表
     * @param index 索引
     */
    public void getProvinces(Context context,int index){
        new Thread(() -> {
            if(countriesList != null && countriesList.size() > index){
                CityBean cityBean = countriesList.get(index);
                provincesList = ZoneTable.getProvince(context,cityBean.getId());
                List<String> provinces = new ArrayList<>();
                if(provincesList != null && provincesList.size() > 0){
                    for (int i=0;i<provincesList.size();i++) {
                        provinces.add(provincesList.get(i).getName());
                    }
                }
                if(provinces.size() >0){
                    getProvincesNext(provinces);
                    getCities(context,0);
                }
            }
        }).start();
    }

    /**
     * 读取城市列表
     * @param index 索引
     */
    public void getCities(Context context,int index){
        new Thread(() -> {
            if(provincesList != null && provincesList.size() > index){
                CityBean cityBean = provincesList.get(index);
                citiesList = ZoneTable.getCity(context,cityBean.getId());
                List<String> cities = new ArrayList<>();
                if(citiesList != null && citiesList.size() > 0){
                    for (int i=0;i<citiesList.size();i++) {
                        cities.add(citiesList.get(i).getName());
                    }
                }
                if(cities.size() >0){
                    getCitiesNext(cities);
                }
            }
        }).start();
    }

    /* *********************************  读取国家列表  ************************************** */

    private OnGetCountriesListener onGetCountriesListener;

    public void getCountriesNext(List<String> countries) {
        if (onGetCountriesListener != null) {
            runMainThread(() -> onGetCountriesListener.onGetCountries(countries));
        }
    }

    public void setOnGetCountriesListener(OnGetCountriesListener onGetCountriesListener) {
        this.onGetCountriesListener = onGetCountriesListener;
    }

    public interface OnGetCountriesListener {
        void onGetCountries(List<String> countries);
    }

    /* *********************************  读取省份列表  ************************************** */

    private OnGetProvincesListener onGetProvincesListener;

    public void getProvincesNext(List<String> provinces) {
        if (onGetProvincesListener != null) {
            runMainThread(() -> onGetProvincesListener.onGetProvinces(provinces));
        }
    }

    public void setOnGetProvincesListener(OnGetProvincesListener onGetProvincesListener) {
        this.onGetProvincesListener = onGetProvincesListener;
    }

    public interface OnGetProvincesListener {
        void onGetProvinces(List<String> provinces);
    }

    /* *********************************  读取城市列表  ************************************** */

    private OnGetCitiesListener onGetCitiesListener;

    public void getCitiesNext(List<String> cities) {
        if (onGetCitiesListener != null) {
            runMainThread(() -> onGetCitiesListener.onGetCities(cities));
        }
    }

    public void setOnGetCitiesListener(OnGetCitiesListener onGetCitiesListener) {
        this.onGetCitiesListener = onGetCitiesListener;
    }

    public interface OnGetCitiesListener {
        void onGetCities(List<String> cities);
    }

    /* ***************************** RegisterSuccess ***************************** */

    private OnRegisterSuccessListener onRegisterSuccessListener;

    // 接口类 -> OnRegisterSuccessListener
    public interface OnRegisterSuccessListener {
        void onRegisterSuccess();
    }

    // 对外暴露接口 -> setOnRegisterSuccessListener
    public void setOnRegisterSuccessListener(OnRegisterSuccessListener onRegisterSuccessListener) {
        this.onRegisterSuccessListener = onRegisterSuccessListener;
    }

    // 内部使用方法 -> RegisterSuccessNext
    private void onRegisterSuccessNext() {
        if (onRegisterSuccessListener != null) {
            runMainThread(() -> onRegisterSuccessListener.onRegisterSuccess());
        }
    }

    /* ***************************** RegisterFailed ***************************** */

    private OnRegisterFailedListener onRegisterFailedListener;

    // 接口类 -> OnRegisterFailedListener
    public interface OnRegisterFailedListener {
        void onRegisterFailed(String message);
    }

    // 对外暴露接口 -> setOnRegisterFailedListener
    public void setOnRegisterFailedListener(OnRegisterFailedListener onRegisterFailedListener) {
        this.onRegisterFailedListener = onRegisterFailedListener;
    }

    // 内部使用方法 -> RegisterFailedNext
    private void onRegisterFailedNext(String message) {
        if (onRegisterFailedListener != null) {
            runMainThread(() -> onRegisterFailedListener.onRegisterFailed(message));
        }
    }
}
