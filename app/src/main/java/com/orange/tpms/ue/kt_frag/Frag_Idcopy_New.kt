package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.de.rocket.Rocket
import com.orange.blelibrary.blelibrary.RootFragement

import com.orange.tpms.R
import com.orange.tpms.adapter.IDCopyAdapter
import com.orange.tpms.bean.IDCopyBean
import com.orange.tpms.bean.MMYQrCodeBean
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.bean.SensorQrCodeBean
import com.orange.tpms.helper.CopyIDHelper
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.ue.frag.Frag_id_copy_detail
import com.orange.tpms.utils.Command
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.LoadingWidget
import com.orange.tpms.widget.ScanWidget
import com.orange.tpms.widget.SensorWayWidget
import kotlinx.android.synthetic.main.fragment_frag__idcopy__new.view.*
import kotlinx.android.synthetic.main.fragment_frag__idcopy__new.view.bt_menue
import kotlinx.android.synthetic.main.fragment_frag__idcopy__new.view.tv_content
import kotlinx.android.synthetic.main.fragment_frag__idcopy_original.view.*
import java.util.ArrayList
import java.util.HashSet


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Idcopy_New : RootFragement() {
    lateinit var rvIDCopy: RecyclerView//IDCopy
    lateinit var tvContent: TextView//title
    lateinit var lwLoading: LoadingWidget//Loading
    lateinit var scwTips: ScanWidget//Tips
    lateinit var idCopyAdapter: IDCopyAdapter//适配器
    lateinit var linearLayoutManager: LinearLayoutManager//列表表格布局
     var ObdHex = "00"
    lateinit var dataReceiver: HardwareApp.DataReceiver
    lateinit var vibMediaUtil: VibMediaUtil//音效与振动
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__idcopy__new, container, false)
        rootview.tv_content.text="${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        rvIDCopy=rootview.findViewById(R.id.rv_id_copy)
        tvContent=rootview.findViewById(R.id.tv_content)
        lwLoading=rootview.findViewById(R.id.ldw_loading)
        scwTips=rootview.findViewById(R.id.scw_tips)
        rootview.bt_menue.setOnClickListener { GoMenu()}
        rootview.bt_program.setOnClickListener {
            vibMediaUtil.playVibrate()
            if (checkHasSensor()) {
                val newSensorList = getSensoridList()
                if (PublicBean.SensorList.size != newSensorList.size) {
                    act.Toast(R.string.app_sensor_copy_different)
                } else {
                    PublicBean.NewSensorList = newSensorList
                    act.ChangePage(Frag_Idcopy_Detail(),R.id.frage,"Frag_Idcopy_Detail",true)
                }
            } else {
                act.Toast(R.string.app_no_sensor_set)
            }
        }
        initView()
        ObdHex=(activity as KtActivity).itemDAO.GetHex(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        tvContent.text = "${PublicBean.SelectMake}${PublicBean.SelectModel}${PublicBean.SelectYear}"
        act.ShowDaiLog(R.layout.sensor_way_dialog,false,false)
        act.mDialog!!.findViewById<RelativeLayout>(R.id.scan).setOnClickListener {
            act.DaiLogDismiss()
        }
        act.mDialog!!.findViewById<RelativeLayout>(R.id.trigger).setOnClickListener {
            act.DaiLogDismiss()
        }
        act.mDialog!!.findViewById<RelativeLayout>(R.id.keyin).setOnClickListener {
            act.DaiLogDismiss()
            updateEditable()
        }
        return rootview
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
      act.DaiLogDismiss()
        Trigger()
    }
    fun Trigger(){
        if(run){return}
        run=true
        lwLoading.show(getResources().getString(R.string.app_data_reading))
        Thread{
            val a = Command.GetId(ObdHex, "00")
            handler.post {
                run = false
                if(!act.NowFrage.equals("Frag_Idcopy_New")){return@post}
                vibMediaUtil.playBeep()
                lwLoading.hide()
                if(a.success){
                    if (!haveSameSensorid(a.id)) {
                        updateSensorid(a.id,""+a.kpa,""+a.c,""+a.vol);
                    } else {
                        act.Toast(R.string.app_sensor_repeated)
                    }
                }else{
                    act.Toast(resources.getString(R.string.app_read_failed))
                }
            }
        }.start()
    }
    override fun onKeyScan() {
        super.onKeyScan()
        if(run){return}
       act.DaiLogDismiss()
        HardwareApp.getInstance().scan()
        lwLoading.hide()
        lwLoading.show(resources.getString(R.string.app_scaning))
        Thread{
            handler.post { lwLoading.hide() }
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
            linearLayoutManager = LinearLayoutManager(activity)
        rvIDCopy.layoutManager = linearLayoutManager
        idCopyAdapter = IDCopyAdapter(activity)
        rvIDCopy.adapter = idCopyAdapter
        //数据源
        val numberList = ArrayList<IDCopyBean>()
        val titleBean = IDCopyBean(
            "",
            getString(R.string.app_id_clear),
            getString(R.string.app_psi),
            getString(R.string.app_temp),
            getString(R.string.app_bat_clear),
            false
        )
        val frBean = IDCopyBean("FR", "", "", "", "", false)
        val rrBean = IDCopyBean("RR", "", "", "", "", false)
        val rlBean = IDCopyBean("RL", "", "", "", "", false)
        val flBean = IDCopyBean("FL", "", "", "", "", false)
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
                lwLoading.hide()
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
                if (!haveSameSensorid(sensorid)) {
                    updateSensorid(sensorid)
                } else {
                    act.Toast(R.string.app_sensor_repeated)
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
    private fun updateEditable() {
        for (i in 1 until idCopyAdapter.items.size) {
            val idCopyBean = idCopyAdapter.items[i]
            idCopyBean.isEditable = true
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
