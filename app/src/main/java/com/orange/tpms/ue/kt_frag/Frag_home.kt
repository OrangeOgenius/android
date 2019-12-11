package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.orange.blelibrary.blelibrary.Callback.DaiSetUp
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.HttpCommand.SensorRecord
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.mmySql.ItemDAO
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.PackageUtils
import kotlinx.android.synthetic.main.activity_frag_home.view.*
import kotlinx.android.synthetic.main.check_update.*


class Frag_home : RootFragement() {
    var focus=0
    var butt=ArrayList<ImageView>()
    var FuBt=ArrayList<RelativeLayout>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(!GetPro("Firebasetitle","nodata").equals("nodata")){
            act.ShowDaiLog(R.layout.check_update, false, false, DaiSetUp {
                it.content.text=GetPro("Firebasebody","nodata")
                it.cancel.setOnClickListener { act.DaiLogDismiss() }
                it.yes.setOnClickListener {
                    PublicBean.Update=true
                    act.ChangePage(Frag_Update(),R.id.frage,"Frag_Update",true)
                }
                if(GetPro("Firebasebody","nodata").trim().isEmpty()||GetPro("Firebasebody","nodata").trim().equals("nodata")){
                    it.content.visibility=View.GONE
                }
            })
        }
        butt.clear()
        FuBt.clear()
        focus=0
        rootview = inflater.inflate(R.layout.activity_frag_home, container, false)
        rootview.i1.isSelected=true
        butt.add(rootview.i1)
        butt.add(rootview.i2)
        butt.add(rootview.i3)
        butt.add(rootview.i4)
        butt.add(rootview.i5)
        butt.add(rootview.i6)
        butt.add(rootview.i7)
        butt.add(rootview.i8)
        butt.add(rootview.i9)
        butt.add(rootview.i10)
        butt.add(rootview.i11)
        butt.add(rootview.i12)
        FuBt.add(rootview.bt_check_sensor)
        FuBt.add(rootview.bt_program_sensor)
        FuBt.add(rootview.bt_sensor_idcopy)
        FuBt.add(rootview.btn_idcopy_obd)
        FuBt.add(rootview.btn_obd_relearm)
        FuBt.add(rootview.btn_relearn)
        FuBt.add(rootview.bt_pad_program)
        FuBt.add(rootview.bt_pad_copy)
        FuBt.add(rootview.bt_shopping)
        FuBt.add(rootview.bt_setting)
        FuBt.add(rootview.bt_could)
        FuBt.add(rootview.bt_manual)
        Laninit()
        SleepInit()
        val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        PublicBean.admin = profilePreferences.getString("admin", "")
        PublicBean.password = profilePreferences.getString("password", "")
        rootview.btn_idcopy_obd.setOnClickListener {
            PublicBean.position = PublicBean.ID_COPY_OBD
            act.ChangePage(Frag_Obd(), R.id.frage, "Frag_Obd", true)
        }
        rootview.btn_obd_relearm.setOnClickListener {
            PublicBean.position = PublicBean.OBD_RELEARM
            act.ChangePage(Frag_Obd(), R.id.frage, "Frag_Obd", true)
        }
        rootview.bt_check_sensor.setOnClickListener {
            PublicBean.position = PublicBean.檢查傳感器
            act.ChangePage(Frag_CheckSensor(), R.id.frage, "Frag_CheckSensor", true)
        }
        rootview.bt_program_sensor.setOnClickListener {
            PublicBean.position = PublicBean.燒錄傳感器
            act.ChangePage(Frag_Program_Sensor(), R.id.frage, "Frag_Program_Sensor", true)
        }
        rootview.bt_sensor_idcopy.setOnClickListener {
            PublicBean.position = PublicBean.複製傳感器
            act.ChangePage(Frag_Id_Copy(), R.id.frage, "Frag_Id_Copy", true)
        }
        rootview.bt_setting.setOnClickListener {
            PublicBean.position = PublicBean.設定
            act.ChangePage(Frag_Setting(), R.id.frage, "Frag_Setting", true)
        }
        rootview.btn_relearn.setOnClickListener {
            PublicBean.position = PublicBean.學碼步驟
            act.ChangePage(Frag_Relearm(), R.id.frage, "Frag_Relearm", true)
        }
        rootview.bt_pad_copy.setOnClickListener {
            PublicBean.position = PublicBean.PAD_COPY
            act.ChangePage(Frag_Pad_IdCopy(), R.id.frage, "Frag_Pad_IdCopy", true)
        }
        rootview.bt_pad_program.setOnClickListener {
            PublicBean.position = PublicBean.PAD_PROGRAM
            act.ChangePage(Frag_Pad_IdCopy(), R.id.frage, "Frag_Pad_Program", true)

        }
        rootview.bt_shopping.setOnClickListener {
            PublicBean.position = PublicBean.Go_Web
            act.ChangePage(Frag_WebView(), R.id.frage, "Frag_WebView", true)
        }
        (activity as KtActivity).itemDAO = ItemDAO(activity!!);
        val mmyname = GetPro("mmyname", "nodata")
        SensorRecord.DB_Version = if (mmyname.length > 19) mmyname.substring(16) else mmyname
//        Thread{OgCommand.reboot()}.start()
        Log.e("version_internet", GetPro("mcu", "no").replace(".x2", ""))
        Log.e("version_local", GetPro("Version", "no"))
        Log.e("Version_APP", GetPro("apk", "" + PackageUtils.getVersionCode(act)).replace(".apk", ""))
        return rootview
    }

    override fun onLeft() {
        FocusReset(-1)
    }
    override fun onRight() {
        FocusReset(1)
    }
    override fun onTop() {
        FocusReset(-2)
    }
    override fun onDown() {
        FocusReset(2)
    }
    fun FocusReset(re:Int){
        if(focus+re>=0&&focus+re<butt.size){
            focus+=re
        }
        if(focus<4){rootview.sc.scrollY=0}
        rootview.sc.scrollY=focus/2*200
        for (i in butt){
            i.isSelected=false
        }
        butt[focus].isSelected=true
    }
    override fun enter(){
        FuBt[focus].performClick()
    }
}
