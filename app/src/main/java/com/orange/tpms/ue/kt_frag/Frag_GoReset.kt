package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import kotlinx.android.synthetic.main.fragment_frag__go_reset.view.*


class Frag_GoReset : RootFragement(R.layout.fragment_frag__go_reset) {
    override fun viewInit() {
        rootview.button2.setOnClickListener {
            JzActivity.getControlInstance().goBack()
        }
    }
}
