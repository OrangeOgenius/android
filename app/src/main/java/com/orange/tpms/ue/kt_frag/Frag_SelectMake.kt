package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.adapter.ShowItemImage
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.mmySql.Item
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.fragment_frag__select_make.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_SelectMake : RootFragement(R.layout.fragment_frag__select_make) {
    override fun viewInit() {
        if(PublicBean.position==PublicBean.ID_COPY_OBD||PublicBean.position==PublicBean.OBD_RELEARM){
            make=(activity as KtActivity).itemDAO.getobdmake();
        }else{make=(activity as KtActivity).itemDAO.getMake(activity!!);}

        carLogoAdapter = ShowItemImage(make)
        rootview.rv_makes.layoutManager= androidx.recyclerview.widget.GridLayoutManager(activity, 2)
        rootview.rv_makes.adapter= carLogoAdapter
    }

    private var make: ArrayList<Item> = ArrayList()
    lateinit var carLogoAdapter:ShowItemImage

    override fun onLeft() {
        FocusReset(-1)
    }
    override fun onRight() {
        FocusReset(1)
    }
    override fun onTop() {
        FocusReset(-2)
    }
    override fun onDown() {
        FocusReset(2)
    }
    fun FocusReset(re:Int){
        if(carLogoAdapter.focus+re>=0&&carLogoAdapter.focus+re<make!!.size){
            carLogoAdapter.focus+=re
        }
        rootview.rv_makes.scrollToPosition(carLogoAdapter.focus)
        carLogoAdapter.notifyDataSetChanged()
    }

    override fun enter() {
        PublicBean.SelectMake = make!!.get(carLogoAdapter.focus).make
        JzActivity.getControlInstance().changeFrag(Frag_SelectModle(), R.id.frage, "Frag_car_model", true)
    }
}
