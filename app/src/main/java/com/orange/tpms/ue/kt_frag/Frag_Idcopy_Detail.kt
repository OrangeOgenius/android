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
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.Callback.Copy_C
import com.orange.tpms.R
import com.orange.tpms.adapter.IDCopyDetailAdapter
import com.orange.tpms.bean.IDCopyDetailBean
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.helper.CopyIDHelper
import com.orange.tpms.utils.Command
import com.orange.tpms.utils.NumberUtil
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.CarWidget
import com.orange.tpms.widget.LoadingWidget
import com.orange.tpms.widget.TitleWidget
import kotlinx.android.synthetic.main.fragment_frag__idcopy__detail.view.*
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Idcopy_Detail : RootFragement(), Copy_C {


    override fun Copy_Next(success: Boolean, position: Int) {
        handler.post { vibMediaUtil.playBeep()
            lwLoading.tvLoading.setText("${position*100/PublicBean.SensorList.size}%")
            copySuccess(position, success)}

    }

    override fun Copy_Finish() {
        handler.post { lwLoading.hide() }
        run=false
    }

    lateinit var rvIDCopyDetail: RecyclerView
    lateinit var lwLoading: LoadingWidget//Loading
    lateinit var cwCar: CarWidget//CarWidget
    lateinit var idCopyDetailAdapter: IDCopyDetailAdapter//适配器
    lateinit var linearLayoutManager: LinearLayoutManager//列表表格布局
    lateinit var vibMediaUtil: VibMediaUtil//音效与振动
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__idcopy__detail, container, false)
        rootview.tv_content.text="${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        run=false
        rvIDCopyDetail=rootview.findViewById(R.id.rv_id_copy_neww)
        lwLoading=rootview.findViewById(R.id.ldw_loading)
        cwCar=rootview.findViewById(R.id.cw_car)
        rootview.bt_program.setOnClickListener { program() }
        rootview.bt_menue.setOnClickListener { GoMenu() }
        initView()
        updateView()
        return rootview
    }

    override fun onKeyTrigger() {
        super.onKeyTrigger()
        program()
    }
    /**
     * 烧录
     */
    private fun program() {
        if(run){return}
        run=true
        lwLoading.show()
        vibMediaUtil.playVibrate()
        if (checkCanCopy()) {
            Thread{Command.IdCopy(this,"Frag_Idcopy_Detail")}.start()
        } else {
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

    /**
     * Copy成功
     */
    private fun copySuccess(index: Int, success: Boolean) {
        if (success) {
            cwCar.setCarStatus(CarWidget.CAR_LOCATION.TOP_LEFT, CarWidget.CAR_STATUS.NORMAL)
        } else {
            cwCar.setCarStatus(CarWidget.CAR_LOCATION.TOP_LEFT, CarWidget.CAR_STATUS.BAD)
        }
        if (PublicBean.SensorList.size == PublicBean.NewSensorList.size) {
            for (i in 1 until idCopyDetailAdapter.items.size) {
                if (i <= PublicBean.SensorList.size && i == index + 1) {
                    val idCopyDetailBean = idCopyDetailAdapter.items[i]
                    if (success) {
                        idCopyDetailBean.state = IDCopyDetailBean.STATE_SUCCESS
                    } else {
                        idCopyDetailBean.state = IDCopyDetailBean.STATE_FAILED
                    }
                    idCopyDetailAdapter.setItem(i, idCopyDetailBean)
                }
            }
            rvIDCopyDetail.adapter = idCopyDetailAdapter
        }
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
            "",
            getString(R.string.app_original_id),
            getString(R.string.app_new_sensor),
            getString(R.string.app_check_up),
            IDCopyDetailBean.STATE_HIDE
        )
        val frBean = IDCopyDetailBean("FR", "", "", "", IDCopyDetailBean.STATE_HIDE)
        val rrBean = IDCopyDetailBean("RR", "", "", "", IDCopyDetailBean.STATE_HIDE)
        val rlBean = IDCopyDetailBean("RL", "", "", "", IDCopyDetailBean.STATE_HIDE)
        val flBean = IDCopyDetailBean("FL", "", "", "", IDCopyDetailBean.STATE_HIDE)
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
