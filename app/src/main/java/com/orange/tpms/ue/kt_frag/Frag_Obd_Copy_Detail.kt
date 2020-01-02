package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.jzchi.jzframework.tool.FormatConvert
import com.orange.tpms.Callback.Copy_C
import com.orange.tpms.Callback.Obd_C
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.adapter.obdadapter
import com.orange.tpms.bean.MMYQrCodeBean
import com.orange.tpms.bean.ObdBeans
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.ue.dialog.EmptyDialog
import com.orange.tpms.ue.dialog.SensorWay
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.VibMediaUtil
import kotlinx.android.synthetic.main.fragment_frag__obd__copy__detail.view.*
import kotlinx.android.synthetic.main.normal_dialog.*
import java.text.SimpleDateFormat
import java.util.*

class Frag_Obd_Copy_Detail : RootFragement(R.layout.fragment_frag__obd__copy__detail) , Copy_C {
    lateinit var adapter: obdadapter
    lateinit var beans: ObdBeans
    lateinit var dataReceiver: HardwareApp.DataReceiver
    lateinit var vibMediaUtil: VibMediaUtil
    lateinit var srec: String
    var ObdHex = "00"
    override fun viewInit() {
        rootview.tv_content.text = "${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        ObdHex = (activity as KtActivity).itemDAO.GetHex(
            PublicBean.SelectMake,
            PublicBean.SelectModel,
            PublicBean.SelectYear
        )
        srec = (activity!! as KtActivity).itemDAO.getPart(
            PublicBean.SelectMake,
            PublicBean.SelectModel,
            PublicBean.SelectYear
        )
        (activity!! as KtActivity).ObdCommand.donloads19=GetPro("obd"+srec,"OBDB_APP_TO001_191030")
        Log.e("srec", (activity!! as KtActivity).ObdCommand.donloads19)
        beans = ObdBeans()
        beans.rowcount = if ((activity!! as KtActivity).itemDAO.IsFiveTire(srec)) 6 else 5
        adapter = obdadapter(beans)
        rootview.rv_id_copy_neww.layoutManager = LinearLayoutManager(act)
        rootview.rv_id_copy_neww.adapter = adapter
        initview()
        rootview.bt_menue.setOnClickListener {
            JzActivity.getControlInstance().goMenu()
        }
        JzActivity.getControlInstance().showDiaLog(R.layout.sensor_way_dialog,false,false, object : SensorWay() {
            override fun dismess() {
                super.dismess()
                Downs19()
                beans.CanEdit=true
            }
        })

