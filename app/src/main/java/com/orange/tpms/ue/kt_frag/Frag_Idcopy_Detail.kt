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
import com.orange.blelibrary.blelibrary.Callback.DaiSetUp
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.Callback.Copy_C
import com.orange.tpms.Callback.Program_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.HttpCommand.SensorRecord
import com.orange.tpms.R
import com.orange.tpms.adapter.IDCopyDetailAdapter
import com.orange.tpms.bean.IDCopyDetailBean
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.CarWidget
import kotlinx.android.synthetic.main.activity_kt.*
import kotlinx.android.synthetic.main.data_loading.*
import kotlinx.android.synthetic.main.fragment_frag__idcopy__detail.view.*
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Idcopy_Detail : RootFragement(), Copy_C, Program_C {
    override fun Program_Progress(i: Int) {
        if (!act.NowFrage.equals("Frag_Idcopy_Detail")) {
            return
        }
        handler.post {
            act.ShowDaiLog(R.layout.data_loading, false, true, DaiSetUp {
                it.pass.visibility = View.VISIBLE
                it.pass.text = "$i%"
            })
        }
    }

    override fun Program_Finish(boolean: Boolean) {
        if (!act.NowFrage.equals("Frag_Idcopy_Detail")) {
            return
        }
        run = false
        endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        if (boolean) {
            Log.e("DATA:", "燒錄成功")
            val result = OgCommand.GetPrId(ObdHex, LF)
            if (!act.NowFrage.equals("Frag_Idcopy_Detail")) {
                return
            }
            handler.post {
                var position=0
                for (i in result) {
                    if(position<PublicBean.NewSensorList.size){PublicBean.NewSensorList[position]=i.id}
                    Log.e("DATA:", "成功id:" + i.id)
                    position++
                }
                Thread {
                    sleep(2000)
                    OgCommand.IdCopy(this,ObdHex) }.start()
            }
        } else {
            handler.post { AllFall()
                Log.e("DATA:", "燒錄失敗")
                act.DaiLogDismiss()}
        }
        handler.post {
            vibMediaUtil.playBeep()
        }
    }
fun AllFall(){
    for(i in 0 until PublicBean.SensorList.size){
        copySuccess(i, false)
    }
}
    override fun Copy_Next(success: Boolean, position: Int) {
        Log.e("Copy_Finish","$position"+success)
        BooResult[position]=success
        handler.post {
            vibMediaUtil.playBeep()
            act.ShowDaiLog(R.layout.data_loading,false,true, DaiSetUp {
                it.pass.visibility=View.VISIBLE
                it.pass.text="${position * 100 / PublicBean.SensorList.size}%"
            })
            copySuccess(position, success)
        }

    }

    override fun Copy_Finish(boolean: Boolean) {
        handler.post { act.DaiLogDismiss() }
        run = false
        endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        if(!boolean){
            handler.post { AllFall() }}
        Log.e("Copy_Finish","$boolean")
        upload()
    }
    var LF = "00"
    var ObdHex = "00"
    var startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    var endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    lateinit var rvIDCopyDetail: RecyclerView
    lateinit var cwCar: CarWidget//CarWidget
    var idcount=8;
    lateinit var idCopyDetailAdapter: IDCopyDetailAdapter//适配器
    lateinit var linearLayoutManager: LinearLayoutManager//列表表格布局
    lateinit var vibMediaUtil: VibMediaUtil//音效与振动
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(isInitialized()){return rootview}
        rootview = inflater.inflate(R.layout.fragment_frag__idcopy__detail, container, false)
        LF = (activity as KtActivity).itemDAO.GetLf(PublicBean.SelectMake, PublicBean.SelectModel, PublicBean.SelectYear)
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
        ObdHex=(activity as KtActivity).itemDAO.GetHex(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        run = false
        rvIDCopyDetail = rootview.findViewById(R.id.rv_id_copy_neww)
        cwCar = rootview.findViewById(R.id.cw_car)
        rootview.bt_program.setOnClickListener { program() }
        rootview.bt_menue.setOnClickListener { GoMenu() }
        initView()
        updateView()
        val s19=GetPro((activity as KtActivity).itemDAO.getMMY(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear),"nodata")
        SensorRecord.SensorCode_Version = s19
        return rootview
    }

    override fun enter() {
        super.enter()
        program()
    }
    override fun onKeyTrigger() {
        super.onKeyTrigger()
        program()
    }

    /**
     * 烧录
     */
    private fun program() {
        act.back.setImageResource(R.mipmap.menu)
        act.back.setOnClickListener { GoMenu() }
        rootview.bt_menue.text=resources.getString(R.string.Relearn_Procedure)
        rootview.bt_menue.setOnClickListener { act.ChangePage(Frag_Relearm_Detail(),R.id.frage,"Frag_Relearm_Detail",true) }
        if (run) {
            return
        }
        run = true
        act.DaiLogDismiss()
        act.ShowDaiLog(R.layout.data_loading,false,true, DaiSetUp {
        })
        startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        vibMediaUtil.playVibrate()
        if (checkCanCopy()) {
            Thread{
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
                )
            }.start()
        } else {
            run=false
            act.Toast(R.string.app_no_data_to_copy)
        }
    }

    /**
     * 刷新页面
     */
    private fun updateView() {
        if (PublicBean.SensorList.size == PublicBean.NewSensorList.size) {
            for (i in 1 until idCopyDetailAdapter.items.size) {
                if (i <= PublicBean.SensorList.size) {
                    val idCopyDetailBean = idCopyDetailAdapter.items[i]
                    idCopyDetailBean.originalid = PublicBean.SensorList[i - 1]
                    idCopyDetailBean.newid = PublicBean.NewSensorList[i - 1]
                    idCopyDetailBean.state = IDCopyDetailBean.STATE_NORMAL
                    idCopyDetailAdapter.setItem(i, idCopyDetailBean)
                }
            }
            rvIDCopyDetail.adapter = idCopyDetailAdapter
        }
    }
