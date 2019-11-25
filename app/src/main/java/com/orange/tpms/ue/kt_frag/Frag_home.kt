package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.HttpCommand.SensorRecord
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.mmySql.ItemDAO
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.PackageUtils
import kotlinx.android.synthetic.main.activity_frag_home.view.*
import kotlinx.android.synthetic.main.bledialog.*
import java.io.File


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_home : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.activity_frag_home, container, false)
        Laninit()
        SleepInit()
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        PublicBean.admin=profilePreferences.getString("admin","")
        PublicBean.password=profilePreferences.getString("password","")
        rootview.btn_idcopy_obd.setOnClickListener {
            PublicBean.position=PublicBean.ID_COPY_OBD
            act.ChangePage(Frag_Obd(),R.id.frage,"Frag_Obd",true)
        }
        rootview.btn_obd_relearm.setOnClickListener {
            PublicBean.position=PublicBean.OBD_RELEARM
            act.ChangePage(Frag_Obd(),R.id.frage,"Frag_Obd",true)
        }
        rootview.bt_check_sensor.setOnClickListener {
            PublicBean.position=PublicBean.檢查傳感器
            act.ChangePage(Frag_CheckSensor(),R.id.frage,"Frag_CheckSensor",true)
        }
        rootview.bt_program_sensor.setOnClickListener {
            PublicBean.position=PublicBean.燒錄傳感器
            act.ChangePage(Frag_Program_Sensor(),R.id.frage,"Frag_Program_Sensor",true)
        }
        rootview.bt_sensor_idcopy.setOnClickListener {
            PublicBean.position=PublicBean.複製傳感器
            act.ChangePage(Frag_Id_Copy(),R.id.frage,"Frag_Id_Copy",true)
        }
        rootview.bt_setting.setOnClickListener {
            PublicBean.position=PublicBean.設定
            act.ChangePage(Frag_Setting(),R.id.frage,"Frag_Setting",true)
        }
        rootview.btn_relearn.setOnClickListener {
            PublicBean.position=PublicBean.學碼步驟
            act.ChangePage(Frag_Relearm(),R.id.frage,"Frag_Relearm",true)
        }
        rootview.bt_pad_copy.setOnClickListener {
            PublicBean.position=PublicBean.PAD_COPY
            act.ChangePage(Frag_Pad_IdCopy(),R.id.frage,"Frag_Pad_IdCopy",true)
        }
        rootview.bt_pad_program.setOnClickListener {
            PublicBean.position=PublicBean.PAD_PROGRAM
            act.ChangePage(Frag_Pad_IdCopy(),R.id.frage,"Frag_Pad_Program",true)

        }
        rootview.bt_shopping.setOnClickListener {
            PublicBean.position=PublicBean.Go_Web
            act.ChangePage(Frag_WebView(),R.id.frage,"Frag_WebView",true)
        }
        (activity as KtActivity).itemDAO = ItemDAO(activity!!);
         val mmyname=GetPro("mmyname","nodata")
         SensorRecord.DB_Version = if (mmyname.length > 19) mmyname.substring(16) else mmyname
//        Thread{OgCommand.reboot()}.start()
        Log.e("version_internet",GetPro("mcu","no").replace(".x2",""))
        Log.e("version_local",GetPro("Version","no"))
        Log.e("Version_APP",GetPro("apk", ""+PackageUtils.getVersionCode(act)).replace(".apk",""))
        CheckMcuUpdate()
        return rootview
    }
fun CheckMcuUpdate(){
    val internetversion= GetPro("mcu","no").replace(".x2","")
    val localversion=GetPro("Version","no")
    if(internetversion!="no"&&internetversion!=localversion){
        act.ShowDaiLog(R.layout.bledialog,false,false)
        act.mDialog!!.tit.text=resources.getString(R.string.app_new_version_detect)
        act.mDialog!!.yes.text=resources.getString(R.string.app_ok)
        act.mDialog!!.no.setOnClickListener { act.DaiLogDismiss() }
        act.mDialog!!.yes.setOnClickListener {
            act.DaiLogDismiss()
            act.ShowDaiLog(R.layout.update_dialog,false,false)
            Thread{
                OgCommand.reboot()
                handler.post {
                    act.DaiLogDismiss()
                    val intent2 = context!!.getPackageManager().getLaunchIntentForPackage(context!!.getPackageName())
                    context!!.startActivity(intent2)
                }
            }.start()}
     }else{CheckApk()}
}
fun CheckApk(){
    val version=PackageUtils.getVersionCode(act)
    Log.e("Version_APP",GetPro("apk", ""+PackageUtils.getVersionCode(act)).replace(".apk",""))
    Log.e("Version_APP",""+version)
    if(GetPro("apk", "$version").replace(".apk","")!="$version"){
        handler.post {
            try{
                val intent =  Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(File("/sdcard/update/update.apk")), "application/vnd.android.package-archive");//image/*
                startActivity(intent);//此处可能会产生异常（比如说你的MIME类型是打开视频，但是你手机里面没装视频播放器，就会报错）
            }catch (e:Exception){e.printStackTrace()}
        }
    }
}
}
