package com.orango.electronic.orangetxusb.SettingPagr


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.utils.LanguageUtil
import kotlinx.android.synthetic.main.frag_language.view.*

import kotlinx.android.synthetic.main.fragment_set__languages.view.*
import java.util.ArrayList


class Set_Languages : RootFragement() {
    companion object {
       var place=0
    }
    lateinit var rootView:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView=inflater.inflate(R.layout.frag_language, container, false)
        rootView.bt_select_en.setOnClickListener { LanguageUtil.updateLocale(activity, LanguageUtil.LOCALE_ENGLISH);
            act.ChangePage(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",true)}
        rootView.bt_select_cn.setOnClickListener {  LanguageUtil.updateLocale(activity, LanguageUtil.LOCALE_CHINESE);
            act.ChangePage(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",true)}
        rootView.bt_select_tr.setOnClickListener { LanguageUtil.updateLocale(activity, LanguageUtil.LOCALE_TAIWAIN);
            act.ChangePage(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",true)}
        rootView.bt_select_ita.setOnClickListener { LanguageUtil.updateLocale(activity, LanguageUtil.LOCALE_ITALIANO);
            act.ChangePage(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",true)}
        rootView.bt_select_deu.setOnClickListener {  LanguageUtil.updateLocale(activity, LanguageUtil.LOCALE_DE);
        act.ChangePage(PrivaryPolicy(),R.id.frage,"PrivaryPolicy",true)}
        return rootView
    }

    override fun onResume() {
        super.onResume()
    }
}
