package com.orange.tpms.app;

import com.de.rocket.app.RoApplication;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.lib.test.MTest;
import com.orange.tpms.utils.Command;

/**
 * 进程唯一的Application
 * Created by haide.yin() on 2019/3/26 14:35.
 */
public class TPMSApp extends RoApplication {

    public static String TAG = TPMSApp.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        // 硬件初始化
//        HardwareApp.getInstance()
//                .setApplication(this)       //设置Application
//                .setEnableHareware(true)   //是否开启硬件功能，可用于关闭调试UI
//                .onCreate();
        //最后统一调用初始化硬件

//        MTest mTest = new MTest();
//        mTest.setContext(this);
//        mTest.setTPMS(this);
        //mTest.run();
    }
}
