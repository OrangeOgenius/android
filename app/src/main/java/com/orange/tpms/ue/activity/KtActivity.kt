package com.orange.tpms.ue.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.KeyEvent.*
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.orange.blelibrary.blelibrary.BleActivity
import com.orange.blelibrary.blelibrary.Callback.DaiSetUp
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.Callback.Scan_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.db.share.SettingShare
import com.orange.tpms.mmySql.ItemDAO
import com.orange.tpms.ue.kt_frag.Frag_Idcopy_New
import com.orange.tpms.ue.kt_frag.Frag_Idcopy_original
import com.orange.tpms.ue.kt_frag.Frag_Program_Detail
import com.orange.tpms.ue.kt_frag.kt_splash
import com.orange.tpms.utils.HttpDownloader
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.OgCommand.StringHexToByte
import com.orange.tpms.utils.RxCommand
import kotlinx.android.synthetic.main.dataloading.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class KtActivity : BleActivity(), Scan_C {
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.e("觸控","$event")
        return super.onTouchEvent(event)
    }

    override fun GetScan(a: String?) {
        if (Fraging != null) {
            (Fraging as RootFragement).ScanContent(a!!)
        }
    }
    var donload = HttpDownloader()
    var BleCommand = com.orange.tpms.utils.BleCommand()
    var ObdCommand = com.orange.tpms.utils.ObdCommand()
    lateinit var xml: ArrayList<String>
    lateinit var itemDAO: ItemDAO
    lateinit var back: ImageView
    lateinit var logout: ImageView
    var uploadtimer = Timer()
    var Fragnumber = 0
    override fun ChangePageListener(tag: String, frag: Fragment) {
        Log.e("switch", tag)
        Log.e("switch", "count:" + supportFragmentManager.backStackEntryCount)
        back.setImageResource(R.mipmap.back)
        back.setOnClickListener { GoBack() }
        if (supportFragmentManager.backStackEntryCount != 0) {
            back.setImageResource(R.mipmap.back)
            back.visibility = View.VISIBLE
            back.setOnClickListener { GoBack() }
        } else {
            back.visibility = View.GONE
        }
        OgCommand.NowTag = "${Fragnumber++}"
        if (Fragnumber > 100) {
            Fragnumber = 0
        }
        when (tag) {
            "Frag_home" -> {
                tit.text = resources.getString(R.string.app_o_genius)
            }
            "Frag_CheckSensor" -> {
                tit.text = resources.getString(R.string.app_home_check_sensor)
            }
            "Frag_Check_Sensor_Read" -> {
                tit.text = resources.getString(R.string.app_sensor_info_read)
            }
            "Frag_Check_Location" -> {
                tit.text = resources.getString(R.string.app_home_check_sensor)
            }
            "Frag_Program_Sensor" -> {
                tit.text = resources.getString(R.string.app_program)
            }
            "Frag_Id_Copy" -> {
                tit.text = resources.getString(R.string.app_home_id_copy)
            }
            "Frag_Relearm" -> {
                tit.text = resources.getString(R.string.Relearn_Procedure)
            }
            "Frag_Pad_IdCopy" -> {
                tit.text = "${resources.getString(R.string.Program_USB_PAD)}(${resources.getString(R.string.ID_COPY)})"
            }
            "Frag_Pad_Program" -> {
                tit.text = "${resources.getString(R.string.Program_USB_PAD)}(${resources.getString(R.string.Program)})"
            }
            "Frag_WebView" -> {
                tit.text = resources.getString(R.string.Online_shopping)
            }
            "Frag_Obd" -> {
                when (PublicBean.position) {
                    PublicBean.OBD_RELEARM -> {
                        tit.text = resources.getString(R.string.app_home_obdii_relearn)
                    }
                    PublicBean.ID_COPY_OBD -> {
                        tit.text = resources.getString(R.string.idcopyobd)
                    }
                }
            }
            "Frag_Setting" -> {
                tit.text = resources.getString(R.string.app_setting)
            }
        }
    }

    lateinit var titlebar: RelativeLayout
    lateinit var tit: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this) { instanceIdResult ->
                val mToken = instanceIdResult.token
                println("printing fcm token: $mToken")
            }
        FirebaseMessaging.getInstance().subscribeToTopic("update")
            .addOnCompleteListener( {
//             Toast("註冊成功")
            });
        val file = File("/sdcard/update/");
        val files19 = File("/sdcard/files19/");
        while (!file.exists()) {
            file.mkdirs()
        }
        while (!files19.exists()) {
            files19.mkdirs()
        }
        xml = ArrayList()
        setContentView(R.layout.activity_kt)
        tit = findViewById(R.id.textView12)
        back = findViewById(R.id.back)
        logout = findViewById(R.id.logout)
        titlebar = findViewById(R.id.toolbar)
        back.setOnClickListener {
            GoBack()
        }
        logout.setOnClickListener {

        }
        Laninit()
