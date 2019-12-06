package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.orange.blelibrary.blelibrary.Callback.DaiSetUp
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.Callback.Register_C
import com.orange.tpms.Callback.Update_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.helper.WifiConnectHelper
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.FileDowload
import kotlinx.android.synthetic.main.fragment_frag__register.view.*
import java.util.*


class Frag_Register : RootFragement(), Register_C, Update_C {
    override fun WifiError() {
        handler.post {
            WifiConnectHelper().switchWifi(act,false)
            act.DaiLogDismiss()
            act.Toast(resources.getString(R.string.nointernet))
            act.ChangePage(Frag_Wifi(),R.id.frage,"Frag_Wifi",false)
        }
    }

    override fun Result(a: Boolean) {
        handler.post { act.DaiLogDismiss() }
        if(a){
            handler.post { act.ShowDaiLog(R.layout.update_dialog,false,false, DaiSetUp {
            }) }
            FileDowload.HaveData(act,this)
        }else{handler.post { act.Toast(resources.getString(R.string.be_register)) }
            run=false}
    }
    override fun Updateing(progress: Int) {
        handler.post {  try{
            act.ShowDaiLog(R.layout.update_dialog,false,false, DaiSetUp {
                it.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_updating)+"$progress%"
            })
        }catch (e: Exception){e.printStackTrace()}  }
    }

    override fun Finish(a: Boolean) {
        handler.post {
            act.DaiLogDismiss()
            if(a){
                val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
                profilePreferences.edit().putString("admin",email.text.toString()).putString("password",password.text.toString()).commit()
                SetPro("Firebasetitle","")
                act.ChangePage(Frag_home(),R.id.frage,"Frag_home",false)
                PublicBean.admin=email.text.toString()
                PublicBean.password=password.text.toString()
                Thread{ Fuction.AddIfNotValid("SP:"+PublicBean.OG_SerialNum, "OGenius", activity as KtActivity) }.start()
            }else{
                WifiConnectHelper().switchWifi(act,false)
                act.Toast(resources.getString(R.string.updatefault))
                act.ChangePage(Frag_Wifi(),R.id.frage,"Frag_Wifi",false)
            }
        }
    }
    lateinit var AreaSpinner: Spinner
    lateinit var Store: Spinner
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var repeatpassword: EditText
    lateinit var serialnumber: EditText
    lateinit var firstname: EditText
    lateinit var lastname: EditText
    lateinit var company: EditText
    lateinit var phone: EditText
    lateinit var streat: EditText
    lateinit var city: EditText
    lateinit var state: EditText
    lateinit var zpcode: EditText
    var Arealist= ArrayList<String>()
    var Arealist2= ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__register, container, false)
        rootview.cancel.setOnClickListener {
            act.ChangePage(Sign_in(),R.id.frage,"Sign_in",false)
        }
        rootview.next.setOnClickListener {
            register()
        }
        email=rootview.findViewById(R.id.email)
        password=rootview.findViewById(R.id.password)
        repeatpassword=rootview.findViewById(R.id.repeatpassword)
        serialnumber=rootview.findViewById(R.id.serialnumber)
        firstname=rootview.findViewById(R.id.firstname)
        lastname=rootview.findViewById(R.id.lastname)
        company=rootview.findViewById(R.id.company)
        phone=rootview.findViewById(R.id.phone)
        streat=rootview.findViewById(R.id.streat)
        city=rootview.findViewById(R.id.city)
        state=rootview.findViewById(R.id.state)
        zpcode=rootview.findViewById(R.id.zpcode)
        Store=rootview.findViewById(R.id.spinner6)
        AreaSpinner=rootview.findViewById(R.id.spinner5)
        Arealist.add("Select")
        Arealist.add("EU")
        Arealist.add("North America")
        Arealist.add("台灣")
        Arealist.add("中國大陸")
        Arealist2.add(getString(R.string.Distributor))
        Arealist2.add(getString(R.string.Retailer))
        val arrayAdapter = ArrayAdapter<String>(act, R.layout.spinner, Arealist)
        val arrayAdapter2 = ArrayAdapter<String>(act, R.layout.spinner, Arealist2)
        AreaSpinner.adapter=arrayAdapter
        Store.adapter=arrayAdapter2
        super.onCreateView(inflater, container, savedInstanceState)
        rootview.linear.setOnClickListener {act.HideKeyBoard()  }
        return rootview
    }
fun register(){
    if(run){
        return
    }
    val email=email.text.toString()
    val password=password.text.toString()
    val repeatpassword=repeatpassword.text.toString()
    val serialnumber=serialnumber.text.toString()
    val firstname=firstname.text.toString()
    val lastname=lastname.text.toString()
    val company=company.text.toString()
    val phone=phone.text.toString()
    val streat=streat.text.toString()
    val city=city.text.toString()
    val state=AreaSpinner.selectedItem.toString()
    val storetype=Store.selectedItem.toString()
    val zpcode=zpcode.text.toString()
    if(!password.equals(repeatpassword)){
        act.Toast(resources.getString(R.string.confirm_password))
        return
    }
    run=true
    act.ShowDaiLog(R.layout.normal_dialog,false,false, DaiSetUp {  })
    Thread{
        if(storetype.equals(getString(R.string.Distributor))){
            Fuction.Register(email,password,serialnumber,"Distributor",company,firstname,lastname,phone,state,city,streat,zpcode,this,"OGenius")
        }else{
            Fuction.Register(email,password,serialnumber,"Retailer",company,firstname,lastname,phone,state,city,streat,zpcode,this,"OGenius")
        }
    }.start()
}

}
