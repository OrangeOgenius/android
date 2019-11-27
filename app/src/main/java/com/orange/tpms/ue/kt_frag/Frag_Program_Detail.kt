package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import bean.hardware.SensorDataBean
import com.orange.blelibrary.blelibrary.RootFragement
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
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.OgCommand.Program
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.LoadingWidget
import com.orange.tpms.widget.ScanWidget
import kotlinx.android.synthetic.main.fragment_frag__program__detail.view.*
import java.text.SimpleDateFormat
import java.util.*


class Frag_Program_Detail : RootFragement(),Program_C{

    override fun Program_Progress(i: Int) {
        if(!act.NowFrage.equals("Frag_Program_Detail")){return}
        handler.post{   if (lwLoading.visibility != View.VISIBLE) {
            lwLoading.show()
        }
            lwLoading.tvLoading.text = "$i%"}
    }

    override fun Program_Finish(boolean: Boolean) {
        if(!act.NowFrage.equals("Frag_Program_Detail")){return}
        run=false
        endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        if(boolean){
            Log.e("DATA:", "燒錄成功")
            val result = OgCommand.GetPrId(ObdHex, LF)
            if(!act.NowFrage.equals("Frag_Program_Detail")){return}
                handler.post {
                    AllFall()
                    for(i in result){
                    updateProgramState(i.id, ProgramItemBean.STATE_SUCCESS,i.idcount)
                        Log.e("DATA:", "成功id:"+i.id)
                    }
            }
        }else{
            handler.post { AllFall() }
            Log.e("DATA:", "燒錄失敗")
        }
        UploadData()
        handler.post {
            lwLoading.hide()
            vibMediaUtil.playBeep()
            btProgram.setText(R.string.app_re_program)

        }
    }
    var idcount=8;
    var LF="00"
    lateinit var vibMediaUtil: VibMediaUtil
    lateinit var programAdapter: ProgramAdapter
    lateinit var rvProgram: RecyclerView
    lateinit var lwLoading: LoadingWidget
    lateinit var btProgram: Button
    lateinit var scwTips: ScanWidget
    var startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    var endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    lateinit var dataReceiver: HardwareApp.DataReceiver
    var numberList = ArrayList<ProgramItemBean>()
    private var linearLayoutManager: LinearLayoutManager? = null//列表表格布局
    var ObdHex = "00"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__program__detail, container, false)
        LF=(activity as KtActivity).itemDAO.GetLf(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        rootview.tv_program_title.text="${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        rootview.bt_menue.setOnClickListener { GoMenu() }
        rootview.bt_program.setOnClickListener {
            Program()
        }
        scwTips=rootview.findViewById(R.id.scw_tips)
        btProgram=rootview.findViewById(R.id.bt_program)
        lwLoading=rootview.findViewById(R.id.ldw_loading)
        rvProgram=rootview.findViewById(R.id.rv_program)
        ObdHex=(activity as KtActivity).itemDAO.GetHex(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        while(ObdHex.length<2){ObdHex="0"+ObdHex}
        initView()
        updateList(PublicBean.ProgramNumber)
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
        idcount=(activity as KtActivity).itemDAO.GetCopyId((activity as KtActivity).itemDAO.getMMY(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear))
        return rootview
    }
fun Program(){
    if(run){return}
    if (checkSelectFinish()) {
        if (haveSameSensorid()) {
            act.Toast(R.string.app_duplicate_items)
            return
        }
    } else {
        act.Toast(R.string.app_fillin_all_sensor_id)
        return
    }
    run=true
    lwLoading.tvLoading.text = "0%"
    lwLoading.show()
    Thread{
        startime=SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        Program("00",ObdHex,Integer.toHexString(PublicBean.ProgramNumber),(activity as KtActivity).itemDAO.getMMY(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear),act,this)
    }.start()
}
    override fun onKeyScan() {
        super.onKeyScan()
        if(run){return}
        act.DaiLogDismiss()
        if (scwTips.isShown) {
            scwTips.hide()
        }
        HardwareApp.getInstance().scan()
        lwLoading.hide()
        lwLoading.show(resources.getString(R.string.app_scaning))
        Thread{
            Thread.sleep(3000)
            run=false
        }.start()
    }
    override fun onPause() {
        super.onPause()
        vibMediaUtil.release()
        try{
            HardwareApp.getInstance().switchScan(false)
            HardwareApp.getInstance().removeDataReceiver(dataReceiver)
        }catch (e:Exception){e.printStackTrace()}
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
        programAdapter = ProgramAdapter(activity,(activity as KtActivity).itemDAO.GetCopyId((activity as KtActivity).itemDAO.getMMY(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)),activity)
        rvProgram.adapter = programAdapter
        //数据源
        for (i in 0..11) {
            numberList.add(ProgramItemBean(false, "", ProgramItemBean.STATE_HIDE, false))
        }
        programAdapter.items = numberList
        //选择方式
        HardwareApp.getInstance().switchScan(true)
        dataReceiver = object : HardwareApp.DataReceiver {
            override fun scanReceive() {

            }

            override fun scanMsgReceive(content: String) {
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
        Trigger()
    }
    fun Trigger(){
        if(run){return}
        if(checkSelectFinish()){Program()
        return}
        run=true
        lwLoading.show()
        act.DaiLogDismiss()
        if (scwTips.isShown()) {
            scwTips.hide()
        }
        Thread{
            val a = OgCommand.GetPr("00", PublicBean.ProgramNumber)
            handler.post {
                run = false
                if(!act.NowFrage.equals("Frag_Program_Detail")){return@post}
                vibMediaUtil.playBeep()
                lwLoading.hide()
                if(PublicBean.ProgramNumber==a.size){
                    for(i in a){
                        updateSensorid(i.id.substring(8-idcount))
                    }
                }else{
                    act.Toast(resources.getString(R.string.app_read_failed))
                }
            }
        }.start()
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
    private fun UploadData(){
        Thread{
            if (programAdapter.items.size >= PublicBean.ProgramNumber) {
                val idrecord: ArrayList<SensorRecord> = ArrayList()
                for (i in 0 until PublicBean.ProgramNumber) {
                    val programItemBean = programAdapter.items[i]
                    val b = SensorRecord()
                    b.SensorID=programItemBean.sensorid
                    Log.e("copy",""+programItemBean.state)
                    b.IsSuccess = if(programItemBean.state==ProgramItemBean.STATE_FAILED) "false" else "true"
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
            }
        }.start()

    }
    private fun AllFall(){
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
        for (i in 0 until PublicBean.ProgramNumber) {
            val programItemBean = programAdapter.items[i]
                programAdapter.setItem(i, programItemBean)
                 programItemBean.state=ProgramItemBean.STATE_FAILED
            programAdapter.setItem(i, programItemBean)
            }
            rvProgram.adapter = programAdapter
        }
    }
    /**
     * 更新状态
     */
    private fun updateProgramState(sensorid: String, state: Int,lo:Int) {
        //数目要对上
        if (programAdapter.items.size >= PublicBean.ProgramNumber) {
            for (i in 0 until PublicBean.ProgramNumber) {
                val programItemBean = programAdapter.items[i]
                if (!TextUtils.isEmpty(sensorid) && !TextUtils.isEmpty(programItemBean.sensorid)) {
                    var compareid=programItemBean.sensorid
                    while(compareid.length<8){compareid="0"+compareid}
                    if (sensorid.substring(8-lo+2) == compareid.substring(8-lo+2)) {
                        Log.e("sensorid", sensorid.substring(8-8+lo)+":"+programItemBean.sensorid.substring(8-8+lo))
                        programItemBean.sensorid=sensorid.substring(8-idcount)
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
