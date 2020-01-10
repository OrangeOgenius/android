package com.orange.tpms.ue.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.annotations.SerializedName
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.DownloadCallback
import com.orange.jzchi.jzframework.tool.LanguageUtil
import com.orange.tpms.BleManager
import com.orange.tpms.Brocast.ScreenReceiver
import com.orange.tpms.Callback.Scan_C
import com.orange.tpms.Callback.Update_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.db.share.SettingShare
import com.orange.tpms.mmySql.ItemDAO
import com.orange.tpms.ue.kt_frag.Frag_Manager
import com.orange.tpms.ue.kt_frag.kt_splash
import com.orange.tpms.utils.BleCommand
import com.orange.tpms.utils.FileDowload.*
import com.orange.tpms.utils.ObdCommand
import com.orange.tpms.utils.OgCommand
import kotlinx.android.synthetic.main.activity_kt.*
import java.io.File
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class KtActivity : JzActivity(), Scan_C {
    companion object {
        var beta = true
    }

    var temppass = ""
    lateinit var BleManager: BleManager
    override fun keyEventListener(event: KeyEvent): Boolean {
        Log.e("event", "" + event)
        if (event.action == KeyEvent.ACTION_UP) {//只处理按下的动画,抬起的动作忽略
            Log.v("yhd-", "event:${event.keyCode}")
            //页面在顶层才会分发
            if (event.keyCode == KEYCODE_DEL) {
                JzActivity.getControlInstance().goBack()
                return false
            } else if (event.keyCode == KEYCODE_BACK) {
                val even = KeyEvent(ACTION_DOWN, KEYCODE_DEL)
                dispatchKeyEvent(even)
                return false
            }
            if (event.keyCode == 19 || event.keyCode == 20 || event.keyCode == 21 || event.keyCode == 22 || event.keyCode == 66) {
                if (event.keyCode == 19) {
                    temppass = ""
                }
                temppass += "${event.keyCode}"
                if (temppass == "1920212266") {
                    val intent = Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                } else if (temppass == "1920202121222266") {
                    JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog, false, false)
                    beta = true
                    Thread {
                        val caller = object : Update_C {
                            override fun Finish(a: Boolean) {
                                if (a) {
                                    JzActivity.getControlInstance()
                                        .apkDownload("https://bento2.orange-electronic.com/Orange%20Cloud/Beta/Drive/OG/APP%20Software/Beta.apk",
                                            object : DownloadCallback {
                                                override fun progress(a: Int) {

                                                }

                                                override fun result(a: Boolean) {
                                                    handler.post {
                                                        beta=false
                                                        JzActivity.getControlInstance().closeDiaLog()
                                                        if (a) {
                                                            JzActivity.getControlInstance().openAPK()
                                                        } else {
                                                            JzActivity.getControlInstance().toast("下載失敗")
                                                        }
                                                    }
                                                }
                                            })
                                } else {
                                    handler.post {
                                        beta=false
                                        JzActivity.getControlInstance().closeDiaLog() }
                                }
                            }

                            override fun Updateing(progress: Int) {

                            }
                        }
                        if (!DownMMy(JzActivity.getControlInstance().getRootActivity()) || !DownAllS19(
                                JzActivity.getControlInstance().getRootActivity(),
                                caller
                            )
                        ) {
                            caller.Finish(false);
                        } else {
                            caller.Finish(true)
                        }

                    }.start()
                } else if (temppass == "1920202222212166") {
                    JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog, false, false)
                    Thread {
                        val result = FileDonload(
                            this.applicationContext.filesDir.path + "/update.x2",
                            "https://bento2.orange-electronic.com/Orange%20Cloud/Beta/Drive/OG/Firmware/Beta.x2", 30
                        ) { progress ->

                        }
                        if (result) {
                            OgCommand.reboot()
                            handler.post {
                                JzActivity.getControlInstance().closeDiaLog()
                                val intent = Intent(applicationContext, KtActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                android.os.Process.killProcess(android.os.Process.myPid())
                            }
                        }
                    }.start()

                }
                if (temppass.length > 50) {
                    temppass = ""
                }
                return false
            }
            return true
        } else if ((event.action == KeyEvent.ACTION_DOWN)) {
            return !(event.keyCode == 19 || event.keyCode == 20 || event.keyCode == 21 || event.keyCode == 22 || event.keyCode == 66)
        }
        return false
    }

    override fun viewInit(rootview: View) {
        hookWebView()
        BleManager = BleManager(this)
        FirebaseMessaging.getInstance().subscribeToTopic("update")
            .addOnCompleteListener {
                //             Toast("註冊成功")
            };
        val file = File("/sdcard/update/");
        val files19 = File("/sdcard/files19/");
        while (!file.exists()) {
            file.mkdirs()
        }
        while (!files19.exists()) {
            files19.mkdirs()
        }
        xml = ArrayList()
        Laninit()
        uploadtimer.schedule(0, 1000 * 600) {
            GetXml()
            DataUpload()
        }
        PublicBean.OG_SerialNum = "SP:" + SettingShare.getDeviceId(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this) { instanceIdResult ->
                val mToken = instanceIdResult.token
                Log.e("token", mToken)
            }
        val sOnBroadcastReciver = ScreenReceiver()
        val recevierFilter = IntentFilter()
        recevierFilter.addAction(Intent.ACTION_SCREEN_ON)
        recevierFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(sOnBroadcastReciver, recevierFilter)
        while (!JzActivity.getControlInstance().isFrontDesk()) {
            Thread.sleep(100)
        }
        JzActivity.getControlInstance().setHome(kt_splash(), "kt_splash")
        BleCommand = BleCommand()
        ObdCommand = ObdCommand()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.e("觸控", "$event")
        return super.onTouchEvent(event)
    }

    override fun GetScan(a: String?) {
        if (Fraging != null) {
            (Fraging as RootFragement).ScanContent(a!!)
        }
    }

    lateinit var BleCommand: BleCommand
    lateinit var ObdCommand: ObdCommand
    lateinit var xml: ArrayList<String>
    lateinit var itemDAO: ItemDAO

    var uploadtimer = Timer()
    var Fragnumber = 0
    override fun changePageListener(tag: String, frag: androidx.fragment.app.Fragment) {
        val mainfrag = JzActivity.getControlInstance().findFragByTag("Frag_Manager")
        if (mainfrag is Frag_Manager && tag != "Frag_Manager") {
            Log.e("switch", tag)
            Log.e("switch", "count:" + supportFragmentManager.backStackEntryCount)
            mainfrag.back.setImageResource(R.mipmap.back)
            if (beta) {
                mainfrag.titlebar.setBackgroundResource(R.color.material_deep_teal_200)
            }
            mainfrag.back.setOnClickListener { JzActivity.getControlInstance().goBack() }
            if (supportFragmentManager.backStackEntryCount != 0) {
                mainfrag.back.setImageResource(R.mipmap.back)
                mainfrag.back.visibility = View.VISIBLE
                mainfrag.back.setOnClickListener { JzActivity.getControlInstance().goBack() }
            } else {
                mainfrag.back.visibility = View.GONE
            }
            OgCommand.NowTag = "${Fragnumber++}"
            if (Fragnumber > 100) {
                Fragnumber = 0
            }
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            if (beta) {
                mainfrag.tit.text = "Beta版本"
                return
            }
            when (tag) {
                "Frag_home" -> {
                    mainfrag.tit.text = resources.getString(R.string.app_o_genius)
                }
                "Frag_CheckSensor" -> {
                    mainfrag.tit.text = resources.getString(R.string.app_home_check_sensor)
                }
                "Frag_Check_Sensor_Read" -> {
                    mainfrag.tit.text = resources.getString(R.string.app_sensor_info_read)
                }
                "Frag_Check_Location" -> {
                    mainfrag.tit.text = resources.getString(R.string.app_home_check_sensor)
                }
                "Frag_Program_Sensor" -> {
                    mainfrag.tit.text = resources.getString(R.string.app_program)
                }
                "Frag_Id_Copy" -> {
                    mainfrag.tit.text = resources.getString(R.string.app_home_id_copy)
                }
                "Frag_Relearm" -> {
                    mainfrag.tit.text = resources.getString(R.string.Relearn_Procedure)
                }
                "Frag_Pad_IdCopy" -> {
                    mainfrag.tit.text =
                        "${resources.getString(R.string.Program_USB_PAD)}(${resources.getString(R.string.ID_COPY)})"
                }
                "Frag_Pad_Program" -> {
                    mainfrag.tit.text =
                        "${resources.getString(R.string.Program_USB_PAD)}(${resources.getString(R.string.Program)})"
                }
                "Frag_WebView" -> {
                    mainfrag.tit.text = resources.getString(R.string.Online_shopping)
                }
                "Frag_Obd" -> {
                    when (PublicBean.position) {
                        PublicBean.OBD_RELEARM -> {
                            mainfrag.tit.text = resources.getString(R.string.app_home_obdii_relearn)
                        }
                        PublicBean.ID_COPY_OBD -> {
                            mainfrag.tit.text = resources.getString(R.string.idcopyobd)
                        }
                    }
                }
                "Frag_Setting" -> {
                    mainfrag.tit.text = resources.getString(R.string.app_setting)
                }
            }
        } else {
            if (tag == "kt_splash") {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }

//
//    override fun LoadingUI(a: String, pass: Int) {
//        ShowDaiLog(R.layout.dataloading, false, false, object :SetupDialog {
//            it.tit.text = a
//        })
//    }
//
//    override fun LoadingSuccessUI() {
//        DaiLogDismiss()
//    }
//
//    override fun ConnectSituation(b: Boolean) {
//        super.ConnectSituation(b)
//        if (!b) {
//            when (PublicBean.position) {
//                PublicBean.PAD_PROGRAM -> {
//                    GoMenu()
//                }
//                PublicBean.PAD_COPY -> {
//                    GoMenu()
//                }
//                PublicBean.OBD_RELEARM -> {
//                    GoBack("Frag_Obd")
//                }
//                PublicBean.ID_COPY_OBD -> {
//                    GoBack("Frag_Obd")
//                }
//            }
//        } else {
//            BleCommand.act = this
//            when (PublicBean.position) {
//                PublicBean.PAD_PROGRAM -> {
//                    Thread { BleCommand.Setserial(this) }.start()
//                }
//                PublicBean.PAD_COPY -> {
//                    Thread { BleCommand.Setserial(this) }.start()
//                }
//            }
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
//        timer.cancel()
    }

    override fun onResume() {
        super.onResume()
        SetNaVaGation(true)
    }

    fun SetNaVaGation(hide: Boolean) {
        var intent = Intent("hbyapi.intent.action_hide_navigationbar")
        intent.putExtra("hide", hide)
        JzActivity.getControlInstance().getRootActivity().sendBroadcast(intent)
        var intent2 = Intent("hbyapi.intent.action_lock_panelbar")
        intent2.putExtra("state", hide)
        JzActivity.getControlInstance().getRootActivity().sendBroadcast(intent2)
    }

    @SuppressLint("InvalidWakeLockTag")
    override fun onPause() {
        super.onPause()
    }

//    override fun RX(a: String) {
//        Log.w("BLEDATA", "RX:$a")
//        RxCommand.RX(StringHexToByte(a), this)
//    }


    fun GetXml() {
        xml.clear()
        val profilePreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE)
        val a = profilePreferences.getInt("xml_count", 0)
        for (i in 0 until a) {
            var tmpdata = profilePreferences.getString("xml_$i", "nodata")
            if (!tmpdata.equals("nodata")) {
                xml.add(tmpdata)
            }
        }
    }

    fun SetXml() {
        val profilePreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE)
        profilePreferences.edit().putInt("xml_count", xml.size).commit()
        for (i in 0 until xml.size) {
            profilePreferences.edit().putString("xml_$i", xml[i]).commit()
        }
    }

    fun DataUpload() {
        while (xml.size > 100) {
            xml.removeAt(0)
        }
        val tmp = ArrayList<String>()
        for (i in 0 until xml.size) {
            if (Fuction._req(Fuction.wsdl, xml[i], Fuction.timeout).status == -1) {
                tmp.add(xml[i])
            }
        }
        xml = tmp
        SetXml()
    }

    val LOCALE_ENGLISH = "en"
    val LOCALE_CHINESE = "zh"
    val LOCALE_TAIWAIN = "tw"
    val LOCALE_ITALIANO = "it"
    val LOCALE_DE = "de"
    fun Laninit() {
        when (JzActivity.getControlInstance().getPro("Lan", LOCALE_ENGLISH)) {
            LOCALE_ENGLISH -> {
                LanguageUtil.updateLocale(
                    JzActivity.getControlInstance().getRootActivity(),
                    LanguageUtil.LOCALE_ENGLISH
                );
            }
            LOCALE_CHINESE -> {
                LanguageUtil.updateLocale(
                    JzActivity.getControlInstance().getRootActivity(),
                    LanguageUtil.LOCALE_CHINESE
                );
            }
            LOCALE_TAIWAIN -> {
                LanguageUtil.updateLocale(
                    JzActivity.getControlInstance().getRootActivity(),
                    LanguageUtil.LOCALE_TAIWAIN
                );
            }
            LOCALE_ITALIANO -> {
                LanguageUtil.updateLocale(
                    JzActivity.getControlInstance().getRootActivity(),
                    LanguageUtil.LOCALE_ITALIANO
                );
            }
            LOCALE_DE -> {
                LanguageUtil.updateLocale(JzActivity.getControlInstance().getRootActivity(), LanguageUtil.LOCALE_DE);
            }
        }
    }
    private fun hookWebView() {
        var factoryClass: Class<*>? = null
        try {
            factoryClass = Class.forName("android.webkit.WebViewFactory")
            var getProviderClassMethod: Method? = null
            var sProviderInstance: Any? = null

            if (Build.VERSION.SDK_INT == 23) {
                getProviderClassMethod = factoryClass!!.getDeclaredMethod("getProviderClass")
                getProviderClassMethod!!.isAccessible = true
                val providerClass = getProviderClassMethod.invoke(factoryClass) as Class<*>
                val delegateClass = Class.forName("android.webkit.WebViewDelegate")
                val constructor = providerClass.getConstructor(delegateClass)
                if (constructor != null) {
                    constructor.isAccessible = true
                    val constructor2 = delegateClass.getDeclaredConstructor()
                    constructor2.isAccessible = true
                    sProviderInstance = constructor.newInstance(constructor2.newInstance())
                }
            } else if (Build.VERSION.SDK_INT == 22) {
                getProviderClassMethod = factoryClass!!.getDeclaredMethod("getFactoryClass")
                getProviderClassMethod!!.isAccessible = true
                val providerClass = getProviderClassMethod.invoke(factoryClass) as Class<*>
                val delegateClass = Class.forName("android.webkit.WebViewDelegate")
                val constructor = providerClass.getConstructor(delegateClass)
                if (constructor != null) {
                    constructor.isAccessible = true
                    val constructor2 = delegateClass.getDeclaredConstructor()
                    constructor2.isAccessible = true
                    sProviderInstance = constructor.newInstance(constructor2.newInstance())
                }
            } else if (Build.VERSION.SDK_INT == 21) {// Android 21无WebView安全限制
                getProviderClassMethod = factoryClass!!.getDeclaredMethod("getFactoryClass")
                getProviderClassMethod!!.isAccessible = true
                val providerClass = getProviderClassMethod.invoke(factoryClass) as Class<*>
                sProviderInstance = providerClass.newInstance()
            }
            if (sProviderInstance != null) {
                val field = factoryClass!!.getDeclaredField("sProviderInstance")
                field.isAccessible = true
                field.set("sProviderInstance", sProviderInstance)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
