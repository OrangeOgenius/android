package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.adapter.SelectAdapter
import com.orange.tpms.helper.UnitHelper
import com.orange.tpms.lib.db.share.SettingShare

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Setting_Unit : RootFragement() {
    lateinit var rvTemperature: RecyclerView//列表
    lateinit var rvTirePressure: RecyclerView//列表
    lateinit var rvNumeral: RecyclerView//列表 lateinit var SelectAdapter tempAdapter;//Adapter
    lateinit var tempAdapter: SelectAdapter//Adapter
    lateinit var tireAdapter: SelectAdapter//Adapter
    lateinit var numeralAdapter: SelectAdapter//Adapter
    lateinit var tempLinearLayoutManager: LinearLayoutManager//列表表格布局
    lateinit var tireLinearLayoutManager: LinearLayoutManager//列表表格布局
    lateinit var numeralLinearLayoutManager: LinearLayoutManager//列表表格布局
    lateinit var unitHelper: UnitHelper//Helper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__setting__unit, container, false)
        rvTemperature=rootview.findViewById(R.id.rv_temp)
        rvTirePressure=rootview.findViewById(R.id.rv_tire)
        rvNumeral=rootview.findViewById(R.id.rv_numeral)
        initView()
        initHelper()
        return rootview
    }

    /**
     * 初始化页面
     */
    private fun initView() {
            tempLinearLayoutManager = LinearLayoutManager(activity)
        rvTemperature.layoutManager = tempLinearLayoutManager
        tempAdapter = SelectAdapter(activity)
        rvTemperature.adapter = tempAdapter
        tempAdapter.setOnItemClickListener { pos, content -> unitHelper.setTemp(activity, pos)
            SetTem(pos)}
        //配置TireRecyclerView
            tireLinearLayoutManager = LinearLayoutManager(activity)
        rvTirePressure.layoutManager = tireLinearLayoutManager
        tireAdapter = SelectAdapter(activity)
        rvTirePressure.adapter = tireAdapter
        tireAdapter.setOnItemClickListener { pos, content -> unitHelper.setPressure(activity, pos)
            SetPr(pos)}
        //配置NumeralRecyclerView
            numeralLinearLayoutManager = LinearLayoutManager(activity)
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
        unitHelper.setOnFailedRequestListener { `object` -> act.Toast(`object`.toString()) }
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
