package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.Callback.Reset_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import kotlinx.android.synthetic.main.fragment_frag__reset__ps.view.*

class Frag_Reset_Ps : RootFragement(R.layout.fragment_frag__reset__ps), Reset_C {
    override fun viewInit() {
        rootview.button.setOnClickListener {
            val admin=rootview.editText2.text.toString()
            if(admin.isEmpty()){JzActivity.getControlInstance().toast(R.string.app_content_empty)
                return@setOnClickListener}
            run=true
            JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog,false,false, object : SetupDialog {
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                  return false
                }

                override fun setup(rootview: Dialog) {
                    rootview.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_data_uploading)
                }

            })
            Thread{Fuction.ResetPassword(admin,this)}.start()
        }
    }

    override fun Result(a: Boolean) {
        handler.post {
            JzActivity.getControlInstance().closeDiaLog()
            if(a){
JzActivity.getControlInstance().changeFrag(Frag_GoReset(),R.id.frage,"Frag_GoReset",false)
            }else{
                JzActivity.getControlInstance().toast(R.string.nointernet)
            }
        }

    }
}
