package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.Callback.Reset_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.R
import kotlinx.android.synthetic.main.fragment_frag__reset__ps.view.*

class Frag_Reset_Ps : RootFragement(), Reset_C {
    override fun Result(a: Boolean) {
        handler.post {
            act.DaiLogDismiss()
            if(a){
act.ChangePage(Frag_GoReset(),R.id.frage,"Frag_GoReset",false)
            }else{
                act.Toast(R.string.nointernet)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       rootview=inflater.inflate(R.layout.fragment_frag__reset__ps, container, false)

        rootview.button.setOnClickListener {
            val admin=rootview.editText2.text.toString()
            if(admin.isEmpty()){act.Toast(R.string.app_content_empty)
            return@setOnClickListener}
            run=true
            act.ShowDaiLog(R.layout.update_dialog,false,false)
            act.mDialog!!.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_data_uploading)
            Thread{Fuction.ResetPassword(admin,this)}.start()
        }
        return rootview
    }


}
