package com.orange.tpms.lib.db.tb;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.orange.tpms.lib.test.MTest;
import com.orange.tpms.lib.db.core.BasicNameValuePair;
import com.orange.tpms.lib.utils.SharedPreferencesUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bean.normal.CityBean;

import static com.orange.tpms.lib.db.share.SettingShare.CITY_LIST;

/**
 * Created by john on 2019/5/1.
 */
public class ZoneTable extends BaseDBModel {

    public static String TAG = MTest.class.getName();
    private static boolean IfDataLock = false;

    public static String ID = "id";
    public static String PATH = "path";
    public static String LEVEL = "level";
    public static String NAME = "name";
    public static String NAME_EN = "name_en";
    public static String NAME_PINYIN = "name_pinyin";
    public static String CODE = "code";

    public ZoneTable () {
    }

    @Override
    protected String getTableName() {
        return "travel_location";
    }

    /**
     * 初始化城市数据
     */
    public static void initData (Context context, SQLInitCb cb) {
        if (IfDataLock) {
            // 如果已经有一个函数正在执行, 则直接退出
            return ;
        }
        IfDataLock = true;
        // 执行SQL脚本
        runSQL(context, cb);
        IfDataLock = false;
    }

    private static String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    /**
     * 执行脚本
     * @param context
     */
    private static void runSQL (Context context, SQLInitCb cb) {
        int total = 0;
        int progress = 0;

        if (cb==null) {
            cb = new SQLInitCb() {
                @Override
                public void onStart() {
                }

                @Override
                public void onStop() {
                }

                @Override
                public void onProgress(String s, int total, int progress) {
                }

                @Override
                public void onFail(int total, int progress, Exception e) {
                }
            };
        }

        // 开始
        cb.onStart();
        boolean ifSaved = (Boolean) SharedPreferencesUtils.getParam(context, CITY_LIST, false);  // 默认是未烧录

        if (ifSaved) {
            cb.onStop();
            return ;
        }

        try {
            ZoneTable zoneTable = new ZoneTable ();
            zoneTable.openObject(context, "");
            SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) zoneTable.getObject();

            InputStream in = context.getAssets().open("location.sql");
            String sqlUpdate = null;
            try {
                sqlUpdate = readTextFromSDcard(in);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String[] s = sqlUpdate.split(";");
            total = s.length;
            for (int i = 0; i < s.length; i++) {
                progress = i;
                Pattern p = Pattern.compile("\r|\n");
                Matcher m = p.matcher(s[i]);
                String destination = m.replaceAll("");

                if (!TextUtils.isEmpty(destination)) {
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair(MMyAllTable.SQL, destination));
                    MMyAllTable.Execute(dbHelper, params);
                    cb.onProgress(destination, s.length, i);

                    if (s.length <= (i+1)) {
                        SharedPreferencesUtils.setParam(context, CITY_LIST, true);
                    }
                }
            }
            in.close();
            dbHelper.close();
            zoneTable.closeObject();
        } catch (SQLException e) {
            cb.onFail(total, progress, e);
            e.printStackTrace();
        } catch (IOException e) {
            cb.onFail(total, progress, e);
            e.printStackTrace();
        } finally {
            cb.onStop();
        }
    }

    public interface SQLInitCb {
        public void onStart ();
        public void onStop ();
        public void onProgress (String s, int total, int progress);
        public void onFail (int total, int progress, Exception e);
    }

    /**
     * 获取国家, level=2
     * @return
     */
    public static ArrayList<CityBean> getCountry (Context context, String continentId) {
        return getLocationListWithLevelAndId (context, "2", continentId);
    }

    public static void Print (CityBean cityBean) {
        Log.d(TAG, "Print: "+cityBean.getName());
        Log.d(TAG, "Print: "+cityBean.getName_en());
        Log.d(TAG, "Print: "+cityBean.getName_pinyin());
    }

    /**
     * 获取洲, level=1
     * @return
     */
    public static ArrayList<CityBean> getContinent (Context context) {
        return getLocationWithLevel (context, "1");       // 获取洲级别列表
    }

    /**
     * 获取省份, level=3
     * @return
     */
    public static ArrayList<CityBean> getProvince(Context context, String countryId) {
        return getLocationListWithLevelAndId (context, "3", countryId);
    }

    /**
     * 获取城市, level=4
     * @return
     */
    public static ArrayList<CityBean> getCity(Context context, String provinceId) {
        return getLocationListWithLevelAndId (context, "4", provinceId);
    }

    private static ArrayList<CityBean> getLocationListWithLevelAndId (Context context, String level, String id) {
        // 获取所有城市, 返回path第一级为countryId的城市
        ArrayList<CityBean> arrayList = getLocationWithLevel (context, level);

        ArrayList<CityBean> ret_arrayList = new ArrayList<>();
        for (int i=0;i<arrayList.size();i++) {
            CityBean cityBean = arrayList.get(i);
            String path = cityBean.getPath();

            // 判断是否符合路径
            String[] arr = path.split(",");
            for (int j=0;j<arr.length;j++) {
                if (id.equals(arr[j])) {
                    // 包括在路径里, 需要返回
                    ret_arrayList.add(cityBean);
                } else {
                    continue;
                }
            }
        }
        return ret_arrayList;
    }

    private static ArrayList<CityBean> getLocationWithLevel (Context context, String level) {
        ArrayList<CityBean> arrayList = new ArrayList<CityBean>();
        ZoneTable zoneTable = new ZoneTable ();

        zoneTable.openObject(context, "");
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) zoneTable.getObject();

        // 查询库
        String sql = String.format(
                " select id, path, level, name, name_en, name_pinyin, code " +
                        " from travel_location " +
                        " where level=%s ", level
        );
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));

        Message msg = MMyAllTable.Query(dbHelper, params);
        zoneTable.closeObject();

        // 处理查询结果
        Bundle dataBundle = msg.getData ().getBundle(MMyAllTable.DATA);
        String[] ids = dataBundle.getStringArray (ID);
        String[] paths = dataBundle.getStringArray (PATH);
        String[] levels = dataBundle.getStringArray (LEVEL);
        String[] names = dataBundle.getStringArray (NAME);
        String[] name_ens = dataBundle.getStringArray (NAME_EN);
        String[] name_pinyins = dataBundle.getStringArray (NAME_PINYIN);
        String[] codes = dataBundle.getStringArray (CODE);

        for (int i=0;i<ids.length;i++) {
            CityBean cityBean = new CityBean();
            cityBean.setId(ids[i]);
            cityBean.setName_pinyin(name_pinyins[i]);
            cityBean.setPath(paths[i]);
            cityBean.setLevel(levels[i]);
            cityBean.setName(names[i]);
            cityBean.setName_en(name_ens[i]);
            cityBean.setCode(codes[i]);
            arrayList.add(cityBean);
        }
        return arrayList;
    }
}
