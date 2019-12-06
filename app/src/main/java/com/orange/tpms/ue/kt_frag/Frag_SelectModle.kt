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
import com.orango.electronic.orangetxusb.Adapter.ShowModel
import kotlinx.android.synthetic.main.fragment_frag__select_modle.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_SelectModle : RootFragement() {
    private var modle: ArrayList<String> = ArrayList()
    lateinit var adapter:ShowModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(isInitialized()){return  rootview}
        rootview=inflater.inflate(R.layout.fragment_frag__select_modle, container, false)
        if(PublicBean.position==PublicBean.ID_COPY_OBD||PublicBean.position==PublicBean.OBD_RELEARM){
            modle=(activity as KtActivity).itemDAO.getobdmodel(PublicBean.SelectMake)
        }else{
            modle = (activity as KtActivity).itemDAO.getModel(PublicBean.SelectMake)
        }
        adapter=ShowModel(modle,act)
        rootview.rv_model.layoutManager= LinearLayoutManager(activity)
        rootview.rv_model.adapter=adapter
        return rootview
    }

    override fun onTop() {
        FocusReset(-1)
    }
    override fun onDown() {
        FocusReset(1)
    }
    fun FocusReset(re:Int){
        if(adapter.focus+re>=0&&adapter.focus+re<modle.size){
            adapter.focus+=re
        }
        rootview.rv_model.scrollToPosition(adapter.focus)
        adapter.notifyDataSetChanged()
    }

    override fun enter() {
        PublicBean.SelectModel = modle.get(adapter.focus)
        act.ChangePage(Frag_SelectYear(),R.id.frage,"Frag_SelectYear",true)
    }

}
