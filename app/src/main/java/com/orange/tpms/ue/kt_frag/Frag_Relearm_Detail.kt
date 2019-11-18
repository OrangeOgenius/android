package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.fragment_frag__relearm__detail.view.*


class Frag_Relearm_Detail : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__relearm__detail, container, false)
        rootview.mmy_text.text="${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        rootview.textView10.text=(activity as KtActivity).itemDAO.GetreLarm(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear,act)
        rootview.prog_bt.setOnClickListener { GoMenu() }
        return rootview
    }


}
