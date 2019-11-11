package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.mmySql.ItemDAO
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.activity_frag_home.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_home : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.activity_frag_home, container, false)
        rootview.bt_check_sensor.setOnClickListener {
            act.ChangePage(Frag_CheckSensor(),R.id.frage,"Frag_CheckSensor",true)
        }
        (activity as KtActivity).itemDAO = ItemDAO(activity!!);
        return rootview
    }


}
