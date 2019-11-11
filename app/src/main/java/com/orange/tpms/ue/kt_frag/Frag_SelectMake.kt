package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.BleActivity
import com.orange.blelibrary.blelibrary.RootFragement

import com.orange.tpms.R
import com.orange.tpms.adapter.ShowItemImage
import com.orange.tpms.mmySql.Item
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.fragment_frag__select_make.view.*
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_SelectMake : RootFragement() {
    private var make: ArrayList<Item>? = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__select_make, container, false)
        make=(activity as KtActivity).itemDAO!!.getMake(activity!!);
        val carLogoAdapter = ShowItemImage(act, make)
        rootview.rv_makes.layoutManager=GridLayoutManager(activity, 2)
        rootview.rv_makes.adapter= carLogoAdapter
        return rootview
    }


}