        rootview.bt_program.setOnClickListener {
            if(Check_Complete()){
                Program()
            }
        }
    }

    override fun Copy_Finish(boolean: Boolean) {
        run=false
        endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        handler.post {JzActivity.getControlInstance().closeDiaLog()}
    }


    override fun Copy_Next(success: Boolean, position: Int) {
        handler.post {
            vibMediaUtil.playBeep()
            copySuccess(position, success)
        }
    }
    fun copySuccess(index: Int, success: Boolean){
        beans.state[index]=if(success) ObdBeans.PROGRAM_SUCCESS else ObdBeans.PROGRAM_FALSE
        adapter.notifyDataSetChanged()
    }



    fun initview() {
        //音效与震动
        vibMediaUtil = VibMediaUtil(activity)
        HardwareApp.getInstance().switchScan(true)
        dataReceiver = object : HardwareApp.DataReceiver {
            override fun scanReceive() {

            }
            override fun scanMsgReceive(content: String) {
                JzActivity.getControlInstance().closeDiaLog()
                Log.v("yhd-", "content:$content")
                //兼容三种
                if (!content.contains(":") && !content.contains("*")) {
                    if (content != "nofound") {
                        JzActivity.getControlInstance().toast(R.string.app_invalid_sensor_qrcode)
                    } else {
                        JzActivity.getControlInstance().toast(R.string.app_scan_code_timeout)
                    }
                    return
                }
                var sensorid = ""
                if (content.contains("**")) {
                    sensorid = MMYQrCodeBean.toQRcodeBean(content).mmyNumber
                } else {
                    if (content.split(":", "*").size >= 3) {
                        sensorid = content.split(":", "*")[1]
                    }
                }
                if (TextUtils.isEmpty(sensorid)) {
                    JzActivity.getControlInstance().toast(R.string.app_invalid_sensor_qrcode)
                    return
                }
                vibMediaUtil.playBeep()
                updateSensorid(sensorid)
                adapter.notifyDataSetChanged()
            }

            override fun uart2MsgReceive(content: String) {

            }
        }
        HardwareApp.getInstance().addDataReceiver(dataReceiver)
    }
    override fun onKeyScan() {
        super.onKeyScan()
        if(run){return}
        run=true
        JzActivity.getControlInstance().showDiaLog(R.layout.normal_dialog, false, true, object:SetupDialog{
            override fun keyevent(event: KeyEvent): Boolean {
              return true
            }

            override fun setup(rootview: Dialog) {
                rootview.tit.text= act.resources.getString(R.string.Data_Loading)
            }

            override fun dismess() {
            }

        })
        HardwareApp.getInstance().scan()
        Thread{
            Thread.sleep(3000)
            handler.post {  JzActivity.getControlInstance().closeDiaLog() }
            run=false
        }.start()
    }
    fun updateSensorid(sensorid: String) {
        handler.post {
            if (!beans.NewSensor.contains(sensorid)) {
                Log.e("readid", sensorid)
                Log.e("readid", "" + beans.NewSensor.size)
                for (i in 0 until beans.NewSensor.size) {
                    if (beans.NewSensor[i] == "") {
                        beans.NewSensor[i] = sensorid
                        adapter.notifyDataSetChanged()
                        return@post
                    }
                }
            } else {
                JzActivity.getControlInstance().toast(R.string.app_sensor_repeated)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        vibMediaUtil.release()
        try {
            HardwareApp.getInstance().switchScan(false)
            HardwareApp.getInstance().removeDataReceiver(dataReceiver)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onKeyTrigger() {
        super.onKeyTrigger()
        Trigger()
    }
    var startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    var endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
fun Program(){
    if (run) {
        return
    }
    run = true

    JzActivity.getControlInstance().showDiaLog(R.layout.normal_dialog, false, true, object:SetupDialog{
        override fun dismess() {

        }

        override fun keyevent(event: KeyEvent): Boolean {
           return false
        }

        override fun setup(rootview: Dialog) {
            rootview.tit.text = act.resources.getString(R.string.Programming)
        }

    })
    startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    vibMediaUtil.playVibrate()
    PublicBean.NewSensorList=beans.NewSensor
    PublicBean.SensorList=beans.OldSemsor
    if (Check_Complete()) {
        when(PublicBean.position){
            PublicBean.ID_COPY_OBD->{Thread {
                OgCommand.IdCopy(this,ObdHex) }.start()}
            PublicBean.OBD_RELEARM->{
                Thread{ (activity!! as KtActivity).ObdCommand.setTireId(beans.NewSensor, Obd_C{
                    handler.post {
                        run=false
                        if(it){
                            AllSuccess()
                        }else{
                            Allfalse()
                        }
                    }
                })
                }.start()
            }
        }
    } else {
        JzActivity.getControlInstance().closeDiaLog()
        run=false
        JzActivity.getControlInstance().toast(R.string.app_no_data_to_copy)
    }
}
fun Allfalse(){
    for(i in 0 until beans.state.size){
        beans.state[i]=ObdBeans.PROGRAM_FALSE
    }
    adapter.notifyDataSetChanged()
    JzActivity.getControlInstance().closeDiaLog()
}
    fun AllSuccess(){
        beans.OldSemsor=beans.NewSensor
        for(i in 0 until beans.state.size){
            beans.state[i]=ObdBeans.PROGRAM_SUCCESS
        }
        adapter.notifyDataSetChanged()
        JzActivity.getControlInstance().closeDiaLog()
    }
    fun Trigger() {
        if (run) {
            return
        }
        if(Check_Complete()){Program()
        return}
        run = true
        JzActivity.getControlInstance().showDiaLog(R.layout.normal_dialog, false, true, object :SetupDialog{
            override fun dismess() {

            }

            override fun keyevent(event: KeyEvent): Boolean {
                return false
            }

            override fun setup(rootview: Dialog) {
                rootview.tit.text=act.resources.getString(R.string.Data_Loading)
            }

        })
        Thread {
            val a = OgCommand.GetId(ObdHex, "00")
            handler.post {
                run = false
                if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Obd_Copy_Detail")) {
                    return@post
                }
                vibMediaUtil.playBeep()
                JzActivity.getControlInstance().closeDiaLog()
                if (a.success) {
                    updateSensorid(a.id)
                    adapter.notifyDataSetChanged()
                } else {
                    JzActivity.getControlInstance().toast(resources.getString(R.string.app_read_failed))
                }
            }
        }.start()
    }

    fun Downs19() {
        JzActivity.getControlInstance().showDiaLog(R.layout.normal_dialog, false, true, object :SetupDialog{
            override fun dismess() {

            }

            override fun keyevent(event: KeyEvent): Boolean {
                return false
            }

            override fun setup(rootview: Dialog) {
                rootview.tit.text = act.resources.getString(R.string.Programming)
            }

        })
        handler.post {
//            (activity!! as KtActivity).back.isEnabled = false
        }
        Thread {
            if (!(activity!! as KtActivity).ObdCommand.HandShake()) {
                (activity!! as KtActivity).ObdCommand.Reboot()
            }
            (activity!! as KtActivity).ObdCommand.AskVersion()
            if ((activity!! as KtActivity).ObdCommand.AppVersion == FormatConvert.bytesToHex(
                    (activity!! as KtActivity).ObdCommand.donloads19.replace(
                        ".srec",
                        ""
                    ).toByteArray()
                )
            ) {
                if ((activity!! as KtActivity).ObdCommand.GoApp()) {
                    handler.post {
                        SetId()
//                        (activity!! as KtActivity).back.isEnabled = true
                    }
                    return@Thread
                }
            } else {
                if (!(activity!! as KtActivity).ObdCommand.WriteVersion() || !(activity!! as KtActivity).ObdCommand.GoBootloader()) {
                    handler.post {
                        JzActivity.getControlInstance().toast("燒錄失敗")
                        JzActivity.getControlInstance().closeDiaLog()
                        JzActivity.getControlInstance().showDiaLog(R.layout.program_false, false, false, object : SetupDialog  {
                            override fun dismess() {

                            }

                            override fun keyevent(event: KeyEvent): Boolean {
                                return false
                            }

                            override fun setup(rootview: Dialog) {
                                rootview.findViewById<TextView>(R.id.ok).setOnClickListener { JzActivity.getControlInstance().closeDiaLog()
                                    JzActivity.getControlInstance().goBack("Frag_Obd")
                                }
                                rootview.findViewById<TextView>(R.id.yes).setOnClickListener {
                                    JzActivity.getControlInstance().closeDiaLog()
                                    Downs19() }
                            }

                        });
//                        (activity!! as KtActivity).back.isEnabled = true
                    }
                    return@Thread
                }
            }
            Thread.sleep(2000)
            val Pro =
                (JzActivity.getControlInstance().getRootActivity() as KtActivity).ObdCommand.WriteFlash((JzActivity.getControlInstance().getRootActivity() as KtActivity), srec, 296)
            handler.post {
//                (activity!! as KtActivity).back.isEnabled = true
//                (activity!! as KtActivity).LoadingSuccessUI()
                if (Pro) {
                    JzActivity.getControlInstance().toast("燒錄成功")
//
//                    "obd$name"
                    SetId()
                } else {
                    JzActivity.getControlInstance().toast("燒錄失敗")
                    JzActivity.getControlInstance().closeDiaLog()
                    JzActivity.getControlInstance().showDiaLog(R.layout.program_false, false, false, object : SetupDialog {
                        override fun dismess() {

                        }

                        override fun keyevent(event: KeyEvent): Boolean {
                            return false
                        }

                        override fun setup(rootview: Dialog) {
                            rootview.findViewById<TextView>(R.id.ok).setOnClickListener { JzActivity.getControlInstance().closeDiaLog()
                                JzActivity.getControlInstance().goBack("Frag_Obd")
                            }
                            rootview.findViewById<TextView>(R.id.yes).setOnClickListener {
                                JzActivity.getControlInstance().closeDiaLog()
                                Downs19() }
                        }

                    });

                }
            }
        }.start()
    }

    fun SetId() {
        JzActivity.getControlInstance().showDiaLog(R.layout.normal_dialog, false, true, object : SetupDialog {
            override fun dismess() {

            }

            override fun keyevent(event: KeyEvent): Boolean {
               return false
            }

            override fun setup(rootview: Dialog) {
                rootview.tit.text= act.resources.getString(R.string.Data_Loading)
            }

        })
        Thread {
            val a = (activity!! as KtActivity).ObdCommand.GetId(if (beans.rowcount == 6) "05" else "04");
            handler.post {
                JzActivity.getControlInstance().closeDiaLog()
                if (a.success) {
                    beans.add(a.LF, "", ObdBeans.PROGRAM_WAIT)
                    beans.add(a.RF, "", ObdBeans.PROGRAM_WAIT)
                    beans.add(a.LR, "", ObdBeans.PROGRAM_WAIT)
                    beans.add(a.RR, "", ObdBeans.PROGRAM_WAIT)
                    if (beans.rowcount == 6) {
                        beans.add(a.SP, "", ObdBeans.PROGRAM_WAIT)
                    }
                    adapter.notifyDataSetChanged()
                    Log.e("ID", a.LF)
                    Log.e("ID", a.RF)
                    Log.e("ID", a.LR)
                    Log.e("ID", a.RR)
                    Log.e("ID", a.SP)
                } else {
                    JzActivity.getControlInstance().closeDiaLog()
                    JzActivity.getControlInstance().toast("車種選擇錯誤")
                    JzActivity.getControlInstance().goBack("Frag_Obd")
                }
            }
        }.start()
    }

    fun Clear() {
        for (i in 0 until beans.NewSensor.size) {
            beans.NewSensor[i] = ""
        }
    }

    fun Check_Complete(): Boolean {
        if(beans.NewSensor.size<beans.rowcount-1){return false}
        for (i in 0 until beans.rowcount - 1) {
            if (beans.NewSensor[i] == "") {
                return false
            }
        }
        return true
    }
}
