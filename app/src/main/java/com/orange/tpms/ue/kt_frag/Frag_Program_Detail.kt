package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.Button
import bean.hardware.SensorDataBean
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.RootFragement
import com.orange.tpms.ue.dialog.SensorWay
import kotlinx.android.synthetic.main.data_loading.*
import kotlinx.android.synthetic.main.fragment_frag__program__detail.view.*
import com.orange.tpms.Callback.Program_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.HttpCommand.SensorRecord
import com.orange.tpms.R
import com.orange.tpms.adapter.ProgramAdapter
import com.orange.tpms.bean.MMYQrCodeBean
import com.orange.tpms.bean.ProgramItemBean
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.ue.dialog.EmptyDialog
import com.orange.tpms.utils.HttpDownloader.post
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.OgCommand.Program
import com.orange.tpms.utils.OgCommand.tx_memory
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.ScanWidget
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Frag_Program_Detail : RootFragement(R.layout.fragment_frag__program__detail), Program_C {
    override fun viewInit() {
        LF =
            (activity as KtActivity).itemDAO.GetLf(PublicBean.SelectMake, PublicBean.SelectModel, PublicBean.SelectYear)
        rootview.tv_program_title.text = "${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        rootview.bt_menue.setOnClickListener { GoMenu() }
        rootview.bt_program.setOnClickListener {
            Program()
        }
        scwTips = rootview.findViewById(R.id.scw_tips)
        btProgram = rootview.findViewById(R.id.bt_program)
        rvProgram = rootview.findViewById(R.id.rv_program)
        ObdHex = (activity as KtActivity).itemDAO.GetHex(
            PublicBean.SelectMake,
            PublicBean.SelectModel,
            PublicBean.SelectYear
        )
        while (ObdHex.length < 2) {
            ObdHex = "0" + ObdHex
        }
        initView()
        updateList(PublicBean.ProgramNumber)
        JzActivity.getControlInstance().showDiaLog(R.layout.sensor_way_dialog, false, false, SensorWay())
        idcount = (activity as KtActivity).itemDAO.GetCopyId(
            (activity as KtActivity).itemDAO.getMMY(
                PublicBean.SelectMake,
                PublicBean.SelectModel,
                PublicBean.SelectYear
            )
        )
        val s19 = GetPro(
            (activity as KtActivity).itemDAO.getMMY(
                PublicBean.SelectMake,
                PublicBean.SelectModel,
                PublicBean.SelectYear
            ), "nodata"
        )
        SensorRecord.SensorCode_Version = s19
        updateEditable(true)
    }

    override fun Program_Progress(i: Int) {
        if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Program_Detail")) {
            return
        }
        handler.post {
            JzActivity.getControlInstance().showDiaLog(R.layout.data_loading, false, true, object : SetupDialog {
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {

                    return false
                }

                override fun setup(rootview: Dialog) {
                    rootview.pass.visibility = View.VISIBLE
                    rootview.pass.text = "$i%"
                }

            })
        }
    }

    override fun Program_Finish(boolean: Boolean) {
        if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Program_Detail")) {
            return
        }
        run = false
        endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        if (boolean) {
            Log.e("DATA:", "燒錄成功")
            Thread.sleep(3000)
            val result = OgCommand.GetPrId(ObdHex, LF)
            if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Program_Detail")) {
                return
            }
            handler.post {
                AllFall()
                for (i in result) {
                    updateProgramState(i.id, ProgramItemBean.STATE_SUCCESS, i.idcount)
                    Log.e("DATA:", "成功id:" + i.id)
                }
                if (result.size == PublicBean.ProgramNumber) {
                    btProgram.setText("PROG.Sensor")
                    btProgram.setOnClickListener {
                        JzActivity.getControlInstance().goBack()
                    }
                } else {
                    btProgram.setText(resources.getString(R.string.app_re_program))
                }
            }
        } else {
            handler.post {
                AllFall()
                btProgram.setText(resources.getString(R.string.app_re_program))
            }
            Log.e("DATA:", "燒錄失敗")
        }
        UploadData()
        handler.post {
            JzActivity.getControlInstance().closeDiaLog()
            vibMediaUtil.playBeep()
        }
    }

    var ProgramTrigger = ArrayList<String>()
    var idcount = 8;
    var LF = "00"
    lateinit var vibMediaUtil: VibMediaUtil
    lateinit var programAdapter: ProgramAdapter
    lateinit var rvProgram: androidx.recyclerview.widget.RecyclerView
    lateinit var btProgram: Button
    lateinit var scwTips: ScanWidget
    var startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    var endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    lateinit var dataReceiver: HardwareApp.DataReceiver
    var numberList = ArrayList<ProgramItemBean>()
    private var linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager? = null//列表表格布局
    var ObdHex = "00"

    fun Program() {
        if (run) {
            return
        }
        if (checkSelectFinish()) {
            if (haveSameSensorid()) {
                JzActivity.getControlInstance().toast(R.string.app_duplicate_items)
                return
            }
        } else {
            Trigger()
            return
        }
        run = true
        rootview.bt_menue.text = resources.getString(R.string.Relearn_Procedure)
        rootview.bt_menue.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Relearm_Detail(), R.id.frage, "Frag_Relearm_Detail", true)
        }
