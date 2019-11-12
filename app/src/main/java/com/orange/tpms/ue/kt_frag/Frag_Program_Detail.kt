package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import bean.hardware.SensorDataBean
import com.de.rocket.Rocket
import com.orange.blelibrary.blelibrary.RootFragement

import com.orange.tpms.R
import com.orange.tpms.adapter.ProgramAdapter
import com.orange.tpms.bean.MMYQrCodeBean
import com.orange.tpms.bean.ProgramItemBean
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.bean.SensorQrCodeBean
import com.orange.tpms.helper.ProgramSensorHelper
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.ue.activity.MainActivity
import com.orange.tpms.ue.frag.Frag_program_detail
import com.orange.tpms.utils.Command
import com.orange.tpms.utils.Command.ProgramFirst
import com.orange.tpms.utils.NumberUtil
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.LoadingWidget
import com.orange.tpms.widget.ScanWidget
import com.orange.tpms.widget.SensorWayWidget
import com.orange.tpms.widget.TitleWidget
import kotlinx.android.synthetic.main.fragment_frag__program__detail.view.*
import java.util.ArrayList
import java.util.HashSet


class Frag_Program_Detail : RootFragement() {
    lateinit var programSensorHelper: ProgramSensorHelper
    lateinit var vibMediaUtil: VibMediaUtil
    lateinit var programAdapter: ProgramAdapter
    lateinit var rvProgram: RecyclerView
    lateinit var lwLoading: LoadingWidget
    lateinit var btProgram: Button
    lateinit var swwSelect: SensorWayWidget
    lateinit var scwTips: ScanWidget
    lateinit var dataReceiver: HardwareApp.DataReceiver
     var  numberList = ArrayList<ProgramItemBean>()
    private var linearLayoutManager: LinearLayoutManager? = null//列表表格布局
    var ObdHex = "00"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
  rootview=inflater.inflate(R.layout.fragment_frag__program__detail, container, false)

        rootview.bt_program.setOnClickListener {
            Thread{ProgramFirst("00",ObdHex,Integer.toHexString(PublicBean.ProgramNumber),(activity as KtActivity).itemDAO.getMMY(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear),act)}.start()

//            program()
            }
        scwTips=rootview.findViewById(R.id.scw_tips)
        btProgram=rootview.findViewById(R.id.bt_program)
        lwLoading=rootview.findViewById(R.id.ldw_loading)
        swwSelect=rootview.findViewById(R.id.sww_select)
        rvProgram=rootview.findViewById(R.id.rv_program)
        ObdHex=(activity as KtActivity).itemDAO.GetHex(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        while(ObdHex.length<2){ObdHex="0"+ObdHex}
        initHelper()
        initView()
        updateList(PublicBean.ProgramNumber)
        return rootview
    }

