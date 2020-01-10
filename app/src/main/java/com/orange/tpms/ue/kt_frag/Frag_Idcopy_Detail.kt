package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.text.InputFilter
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.Callback.Copy_C
import com.orange.tpms.Callback.Program_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.HttpCommand.SensorRecord
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.adapter.IDCopyDetailAdapter
import com.orange.tpms.bean.IDCopyDetailBean
import com.orange.tpms.bean.MMYQrCodeBean
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.ue.dialog.SensorWay
import com.orange.tpms.utils.HttpDownloader
import com.orange.tpms.utils.KeyboardUtil
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.CarWidget
import com.orange.tpms.widget.ClearEditText
import kotlinx.android.synthetic.main.activity_kt.*
import kotlinx.android.synthetic.main.data_loading.*
import kotlinx.android.synthetic.main.fragment_frag__idcopy__detail.view.*
import kotlinx.android.synthetic.main.sensor_way_dialog.*
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Frag_Idcopy_Detail : RootFragement(R.layout.fragment_frag__idcopy__detail), Copy_C, Program_C {
    lateinit var dataReceiver: HardwareApp.DataReceiver
    override fun viewInit() {
        LF =
            (activity as KtActivity).itemDAO.GetLf(PublicBean.SelectMake, PublicBean.SelectModel, PublicBean.SelectYear)
        ObdHex = (activity as KtActivity).itemDAO.GetHex(
            PublicBean.SelectMake,
            PublicBean.SelectModel,
            PublicBean.SelectYear
        )
        while (ObdHex.length < 2) {
            ObdHex = "0" + ObdHex
        }
        idcount = (activity as KtActivity).itemDAO.GetCopyId(
            (activity as KtActivity).itemDAO.getMMY(
                PublicBean.SelectMake,
                PublicBean.SelectModel,
                PublicBean.SelectYear
            )
        )
        rootview.tv_content.text = "${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        ObdHex = (activity as KtActivity).itemDAO.GetHex(
            PublicBean.SelectMake,
            PublicBean.SelectModel,
            PublicBean.SelectYear
        )
        run = false
        cwCar = rootview.findViewById(R.id.cw_car)
        rootview.bt_program.setOnClickListener { program() }
        rootview.bt_menue.setOnClickListener { onKeyTrigger() }
        initView()
        updateView()
        val s19 = GetPro(
            (activity as KtActivity).itemDAO.getMMY(
                PublicBean.SelectMake,
                PublicBean.SelectModel,
                PublicBean.SelectYear
            ), "nodata"
        )
        SensorRecord.SensorCode_Version = s19
        originalist.add(rootview.o1)
        originalist.add(rootview.o2)
        originalist.add(rootview.o3)
        originalist.add(rootview.o4)
        newlist.add(rootview.n1)
        newlist.add(rootview.n2)
        newlist.add(rootview.n3)
        newlist.add(rootview.n4)
        staticlist.add(rootview.i1)
        staticlist.add(rootview.i2)
        staticlist.add(rootview.i3)
        staticlist.add(rootview.i4)
        updateEditable(false)
        updateEditable(true)
        for (i in newlist) {
            KeyboardUtil.hideEditTextKeyboard(i)
            i.filters = arrayOf(InputFilter.AllCaps(), InputFilter.LengthFilter(8))
            i.setClearStatusListener {
                if (haveSameSensorid(i.text.toString()) && i.text.toString().length >= 6) {
//                    JzActivity.getControlInstance().toast(R.string.app_sensor_repeated)
                } else if (i.text.toString().length >= 6) {
                    updateEditable(true)
                }
            }
        }
        for (i in originalist) {
            KeyboardUtil.hideEditTextKeyboard(i)
            i.filters = arrayOf(InputFilter.AllCaps(), InputFilter.LengthFilter(8))
            i.setClearStatusListener {
                if (haveSameSensorid(i.text.toString()) && i.text.toString().length >= 6) {
//                    JzActivity.getControlInstance().toast(R.string.app_sensor_repeated)
                } else if (i.text.toString().length >= 6) {
                    updateEditable(true)
                }
            }
        }
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
                Log.e("id為",""+sensorid)
                if (!haveSameSensorid(sensorid)) {
                    updateEditable(false)
                    updateSensor(sensorid)
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

    override fun Program_Progress(i: Int) {

        if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Idcopy_Detail")) {
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
        if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Idcopy_Detail")) {
            return
        }
        run = false
        endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        if (boolean) {
            Log.e("DATA:", "燒錄成功")
            PublicBean.ProgramNumber = PublicBean.SensorList.size
            sleep(3000)
            val result = OgCommand.GetPrId(ObdHex, LF)
            if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Idcopy_Detail")) {
                return
            }
            handler.post {
                var position = 0
                for (i in result) {
                    for (check in 0 until PublicBean.NewSensorList.size) {
                        val a = PublicBean.NewSensorList[check]
                        if (PublicBean.NewSensorList[check].substring(a.length - 4, a.length).equals(
                                i.id.substring(
                                    4,
                                    8
                                )
                            )
                        ) {
                            PublicBean.NewSensorList[check] = i.id
                        }
                    }
                    Log.e("DATA:", "成功id:" + i.id)
                    position++
                }
                Thread {
                    sleep(2000)
                    OgCommand.IdCopy(this, ObdHex, idcount)
                }.start()
            }
        } else {
            handler.post {
                AllFall()
                Log.e("DATA:", "燒錄失敗")
                JzActivity.getControlInstance().closeDiaLog()
                upload()
            }
        }
        handler.post {
            vibMediaUtil.playBeep()
        }
    }

    fun AllFall() {
        for (i in 0 until PublicBean.SensorList.size) {
            copySuccess(i, false)
        }
    }

    override fun Copy_Next(success: Boolean, position: Int) {
        Log.e("Copy_Finish", "$position" + success)
        BooResult[position] = success
        handler.post {
            vibMediaUtil.playBeep()
            JzActivity.getControlInstance().showDiaLog(R.layout.data_loading, false, true, object : SetupDialog {
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                    return false
                }

                override fun setup(rootview: Dialog) {
                    rootview.pass.visibility = View.VISIBLE
                    rootview.pass.text = "${position * 100 / PublicBean.SensorList.size}%"
                }

            })
            copySuccess(position, success)
        }

    }

    override fun Copy_Finish(boolean: Boolean) {
        handler.post {
            JzActivity.getControlInstance().closeDiaLog()
        }
        run = false
        endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        if (!boolean) {
            handler.post {

                for (i in 0 until PublicBean.CopySuccess.size) {
                    copySuccess(i, PublicBean.CopySuccess[i])
                }
            }
        }
        Log.e("Copy_Finish", "$boolean")
        upload()

    }

    var LF = "00"
    var ObdHex = "00"
    var startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    var endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    lateinit var cwCar: CarWidget//CarWidget
    var idcount = 8;
    lateinit var vibMediaUtil: VibMediaUtil//音效与振动
    var originalist = ArrayList<ClearEditText>()
    var newlist = ArrayList<ClearEditText>()
    var staticlist = ArrayList<ImageView>()
    override fun enter() {
        super.enter()
        program()
    }

    override fun onKeyTrigger() {
        super.onKeyTrigger()
//        program()
        if (run) {
            return
        }
        run = true
        JzActivity.getControlInstance().showDiaLog(R.layout.data_loading, false, true)
        updateEditable(false)
        Thread {
            val a = OgCommand.GetPr("00", 1, ObdHex)
            handler.post {
                run = false
                if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Idcopy_Detail")) {
                    return@post
                }
                vibMediaUtil.playBeep()
                JzActivity.getControlInstance().closeDiaLog()
                if (a.size == 1) {
                    for (i in a) {
                        if (!haveSameSensorid(i.id)) {
                            updateSensor(i.id);
                        } else {
                            JzActivity.getControlInstance().toast(R.string.app_sensor_repeated)
                        }
                    }
                } else {
                    JzActivity.getControlInstance().toast(resources.getString(R.string.app_read_failed))
                }
                updateEditable(true)
            }
        }.start()
    }

    fun haveSameSensorid(id: String): Boolean {
        Log.e("haveSameSensorid",id)
        for (i in 0 until originalist.size) {
            if (originalist[i].text!!.isEmpty()) {
                for (a in originalist) {
                    if (a.text!!.toString() == id) {
                        return true
                    }
                }
                return false
            }
            if (newlist[i].text!!.isEmpty()) {
                for (a in newlist) {
                    if (a.text!!.toString() == id) {
                        return true
                    }
                }
                return false
            }
        }
        return false
    }

    fun updateSensor(id: String) {
        Log.e("getid",id)
        for (i in 0 until originalist.size) {
            if (originalist[i].text!!.isEmpty()) {
                originalist[i].setText(id)
                return
            }
            if (newlist[i].text!!.isEmpty()) {
                newlist[i].setText(id)
                return
            }
        }
    }

    fun updateEditable(a: Boolean) {
        if (a) {
            for (i in 0 until originalist.size) {
                if (originalist[i].text!!.isNotEmpty() || newlist[i].text!!.isNotEmpty()) {
                    originalist[i].isEnabled = true
                    newlist[i].isEnabled = true
                } else {
                    if (originalist[i].text!!.isEmpty()) {
                        originalist[i].isEnabled = true
                        return
                    }
                    if (newlist[i].text!!.isEmpty()) {
                        newlist[i].isEnabled = true
                        return
                    }
                }
            }
        } else {
            for (i in 0 until originalist.size) {
                originalist[i].isEnabled = false
                newlist[i].isEnabled = false
            }
        }
    }



    /**
     * 烧录
     */
    private fun program() {
        if (!checkComplete()) {
            return
        }
        act.back.setImageResource(R.mipmap.menu)
        act.back.setOnClickListener { GoMenu() }
        rootview.bt_menue.text = resources.getString(R.string.Relearn_Procedure)
        rootview.bt_menue.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(
                Frag_Relearm_Detail(),
                R.id.frage,
                "Frag_Relearm_Detail",
                true
            )
        }
        if (run) {
            return
        }
        run = true
        JzActivity.getControlInstance().closeDiaLog()
        JzActivity.getControlInstance().showDiaLog(R.layout.data_loading, false, true, object : SetupDialog {
            override fun dismess() {
            }

            override fun keyevent(event: KeyEvent): Boolean {
                return false
            }

            override fun setup(rootview: Dialog) {
            }
        })
        startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        vibMediaUtil.playVibrate()
        if (checkCanCopy()) {
            Thread {
                OgCommand.Program(
                    "00",
                    ObdHex,
                    Integer.toHexString(PublicBean.SensorList.size),
                    (activity as KtActivity).itemDAO.getMMY(
                        PublicBean.SelectMake,
                        PublicBean.SelectModel,
                        PublicBean.SelectYear
                    ),
                    act,
                    this
                    ,ArrayList(PublicBean.NewSensorList)
                )
            }.start()
        } else {
            run = false
            JzActivity.getControlInstance().toast(R.string.app_no_data_to_copy)
        }
    }

    private fun checkComplete(): Boolean {
        for (i in 0 until originalist.size) {
            if ((originalist[i].text!!.isEmpty()) && (newlist[i].text!!.isEmpty()) && i == 0) {
                JzActivity.getControlInstance().toast(R.string.app_no_sensor_set)
                return false
            }
            if (newlist[i].text!!.isEmpty() && i == 0) {
                JzActivity.getControlInstance().toast(R.string.app_sensor_copy_different)
                return false
            }
            if (originalist[i].text!!.isEmpty() && i == 0) {
                JzActivity.getControlInstance().toast(R.string.app_sensor_copy_different)
                return false
            }
            if (originalist[i].text!!.isNotEmpty() || newlist[i].text!!.isNotEmpty()) {
                if (originalist[i].text!!.isEmpty() || newlist[i].text!!.isEmpty()) {
                    JzActivity.getControlInstance().toast(R.string.app_sensor_copy_different)
                    return false
                }
            }
        }
        var originallist = ArrayList<String>()
        var newslist = ArrayList<String>()
        for (i in 0 until originalist.size) {
            if (originalist[i].text!!.isNotEmpty() && newlist[i].text!!.isNotEmpty()) {
                originallist.add(originalist[i].text.toString())
                newslist.add(newlist[i].text.toString())
            }
        }
        PublicBean.SensorList = originallist
        PublicBean.NewSensorList = newslist
        return true
    }

    /**
     * 刷新页面
     */
    private fun updateView() {
//        if (PublicBean.SensorList.size == PublicBean.NewSensorList.size) {
//            for (i in 1 until idCopyDetailAdapter.items.size) {
//                if (i <= PublicBean.SensorList.size) {
//                    val idCopyDetailBean = idCopyDetailAdapter.items[i]
//                    idCopyDetailBean.originalid = PublicBean.SensorList[i - 1]
//                    idCopyDetailBean.newid = PublicBean.NewSensorList[i - 1]
//                    idCopyDetailBean.state = IDCopyDetailBean.STATE_NORMAL
//                    idCopyDetailAdapter.setItem(i, idCopyDetailBean)
//                }
//            }
//            rvIDCopyDetail.adapter = idCopyDetailAdapter
//        }
    }

    var BooResult = arrayOf(false, false, false, false)
    /**
     * Copy成功
     */
    private fun copySuccess(index: Int, success: Boolean) {
        PublicBean.CopySuccessID = ArrayList(PublicBean.SensorList)
        BooResult[index] = success
        if (index == PublicBean.SensorList.size - 1) {
            var issuccess = true
            for (i in 0 until PublicBean.SensorList.size) {
                issuccess = BooResult[i] && issuccess
            }
            if (issuccess) {
                rootview.bt_program.visibility = View.GONE
                rootview.bt_menue.visibility = View.VISIBLE
            } else {
                rootview.bt_program.text = resources.getString(R.string.app_re_program)
                rootview.bt_menue.visibility = View.GONE
                rootview.bt_program.visibility = View.VISIBLE
            }
        }
        when (index) {
            0 -> {
                cwCar.setCarStatus(
                    CarWidget.CAR_LOCATION.TOP_LEFT,
                    if (success) CarWidget.CAR_STATUS.NORMAL else CarWidget.CAR_STATUS.BAD
                )
            }
            1 -> {
                cwCar.setCarStatus(
                    CarWidget.CAR_LOCATION.TOP_RIGHT,
                    if (success) CarWidget.CAR_STATUS.NORMAL else CarWidget.CAR_STATUS.BAD
                )
            }
            2 -> {
                cwCar.setCarStatus(
                    CarWidget.CAR_LOCATION.BOTTOM_RIGHT,
                    if (success) CarWidget.CAR_STATUS.NORMAL else CarWidget.CAR_STATUS.BAD
                )
            }
            3 -> {
                cwCar.setCarStatus(
                    CarWidget.CAR_LOCATION.BOTTOM_LEFT,
                    if (success) CarWidget.CAR_STATUS.NORMAL else CarWidget.CAR_STATUS.BAD
                )
            }
        }
                if (success) {
                    staticlist[index].setImageResource(R.mipmap.icon_correct)
                    newlist[index].setText(PublicBean.NewSensorList[index].substring(8 - idcount))
                    newlist[index].setTextColor(resources.getColor(R.color.color_black))
                } else {
                    staticlist[index].setImageResource(R.mipmap.icon_error)
                }

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

    fun upload() {
        var allsuccess = true
        Thread {
            val idrecord: ArrayList<SensorRecord> = ArrayList()
            if (PublicBean.SensorList.size == PublicBean.NewSensorList.size) {
                for (i in 0 until newlist.size) {
                    if (originalist[i].text!!.isNotEmpty()) {
                        val b = SensorRecord()
                        b.Car_SensorID = originalist[i].text.toString()
                        b.SensorID = newlist[i].text.toString()
                        Log.e("copy", "${BooResult[0]}${BooResult[1]}${BooResult[2]}${BooResult[3]}")
                        b.IsSuccess = "" + BooResult[i]
                        if (b.IsSuccess == "false") {
                            allsuccess = false
                        }
                        idrecord.add(b)
                    }
                }
            }
            Fuction.Upload_IDCopyRecord(
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
            HttpDownloader.post(
                "/topics/LogCat",
                if (allsuccess) "COPY成功-(${PublicBean.ProgramNumber})${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}*${PublicBean.OG_SerialNum}" else "COPY失敗-(${PublicBean.ProgramNumber})${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}*${PublicBean.OG_SerialNum}",
                OgCommand.tx_memory.toString()
            )
            OgCommand.tx_memory = StringBuffer("");
        }.start()

    }

    /**
     * 检测是否可以copy数据了
     */
    private fun checkCanCopy(): Boolean {
//        ProgramTrigger.clear()
//        for (i in 0 until PublicBean.NewSensorList.size) {
//            val idCopyDetailBean = idCopyDetailAdapter.items[i + 1]
//            ProgramTrigger.add(idCopyDetailBean.newid)
//            //只要有一个不为空即可
//            if (TextUtils.isEmpty(idCopyDetailBean.originalid) && TextUtils.isEmpty(idCopyDetailBean.newid)) {
//                return false
//            }
//        }
        return true
    }

    /**
     * 初始化页面
     */
    private fun initView() {
        JzActivity.getControlInstance().showDiaLog(R.layout.sensor_way_dialog, false, false, SensorWay())
        //音效与震动
        vibMediaUtil = VibMediaUtil(activity)
    }

    override fun onPause() {
        super.onPause()
        vibMediaUtil.release()
        try{
            HardwareApp.getInstance().switchScan(false)
            HardwareApp.getInstance().removeDataReceiver(dataReceiver)
        }catch (e:Exception){e.printStackTrace()}
    }
}
