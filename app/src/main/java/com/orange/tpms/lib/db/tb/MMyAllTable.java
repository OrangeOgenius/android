package com.orange.tpms.lib.db.tb;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Message;

import com.orange.tpms.lib.test.MTest;
import com.orange.tpms.lib.db.core.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import bean.mmy.MMyBean;

/**
 * 所有的MMy车型
 * Created by john on 2019/3/31.
 */
public class MMyAllTable extends BaseDBModel {

    public static String TAG = MTest.class.getName();

    public static final String MAKE_ID = "make_id";
    public static final String MAKE = "make";
    public static final String MODEL = "model";
    public static final String YEAR = "year";
    public static final String HEX = "hex";
    public static final String LF_POWER = "lf_power";
    public static final String MMY_NUM = "mmy_num";
    public static final String PRD_NUM = "prd_num";

    public MMyAllTable () {}

    public MMyAllTable (Context context) {}

    @Override
    protected String getTableName() {
        return DBConf.TABLE[0];
    }

    /**
     * 清理表格
     * @param context
     */
    public static void ClearTable (Context context, SQLiteOpenHelper dbHelper) {
        MMyAllTable mMyAllTable = new MMyAllTable ();

        mMyAllTable.openObject(context, "");

        // 删除tb_mmy_all
        String sql = String.format(
            "delete from %s",
            DBConf.TABLE[0]
        );
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));
        MMyAllTable.Execute(dbHelper, params);

        // 删除tb_mmy_like
        sql = String.format(
            "delete from %s",
            DBConf.TABLE[2]
        );
        params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));
        MMyAllTable.Execute(dbHelper, params);

        mMyAllTable.closeObject();
    }

    public static void InsertMMY (Context context, MMyBean mMyBean) {
        MMyAllTable mMyAllTable = new MMyAllTable ();

        mMyAllTable.openObject(context, "");
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMyAllTable.getObject();

        if (mMyBean != null) {
            String make = mMyBean.getMake();
            String model = mMyBean.getModel();
            String year = mMyBean.getYear();
            String hex = mMyBean.getHex();
            String lf_power = mMyBean.getLf_power();
            String mmy_num = mMyBean.getMmy_num();
            String prd_num = mMyBean.getPrd_num();

            // 构建SQL语句
            String sql = String.format(
                    "insert into %s(%s,%s,%s,%s,%s) " +
                    "select \"%s\", \"%s\", \"%s\", \"%s\", \"%s\" " +
                    "where not exists (select 1 from %s where %s=\"%s\" and %s=\"%s\")",
                    DBConf.TABLE[0], MAKE, MODEL, YEAR, HEX, LF_POWER, PRD_NUM, MMY_NUM,
                    make, model, year, hex, lf_power, prd_num, mmy_num,
                    DBConf.TABLE[0], MAKE, make, MODEL, model
                    );

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));
            MMyAllTable.Execute(dbHelper, params);
        }
        mMyAllTable.closeObject();
    }

    /**
     * 不需要重复开关
     * @param context
     * @param mMyBean
     * @param dbHelper open状态的dbHelper
     */
    public static void InsertMMY (Context context, MMyBean mMyBean, SQLiteOpenHelper dbHelper) {
        if (mMyBean != null) {
            String make = mMyBean.getMake();
            String model = mMyBean.getModel();
            String year = mMyBean.getYear();
            String hex = mMyBean.getHex();
            String mmy_num = mMyBean.getMmy_num();
            String prd_num = mMyBean.getPrd_num();
            String lf_power = mMyBean.getLf_power();

            // 构建SQL语句
            String sql = String.format(
                    "insert into %s(%s,%s,%s,%s,%s,%s,%s) " +
                            "select \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\" " +
                            "where not exists (select 1 from %s where %s=\"%s\" and %s=\"%s\")",
                    DBConf.TABLE[0], MAKE, MODEL, YEAR, HEX, LF_POWER, PRD_NUM, MMY_NUM,
                    make, model, year, hex, lf_power, prd_num, mmy_num,
                    DBConf.TABLE[0], MAKE, make, MODEL, model
            );

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));
            MMyAllTable.Execute(dbHelper, params);
        }
    }

    /**
     * 读取库中所有MMY车型
     * @return
     */
    public static ArrayList<MMyBean> ReadAllMMy (Context context) {
        return ReadMMy(context,"");
    }

    /**
     * 读取指定的车型
     * @param context
     * @param make
     * @return
     */
    public static ArrayList<MMyBean> ReadMMyWithMake (Context context, String make) {
        return ReadMMy(context, make);
    }

    /**
     * 根据make_id获取MMy信息
     * @param context
     * @param make_id
     * @return
     */
    private static MMyBean ReadMMyWithMakeId (Context context, String make_id) {
        MMyAllTable mMyAllTable = new MMyAllTable ();

        mMyAllTable.openObject(context, "");
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMyAllTable.getObject();

        // 查询库
        String sql = String.format(
                " select %s,%s,%s,%s,%s,%s,%s,%s " +
                        " from %s " +
                        " where 1 ",
                MAKE_ID, MAKE, MODEL, YEAR, HEX, LF_POWER, MMY_NUM, PRD_NUM,
                DBConf.TABLE[0]
        );
        if (!make_id.equals("")) {
            sql = sql + " and make_id=" + make_id;
        }
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));

        Message msg = MMyAllTable.Query(dbHelper, params);
        mMyAllTable.closeObject();

        // 处理查询结果
        Bundle dataBundle = msg.getData ().getBundle(MMyAllTable.DATA);
        String[] make_ids = dataBundle.getStringArray (MAKE_ID);
        String[] makes = dataBundle.getStringArray (MAKE);
        String[] models = dataBundle.getStringArray (MODEL);
        String[] years = dataBundle.getStringArray (YEAR);
        String[] hexs = dataBundle.getStringArray (HEX);
        String[] mmy_nums = dataBundle.getStringArray (MMY_NUM);
        String[] prd_nums = dataBundle.getStringArray (PRD_NUM);
        String[] lf_powers = dataBundle.getStringArray (LF_POWER);

        if (make_ids.length > 0) {
            MMyBean mMyBean = new MMyBean();
            mMyBean.setMake_id(make_ids[0]);
            mMyBean.setMake(makes[0]);
            mMyBean.setModel(models[0]);
            mMyBean.setYear(years[0]);
            mMyBean.setHex(hexs[0]);
            mMyBean.setMmy_num(mmy_nums[0]);
            mMyBean.setPrd_num(prd_nums[0]);
            mMyBean.setLf_power(lf_powers[0]);
            return mMyBean;
        } else {
            return null;
        }
    }

    /**
     * 读取库中我收藏的所有MMY车型
     * @return
     */
    public static ArrayList<MMyBean> ReadMyAllMMy (Context context) {
        return ReadMyMMy(context,"");
    }

    /**
     * 读取我收藏的车型列表里指定的车型
     * @param context
     * @param make
     * @return
     */
    public static ArrayList<MMyBean> ReadMyMMyWithMake (Context context, String make) {
        return ReadMyMMy(context, make);
    }

    /**
     * 读取我收藏的MMy
     * @param context
     * @param make
     * @return
     */
    private static ArrayList<MMyBean> ReadMyMMy (Context context, String make) {
        // make转成小写
        make = make.toLowerCase();

        ArrayList<MMyBean> arrayList = new ArrayList<MMyBean>();
        MMyAllTable mMyAllTable = new MMyAllTable ();

        mMyAllTable.openObject(context, "");
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMyAllTable.getObject();

        // 查询库
        String sql = String.format(
                " select %s,%s,%s,%s,%s,%s,%s,%s " +
                        " from %s " +
                        " where 1 ",
                MAKE_ID, MAKE, MODEL, YEAR, HEX, LF_POWER, MMY_NUM, PRD_NUM,
                DBConf.TABLE[2]
        );
        if (!make.equals("")) {
            sql = sql + " and LOWER(make)=\"" + make + "\"";
        }
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));

        Message msg = MMyAllTable.Query(dbHelper, params);
        mMyAllTable.closeObject();

        // 处理查询结果
        Bundle dataBundle = msg.getData ().getBundle(MMyAllTable.DATA);
        String[] make_ids = dataBundle.getStringArray (MAKE_ID);
        String[] makes = dataBundle.getStringArray (MAKE);
        String[] models = dataBundle.getStringArray (MODEL);
        String[] years = dataBundle.getStringArray (YEAR);
        String[] hexs = dataBundle.getStringArray (HEX);
        String[] prd_nums = dataBundle.getStringArray (PRD_NUM);
        String[] mmy_nums = dataBundle.getStringArray (MMY_NUM);
        String[] lf_powers = dataBundle.getStringArray (LF_POWER);

        for (int i=0;i<make_ids.length;i++) {
            MMyBean mMyBean = new MMyBean();
            mMyBean.setMake_id(make_ids[i]);
            mMyBean.setMake(makes[i]);
            mMyBean.setModel(models[i]);
            mMyBean.setYear(years[i]);
            mMyBean.setHex(hexs[i]);
            mMyBean.setPrd_num(prd_nums[i]);
            mMyBean.setMmy_num(mmy_nums[i]);
            mMyBean.setLf_power(lf_powers[i]);
            arrayList.add(mMyBean);
        }
        return arrayList;
    }

    /**
     * 读取所有MMy
     * @param context
     * @param make
     * @return
     */
    private static ArrayList<MMyBean> ReadMMy (Context context, String make) {
        // make转成小写
        make = make.toLowerCase();

        ArrayList<MMyBean> arrayList = new ArrayList<MMyBean>();
        MMyAllTable mMyAllTable = new MMyAllTable ();

        mMyAllTable.openObject(context, "");
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMyAllTable.getObject();

        // 查询库
        String sql = String.format(
                " select %s,%s,%s,%s,%s,%s,%s,%s" +
                        " from %s " +
                        " where 1 ",
                MAKE_ID, MAKE, MODEL, YEAR, HEX, LF_POWER, PRD_NUM, MMY_NUM,
                DBConf.TABLE[0]
        );
        if (!make.equals("")) {
            sql = sql + " and LOWER(make)=\"" + make + "\"";
        }
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));

        Message msg = MMyAllTable.Query(dbHelper, params);
        mMyAllTable.closeObject();

        // 处理查询结果
        Bundle dataBundle = msg.getData ().getBundle(MMyAllTable.DATA);
        String[] make_ids = dataBundle.getStringArray (MAKE_ID);
        String[] makes = dataBundle.getStringArray (MAKE);
        String[] models = dataBundle.getStringArray (MODEL);
        String[] years = dataBundle.getStringArray (YEAR);
        String[] hexs = dataBundle.getStringArray (HEX);
        String[] mmy_nums = dataBundle.getStringArray (MMY_NUM);
        String[] prd_nums = dataBundle.getStringArray (PRD_NUM);
        String[] lf_powers = dataBundle.getStringArray (LF_POWER);

        for (int i=0;i<make_ids.length;i++) {
            MMyBean mMyBean = new MMyBean();
            mMyBean.setMake_id(make_ids[i]);
            mMyBean.setMake(makes[i]);
            mMyBean.setModel(models[i]);
            mMyBean.setYear(years[i]);
            mMyBean.setHex(hexs[i]);
            mMyBean.setLf_power(lf_powers[i]);
            mMyBean.setMmy_num(mmy_nums[i]);
            mMyBean.setPrd_num(prd_nums[i]);
            arrayList.add(mMyBean);
        }
        return arrayList;
    }

    /**
     * 读取所有MMy
     * @param context
     * @param mmy_num
     * @return
     */
    public static ArrayList<MMyBean> ReadMMyWithMMyNum (Context context, String mmy_num) {
        // mmy_num转成小写
        mmy_num = mmy_num.toLowerCase();

        ArrayList<MMyBean> arrayList = new ArrayList<MMyBean>();
        MMyAllTable mMyAllTable = new MMyAllTable ();

        mMyAllTable.openObject(context, "");
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMyAllTable.getObject();

        // 查询库
        String sql = String.format(
                " select %s,%s,%s,%s,%s,%s,%s,%s" +
                        " from %s " +
                        " where 1 ",
                MAKE_ID, MAKE, MODEL, YEAR, HEX, LF_POWER, PRD_NUM, MMY_NUM,
                DBConf.TABLE[0]
        );
        if (!mmy_num.equals("")) {
            sql = sql + " and LOWER(mmy_num)=\"" + mmy_num + "\"";
        }
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));

        Message msg = MMyAllTable.Query(dbHelper, params);
        mMyAllTable.closeObject();

        // 处理查询结果
        Bundle dataBundle = msg.getData ().getBundle(MMyAllTable.DATA);
        String[] make_ids = dataBundle.getStringArray (MAKE_ID);
        String[] makes = dataBundle.getStringArray (MAKE);
        String[] models = dataBundle.getStringArray (MODEL);
        String[] years = dataBundle.getStringArray (YEAR);
        String[] hexs = dataBundle.getStringArray (HEX);
        String[] mmy_nums = dataBundle.getStringArray (MMY_NUM);
        String[] prd_nums = dataBundle.getStringArray (PRD_NUM);
        String[] lf_powers = dataBundle.getStringArray (LF_POWER);

        for (int i=0;i<make_ids.length;i++) {
            MMyBean mMyBean = new MMyBean();
            mMyBean.setMake_id(make_ids[i]);
            mMyBean.setMake(makes[i]);
            mMyBean.setModel(models[i]);
            mMyBean.setYear(years[i]);
            mMyBean.setHex(hexs[i]);
            mMyBean.setLf_power(lf_powers[i]);
            mMyBean.setMmy_num(mmy_nums[i]);
            mMyBean.setPrd_num(prd_nums[i]);
            arrayList.add(mMyBean);
        }
        return arrayList;
    }

    /**
     * 读取所有MMy
     * @param context
     * @param prd_num
     * @return
     */
    public static ArrayList<MMyBean> ReadMMyWithPrdNum (Context context, String prd_num) {
        // prd_num转成小写
        prd_num = prd_num.toLowerCase();

        ArrayList<MMyBean> arrayList = new ArrayList<MMyBean>();
        MMyAllTable mMyAllTable = new MMyAllTable ();

        mMyAllTable.openObject(context, "");
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMyAllTable.getObject();

        // 查询库
        String sql = String.format(
                " select %s,%s,%s,%s,%s,%s,%s,%s" +
                        " from %s " +
                        " where 1 ",
                MAKE_ID, MAKE, MODEL, YEAR, HEX, LF_POWER, PRD_NUM, MMY_NUM,
                DBConf.TABLE[0]
        );
        if (!prd_num.equals("")) {
            sql = sql + " and LOWER(prd_num)=\"" + prd_num + "\"";
        }
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));

        Message msg = MMyAllTable.Query(dbHelper, params);
        mMyAllTable.closeObject();

        // 处理查询结果
        Bundle dataBundle = msg.getData ().getBundle(MMyAllTable.DATA);
        String[] make_ids = dataBundle.getStringArray (MAKE_ID);
        String[] makes = dataBundle.getStringArray (MAKE);
        String[] models = dataBundle.getStringArray (MODEL);
        String[] years = dataBundle.getStringArray (YEAR);
        String[] hexs = dataBundle.getStringArray (HEX);
        String[] mmy_nums = dataBundle.getStringArray (MMY_NUM);
        String[] prd_nums = dataBundle.getStringArray (PRD_NUM);
        String[] lf_powers = dataBundle.getStringArray (LF_POWER);

        for (int i=0;i<make_ids.length;i++) {
            MMyBean mMyBean = new MMyBean();
            mMyBean.setMake_id(make_ids[i]);
            mMyBean.setMake(makes[i]);
            mMyBean.setModel(models[i]);
            mMyBean.setYear(years[i]);
            mMyBean.setHex(hexs[i]);
            mMyBean.setLf_power(lf_powers[i]);
            mMyBean.setMmy_num(mmy_nums[i]);
            mMyBean.setPrd_num(prd_nums[i]);
            arrayList.add(mMyBean);
        }
        return arrayList;
    }

    /**
     * 是否存在于访问表
     * @return
     */
    private static boolean ifExistLikeMmy (SQLiteOpenHelper dbHelper, Context context,
        String make_id) {
        // 如果不存在则插入
        String sql = String.format(
                "select %s from %s " +
                "where %s=%s limit 1",
                MAKE_ID, DBConf.TABLE[2], MAKE_ID, make_id
        );

        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));
        Message msg = MMyAllTable.Query(dbHelper, params);

        // 处理查询结果
        Bundle dataBundle = msg.getData ().getBundle(MMyAllTable.DATA);
        String[] make_ids = dataBundle.getStringArray (MAKE_ID);
        if (make_ids.length>0) {
            return true;
        }
        return false;
    }

    /**
     * MMy访问量+1
     * @param context
     * @param make_id
     * @return
     */
    public static boolean IncVisitOfMMy (Context context, String make_id) {
        // 增加mmy访问数
        MMyAllTable mMyAllTable = new MMyAllTable ();

        mMyAllTable.openObject(context, "");
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMyAllTable.getObject();

        if (make_id != "") {
            boolean ifExist = ifExistLikeMmy(dbHelper, context, make_id);
            if (!ifExist) {
                // 如果不存在则插入
                // 获取make_id的车型信息
                MMyBean mMyBean = MMyAllTable.ReadMMyWithMakeId(context, make_id);
                if (mMyBean != null) {
                    String sql = String.format(
                            "insert into %s(%s,%s,%s,%s,%s,%s,%s,%s) " +
                                    "values(%s,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\") ",
                            DBConf.TABLE[2], MAKE_ID,MAKE, MODEL, YEAR, HEX, LF_POWER, MMY_NUM, PRD_NUM,
                            mMyBean.getMake_id(), mMyBean.getMake(), mMyBean.getModel(),
                            mMyBean.getYear(),
                            mMyBean.getHex(), mMyBean.getLf_power(), mMyBean.getMmy_num(), mMyBean.getPrd_num()
                    );

                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));
                    MMyAllTable.Execute(dbHelper, params);
                }
            } else {
                // 存在则更新
                String sql = String.format(
                        "update %s set visit=visit+1 " +
                                "where make_id=%s",
                        DBConf.TABLE[2], make_id
                );

                List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));
                MMyAllTable.Execute(dbHelper, params);
            }
        }
        return true;
    }

    /**
     * 移除我的最爱MMY
     * @return false-删除失败, true-成功删除
     */
    public static boolean RmMyMMy (Context context, String make_id) {
        // 增加mmy访问数
        MMyAllTable mMyAllTable = new MMyAllTable ();

        mMyAllTable.openObject(context, "");
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMyAllTable.getObject();

        // 获取make_id的车型信息
        MMyBean mMyBean = MMyAllTable.ReadMMyWithMakeId(context, make_id);
        if (mMyBean != null) {
            String sql = String.format(
                "delete from %s where make_id=%s", DBConf.TABLE[2], make_id
            );

            List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
            params.add(new BasicNameValuePair(MMyAllTable.SQL, sql));
            MMyAllTable.Execute(dbHelper, params);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据sensorId获取hex
     * @return
     */
    public static String ReadHexWithSensorId (Context context, String sensor_id) {
        return "2C";
    }
}
