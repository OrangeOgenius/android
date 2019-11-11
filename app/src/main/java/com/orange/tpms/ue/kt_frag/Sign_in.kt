package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.R
import com.orange.tpms.widget.LoadingWidget


class Sign_in : RootFragement() {
    lateinit var admin:EditText
    lateinit var password:EditText
    var run=false
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
                val a= Fuction.ValidateUser(admin,password)
                run=false
                handler.post {
                    lwLoading.hide()
                    if(a){
                        val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
                        profilePreferences.edit().putString("admin",admin).putString("password",password).commit()
                       act.ChangePage(Frag_home(),R.id.frage,"Home",false)
                    }else{
                       Toast.makeText(act,R.string.signfall, Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
        (rootview.findViewById(R.id.button2) as Button).setOnClickListener {
//            act.ChangePage(Enroll(),R.id.frage,"Enroll",true)
        }
        (rootview.findViewById(R.id.textView26) as TextView).setOnClickListener {
//            act.ChangePage(ResetPass(),R.id.frage,"ResetPass",true)
        }
        (rootview.findViewById(R.id.imageView22) as ImageView).setOnClickListener {
//            act.ChangePage(ResetPass(),R.id.frage,"ResetPass",true)
        }
         super.onCreateView(inflater, container, savedInstanceState)
        return rootview
    }


}
