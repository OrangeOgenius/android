package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.Callback.Sign_In_C
import com.orange.tpms.Callback.Update_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.helper.WifiConnectHelper
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.FileDowload
import com.orange.tpms.widget.LoadingWidget
import kotlinx.android.synthetic.main.activity_sign_in.view.*
import java.lang.Exception


class Sign_in : RootFragement(), Update_C,Sign_In_C {
    override fun wifierror() {
        handler.post {
            act.Toast(resources.getString(R.string.signfall))
            WifiConnectHelper().switchWifi(act,false)
            act.ChangePage(Frag_Wifi(),R.id.frage,"Frag_Wifi",false)
        }
    }

    override fun result(a: Boolean) {
        handler.post { lwLoading.hide() }
        if(a){
            handler.post { act.ShowDaiLog(R.layout.update_dialog,false,false) }
            FileDowload.HaveData(act,this)
        }else{handler.post { act.Toast(resources.getString(R.string.signfall)) }
            run=false}
    }

    override fun Updateing(progress: Int) {
        handler.post {  try{
            if(act.mDialog!!.isShowing){
                act.mDialog!!.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_updating)+"$progress%"
            }else{
                act.ShowDaiLog(R.layout.update_dialog,false,false)
                act.mDialog!!.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_updating)+"$progress%"
            }
        }catch (e:Exception){e.printStackTrace()}  }
    }

    override fun Finish(a: Boolean) {
        handler.post {
            act.DaiLogDismiss()
            if(a){
                val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
                profilePreferences.edit().putString("admin",admin.text.toString()).putString("password",password.text.toString()).commit()
                act.ChangePage(Frag_home(),R.id.frage,"Frag_home",false)
                PublicBean.admin=admin.text.toString()
                PublicBean.password=password.text.toString()
                Thread{ Fuction.AddIfNotValid("SP:"+PublicBean.OG_SerialNum, "OGenius", activity as KtActivity) }.start()
            }else{
                WifiConnectHelper().switchWifi(act,false)
                act.Toast(resources.getString(R.string.updatefault))
                act.ChangePage(Frag_Wifi(),R.id.frage,"Frag_Wifi",false)
            }
        }
    }

    lateinit var admin:EditText
    lateinit var password:EditText
    lateinit var lwLoading: LoadingWidget
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootview=inflater.inflate(R.layout.activity_sign_in, container, false)
        admin=rootview.findViewById(R.id.editText3)
        password=rootview.findViewById(R.id.editText4)
        lwLoading=rootview.findViewById(R.id.ldw_loading)
        lwLoading.tvLoading.setText(R.string.app_loading)
        (rootview.findViewById(R.id.button4) as Button).setOnClickListener {
            if(run){
                return@setOnClickListener
            }
            run=true
            val admin=admin.text.toString()
            val password=password.text.toString()
            lwLoading.show(getResources().getString(R.string.app_sign_ing))
            Thread{
               Fuction.ValidateUser(admin,password,this)
            }.start()
        }
        (rootview.findViewById(R.id.textView26) as TextView).setOnClickListener {
            act.ChangePage(Frag_Reset_Ps(),R.id.frage,"Frag_Reset_Ps",true)
        }
        (rootview.findViewById(R.id.imageView22) as ImageView).setOnClickListener {
            act.ChangePage(Frag_Reset_Ps(),R.id.frage,"Frag_Reset_Ps",true)
        }
        rootview.bt_register.setOnClickListener {
            act.ChangePage(Frag_Register(),R.id.frage,"Frag_Register",false)
        }
         super.onCreateView(inflater, container, savedInstanceState)
        return rootview
    }
}
