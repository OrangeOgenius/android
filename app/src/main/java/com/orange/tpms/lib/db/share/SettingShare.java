package com.orange.tpms.lib.db.share;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import com.orange.tpms.lib.utils.SharedPreferencesUtils;
import com.orange.tpms.utils.PackageUtils;

/**
 * 存储Setting配置
 * Created by rivers.zhang on 2019/4/20.
 */
public class SettingShare {

    public static String TAG = SettingShare.class.getName();

    public static final String TemperatureUnitKey = "TemperatureUnit";
    public static final String TirePressureUnitKey = "TirePressureUnit";
    public static final String NumeralSystemUnitKey = "NumeralSystemUnit";
    public static final String Language = "language";
    public static final String AutoLockTimeout = "AutoLockTimeout";
    public static final String Area = "Area";
    public static final String IfSystemAutoUpdate = "SystemAutoUpdate";
    public static final String CITY_LIST = "city_list";
    public static final String MMY_LIST = "mmy_list";

    public static final String SystemName = "systemName";
    public static final String SystemModule = "systemModule";
    public static final String SerialNumber = "serialNumber";
    public static final String Version = "version";
    public static final String DataVersion = "dataVersion";

    /**
     * 设置区域
     */
    public static void setArea (Context context, AreaUnitEnum area) {
        SharedPreferencesUtils.setParam(context, Area, (Integer)area.ordinal());
    }

    /**
     * 获取区域选择
     * @return 区域
     */
    public static AreaUnitEnum getArea (Context context) {
        int timeout_original = (Integer) SharedPreferencesUtils.getParam(context, Area, AreaUnitEnum.EUROPE.ordinal());
        // 获取自动锁屏时间配置
        AreaUnitEnum areaUnitEnum = AreaUnitEnum.valueOf(timeout_original);
        return areaUnitEnum;
    }

    /**
     * 设置自动锁屏时间
     */
    public static void setAutoLockTimeout (Context context, TimeOutUnitEnum timeout) {
        SharedPreferencesUtils.setParam(context, AutoLockTimeout, (Integer)timeout.ordinal());
    }

    /**
     * 获取自动锁屏时间
     * -1则为永不锁屏
     * @return
     */
    public static TimeOutUnitEnum getAutoLockTimeout (Context context) {
        int timeout_original = (Integer) SharedPreferencesUtils.getParam(context, AutoLockTimeout, TimeOutUnitEnum.MIN_NEVEL.ordinal());
        // 获取自动锁屏时间配置
        TimeOutUnitEnum timeOutUnitEnum = TimeOutUnitEnum.valueOf(timeout_original);
        return timeOutUnitEnum;
    }

    // 设置语言
    public static void setLanguage (Context context, String language) {
        SharedPreferencesUtils.setParam(context, Language, language);
    }

    // 获取语言
    public static String getLanguage (Context context) {
        return (String)SharedPreferencesUtils.getParam(context, Language, "English");
    }

    // 设置温度单位
    public static void setTemperatureUnit (Context context, TemperatureUnitEnum unit) {
        int ordinal = unit.ordinal();
        SharedPreferencesUtils.setParam(context, TemperatureUnitKey, ordinal);
    }

    // 设置压力单位
    public static void setTirePressureUnit (Context context, TirePressureUnitEnum unit) {
        int ordinal = unit.ordinal();
        SharedPreferencesUtils.setParam(context, TirePressureUnitKey, ordinal);
    }

    // 设置进制单位
    public static void setNumeralSystemUnit (Context context, NumeralSystemUnitEnum unit) {
        int ordinal = unit.ordinal();
        SharedPreferencesUtils.setParam(context, NumeralSystemUnitKey, ordinal);
    }

    /**
     * 设置是否系统自动更新
     */
    public static void setIfSystemAutoUpdate (Context context, boolean ifAutoUpdate) {
        SharedPreferencesUtils.setParam(context, IfSystemAutoUpdate, ifAutoUpdate);
    }

    /**
     * 获取是否系统自动更新(默认开)
     */
    public static boolean getIfSystemAutoUpdate (Context context) {
        return (Boolean) SharedPreferencesUtils.getParam(context, IfSystemAutoUpdate, true);
    }
    /**
     * 获取系统信息
     * @return
     */
    public static Information getSystemInformation (Context context) {
        SharedPreferences profilePreferences = context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        Information info = new Information();
        info.systemName = "O-GENIUS";
        info.sysModule = "v1";
        info.serialNumber = getDeviceId(context);
        info.version = ""+PackageUtils.getVersionCode(context);
        info.dataVersion = profilePreferences.getString("mmyname","MMY_EU_list_V0.5_191113").substring(12,17);
        return info;
    }
    public static String getVersionName(Context context) {
        //获取包管理器
        PackageManager pm = context.getPackageManager();
        //获取包信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            //返回版本号
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        return deviceId;
    }
    /**
     * 设置系统信息
     * @param information
     */
    public static void setSystemInformation (Information information) {

    }

    /**
     * 重置系统
     */
    public static void systemReset () {
        // 删除数据库Sqlite


        // 删除SharePrefrence

    }

    // 获取单位
    public static Unit getUnit (Context context) {
        int temp_ordinal = (Integer) SharedPreferencesUtils.getParam(context, TemperatureUnitKey, 0);
        int tire_ordinal = (Integer) SharedPreferencesUtils.getParam(context, TirePressureUnitKey, 0);
        int numeral_ordinal = (Integer) SharedPreferencesUtils.getParam(context, NumeralSystemUnitKey, 0);
        Unit unit = new Unit ();
        unit.numeralSystemUnit = NumeralSystemUnitEnum.valueOf(numeral_ordinal);
        unit.temperatureUnit = TemperatureUnitEnum.valueOf(temp_ordinal);
        unit.tirePressureUnit = TirePressureUnitEnum.valueOf(tire_ordinal);
        return unit;
    }

    public static enum TemperatureUnitEnum {
        C, F;
        public static TemperatureUnitEnum valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }

    public static enum NumeralSystemUnitEnum {
        Auto, Dec, Hex;
        public static NumeralSystemUnitEnum valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }

    public static enum TirePressureUnitEnum {
        Psi, Bar, Kpa;
        public static TirePressureUnitEnum valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }

    public static enum TimeOutUnitEnum {
        MIN_1, MIN_3, MIN_5, MIN_10, MIN_30, MIN_NEVEL;
        public static TimeOutUnitEnum valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }

    public static enum AreaUnitEnum {
        EUROPE, NORTH_AMERICA, ASIA;
        public static AreaUnitEnum valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }

    public static class Unit {
        public TemperatureUnitEnum temperatureUnit;        // 温度单位, 1-C, 2-F
        public NumeralSystemUnitEnum numeralSystemUnit;      // 进制单位, 1-Auto, 2-Dec, 3-Hex
        public TirePressureUnitEnum tirePressureUnit;       // 压力单位, 1-psi, 2-bar, 3-kpa
    }

    public static class Information {
        public String systemName;
        public String sysModule;
        public String serialNumber;
        public String version;
        public String dataVersion;
    }
}
