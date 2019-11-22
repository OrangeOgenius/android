package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.bean.MMYQrCodeBean
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.CustomTextWatcherForpad
import com.orange.tpms.utils.KeyboardUtil.hideEditTextKeyboard
import com.orange.tpms.utils.VibMediaUtil
import kotlinx.android.synthetic.main.fragment_frag__pad__keyin.view.*


class Frag_Pad_Keyin : RootFragement() {
    lateinit var navActivity: KtActivity
    var mmyNum=""
    var need=0
    lateinit var dataReceiver: HardwareApp.DataReceiver
    lateinit var vibMediaUtil: VibMediaUtil
    var ObdHex = "00"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        navActivity = activity as KtActivity
        mmyNum = navActivity.itemDAO.getMMY(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        if(!mmyNum.equals("")){
            need=navActivity.itemDAO.GetCopyId( mmyNum)
        }else{navActivity.finish()}
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__pad__keyin, container, false)
super.onCreateView(inflater, container, savedInstanceState)
        rootview.mmy_text6.text="${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        vibMediaUtil = VibMediaUtil(activity)
        rootview.Lft.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(need))
        rootview.Lft.addTextChangedListener(CustomTextWatcherForpad(rootview.Lft,need))
        rootview.Rrt.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(need))
        rootview.Rrt.addTextChangedListener(CustomTextWatcherForpad(rootview.Rrt,need))
        rootview.Rft.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(need))
        rootview.Rft.addTextChangedListener(CustomTextWatcherForpad(rootview.Rft,need))
        rootview.Lrt.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(need))
        rootview.Lrt.addTextChangedListener(CustomTextWatcherForpad(rootview.Lrt,need))
        hideEditTextKeyboard(rootview.Lft)
        hideEditTextKeyboard(rootview.Rrt)
        hideEditTextKeyboard(rootview.Rft)
        hideEditTextKeyboard(rootview.Lrt)
        CheckUnlinked()
        rootview.program.setOnClickListener {
            var lf=rootview.Lft.text.toString()
            var Rf=rootview.Rft.text.toString()
            var Rr=rootview.Rrt.text.toString()
            var lr=rootview.Lrt.text.toString()
            if(!rootview.Lft.isEnabled){lf="00000000"}
            if(!rootview.Rft.isEnabled){Rf="00000000"}
            if(!rootview.Rrt.isEnabled){Rr="00000000"}
            if(!rootview.Lrt.isEnabled){lr="00000000"}
            if(lf.length<need||Rf.length<need||Rr.length<need||lr.length<need){
                Toast.makeText(navActivity,resources.getString(R.string.ID_code_should_be_8_characters).replace("8",""+need),
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            first=false
            PublicBean.WriteLf=lf
            PublicBean.WriteLr=lr
            PublicBean.WriteRf=Rf
            PublicBean.WriteRr=Rr
            act.ChangePage(Frag_Pad_Program_Detail(), R.id.frage,"Frag_Pad_Program_Detail",false);
        }
        act.ShowDaiLog(R.layout.sensor_way_dialog,false,false)
        act.mDialog!!.findViewById<RelativeLayout>(R.id.scan).setOnClickListener {
            act.DaiLogDismiss()
        }
        act.mDialog!!.findViewById<RelativeLayout>(R.id.trigger).setOnClickListener {
            act.DaiLogDismiss()
        }
        act.mDialog!!.findViewById<RelativeLayout>(R.id.keyin).setOnClickListener {
            act.DaiLogDismiss()
            rootview.Lft
        }
        ObdHex=(activity as KtActivity).itemDAO.GetHex(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        while(ObdHex.length<2){ObdHex="0"+ObdHex}
        HardwareApp.getInstance().switchScan(true)
        dataReceiver = object : HardwareApp.DataReceiver {
            override fun scanReceive() {

            }

            override fun scanMsgReceive(content: String) {
                act.DaiLogDismiss()
                if (!content.contains(":") && !content.contains("*")) {
                    if (content != "nofound") {
                        act.Toast(R.string.app_invalid_sensor_qrcode)
                    } else {
                        act.Toast(R.string.app_scan_code_timeout)
                    }
                    return
                }
                var sensorid=""
                if (content.contains("**")) {
                    sensorid = MMYQrCodeBean.toQRcodeBean(content).mmyNumber
                } else {
                    if(content.split(":","*").size>=3){
                        sensorid=content.split(":","*")[1]
                    }
                }
                if (TextUtils.isEmpty(sensorid)) {
                    act.Toast(R.string.app_invalid_sensor_qrcode)
                    return
                }
                vibMediaUtil.playBeep()
                Log.d("sensorid",sensorid)
                if(rootview.Lft.isFocused){rootview.Lft.setText(sensorid)}
                if(rootview.Rrt.isFocused){rootview.Rrt.setText(sensorid)}
                if(rootview.Rft.isFocused){rootview.Rft.setText(sensorid)}
                if(rootview.Lrt.isFocused){rootview.Lrt.setText(sensorid)}
            }

            override fun uart2MsgReceive(content: String) {

            }
        }
        HardwareApp.getInstance().addDataReceiver(dataReceiver)
        return rootview
    }
    override fun onKeyScan() {
        super.onKeyScan()
        if(run){return}
        run=true
        act.DaiLogDismiss()
        HardwareApp.getInstance().scan()
        act.ShowDaiLog(R.layout.normal_dialog,true,true)
        act.mDialog!!.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_scaning)
        Thread{
            Thread.sleep(3000)
            handler.post {act.DaiLogDismiss()  }
            run=false
        }.start()
    }
    override fun onPause() {
        super.onPause()
        act.DaiLogDismiss()
        vibMediaUtil.release()
        try{
            HardwareApp.getInstance().switchScan(false)
            HardwareApp.getInstance().removeDataReceiver(dataReceiver)
        }catch (e:Exception){e.printStackTrace()}
    }
    override fun onKeyTrigger() {
        Trigger()
    }
    fun Trigger(){
        if(run){return}
        run=true
        act.ShowDaiLog(R.layout.normal_dialog,true,true)
        act.mDialog!!.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_scaning)
        Thread{
            val a = OgCommand.GetId(ObdHex, "00")
            handler.post {
                run = false
                if(!act.NowFrage.equals("Frag_Pad_Keyin")){return@post}
                vibMediaUtil.playBeep()
                act.DaiLogDismiss()
                if(a.success){
                    if(rootview.Lft.isFocused){rootview.Lft.setText(a.id)}
                    if(rootview.Rrt.isFocused){rootview.Rrt.setText(a.id)}
                    if(rootview.Rft.isFocused){rootview.Rft.setText(a.id)}
                    if(rootview.Lrt.isFocused){rootview.Lrt.setText(a.id)}
                }else{
                    act.Toast(resources.getString(R.string.app_read_failed))
                }

            }
        }.start()
    }
    fun CheckUnlinked(){
        Thread{
            try{
                for (i in 0..1) {
                    val Ch1 = navActivity.BleCommand.Command_11(i, 1)
                    val Ch2 = navActivity.BleCommand.Command_11(i, 2)
                    if(!navActivity.NowFrage.equals("Frag_Pad_Keyin")){return@Thread}
                    handler.post {  if (Ch1) {
                        if(i==0){
                            rootview.Lft.isEnabled=true
                            rootview.Lft.hint = "Original sensor ID"
                        }else{
                            rootview.Rft.isEnabled=true
                            rootview.Rft.hint = "Original sensor ID"
                        }
                    } else {
                        if(i==0){
                            rootview.Lft.isEnabled=false
                            rootview.Lft.setText("")
                            rootview.Lft.hint = navActivity.resources.getString(R.string.Unlinked)
                        }else{
                            rootview.Rft.isEnabled=false
                            rootview.Rft.setText("")
                            rootview.Rft.hint = navActivity.resources.getString(R.string.Unlinked)
                        }
                    }
                        if (Ch2) {
                            if(i==0){
                                rootview.Lrt.isEnabled=true
                                rootview.Lrt.hint = "Original sensor ID"
                            }else{
                                rootview.Rrt.hint = "Original sensor ID"
                                rootview.Rrt.isEnabled=true
                            }
                        } else {
                            if(i==0){
                                rootview.Lrt.isEnabled=false
                                rootview.Lrt.setText("")
                                rootview.Lrt.hint = navActivity.resources.getString(R.string.Unlinked)
                            }else{
                                rootview.Rrt.isEnabled=false
                                rootview.Rrt.setText("")
                                rootview.Rrt.hint = navActivity.resources.getString(R.string.Unlinked)
                            }
                        }
                    }

                }
                Thread.sleep(4000)
                if(first){CheckUnlinked()}
            }catch (e: Exception){e.printStackTrace()}
        }.start()
    }
    var first=true
    override fun onResume() {
        super.onResume()
        first=true
    }
    override fun onDestroy() {
        super.onDestroy()
        first=false
    }
}
