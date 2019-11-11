package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.RootFragement

import com.orange.tpms.R
import com.orange.tpms.helper.ReadSensorHelper
import com.orange.tpms.ue.frag.Frag_home
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.LoadingWidget
import kotlinx.android.synthetic.main.fragment_frag__check__sensor__read.view.*

class Frag_Check_Sensor_Read : RootFragement() {
    internal var ObdHex = "00"
    lateinit var vibMediaUtil: VibMediaUtil
    lateinit var readSensorHelper: ReadSensorHelper
    lateinit var lwLoading: LoadingWidget
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__check__sensor__read, container, false)
        vibMediaUtil=VibMediaUtil(activity)
        initHelper()
        rootview.bt_tigger.setOnClickListener {
            trigger()
        }
        rootview.bt_menue.setOnClickListener {
            act.supportFragmentManager!!.popBackStack(null,1)
        }
        return rootview
    }
    private fun trigger() {
        rootview.tv_id.setText("")
        rootview.tv_kpa.setText("")
        rootview.tv_c.setText("")
        rootview.tv_bat.setText("")
        rootview.tv_vol.setText("")
        readSensorHelper.readSensor(1, ObdHex, rootview.editText.getText().toString())
        vibMediaUtil.playVibrate()
    }
    private fun initHelper() {
        readSensorHelper = ReadSensorHelper()
        //开始请求
        readSensorHelper.setOnPreRequestListener { lwLoading.show(resources.getString(R.string.app_data_reading)) }
        //结束请求
        readSensorHelper.setOnFinishRequestListener { lwLoading.hide() }
        //请求成功
        readSensorHelper.setOnSuccessRequestListener {
//                `object` -> toFrag(Frag_home::class.java, false, true, null)
        }
        //请求失败
        readSensorHelper.setOnFailedRequestListener {
//                `object` -> toast(`object`.toString(), 2000)
        }
        //读取四个轮胎情况
        readSensorHelper.setReadSensorListener { sensorDataBean ->
            vibMediaUtil.playBeep()
            rootview.tv_id.setText(sensorDataBean.sensor_id.toString())
            rootview.tv_kpa.setText(sensorDataBean.kpa.toString())
            rootview.tv_c.setText(sensorDataBean.temp.toString())
            rootview.tv_bat.setText(sensorDataBean.batteryLevel.toString())
            rootview.tv_vol.setText(sensorDataBean.v.toString())
        }
        //读取失败
        readSensorHelper.setOnReadFailedListener {
            lwLoading.show(
                R.mipmap.img_update_failed,
                resources.getString(R.string.app_read_failed),
                false
            )
        }
    }

}
