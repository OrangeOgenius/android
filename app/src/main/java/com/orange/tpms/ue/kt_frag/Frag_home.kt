package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.HttpCommand.SensorRecord
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.mmySql.ItemDAO
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.PackageUtils
import kotlinx.android.synthetic.main.activity_frag_home.view.*
import kotlinx.android.synthetic.main.check_update.*


class Frag_home : RootFragement(R.layout.activity_frag_home) {
    override fun viewInit() {
        refresh=true
        if(!GetPro("Firebasetitle","nodata").equals("nodata")){
            JzActivity.getControlInstance().showDiaLog(R.layout.check_update, false, false, object :SetupDialog{
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                    JzActivity.getControlInstance().closeDiaLog()
                    return false
                }

                override fun setup(rootview: Dialog) {
                    rootview.content.text=GetPro("Firebasebody","nodata")
                    rootview.cancel.setOnClickListener { JzActivity.getControlInstance().closeDiaLog()}
                    rootview.yes.setOnClickListener {
                        PublicBean.Update=true
                        JzActivity.getControlInstance().closeDiaLog()
                        JzActivity.getControlInstance().changeFrag(Frag_Update(),R.id.frage,"Frag_Update",true)
                    }
                    if(GetPro("Firebasebody","nodata").trim().isEmpty()||GetPro("Firebasebody","nodata").trim().equals("nodata")){
                        rootview.content.visibility=View.GONE
                    }
                }
            })
        }
        butt.clear()
        FuBt.clear()
        focus=0
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
        val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        PublicBean.admin = profilePreferences.getString("admin", "")
        PublicBean.password = profilePreferences.getString("password", "")
        rootview.btn_idcopy_obd.setOnClickListener {
            PublicBean.position = PublicBean.ID_COPY_OBD
            JzActivity.getControlInstance().changeFrag(Frag_Obd(), R.id.frage, "Frag_Obd", true)
        }
        rootview.btn_obd_relearm.setOnClickListener {
            PublicBean.position = PublicBean.OBD_RELEARM
            JzActivity.getControlInstance().changeFrag(Frag_Obd(), R.id.frage, "Frag_Obd", true)
        }
        rootview.bt_check_sensor.setOnClickListener {
            PublicBean.position = PublicBean.檢查傳感器
            JzActivity.getControlInstance().changeFrag(Frag_CheckSensor(), R.id.frage, "Frag_CheckSensor", true)
        }
        rootview.bt_program_sensor.setOnClickListener {
            PublicBean.position = PublicBean.燒錄傳感器
            JzActivity.getControlInstance().changeFrag(Frag_Program_Sensor(), R.id.frage, "Frag_Program_Sensor", true)
        }
        rootview.bt_sensor_idcopy.setOnClickListener {
            PublicBean.position = PublicBean.複製傳感器
            JzActivity.getControlInstance().changeFrag(Frag_Id_Copy(), R.id.frage, "Frag_Id_Copy", true)
        }
        rootview.bt_setting.setOnClickListener {
            PublicBean.position = PublicBean.設定
            JzActivity.getControlInstance().changeFrag(Frag_Setting(), R.id.frage, "Frag_Setting", true)
        }
        rootview.btn_relearn.setOnClickListener {
            PublicBean.position = PublicBean.學碼步驟
            JzActivity.getControlInstance().changeFrag(Frag_Relearm(), R.id.frage, "Frag_Relearm", true)
        }
        rootview.bt_pad_copy.setOnClickListener {
            PublicBean.position = PublicBean.PAD_COPY
            JzActivity.getControlInstance().changeFrag(Frag_Pad_IdCopy(), R.id.frage, "Frag_Pad_IdCopy", true)
        }
        rootview.bt_pad_program.setOnClickListener {
            PublicBean.position = PublicBean.PAD_PROGRAM
            JzActivity.getControlInstance().changeFrag(Frag_Pad_IdCopy(), R.id.frage, "Frag_Pad_Program", true)
        }
        rootview.bt_shopping.setOnClickListener {
            PublicBean.position = PublicBean.Go_Web
            JzActivity.getControlInstance().changeFrag(Frag_WebView(), R.id.frage, "Frag_WebView", true)
        }
        (activity as KtActivity).itemDAO = ItemDAO(activity!!);
        val mmyname = GetPro("mmyname", "nodata")
        SensorRecord.DB_Version = if (mmyname.length > 19) mmyname.substring(16) else mmyname
//        Thread{OgCommand.reboot()}.start()
        Log.e("version_internet", GetPro("mcu", "no").replace(".x2", ""))
        Log.e("version_local", GetPro("Version", "no"))
        Log.e("Version_APP", GetPro("apk", "" + PackageUtils.getVersionCode(act)).replace(".apk", ""))
    }

    var focus=0
    var butt=ArrayList<ImageView>()
    var FuBt=ArrayList<RelativeLayout>()

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
