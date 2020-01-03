package com.orange.tpms.ue.kt_frag


import androidx.fragment.app.Fragment
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.adapter.ShowYear
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.fragment_frag__select_year.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_SelectYear : RootFragement(R.layout.fragment_frag__select_year) {
    private var year: ArrayList<String> = ArrayList()
    lateinit var adapter:ShowYear
    override fun viewInit() {
        if(PublicBean.position==PublicBean.ID_COPY_OBD||PublicBean.position==PublicBean.OBD_RELEARM){
            year = (activity as KtActivity).itemDAO.getObdYear(PublicBean.SelectMake, PublicBean.SelectModel)
        }else{
            year = (activity as KtActivity).itemDAO.getYear(PublicBean.SelectMake, PublicBean.SelectModel)
        }
        rootview.rv_year.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(act)
        adapter=ShowYear(year);
        rootview.rv_year.adapter= adapter
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
