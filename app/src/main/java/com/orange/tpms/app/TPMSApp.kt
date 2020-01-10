package com.orange.tpms.app

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.jianzhi.jzcrashhandler.CrashHandle
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.ue.activity.KtActivity

/**
 * 进程唯一的Application
 * Created by haide.yin() on 2019/3/26 14:35.
 */
class TPMSApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        // 硬件初始化
        HardwareApp.getInstance()
            .setApplication(this)       //设置Application
            .setEnableHareware(true)   //是否开启硬件功能，可用于关闭调试UI
            .onCreate()
        //最后统一调用初始化硬件
        CrashHandle.newInstance(this,KtActivity::class.java).setUP(CrashHandle.UPLOAD_CRASH_MESSAGE)
        //        MTest mTest = new MTest();
        //        mTest.setContext(this);
        //        mTest.setTPMS(this);
        //mTest.run();

    }

    companion object {
        var TAG = TPMSApp::class.java.simpleName
    }

}