//        (act as KtActivity).back.setImageResource(R.mipmap.menu)
//        (act as KtActivity).back.setOnClickListener { GoMenu() }
        JzActivity.getControlInstance().showDiaLog(R.layout.data_loading, false, true, object : SetupDialog {
            override fun dismess() {

            }

            override fun keyevent(event: KeyEvent): Boolean {
                return false
            }

            override fun setup(rootview: Dialog) {
                rootview.pass.visibility = View.VISIBLE
                rootview.pass.text = "0%"
            }

        })
        Thread {
            startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
            Program(
                "00",
                ObdHex,
                Integer.toHexString(PublicBean.ProgramNumber),
                (activity as KtActivity).itemDAO.getMMY(
                    PublicBean.SelectMake,
                    PublicBean.SelectModel,
                    PublicBean.SelectYear
                ),
                act,
                this, ProgramTrigger
            )
        }.start()
    }

    override fun onKeyScan() {
        super.onKeyScan()
        if (run) {
            return
        }
        JzActivity.getControlInstance().closeDiaLog()
        if (scwTips.isShown) {
            scwTips.hide()
        }
        HardwareApp.getInstance().scan()
        JzActivity.getControlInstance().closeDiaLog()
        JzActivity.getControlInstance().showDiaLog(R.layout.data_loading, false, true, object : SetupDialog {
            override fun dismess() {

            }

            override fun keyevent(event: KeyEvent): Boolean {
                return false
            }

            override fun setup(rootview: Dialog) {
                rootview.pass.visibility = View.VISIBLE
                rootview.pass.text = resources.getString(R.string.app_scaning)
            }

        })
        Thread {
            Thread.sleep(3000)
            run = false
        }.start()
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

    /**
     * 初始化页面
     */
    private fun initView() {
        //音效与震动
        vibMediaUtil = VibMediaUtil(activity)
        //设置标题
        //配置RecyclerView,每行是哪个元素
        if (linearLayoutManager == null) {
            linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        }
        rvProgram.layoutManager = linearLayoutManager
        programAdapter = ProgramAdapter(
            activity,
            (activity as KtActivity).itemDAO.GetCopyId(
                (activity as KtActivity).itemDAO.getMMY(
                    PublicBean.SelectMake,
                    PublicBean.SelectModel,
                    PublicBean.SelectYear
                )
            ),
            activity
        )
        rvProgram.adapter = programAdapter
        //数据源
        for (i in 0..3) {
            numberList.add(ProgramItemBean(false, "", ProgramItemBean.STATE_HIDE, false))
        }
        programAdapter.items = numberList
        //选择方式
        HardwareApp.getInstance().switchScan(true)
        dataReceiver = object : HardwareApp.DataReceiver {
            override fun scanReceive() {
                JzActivity.getControlInstance().closeDiaLog()
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
                if (!haveSameSensorid(sensorid)) {
                    updateEditable(false)
                    updateSensorid(sensorid)
                    updateEditable(true)
                } else {
                    JzActivity.getControlInstance().toast(R.string.app_sensor_repeated)
                }
            }

            override fun uart2MsgReceive(content: String) {
                JzActivity.getControlInstance().closeDiaLog()
            }
        }
        HardwareApp.getInstance().addDataReceiver(dataReceiver)
    }

    /**
     * 刷新是否能够编辑的状态
     */
    fun updateEditable(a: Boolean) {
        //数目要对上
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                Log.e("edit", "有近來")
                val programItemBean = programAdapter.items[i]
                programItemBean.isEditable = a
                programAdapter.setItem(i, programItemBean)
            }
            rvProgram.adapter = programAdapter
        }
    }

    override fun onKeyTrigger() {
        Trigger()
    }

    fun Trigger() {
        if (run) {
            return
        }
        if (checkSelectFinish()) {
            Program()
            return
        }
        run = true
        EmptyDialog(R.layout.data_loading).show()
        if (scwTips.isShown()) {
            scwTips.hide()
        }
        Thread {
            val a = OgCommand.GetPr("00", PublicBean.ProgramNumber, ObdHex)
            handler.post {
                run = false
                if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Program_Detail")) {
                    return@post
                }
                vibMediaUtil.playBeep()
                JzActivity.getControlInstance().closeDiaLog()
                if (a.size >= 0) {
                    for (i in a) {
                        if (!haveSameSensorid(i.id.substring(8 - idcount))) {
                            updateEditable(false)
                            updateSensorid(i.id.substring(8 - idcount))
                            updateEditable(true)
                        }
                    }
                } else {
                    JzActivity.getControlInstance().toast(resources.getString(R.string.app_read_failed))
                }
            }
        }.start()
    }

    /**
     * 检测是否有重复的
     */
    private fun haveSameSensorid(): Boolean {
        //数目要对上
        Log.e("size", "${programAdapter.getItems().size}/" + PublicBean.ProgramNumber)
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
            for (i in 0..PublicBean.ProgramNumber) {

                val programItemBean = programAdapter.items[i]
                //sensorid为空才插入
                if (TextUtils.isEmpty(programItemBean.sensorid.trim())) {
                    Log.e("size", "差入${sensorid}")
                    programItemBean.isEditable = false
                    programItemBean.isShowIndex = true
                    programItemBean.sensorid = sensorid
                    programItemBean.state = ProgramItemBean.STATE_NORMAL
                    programAdapter.setItem(i, programItemBean)
                    rvProgram.adapter = programAdapter
                    if (checkSelectFinish()) {
                        rootview.bt_program.text = resources.getString(R.string.app_program)
                    }
                    programAdapter.notifyDataSetChanged()
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
        ProgramTrigger.clear()
        //数目要对上
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                ProgramTrigger.add(programItemBean.sensorid)
                //sensorid都不为空才行
                if (TextUtils.isEmpty(programItemBean.sensorid.trim())) {
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

    fun allsuccess(): Boolean {
        var a = true
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                if (programItemBean.state == ProgramItemBean.STATE_FAILED) {
                    a = false
                }
            }
        }
        return a
    }

    private fun UploadData() {
        Thread {
            if (programAdapter.items.size >= PublicBean.ProgramNumber) {
                val idrecord: ArrayList<SensorRecord> = ArrayList()
                for (i in 0 until PublicBean.ProgramNumber) {
                    val programItemBean = programAdapter.items[i]
                    val b = SensorRecord()
                    b.SensorID = programItemBean.sensorid
                    Log.e("copy", "" + programItemBean.state)
                    b.IsSuccess = if (programItemBean.state == ProgramItemBean.STATE_FAILED) "false" else "true"
                    idrecord.add(b)
                }
                Fuction.Upload_ProgramRecord(
                    PublicBean.SelectMake,
                    PublicBean.SelectModel,
                    PublicBean.SelectYear,
                    startime,
                    endtime,
                    PublicBean.OG_SerialNum,
                    "OGenius",
                    "Program",
                    idrecord.size,
                    "ALL",
                    idrecord,
                    activity as KtActivity
                )
                post(
                    "/topics/LogCat",
                    if (allsuccess()) "燒錄成功-(${PublicBean.ProgramNumber})${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}*${PublicBean.OG_SerialNum}" else "燒錄失敗-(${PublicBean.ProgramNumber})${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}*${PublicBean.OG_SerialNum}",
                    tx_memory.toString()
                )
                tx_memory = StringBuffer("");
            }
        }.start()
    }

    private fun AllSuccess() {
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                programAdapter.setItem(i, programItemBean)
                programItemBean.state = ProgramItemBean.STATE_SUCCESS
                programAdapter.setItem(i, programItemBean)
            }
            rvProgram.adapter = programAdapter
        }
    }

    private fun AllFall() {
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                programAdapter.setItem(i, programItemBean)
                programItemBean.state = ProgramItemBean.STATE_FAILED
                programAdapter.setItem(i, programItemBean)
            }
            rvProgram.adapter = programAdapter
        }
    }

    /**
     * 更新状态
     */
    private fun updateProgramState(sensorid: String, state: Int, lo: Int) {
        //数目要对上
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                if (!TextUtils.isEmpty(sensorid) && !TextUtils.isEmpty(programItemBean.sensorid)) {
                    var compareid = programItemBean.sensorid
                    while (compareid.length < 8) {
                        compareid = "0" + compareid
                    }
                    if (sensorid.substring(8 - lo + 2) == compareid.substring(8 - lo + 2)) {
                        Log.e(
                            "sensorid",
                            sensorid.substring(8 - 8 + lo) + ":" + programItemBean.sensorid.substring(8 - 8 + lo)
                        )
                        programItemBean.sensorid = sensorid.substring(8 - idcount)
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
