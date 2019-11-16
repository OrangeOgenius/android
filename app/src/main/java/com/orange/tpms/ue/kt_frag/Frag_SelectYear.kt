package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement

import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.adapter.ShowYear
import kotlinx.android.synthetic.main.frag_car_year.view.*
import java.util.ArrayList

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_SelectYear : RootFragement() {
    private var year: ArrayList<String> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__select_year, container, false)
        year = (activity as KtActivity).itemDAO.getYear(PublicBean.SelectMake, PublicBean.SelectModel)!!
        rootview.rv_year.layoutManager=LinearLayoutManager(act)
        rootview.rv_year.adapter= ShowYear(year,act);
        return rootview
    }


}
