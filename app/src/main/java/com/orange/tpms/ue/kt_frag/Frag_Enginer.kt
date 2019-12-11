package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.testlauncher.Frage.HomeScreem
import com.orange.tpms.R
import kotlinx.android.synthetic.main.fragment_frag__enginer.view.*

class Frag_Enginer : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__enginer, container, false)
        rootview.connect.setOnClickListener {
            if(rootview.pass.text.toString()=="orangetpms"){
//                (activity as KtActivity).SetNaVaGation(false)
//                act.finish()
                act.ChangePage(HomeScreem(),R.id.frage,"HomeScreem",true)
            }else{
                act.Toast(resources.getString(R.string.errorpass))
            }
        }
        super.onCreateView(inflater, container, savedInstanceState)
        return rootview
    }


}
