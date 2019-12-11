package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.utils.KeyboardUtil
import com.orange.tpms.utils.OggUtils
import com.orange.tpms.utils.number_filter
import kotlinx.android.synthetic.main.fragment_frag__program__sensor_information.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Program_Number_Choice : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       rootview=inflater.inflate(R.layout.fragment_frag__program__sensor_information, container, false)
        rootview.tv_mmy_title.text="${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        rootview.et_number.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(2))
        rootview.et_number.addTextChangedListener(number_filter(rootview.et_number,2,context))
        KeyboardUtil.hideEditTextKeyboard(rootview.et_number)
        rootview.bt_start.setOnClickListener {
            OggUtils.hideKeyBoard(activity)
            if(rootview.et_number.text.isEmpty()){
                PublicBean.ProgramNumber = 1
                act.ChangePage(Frag_Program_Detail(),R.id.frage,"Frag_Program_Detail",true)
            }else{
                PublicBean.ProgramNumber = Integer.valueOf(rootview.et_number.text.toString())
                act.ChangePage(Frag_Program_Detail(),R.id.frage,"Frag_Program_Detail",true)
            }
        }

        rootview.et_number.requestFocus()
        return rootview
    }

    override fun onTop() {
        rootview.et_number.requestFocus()
    }

    override fun onDown() {
        rootview.et_number.requestFocus()
    }
    override fun onLeft() {
        rootview.et_number.requestFocus()
        rootview.et_number.setSelection(0)
    }
    override fun onRight() {
        rootview.et_number.requestFocus()
        rootview.et_number.setSelection(rootview.et_number.text.toString().length)
    }
    override fun enter() {
        OggUtils.hideKeyBoard(activity)
        if(rootview.et_number.text.isEmpty()){
            PublicBean.ProgramNumber = 1
            act.ChangePage(Frag_Program_Detail(),R.id.frage,"Frag_Program_Detail",true)
        }else{
            PublicBean.ProgramNumber = Integer.valueOf(rootview.et_number.text.toString())
            act.ChangePage(Frag_Program_Detail(),R.id.frage,"Frag_Program_Detail",true)
        }
    }
}
