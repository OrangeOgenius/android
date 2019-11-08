package com.orange.tpms.lib.api;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.orange.tpms.lib.db.tb.MMyAllTable;
import com.orange.tpms.lib.utils.SharedPreferencesUtils;
import com.orange.tpms.utils.FileDowload;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;

import bean.mmy.MMyBean;

import static com.orange.tpms.lib.db.share.SettingShare.MMY_LIST;

/**
 * mmy相关操作
 * Created by john on 2019/3/30.
 */
public class MMy extends BApi {

    public static String TAG = MMy.class.getName();

    // private static final String FileName = "MMY.xls";
    private static final String FileName = "MMY_20190814.xls";
    private static final String BookName = "MMY List for Webshop 190603 np_";

    public MMy () {
    }

    /**
     * 打印MMyBean
     */
    public static void Print (MMyBean mMyBean) {
        String s = String.format("make_id:%s,make:%s,model:%s,year:%s,hex:%s,lf:%s,unit:%s,mmy_num:%s,prd_num:%s\n",
                mMyBean.getMake_id(), mMyBean.getMake(),
                mMyBean.getModel(), mMyBean.getYear(),
                mMyBean.getHex(), mMyBean.getLf_power(),
                mMyBean.getUnit(), mMyBean.getMmy_num(),
                mMyBean.getPrd_num());
        Log.d(TAG, s);
    }

    /**
     * 加载Excel里所有MMy配置
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<MMyBean> LoadSDExcel (Context context, String filePath) {
        InputStream stream = null;

        try {
            File fd = new File(filePath);
            if (fd.exists()) {
                // 存在则使用sd文件
                stream = new FileInputStream(fd);
                ArrayList<MMyBean> array_list = LoadExcel(stream);
                return array_list;
            } else {
                // 不存在则使用assets文件
                return LoadAssetsExcel (context);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加载Excel里所有MMy配置
     * @param context
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<MMyBean> LoadAssetsExcel (Context context) {
        AssetManager am = context.getAssets();
        InputStream stream = null;
        try {
            stream = am.open (FileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<MMyBean> array_list = LoadExcel(stream);
        return array_list;
    }

    private static ArrayList<MMyBean> LoadExcel (InputStream stream) {
        if (stream == null) {
            return null;
        }
        HSSFWorkbook workbook = null;//读取现有的Excel
        try {
            workbook = new HSSFWorkbook(stream);
        } catch (IOException e) {
            // e.printStackTrace();
            return null;
        }
        HSSFSheet sheet= workbook.getSheet(BookName);

        int line = 0;
        ArrayList<MMyBean> array_list = new ArrayList();
        for (Row row : sheet) {
            MMyBean mmy_bean = new MMyBean();
            int i = 0;

            line++;
            if (line != 1 && line != 2381 && (line%2 != 0)) {
                continue;
            }
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
                switch (i++) {
                    case 0: {
                        String value = cell.getStringCellValue();
                        mmy_bean.setMake(value);
                    }
                    break;
                    case 1: {
                        mmy_bean.setModel(cell.getStringCellValue());
                    }
                    break;
                    case 2: {
                        mmy_bean.setYear(cell.getStringCellValue());
                    }
                    break;
                    case 4: {
                        mmy_bean.setPrd_num(cell.getStringCellValue());
                    }
                    break;
                    case 5: {
                        mmy_bean.setHex(cell.getStringCellValue());
                    }
                    break;
                    case 6: {
                        String cellValue = cell.getStringCellValue();
                        mmy_bean.setUnit(cellValue);
                    }
                    break;
                    case 7: {
                        mmy_bean.setLf_power(cell.getStringCellValue());
                    }
                    break;
                    case 8: {
                        mmy_bean.setMmy_num(cell.getStringCellValue());
                    } break;
                }
            }
            array_list.add(mmy_bean);

            // 输出mmy
            MMy.Print(mmy_bean);
        }
        return array_list;
    }

    /**
     * 加载MMy数据库, 将Excel转成数据库存储
     */
    @SuppressWarnings("unchecked")
    public static boolean LoadMMy (Context context,String mmyPath, MMyLoadCb cb) {
        // TODO: 2019/10/27 泽强补全逻辑
        if (cb == null) {
            cb = new MMyLoadCb() {
                @Override
                public void onStart() {
                }

                @Override
                public void onStop() {

                }

                @Override
                public void onProgress(int total, int progress) {

                }
            };
        }

        // 开始
        cb.onStart();
        /* boolean ifSaved = (Boolean) SharedPreferencesUtils.getParam(context, MMY_LIST, false);  // 默认是未烧录

        if (ifSaved) {
            cb.onStop();
            return true;
        }*/

        ArrayList<MMyBean> arrayList = MMy.LoadSDExcel (context, mmyPath);
        if (arrayList == null) {
            return false;
        }

        MMyAllTable mMyAllTable = new MMyAllTable ();
        mMyAllTable.openObject(context, "");
        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMyAllTable.getObject();

        // 删除表数据
        MMyAllTable.ClearTable(context, dbHelper);

        // 加载Excel文件到数据库中
        for (int i=0;i<arrayList.size();i++) {
            MMyBean mMyBean = arrayList.get(i);
            // 入库
            MMyAllTable.InsertMMY(context, mMyBean, dbHelper);
            cb.onProgress(arrayList.size(), i);
            if (arrayList.size() <= (i+1)) {
                SharedPreferencesUtils.setParam(context, MMY_LIST, true);
            }
        }
        mMyAllTable.closeObject();
        cb.onStop();
        return true;
    }

