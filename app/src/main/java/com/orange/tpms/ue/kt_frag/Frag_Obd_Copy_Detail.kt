package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.adapter.obdadapter
import com.orange.tpms.bean.ObdBeans
import kotlinx.android.synthetic.main.fragment_frag__obd__copy__detail.view.*

class Frag_Obd_Copy_Detail : RootFragement() {
lateinit var adapter:obdadapter
    lateinit var beans: ObdBeans
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__obd__copy__detail, container, false)
        beans=ObdBeans()
        adapter=obdadapter(beans)
        rootview.rv_id_copy_neww.layoutManager=LinearLayoutManager(act)
        rootview.rv_id_copy_neww.adapter=adapter
        return rootview
    }


}
