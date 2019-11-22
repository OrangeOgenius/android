package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputFilter
import android.text.TextUtils
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
import java.util.regex.Pattern


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
            val result = rootview.et_number.getText().toString()
            val pattern = Pattern.compile("[0-9]*")
            if (!TextUtils.isEmpty(result) && pattern.matcher(result).matches()) {//有可能扫描出来的是一串数字
                PublicBean.ProgramNumber = Integer.valueOf(result)
                act.ChangePage(Frag_Program_Detail(),R.id.frage,"Frag_Program_Detail",true)
            } else {
                act.Toast(R.string.app_wrong_format)
            }
        }
        return rootview
    }


}
