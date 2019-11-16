package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import kotlinx.android.synthetic.main.fragment_frag__go_reset.view.*


class Frag_GoReset : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      rootview=inflater.inflate(R.layout.fragment_frag__go_reset, container, false)
        rootview.button2.setOnClickListener {
            act.GoBack()
        }
        return rootview
    }


}
