package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.adapter.IDCopyAdapter
import com.orange.tpms.bean.IDCopyBean
import com.orange.tpms.bean.MMYQrCodeBean
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.ue.dialog.SensorWay
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.ScanWidget
import kotlinx.android.synthetic.main.data_loading.*
import kotlinx.android.synthetic.main.fragment_frag__idcopy__new.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Idcopy_New : RootFragement(R.layout.fragment_frag__idcopy__new) {
    override fun viewInit() {
        rootview.tv_content.text="${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        rvIDCopy=rootview.findViewById(R.id.rv_id_copy)
        tvContent=rootview.findViewById(R.id.tv_content)
        scwTips=rootview.findViewById(R.id.scw_tips)
        rootview.bt_menue.setOnClickListener { Trigger()}
        rootview.bt_program.setOnClickListener {
            vibMediaUtil.playVibrate()
            if (checkHasSensor()) {
                val newSensorList = getSensoridList()
                if (PublicBean.SensorList.size != newSensorList.size) {
                    JzActivity.getControlInstance().toast(R.string.app_sensor_copy_different)
                } else {
                    PublicBean.NewSensorList = newSensorList
                    JzActivity.getControlInstance().changeFrag(Frag_Idcopy_Detail(),R.id.frage,"Frag_Idcopy_Detail",true)
                }
            } else {
                JzActivity.getControlInstance().toast(R.string.app_no_sensor_set)
            }
        }
        initView()
        ObdHex=(activity as KtActivity).itemDAO.GetHex(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        JzActivity.getControlInstance().showDiaLog(R.layout.sensor_way_dialog,false,false, SensorWay())
        updateEditable(true)
    }

    lateinit var rvIDCopy: androidx.recyclerview.widget.RecyclerView//IDCopy
    lateinit var tvContent: TextView//title
    lateinit var scwTips: ScanWidget//Tips
    var idcount=8
    lateinit var idCopyAdapter: IDCopyAdapter//适配器
    lateinit var linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager//列表表格布局
     var ObdHex = "00"
    lateinit var dataReceiver: HardwareApp.DataReceiver
    lateinit var vibMediaUtil: VibMediaUtil//音效与振动

    override fun enter() {
        rootview.bt_program.performClick()
    }
    override fun onPause() {
        super.onPause()
        vibMediaUtil.release()
        try{
            HardwareApp.getInstance().switchScan(false)
            HardwareApp.getInstance().removeDataReceiver(dataReceiver)
        }catch (e:Exception){e.printStackTrace()}
    }
    override fun onKeyTrigger() {
        JzActivity.getControlInstance().closeDiaLog()
        Trigger()
    }
    fun Trigger(){
        if(run){return}
        run=true
        JzActivity.getControlInstance().showDiaLog(R.layout.data_loading,false,true, object :SetupDialog{
            override fun dismess() {

            }

            override fun keyevent(event: KeyEvent): Boolean {
                return false
            }

            override fun setup(rootview: Dialog) {
            }
        })
        updateEditable(false)
        Thread{
            val a = OgCommand.GetPr("00", 1,ObdHex)
            handler.post {
                run = false
                if(!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Idcopy_New")){return@post}
                vibMediaUtil.playBeep()
                JzActivity.getControlInstance().closeDiaLog()
                if(a.size == 1){
                    for(i in a){
                        if (!haveSameSensorid(i.id)) {
                            updateSensorid(i.id,""+i.kpa,""+i.c,""+i.vol);
                        } else {
                            JzActivity.getControlInstance().toast(R.string.app_sensor_repeated)
                        }
                    }
                }else{
                    JzActivity.getControlInstance().toast(resources.getString(R.string.app_read_failed))
                }
                updateEditable(true)
            }
        }.start()
    }
    override fun onKeyScan() {
        super.onKeyScan()
        if(run){return}
        JzActivity.getControlInstance().closeDiaLog()
        HardwareApp.getInstance().scan()
        JzActivity.getControlInstance().showDiaLog(R.layout.data_loading,false,true, object :SetupDialog {
            override fun dismess() {

            }

            override fun keyevent(event: KeyEvent): Boolean {
                return false
            }

            override fun setup(rootview: Dialog) {
                rootview.pass.visibility=View.VISIBLE
                rootview.pass.text=resources.getString(R.string.app_scaning)
            }

        })
        Thread{
            handler.post { JzActivity.getControlInstance().closeDiaLog()}
            Thread.sleep(3000)
            run=false
        }.start()

    }
    /**
     * 初始化页面
     */
    private fun initView() {
        //swwSelect.setTitle(getResources().getString(R.string.app_copy_new));
        //音效与震动
        vibMediaUtil = VibMediaUtil(activity)
            linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        rvIDCopy.layoutManager = linearLayoutManager
        idCopyAdapter = IDCopyAdapter(activity,idcount)
        idCopyAdapter.newsensoe=true
        rvIDCopy.adapter = idCopyAdapter
        //数据源
        val numberList = ArrayList<IDCopyBean>()
        val titleBean = IDCopyBean(
            "WH",
            "New",
            getString(R.string.app_psi),
            getString(R.string.app_temp),
            getString(R.string.app_bat_clear),
            false
        )
        val frBean = IDCopyBean("LF", "", "", "", "", false)
        val rrBean = IDCopyBean("RF", "", "", "", "", false)
        val rlBean = IDCopyBean("RR", "", "", "", "", false)
        val flBean = IDCopyBean("LR", "", "", "", "", false)
        numberList.add(titleBean)
        numberList.add(frBean)
        numberList.add(rrBean)
        numberList.add(rlBean)
        numberList.add(flBean)
        idCopyAdapter.items = numberList
        idCopyAdapter.notifyDataSetChanged()
        //硬件
        HardwareApp.getInstance().switchScan(true)
        dataReceiver = object : HardwareApp.DataReceiver {
            override fun scanReceive() {

            }
            override fun scanMsgReceive(content: String) {
                JzActivity.getControlInstance().closeDiaLog()
                if (!content.contains(":") && !content.contains("*")) {
                    if (content != "nofound") {
                        JzActivity.getControlInstance().toast(R.string.app_invalid_sensor_qrcode)
                    } else {
                        JzActivity.getControlInstance().toast(R.string.app_scan_code_timeout)
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
                    JzActivity.getControlInstance().toast(R.string.app_invalid_sensor_qrcode)
                    return
                }
                vibMediaUtil.playBeep()
                if (!haveSameSensorid(sensorid)) {
                    updateEditable(false)
                    updateSensorid(sensorid)
                    updateEditable(true)
                } else {
                    JzActivity.getControlInstance().toast(R.string.app_sensor_repeated)
                }
            }

            override fun uart2MsgReceive(content: String) {

            }
        }
        HardwareApp.getInstance().addDataReceiver(dataReceiver)
    }
    /**
     * 插入传感器id
     */
    private fun updateSensorid(sensorid: String) {
        for (i in 1 until idCopyAdapter.items.size) {
            val idCopyBean = idCopyAdapter.items[i]
            //sensorid为空才插入
            if (TextUtils.isEmpty(idCopyBean.sensorid)) {
                idCopyBean.sensorid = sensorid
                idCopyAdapter.setItem(i, idCopyBean)
                rvIDCopy.adapter = idCopyAdapter
                checkSelectFinish()
                return
            }
        }
    }

    /**
     * 插入传感器id
     */
    private fun updateSensorid(sensorid: String, psi: String, temp: String, bat: String) {
        for (i in 1 until idCopyAdapter.items.size) {
            val idCopyBean = idCopyAdapter.items[i]
            //sensorid为空才插入
            if (TextUtils.isEmpty(idCopyBean.sensorid)) {
                idCopyBean.sensorid = sensorid
                idCopyBean.psi = psi
                idCopyBean.temp = temp
                idCopyBean.bat = bat
                idCopyAdapter.setItem(i, idCopyBean)
                rvIDCopy.adapter = idCopyAdapter
                checkSelectFinish()
                return
            }
        }
    }

    /**
     * 获取传感器id列表
     */
    private fun getSensoridList(): List<String> {
        val sensorList = ArrayList<String>()
        for (i in 1 until idCopyAdapter.items.size) {
            val idCopyBean = idCopyAdapter.items[i]
            //sensorid都不为空才行
            if (!TextUtils.isEmpty(idCopyBean.sensorid)) {
                sensorList.add(idCopyBean.sensorid)
            }
        }
        return sensorList
    }

    /**
     * 刷新是否能够编辑的状态
     */
     fun updateEditable(a:Boolean) {
        for (i in 1 until idCopyAdapter.items.size) {
            val idCopyBean = idCopyAdapter.items[i]
            idCopyBean.isEditable = a
            idCopyAdapter.setItem(i, idCopyBean)
        }
        rvIDCopy.adapter = idCopyAdapter
    }

    /**
     * 检测是否完成选择
     */
    private fun checkSelectFinish(): Boolean {
        var finish = true
        for (i in 1 until idCopyAdapter.items.size) {
            val idCopyBean = idCopyAdapter.items[i]
            //sensorid都不为空才行
            if (TextUtils.isEmpty(idCopyBean.sensorid)) {
                finish = false
            }
        }
        return finish
    }

    /**
     * 检测是否有数据了
     */
    private fun checkHasSensor(): Boolean {
        for (i in 1 until idCopyAdapter.items.size) {
            val idCopyBean = idCopyAdapter.items[i]
            //只要有一个不为空即可
            if (!TextUtils.isEmpty(idCopyBean.sensorid)) {
                return true
            }
        }
        return false
    }

    /**
     * 检测是否有重复的
     */
    private fun haveSameSensorid(sensorid: String): Boolean {
        for (i in 1 until idCopyAdapter.items.size) {
            val idCopyBean = idCopyAdapter.items[i]
            if (sensorid == idCopyBean.sensorid) {
                return true
            }
        }
        return false
    }

    /**
     * 检测是否有重复的
     */
    private fun haveSameSensorid(): Boolean {
        val set = HashSet<String>()
        val exist = HashSet<String>()
        for (idCopyBean in idCopyAdapter.items) {
            val sensorid = idCopyBean.sensorid
            if (!TextUtils.isEmpty(sensorid)) {
                if (set.contains(sensorid)) {
                    exist.add(sensorid)
                } else {
                    set.add(sensorid)
                }
            }
        }
        return !exist.isEmpty()
    }

}
