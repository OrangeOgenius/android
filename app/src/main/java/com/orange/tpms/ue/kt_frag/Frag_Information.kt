package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.adapter.InformationAdapter
import com.orange.tpms.helper.InformationHelper

class Frag_Information : RootFragement() {
    lateinit var rvInformation: RecyclerView//列表
    lateinit var informationAdapter: InformationAdapter//Adapter
    lateinit var linearLayoutManager: LinearLayoutManager//列表表格布局
    lateinit var informationHelper: InformationHelper//Helper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       rootview=inflater.inflate(R.layout.fragment_frag__information, container, false)
        rvInformation=rootview.findViewById(R.id.rv_information)
        initView()
        initHelper()
        return rootview
    }

    /**
     * 初始化页面
     */
    private fun initView() {
            linearLayoutManager = LinearLayoutManager(activity)
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
        informationHelper.setOnFailedRequestListener { `object` -> act.Toast(`object`.toString()) }
        //锁定列表回调
        informationHelper.setOnGetInformationListener { arrayList ->
            informationAdapter.items = arrayList
            informationAdapter.notifyDataSetChanged()
        }
        //读取锁定列表
        informationHelper.getInformation(activity)
    }

}
