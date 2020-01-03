package com.orange.tpms.ue.kt_frag


import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.adapter.InformationAdapter
import com.orange.tpms.helper.InformationHelper

class Frag_Information : RootFragement(R.layout.fragment_frag__information) {
    lateinit var rvInformation: androidx.recyclerview.widget.RecyclerView//列表
    lateinit var informationAdapter: InformationAdapter//Adapter
    lateinit var linearLayoutManager: androidx.recyclerview.widget.LinearLayoutManager//列表表格布局
    lateinit var informationHelper: InformationHelper//Helper
    override fun viewInit() {
        rvInformation=rootview.findViewById(R.id.rv_information)
        initView()
        initHelper()
    }

    /**
     * 初始化页面
     */
    private fun initView() {
            linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        rvInformation.layoutManager = linearLayoutManager
        informationAdapter = InformationAdapter(activity)
        rvInformation.adapter = informationAdapter
    }

    /**
     * 初始化Helper
     */
    private fun initHelper() {
        informationHelper = InformationHelper()
        //开始请求
        informationHelper.setOnPreRequestListener { }
        //结束请求
        informationHelper.setOnFinishRequestListener { }
        //请求成功
        informationHelper.setOnSuccessRequestListener { `object` -> }
        //请求失败
        informationHelper.setOnFailedRequestListener { `object` -> JzActivity.getControlInstance().toast(`object`.toString()) }
        //锁定列表回调
        informationHelper.setOnGetInformationListener { arrayList ->
            informationAdapter.items = arrayList
            informationAdapter.notifyDataSetChanged()
        }
        //读取锁定列表
        informationHelper.getInformation(activity)
    }

}