//tit.setOnClickListener {
//    var intent =  Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
//                intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
//                //当中false换成true,会弹出是否关机的确认窗体
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//}
        ShowTitleBar(false)
        ChangePage(kt_splash(), R.id.frage, "kt_splash", false)
        BleCommand.act = this
        uploadtimer.schedule(0, 1000 * 600) {
            GetXml()
            DataUpload()
        }
        PublicBean.OG_SerialNum = "SP:" + SettingShare.getDeviceId(this)
        ObdCommand.act = this
        FirebaseInstanceId.getInstance().instanceId
            .addOnSuccessListener(this) { instanceIdResult ->
                val mToken = instanceIdResult.token
                Log.e("token", mToken)
            }
    }

    override fun LoadingUI(a: String, pass: Int) {
        ShowDaiLog(R.layout.dataloading, false, false, DaiSetUp {
            it.tit.text = a
        })
    }

    override fun LoadingSuccessUI() {
        DaiLogDismiss()
    }

    override fun ConnectSituation(b: Boolean) {
        super.ConnectSituation(b)
        if (!b) {
            when (PublicBean.position) {
                PublicBean.PAD_PROGRAM -> {
                    GoMenu()
                }
                PublicBean.PAD_COPY -> {
                    GoMenu()
                }
                PublicBean.OBD_RELEARM -> {
                    GoBack("Frag_Obd")
                }
                PublicBean.ID_COPY_OBD -> {
                    GoBack("Frag_Obd")
                }
            }
        } else {
            BleCommand.act = this
            when (PublicBean.position) {
                PublicBean.PAD_PROGRAM -> {
                    Thread { BleCommand.Setserial(this) }.start()
                }
                PublicBean.PAD_COPY -> {
                    Thread { BleCommand.Setserial(this) }.start()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    override fun onResume() {
        super.onResume()
        SetNaVaGation(true)
    }

    override fun RX(a: String) {
        Log.w("BLEDATA", "RX:$a")
        RxCommand.RX(StringHexToByte(a), this)
    }

    fun ShowTitleBar(boolean: Boolean) {
        titlebar.visibility = if (boolean) View.VISIBLE else View.GONE
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        Log.e("event", "" + event)
        if (event.action == KeyEvent.ACTION_UP) {//只处理按下的动画,抬起的动作忽略
            Log.v("yhd-", "event:$event")
            //按键事件向Fragment分发
            if (Fraging != null) {
                (Fraging as RootFragement).dispatchKeyEvent(event)
            }
            //页面在顶层才会分发
            if(event.keyCode==KEYCODE_DEL){
                GoBack()
                return false
            }else if(event.keyCode==KEYCODE_BACK){
                val even=KeyEvent(ACTION_DOWN,KEYCODE_DEL)
                dispatchKeyEvent(even)
                return false
            }
        }
        if (event.keyCode == 19 || event.keyCode == 20 || event.keyCode == 21 || event.keyCode == 22||event.keyCode==66) {
            return false
        }
        return superDispatchKeyEvent(event)
    }

    override fun DiaDispath(event: KeyEvent) {
        Log.e("Dia","$event")
        if(event.keyCode==KEYCODE_BACK){DaiLogDismiss()}
        if ((event.keyCode==KEYCODE_ENTER||event.keyCode == 19 || event.keyCode == 20 || event.keyCode == 21 || event.keyCode == 22 )&&event.action==ACTION_UP) {
            if(diaid==R.layout.sensor_way_dialog){
              if(mDialog!!.findViewById<RelativeLayout>(R.id.scan).alpha==1F){focus=0}
                if(mDialog!!.findViewById<RelativeLayout>(R.id.trigger).alpha==1F){focus=1}
                if(mDialog!!.findViewById<RelativeLayout>(R.id.keyin).alpha==1F){focus=2}
                when(event.keyCode ){
                    19->{SensorWayChange(-1)}
                    20->{SensorWayChange(1)}
                    KEYCODE_ENTER->{
                        if(focus==2){
                            when(NowFrage){
                                "Frag_Program_Detail"->{
                                    (Fraging as Frag_Program_Detail).updateEditable(true)
                                    DaiLogDismiss()
                                }
                                "Frag_Idcopy_original"->{
                                    (Fraging as Frag_Idcopy_original).updateEditable(true)
                                    DaiLogDismiss()
                                }
                                "Frag_Idcopy_New"->{
                                    (Fraging as Frag_Idcopy_New).updateEditable(true)
                                    DaiLogDismiss()
                                }
                            }
                        }else{DaiLogDismiss()}
                    }
                }
            }
        }
    }
    var focus=0;
    fun SensorWayChange(a:Int){
        if(focus+a in 0..2){
            focus += a
            if (mDialog!!.isShowing&&diaid==R.layout.sensor_way_dialog) {
                mDialog!!.findViewById<RelativeLayout>(R.id.scan).alpha=0.5F
                mDialog!!.findViewById<RelativeLayout>(R.id.trigger).alpha=0.5F
                mDialog!!.findViewById<RelativeLayout>(R.id.keyin).alpha=0.5F
                when(focus){
                    0->{mDialog!!.findViewById<RelativeLayout>(R.id.scan).alpha=1F}
                    1->{mDialog!!.findViewById<RelativeLayout>(R.id.trigger).alpha=1F}
                    2->{mDialog!!.findViewById<RelativeLayout>(R.id.keyin).alpha=1F}
                }
            }
        }}
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
}