var BooResult= arrayOf(false,false,false,false)
    /**
     * Copy成功
     */
    private fun copySuccess(index: Int, success: Boolean) {
        BooResult[index]=success
        when(index){
            0->{cwCar.setCarStatus(CarWidget.CAR_LOCATION.TOP_LEFT, if(success)CarWidget.CAR_STATUS.NORMAL else CarWidget.CAR_STATUS.BAD)}
            1->{cwCar.setCarStatus(CarWidget.CAR_LOCATION.TOP_RIGHT, if(success)CarWidget.CAR_STATUS.NORMAL else CarWidget.CAR_STATUS.BAD)}
            2->{cwCar.setCarStatus(CarWidget.CAR_LOCATION.BOTTOM_RIGHT, if(success)CarWidget.CAR_STATUS.NORMAL else CarWidget.CAR_STATUS.BAD)}
            3->{cwCar.setCarStatus(CarWidget.CAR_LOCATION.BOTTOM_LEFT, if(success)CarWidget.CAR_STATUS.NORMAL else CarWidget.CAR_STATUS.BAD)}
        }
        if (PublicBean.SensorList.size == PublicBean.NewSensorList.size) {
            for (i in 1 until idCopyDetailAdapter.items.size) {
                if (i <= PublicBean.SensorList.size && i == index + 1) {
                    val idCopyDetailBean = idCopyDetailAdapter.items[i]
                    if (success) {
                        idCopyDetailBean.state = IDCopyDetailBean.STATE_SUCCESS
                        idCopyDetailBean.newid=idCopyDetailBean.newid.substring(8-idcount)
                    } else {
                        idCopyDetailBean.state = IDCopyDetailBean.STATE_FAILED
                    }
                    idCopyDetailAdapter.setItem(i, idCopyDetailBean)
                }
//                BooResult
            }
            idCopyDetailAdapter.notifyDataSetChanged()
        }
    }

    fun upload() {
        Thread {
            val idrecord: ArrayList<SensorRecord> = ArrayList()
            if (PublicBean.SensorList.size == PublicBean.NewSensorList.size) {
                for (i in 1 until idCopyDetailAdapter.items.size) {
                    val idCopyDetailBean = idCopyDetailAdapter.items[i]
                    if(idCopyDetailBean.originalid.isNotEmpty()){
                        val b = SensorRecord()
                        b.Car_SensorID = idCopyDetailBean.originalid
                        b.SensorID = idCopyDetailBean.newid
                        Log.e("copy","${BooResult[0]}${BooResult[1]}${BooResult[2]}${BooResult[3]}")
                        b.IsSuccess = ""+BooResult[i-1]
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
        }.start()

    }

    /**
     * 检测是否可以copy数据了
     */
    private fun checkCanCopy(): Boolean {
        for (i in 1 until idCopyDetailAdapter.items.size) {
            val idCopyDetailBean = idCopyDetailAdapter.items[i]
            //只要有一个不为空即可
            if (!TextUtils.isEmpty(idCopyDetailBean.originalid) && !TextUtils.isEmpty(idCopyDetailBean.newid)) {
                return true
            }
        }
        return false
    }

    /**
     * 初始化页面
     */
    private fun initView() {
        //音效与震动
        vibMediaUtil = VibMediaUtil(activity)
        //配置RecyclerView,每行是哪个元素
        linearLayoutManager = LinearLayoutManager(activity)
        rvIDCopyDetail.layoutManager = linearLayoutManager
        idCopyDetailAdapter = IDCopyDetailAdapter(activity)
        rvIDCopyDetail.adapter = idCopyDetailAdapter
        //数据源
        val numberList = ArrayList<IDCopyDetailBean>()
        val titleBean = IDCopyDetailBean(
            "WH",
            getString(R.string.app_original_id),
            "New Sensor",
            "CHK",
            IDCopyDetailBean.STATE_HIDE
        )
        val frBean = IDCopyDetailBean("LF", "", "", "", IDCopyDetailBean.STATE_HIDE)
        val rrBean = IDCopyDetailBean("FR", "", "", "", IDCopyDetailBean.STATE_HIDE)
        val rlBean = IDCopyDetailBean("RR", "", "", "", IDCopyDetailBean.STATE_HIDE)
        val flBean = IDCopyDetailBean("LR", "", "", "", IDCopyDetailBean.STATE_HIDE)
        numberList.add(titleBean)
        numberList.add(frBean)
        numberList.add(rrBean)
        numberList.add(rlBean)
        numberList.add(flBean)
        idCopyDetailAdapter.items = numberList
        idCopyDetailAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        vibMediaUtil.release()
    }
}