    override fun onPause() {
        super.onPause()
        vibMediaUtil.release()
        HardwareApp.getInstance().switchScan(false)
        HardwareApp.getInstance().removeDataReceiver(dataReceiver)
    }
    /**
     * 初始化页面
     */
    private fun initView() {
        //音效与震动
        vibMediaUtil = VibMediaUtil(activity)
        //设置标题
        //配置RecyclerView,每行是哪个元素
        if (linearLayoutManager == null) {
            linearLayoutManager = LinearLayoutManager(activity)
        }
        rvProgram.layoutManager = linearLayoutManager
        programAdapter = ProgramAdapter(activity)
        rvProgram.adapter = programAdapter
        //数据源
        for (i in 0..11) {
            numberList.add(ProgramItemBean(false, "", ProgramItemBean.STATE_HIDE, false))
        }
        programAdapter.items = numberList
        //选择方式
        swwSelect.setOnScanClickListener { scwTips.show() }
        swwSelect.setOnTriggerClickListener { scwTips.show() }
        swwSelect.setOnKeyinClickListener( { this.updateEditable() })
        HardwareApp.getInstance().switchScan(true)
        dataReceiver = object : HardwareApp.DataReceiver {
            override fun scanReceive() {

            }

            override fun scanMsgReceive(content: String) {
                Rocket.writeOuterLog("Frag_program_detail::scanMsgReceive->content:$content")
                lwLoading.hide()
                Log.v("yhd-", "content:$content")
                //兼容三种
                if (!content.contains(":") && !content.contains("*")) {
                    if (content != "nofound") {
                        act.Toast(R.string.app_invalid_sensor_qrcode)
                    } else {
                        act.Toast(R.string.app_scan_code_timeout)
                    }
                    return
                }
                val sensorid: String
                if (content.contains("**")) {
                    sensorid = MMYQrCodeBean.toQRcodeBean(content).mmyNumber
                } else {
                    sensorid = SensorQrCodeBean.toQRcodeBean(content).sensorID
                }
                if (TextUtils.isEmpty(sensorid)) {
                    act.Toast(R.string.app_invalid_sensor_qrcode)
                    return
                }
                Rocket.writeOuterLog("Frag_id_copy_new::scanMsgReceive->sensorid:$sensorid")
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
     * 刷新是否能够编辑的状态
     */
    private fun updateEditable() {
        //数目要对上
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                programItemBean.isEditable = true
                programAdapter.setItem(i, programItemBean)
            }
            rvProgram.adapter = programAdapter
        }
    }
    override fun onKeyTrigger() {
        if (swwSelect.isShown()) {
            swwSelect.pllTrigger.performClick()
        }
        if (scwTips.isShown()) {
            scwTips.hide()
        }
                programSensorHelper.trigger(ObdHex)
    }
    /**
     * 烧录
     */
    private fun program() {
        vibMediaUtil.playVibrate()
        if (checkSelectFinish()) {
            if (!haveSameSensorid()) {
            programSensorHelper.writeSensor(PublicBean.ProgramNumber,ObdHex,(activity as KtActivity).itemDAO.getMMY(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear));
            } else {
                act.Toast(R.string.app_duplicate_items)
            }
        } else {
            act.Toast(R.string.app_fillin_all_sensor_id)
        }
    }
    private fun initHelper() {
        programSensorHelper = ProgramSensorHelper()
        //开始请求
        programSensorHelper.setOnPreRequestListener { lwLoading.show(resources.getString(R.string.app_loading_data)) }
        //结束请求
        programSensorHelper.setOnFinishRequestListener { lwLoading.hide() }
        //请求失败
        programSensorHelper.setOnFailedRequestListener { `object` -> act.Toast(`object`.toString()) }
        //读取传感器情况
        programSensorHelper.setOnStrggerSuccessListener { sensorid ->
            Log.v("yhd-", "setOnStrggerSuccessListener:sensorid:$sensorid")
            vibMediaUtil.playBeep()
            if (!haveSameSensorid(sensorid)) {
                updateSensorid(sensorid)
            } else {
                act.Toast(R.string.app_sensor_repeated)
            }
        }
        //进度
        programSensorHelper.setOnProgressListener { progress ->
            if (lwLoading.getVisibility() != View.VISIBLE) {
                lwLoading.show()
            }
            val content = NumberUtil.toFormate(progress)
            lwLoading.getTvLoading().setText("$content%")
        }
        //烧录成功
        programSensorHelper.setOnProgramSuccessListener { sensorid, success ->
            Log.v("yhd-", "setOnProgramSuccessListener:sensorid:$sensorid success:$success")
            vibMediaUtil.playBeep()
            btProgram.setText(R.string.app_re_program)
            if (success) {
                updateProgramState(sensorid, ProgramItemBean.STATE_SUCCESS)
            } else {
                updateProgramState(sensorid, ProgramItemBean.STATE_FAILED)
            }
        }
        //开始检测
        programSensorHelper.setOnCheckProgramListener {
            if (lwLoading.getVisibility() != View.VISIBLE) {
                lwLoading.show()
            }
            lwLoading.getTvLoading().setText(R.string.app_checking)
        }
        //检测超时
        programSensorHelper.setOnCheckTimeoutListener { sensorDataBeans ->
            Log.v("yhd-", "setOnCheckTimeoutListener:" + "sensorDataBeans:" + sensorDataBeans.size)
            vibMediaUtil.playBeep()
            updateProgramState(sensorDataBeans)
        }
    }
    /**
     * 检测是否有重复的
     */
    private fun haveSameSensorid(): Boolean {
        //数目要对上
        if (programAdapter.getItems().size >= PublicBean.ProgramNumber) {
            val set = HashSet<String>()
            val exist = HashSet<String>()
            for (programItemBean in programAdapter.getItems()) {
                val sensorid = programItemBean.getSensorid()
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
        return false
    }
    /**
     * 插入传感器id
     */
    private fun updateSensorid(sensorid: String) {
        //数目要对上
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                //sensorid为空才插入
                if (TextUtils.isEmpty(programItemBean.sensorid)) {
                    programItemBean.isShowIndex = true
                    programItemBean.sensorid = sensorid
                    programItemBean.state = ProgramItemBean.STATE_NORMAL
                    programAdapter.setItem(i, programItemBean)
                    rvProgram.setAdapter(programAdapter)
                    checkSelectFinish()
                    return
                }
            }
        }
    }
    private fun updateList(number: Int) {
        //数目要对上
        if (programAdapter.items.size >= number && number > 0) {
            for (i in 0 until PublicBean.ProgramNumber) {
                if (i > number - 1) {
                    return
                }
                val programItemBean = programAdapter.items[i]
                programItemBean.isShowIndex = true
                programItemBean.state = ProgramItemBean.STATE_NORMAL
                programAdapter.setItem(i, programItemBean)
                rvProgram.setAdapter(programAdapter)
            }
        }
    }

    /**
     * 检测是否完成选择
     */
    private fun checkSelectFinish(): Boolean {
        var finish = true
        //数目要对上
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                //sensorid都不为空才行
                if (TextUtils.isEmpty(programItemBean.sensorid)) {
                    finish = false
                }
            }
        }
        return finish
    }
    /**
     * 检测是否有重复的
     */
    private fun haveSameSensorid(sensorid: String): Boolean {
        //数目要对上
        if (programAdapter.items.size >= PublicBean.ProgramNumber && !TextUtils.isEmpty(sensorid)) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                //sensorid都不为空才行
                if (sensorid == programItemBean.sensorid) {
                    return true
                }
            }
        }
        return false
    }
    /**
     * 更新状态
     */
    private fun updateProgramState(sensorid: String, state: Int) {
        //数目要对上
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                if (!TextUtils.isEmpty(sensorid) && !TextUtils.isEmpty(programItemBean.sensorid)) {
                    if (sensorid == programItemBean.sensorid) {
                        Log.e("sensorid", sensorid)
                        programItemBean.state = state
                    }
                }
                if (TextUtils.isEmpty(sensorid)) {
                    programItemBean.state = state
                }
                programAdapter.setItem(i, programItemBean)
            }
            rvProgram.adapter = programAdapter
        }
    }
    /**
     * 设置更新状态
     */
    private fun updateProgramState(sensorDataBeans: List<SensorDataBean>?) {
        //数目要对上
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                if (sensorDataBeans == null || sensorDataBeans.size == 0) {
                    programItemBean.state = ProgramItemBean.STATE_FAILED
                } else {
                    //原始ID
                    val originSensorid = programItemBean.sensorid
                    if (!TextUtils.isEmpty(originSensorid)) {
                        var exist = false
                        for (sensorDataBean in sensorDataBeans) {
                            Log.d("sensorDataBean", "" + sensorDataBean.id_len)
                            if (!TextUtils.isEmpty(sensorDataBean.sensor_id)) {
                                if (originSensorid == sensorDataBean.sensor_id) {
                                    exist = true
                                }
                            }
                        }
                        if (exist) {
                            programItemBean.state = ProgramItemBean.STATE_SUCCESS
                        } else {
                            programItemBean.state = ProgramItemBean.STATE_FAILED
                        }
                    }

                }
                programAdapter.setItem(i, programItemBean)
            }
            rvProgram.adapter = programAdapter
        }
    }
}
