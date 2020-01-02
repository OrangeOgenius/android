package com.orango.electronic.orangetxusb.SettingPagr


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import kotlinx.android.synthetic.main.frag_language.view.*


class Set_Languages : RootFragement(R.layout.frag_language) {
    override fun viewInit() {
        rootview.bt_select_en.setOnClickListener {
            SetLan(LOCALE_ENGLISH)
            JzActivity.getControlInstance().changeFrag(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",false)}
        rootview.bt_select_cn.setOnClickListener {
            SetLan(LOCALE_CHINESE)
            JzActivity.getControlInstance().changeFrag(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",false)}
        rootview.bt_select_tr.setOnClickListener {
            SetLan(LOCALE_TAIWAIN)
            JzActivity.getControlInstance().changeFrag(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",false)}
        rootview.bt_select_ita.setOnClickListener {
            SetLan(LOCALE_ITALIANO)
            JzActivity.getControlInstance().changeFrag(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",false)}
        rootview.bt_select_deu.setOnClickListener {
            SetLan(LOCALE_DE)
            JzActivity.getControlInstance().changeFrag(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",false)}
    }

    companion object {
       var place=0
    }

    override fun onResume() {
        super.onResume()
    }
}
