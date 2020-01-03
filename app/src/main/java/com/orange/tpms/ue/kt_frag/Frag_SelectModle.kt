package com.orange.tpms.ue.kt_frag


import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import com.orango.electronic.orangetxusb.Adapter.ShowModel
import kotlinx.android.synthetic.main.fragment_frag__select_modle.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_SelectModle : RootFragement(R.layout.fragment_frag__select_modle) {
    override fun viewInit() {
        if(PublicBean.position==PublicBean.ID_COPY_OBD||PublicBean.position==PublicBean.OBD_RELEARM){
            modle=(activity as KtActivity).itemDAO.getobdmodel(PublicBean.SelectMake)
        }else{
            modle = (activity as KtActivity).itemDAO.getModel(PublicBean.SelectMake)
        }
        adapter=ShowModel(modle)
        rootview.rv_model.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(activity)
        rootview.rv_model.adapter=adapter
    }

    private var modle: ArrayList<String> = ArrayList()
    lateinit var adapter:ShowModel


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
        JzActivity.getControlInstance().changeFrag(Frag_SelectYear(),R.id.frage,"Frag_SelectYear",true)
    }

}
