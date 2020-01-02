package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.RootFragement
import com.orange.tpms.Callback.Sign_In_C
import com.orange.tpms.Callback.Update_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.helper.WifiConnectHelper
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.FileDowload
import kotlinx.android.synthetic.main.activity_sign_in.view.*
import kotlinx.android.synthetic.main.data_loading.*


class Sign_in : RootFragement(R.layout.activity_sign_in), Update_C,Sign_In_C {
    override fun viewInit() {
        SetPro("Firebasetitle","")
        admin=rootview.findViewById(R.id.editText3)
        password=rootview.findViewById(R.id.editText4)
        (rootview.findViewById(R.id.button4) as Button).setOnClickListener {
            if(run){
                return@setOnClickListener
            }
            run=true
            val admin=admin.text.toString()
            val password=password.text.toString()
            JzActivity.getControlInstance().showDiaLog(R.layout.data_loading,false,true, object :SetupDialog {
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                    return false
                }

                override fun setup(rootview: Dialog) {
                    rootview.pass.visibility=View.VISIBLE
                    rootview.pass.text=resources.getString(R.string.Sign_in)
                }

            })
            Thread{
                Fuction.ValidateUser(admin,password,this)
            }.start()
        }
        (rootview.findViewById(R.id.textView26) as TextView).setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Reset_Ps(),R.id.frage,"Frag_Reset_Ps",true)
        }
        (rootview.findViewById(R.id.imageView22) as ImageView).setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Reset_Ps(),R.id.frage,"Frag_Reset_Ps",true)
        }
        rootview.bt_register.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Register(),R.id.frage,"Frag_Register",false)
        }
    }

    override fun wifierror() {
        handler.post {
            JzActivity.getControlInstance().toast(resources.getString(R.string.signfall))
            WifiConnectHelper().switchWifi(act,false)
            JzActivity.getControlInstance().changeFrag(Frag_Wifi(),R.id.frage,"Frag_Wifi",false)
        }
    }

    override fun result(a: Boolean) {
        handler.post { JzActivity.getControlInstance().closeDiaLog() }
        if(a){
            handler.post { JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog,false,false, object :
                SetupDialog {
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                   return false
                }

                override fun setup(rootview: Dialog) {
                }

            }) }
            FileDowload.HaveData(act,this)
        }else{handler.post { JzActivity.getControlInstance().toast(resources.getString(R.string.signfall)) }
            run=false}
    }

    override fun Updateing(progress: Int) {
        handler.post {  try{
            JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog,false,false, object :SetupDialog {
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                   return false
                }

                override fun setup(rootview: Dialog) {
                    rootview.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_updating)+"$progress%"
                }

            })
        }catch (e:Exception){e.printStackTrace()}  }
    }

    override fun Finish(a: Boolean) {
        handler.post {
            JzActivity.getControlInstance().closeDiaLog()
            if(a){
                val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
                profilePreferences.edit().putString("admin",admin.text.toString()).putString("password",password.text.toString()).commit()
                JzActivity.getControlInstance().changeFrag(Frag_home(),R.id.frage,"Frag_home",false)
                PublicBean.admin=admin.text.toString()
                PublicBean.password=password.text.toString()
                Thread{ Fuction.AddIfNotValid("SP:"+PublicBean.OG_SerialNum, "OGenius", activity as KtActivity) }.start()
            }else{
                WifiConnectHelper().switchWifi(act,false)
                JzActivity.getControlInstance().toast(resources.getString(R.string.updatefault))
                JzActivity.getControlInstance().changeFrag(Frag_Wifi(),R.id.frage,"Frag_Wifi",false)
            }
        }
    }

    lateinit var admin:EditText
    lateinit var password:EditText
}
