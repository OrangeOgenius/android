package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.adapter.SelectAdapter
import com.orange.tpms.helper.UnitHelper

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Setting_Unit : RootFragement(R.layout.fragment_frag__setting__unit) {
    override fun viewInit() {
        rvTemperature=rootview.findViewById(R.id.rv_temp)
        rvTirePressure=rootview.findViewById(R.id.rv_tire)
        rvNumeral=rootview.findViewById(R.id.rv_numeral)
        initView()
        initHelper()
    }

    lateinit var rvTemperature: androidx.recyclerview.widget.RecyclerView//列表
    lateinit var rvTirePressure: androidx.recyclerview.widget.RecyclerView//列表
    lateinit var rvNumeral: androidx.recyclerview.widget.RecyclerView//列表 lateinit var SelectAdapter tempAdapter;//Adapter
    lateinit var tempAdapter: SelectAdapter//Adapter
    lateinit var tireAdapter: SelectAdapter//Adapter
    lateinit var numeralAdapter: SelectAdapter//Adapter
    lateinit var tempLinearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager//列表表格布局
    lateinit var tireLinearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager//列表表格布局
    lateinit var numeralLinearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager//列表表格布局
    lateinit var unitHelper: UnitHelper//Helper


    /**
     * 初始化页面
     */
    private fun initView() {
            tempLinearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        rvTemperature.layoutManager = tempLinearLayoutManager
        tempAdapter = SelectAdapter(activity)
        rvTemperature.adapter = tempAdapter
        tempAdapter.setOnItemClickListener { pos, content -> unitHelper.setTemp(activity, pos)
            SetTem(pos)}
        //配置TireRecyclerView
            tireLinearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        rvTirePressure.layoutManager = tireLinearLayoutManager
        tireAdapter = SelectAdapter(activity)
        rvTirePressure.adapter = tireAdapter
        tireAdapter.setOnItemClickListener { pos, content -> unitHelper.setPressure(activity, pos)
            SetPr(pos)}
        //配置NumeralRecyclerView
            numeralLinearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        rvNumeral.layoutManager = numeralLinearLayoutManager
        numeralAdapter = SelectAdapter(activity)
        rvNumeral.adapter = numeralAdapter
        numeralAdapter.setOnItemClickListener { pos, content -> unitHelper.setNumeral(activity, pos)
            SetNs(pos)}
    }

    /**
     * 初始化Helper
     */
    private fun initHelper() {
        unitHelper = UnitHelper(activity)
        //开始请求
        unitHelper.setOnPreRequestListener { }
        //结束请求
        unitHelper.setOnFinishRequestListener { }
        //请求成功
        unitHelper.setOnSuccessRequestListener { `object` -> }
        //请求失败
        unitHelper.setOnFailedRequestListener { `object` -> JzActivity.getControlInstance().toast(`object`.toString()) }
        //温度单位列表回调
        unitHelper.setOnGetTempListener { select, arrayList ->
            tempAdapter.items = arrayList
            tempAdapter.setSelect(select)
            tempAdapter.notifyDataSetChanged()
        }
        //压力单位列表回调
        unitHelper.setOnGetPressureListener { select, arrayList ->
            tireAdapter.items = arrayList
            tireAdapter.setSelect(select)
            tireAdapter.notifyDataSetChanged()
        }
        //数字单位列表回调
        unitHelper.setOnGetNumeralListener { select, arrayList ->
            numeralAdapter.items = arrayList
            numeralAdapter.setSelect(select)
            numeralAdapter.notifyDataSetChanged()
        }
        //读取单位列表
        unitHelper.getUnit(activity)
    }
}
