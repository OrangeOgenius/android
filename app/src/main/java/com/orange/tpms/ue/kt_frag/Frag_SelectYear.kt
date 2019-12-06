package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.adapter.ShowYear
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.fragment_frag__select_modle.view.*
import kotlinx.android.synthetic.main.fragment_frag__select_year.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_SelectYear : RootFragement() {
    private var year: ArrayList<String> = ArrayList()
    lateinit var adapter:ShowYear
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__select_year, container, false)
        if(PublicBean.position==PublicBean.ID_COPY_OBD||PublicBean.position==PublicBean.OBD_RELEARM){
            year = (activity as KtActivity).itemDAO.getObdYear(PublicBean.SelectMake, PublicBean.SelectModel)
        }else{
            year = (activity as KtActivity).itemDAO.getYear(PublicBean.SelectMake, PublicBean.SelectModel)
        }
        rootview.rv_year.layoutManager=LinearLayoutManager(act)
        adapter=ShowYear(year,act);
        rootview.rv_year.adapter= adapter
        return rootview
    }
    override fun onTop() {
        FocusReset(-1)
    }
    override fun onDown() {
        FocusReset(1)
    }
    fun FocusReset(re:Int){
        if(adapter.focus+re>=0&&adapter.focus+re<year.size){
            adapter.focus+=re
        }
        rootview.rv_year.scrollToPosition(adapter.focus)
        adapter.notifyDataSetChanged()
    }

    override fun enter() {
        PublicBean.SelectYear = year.get(adapter.focus)
        adapter.AddFavorite()
        adapter.ChangePage()
    }
}
