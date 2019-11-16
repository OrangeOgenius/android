package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.Callback.Update_C
import com.orange.tpms.R
import com.orange.tpms.helper.WifiConnectHelper
import com.orange.tpms.utils.FileDowload
import kotlinx.android.synthetic.main.fragment_frag__update.view.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Update : RootFragement(), Update_C {
    override fun Updateing(progress: Int) {
        handler.post {  try{
            if(act.mDialog!!.isShowing){
                act.mDialog!!.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_updating)+"$progress%"
            }else{
                act.ShowDaiLog(R.layout.update_dialog,false,false)
                act.mDialog!!.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_updating)+"$progress%"
            }
        }catch (e: Exception){e.printStackTrace()}  }
    }

    override fun Finish(a: Boolean) {
        run=false
        handler.post {
            act.DaiLogDismiss()
            if(a){
                act.Toast(resources.getString(R.string.update_success))
            }else{
                act.Toast(resources.getString(R.string.updatefault))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__update, container, false)
        rootview.iv_check.isSelected=GetPro("AutoUpdate",true)
        rootview.iv_check.setOnClickListener {
            if(GetPro("AutoUpdate",true)){SetPro("AutoUpdate",false)}else{SetPro("AutoUpdate",true)}
            rootview.iv_check.isSelected=GetPro("AutoUpdate",true)
            }
        rootview.check.setOnClickListener{
            if(run){
                return@setOnClickListener
            }
            run=true
            act.ShowDaiLog(R.layout.update_dialog,false,false)
            Thread{
                FileDowload.ChechUpdate(act,this)
            }.start()
        }
        return rootview
    }


}
