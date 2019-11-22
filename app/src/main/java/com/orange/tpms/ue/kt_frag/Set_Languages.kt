package com.orango.electronic.orangetxusb.SettingPagr


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import kotlinx.android.synthetic.main.frag_language.view.*


class Set_Languages : RootFragement() {
    companion object {
       var place=0
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.frag_language, container, false)
        rootview.bt_select_en.setOnClickListener {
            SetLan(LOCALE_ENGLISH)
            act.ChangePage(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",false)}
        rootview.bt_select_cn.setOnClickListener {
            SetLan(LOCALE_CHINESE)
            act.ChangePage(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",false)}
        rootview.bt_select_tr.setOnClickListener {
            SetLan(LOCALE_TAIWAIN)
            act.ChangePage(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",false)}
        rootview.bt_select_ita.setOnClickListener {
            SetLan(LOCALE_ITALIANO)
            act.ChangePage(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",false)}
        rootview.bt_select_deu.setOnClickListener {
            SetLan(LOCALE_DE)
        act.ChangePage(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",false)}
        return rootview
    }

    override fun onResume() {
        super.onResume()
    }
}
