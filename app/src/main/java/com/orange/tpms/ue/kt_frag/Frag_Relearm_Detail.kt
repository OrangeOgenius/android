package com.orange.tpms.ue.kt_frag


import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.fragment_frag__relearm__detail.view.*


class Frag_Relearm_Detail : RootFragement(R.layout.fragment_frag__relearm__detail) {
    override fun viewInit() {
        rootview.mmy_text.text="${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        rootview.textView10.text=(activity as KtActivity).itemDAO.GetreLarm(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear,act)
        rootview.prog_bt.setOnClickListener { GoMenu() }
    }
}