    /**
     * 加载MMy数据库, 将Excel转成数据库存储
     */
    @SuppressWarnings("unchecked")
    public static boolean LoadMMy (Activity context, MMyLoadCb cb) {

        if (cb == null) {
            cb = new MMyLoadCb() {
                @Override
                public void onStart() {
                }

                @Override
                public void onStop() {

                }

                @Override
                public void onProgress(int total, int progress) {

                }
            };
        }

        // 开始
        cb.onStart();
//        boolean ifSaved = (Boolean) SharedPreferencesUtils.getParam(context, MMY_LIST, false);  // 默认是未烧录
//
//        if (ifSaved) {
//            cb.onStop();
//            return true;
//        }
//
//        ArrayList<MMyBean> arrayList = MMy.LoadAssetsExcel (context);
//        if (arrayList == null) {
//            return false;
//        }
//
//        MMyAllTable mMyAllTable = new MMyAllTable ();
//        mMyAllTable.openObject(context, "");
//        SQLiteOpenHelper dbHelper = (SQLiteOpenHelper) mMyAllTable.getObject();
//
//        // 加载Excel文件到数据库中
//        for (int i=0;i<arrayList.size();i++) {
//            MMyBean mMyBean = arrayList.get(i);
//            // 入库
//            MMyAllTable.InsertMMY(context, mMyBean, dbHelper);
//            cb.onProgress(arrayList.size(), i);
//            if (arrayList.size() <= (i+1)) {
//                SharedPreferencesUtils.setParam(context, MMY_LIST, true);
//            }
//        }
//
//        mMyAllTable.closeObject();
        while(!FileDowload.HaveData(context)){
            try {
                Log.d("下載","下載失敗");
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Log.d("下載","下載成功");
        cb.onStop();
        return true;
    }

    public interface MMyLoadCb {
        public void onStart ();
        public void onStop ();
        public void onProgress (int total, int progress);
    }

    /**
     * 获取所有的MMy车型配置
     * @return
     */
    public static ArrayList<MMyBean> getAllMMy (Context context) {
        return MMyAllTable.ReadAllMMy(context);
    }

    /**
     * 获取我的MMy (常用的)
     * @return
     */
    public static ArrayList<MMyBean> getMyMMy (Context context) {
        return MMyAllTable.ReadMyAllMMy(context);
    }

    /**
     * 根据制造商来获取收藏的车型信息
     * @return
     */
    public static ArrayList<MMyBean> getMyMMyWithMake (Context context, String make) {
        return MMyAllTable.ReadMyMMyWithMake(context, make);
    }

    /**
     * 根据制造商来获取车型信息
     * @return
     */
    public static ArrayList<MMyBean> getMMyWithMake (Context context, String make) {
        return MMyAllTable.ReadMMyWithMake(context, make);
    }

    /**
     * 根据MMy_num来获取车型信息
     * @return
     */
    public static ArrayList<MMyBean> getMMyWithMMyNum (Context context, String mmy_num) {
        return MMyAllTable.ReadMMyWithMMyNum(context, mmy_num);
    }

    /**
     * 根据prd_num来获取车型信息
     * @return
     */
    public static ArrayList<MMyBean> getMMyWithPrdNum (Context context, String prd_num) {
        return MMyAllTable.ReadMMyWithPrdNum(context, prd_num);
    }

    /**
     * 访问+1, 根据这个访问排名返回我的MMy的排序
     * @return true-成功添加访问量, false-添加访问量失败
     */
    public static boolean visit (Context context, String make_id) {
        return MMyAllTable.IncVisitOfMMy(context, make_id);
    }

    /**
     * 根据logo文件名获取make
     * @return
     */
    public static String getMakeWithLogoFileName (String logoFileName) {
        String[] sourceStrArray = logoFileName.split("_");

        // 取最后一个
        return sourceStrArray[sourceStrArray.length-1];
    }

    /**
     * 添加新的我的收藏
     * @return
     */
    public static boolean addMyLikeMMy (Context context, String make_id) {
        return MMyAllTable.IncVisitOfMMy(context, make_id);
    }

    /**
     * 移除我的收藏
     * @return
     */
    public static boolean rmMyLikeMMy (Context context, String make_id) {
        return MMyAllTable.RmMyMMy(context, make_id);
    }
}
