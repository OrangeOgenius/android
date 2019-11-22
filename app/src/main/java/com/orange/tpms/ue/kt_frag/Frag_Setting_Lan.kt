package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import kotlinx.android.synthetic.main.frag_language.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Setting_Lan : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       rootview=inflater.inflate(R.layout.frag_language, container, false)
        rootview.bt_select_en.setOnClickListener {
            SetLan(LOCALE_ENGLISH)
            GoMenu()

        }
        rootview.bt_select_cn.setOnClickListener {
            SetLan(LOCALE_CHINESE)
        GoMenu()
        }
        rootview.bt_select_tr.setOnClickListener {
            SetLan(LOCALE_TAIWAIN)
            GoMenu()
        }
        rootview.bt_select_ita.setOnClickListener {
            SetLan(LOCALE_ITALIANO)
            GoMenu()
        }
        rootview.bt_select_deu.setOnClickListener {
            SetLan(LOCALE_DE)
            GoMenu()
        }
        return rootview
    }


}
