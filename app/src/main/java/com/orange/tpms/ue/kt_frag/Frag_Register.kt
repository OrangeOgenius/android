package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.content.Context
import android.view.KeyEvent
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.Callback.Register_C
import com.orange.tpms.Callback.Update_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.helper.WifiConnectHelper
import com.orange.tpms.lib.db.share.SettingShare
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.FileDowload
import kotlinx.android.synthetic.main.fragment_frag__register.view.*
import java.util.*


class Frag_Register : RootFragement(R.layout.fragment_frag__register), Register_C, Update_C {
    override fun viewInit() {
        rootview.cancel.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Sign_in(), R.id.frage, "Sign_in", false)
        }
        rootview.next.setOnClickListener {
            register()
        }
        email = rootview.findViewById(R.id.email)
        password = rootview.findViewById(R.id.password)
        repeatpassword = rootview.findViewById(R.id.repeatpassword)
        serialnumber = rootview.findViewById(R.id.serialnumber)
        firstname = rootview.findViewById(R.id.firstname)
        lastname = rootview.findViewById(R.id.lastname)
        company = rootview.findViewById(R.id.company)
        phone = rootview.findViewById(R.id.phone)
        streat = rootview.findViewById(R.id.streat)
        city = rootview.findViewById(R.id.city)
        state = rootview.findViewById(R.id.state)
        zpcode = rootview.findViewById(R.id.zpcode)
        Store = rootview.findViewById(R.id.spinner6)
        AreaSpinner = rootview.findViewById(R.id.spinner5)
        Arealist.add("Select")
        Arealist.add("EU")
        Arealist.add("North America")
        Arealist.add("台灣")
        Arealist.add("中國大陸")
        Arealist2.add(getString(R.string.Distributor))
        Arealist2.add(getString(R.string.Retailer))
        val arrayAdapter = ArrayAdapter<String>(act, R.layout.spinner, Arealist)
        val arrayAdapter2 = ArrayAdapter<String>(act, R.layout.spinner, Arealist2)
        AreaSpinner.adapter = arrayAdapter
        Store.adapter = arrayAdapter2
        rootview.linear.setOnClickListener { act.HideKeyBoard() }
    }

    override fun WifiError() {
        handler.post {
            WifiConnectHelper().switchWifi(act, false)
            JzActivity.getControlInstance().closeDiaLog()
            JzActivity.getControlInstance().toast(resources.getString(R.string.nointernet))
            JzActivity.getControlInstance().changeFrag(Frag_Wifi(), R.id.frage, "Frag_Wifi", false)
        }
    }

    override fun Result(a: Boolean) {
        handler.post { JzActivity.getControlInstance().closeDiaLog() }
        if (a) {
            handler.post {
                JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog, false, false, object :
                    SetupDialog {
                    override fun dismess() {

                    }

                    override fun keyevent(event: KeyEvent): Boolean {
                        return false
                    }

                    override fun setup(rootview: Dialog) {
                    }
                })
            }
            FileDowload.HaveData(act, this)
        } else {
            handler.post { JzActivity.getControlInstance().toast(resources.getString(R.string.be_register)) }
            run = false
        }
    }

    override fun Updateing(progress: Int) {
        handler.post {
            try {
                JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog, false, false, object : SetupDialog {
                    override fun dismess() {

                    }

                    override fun keyevent(event: KeyEvent): Boolean {
                       return false
                    }

                    override fun setup(rootview: Dialog) {
                        rootview.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_updating)+"$progress%"
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun Finish(a: Boolean) {
        handler.post {
            JzActivity.getControlInstance().closeDiaLog()
            if (a) {
                val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
                profilePreferences.edit().putString("admin", email.text.toString())
                    .putString("password", password.text.toString()).commit()
                SetPro("Firebasetitle", "")
                JzActivity.getControlInstance().changeFrag(Frag_home(), R.id.frage, "Frag_home", false)
                PublicBean.admin = email.text.toString()
                PublicBean.password = password.text.toString()
                Thread {
                    Fuction.AddIfNotValid(
                        "SP:" + PublicBean.OG_SerialNum,
                        "OGenius",
                        activity as KtActivity
                    )
                }.start()
            } else {
                WifiConnectHelper().switchWifi(act, false)
                JzActivity.getControlInstance().toast(resources.getString(R.string.updatefault))
                JzActivity.getControlInstance().changeFrag(Frag_Wifi(), R.id.frage, "Frag_Wifi", false)
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
    var Arealist = ArrayList<String>()
    var Arealist2 = ArrayList<String>()
    fun register() {
        if (run) {
            return
        }
        val email = email.text.toString()
        val password = password.text.toString()
        val repeatpassword = repeatpassword.text.toString()
//    val serialnumber=serialnumber.text.toString()
        val firstname = firstname.text.toString()
        val lastname = lastname.text.toString()
        val company = company.text.toString()
        val phone = phone.text.toString()
        val streat = streat.text.toString()
        val city = city.text.toString()
        val state = AreaSpinner.selectedItem.toString()
        val storetype = Store.selectedItem.toString()
        val zpcode = zpcode.text.toString()
        if (!password.equals(repeatpassword)) {
            JzActivity.getControlInstance().toast(resources.getString(R.string.confirm_password))
            return
        }
        run = true
        JzActivity.getControlInstance().showDiaLog(R.layout.normal_dialog, false, false, object : SetupDialog {
            override fun dismess() {

            }

            override fun keyevent(event: KeyEvent): Boolean {
                return false
            }

            override fun setup(rootview: Dialog) {
            }
        })
        Thread {
            if (storetype.equals(getString(R.string.Distributor))) {
                Fuction.Register(
                    email,
                    password,
                    "" + SettingShare.getSystemInformation(act).serialNumber,
                    "Distributor",
                    company,
                    firstname,
                    lastname,
                    phone,
                    state,
                    city,
                    streat,
                    zpcode,
                    this,
                    "OGenius"
                )
            } else {
                Fuction.Register(
                    email,
                    password,
                    "" + SettingShare.getSystemInformation(act).serialNumber,
                    "Retailer",
                    company,
                    firstname,
                    lastname,
                    phone,
                    state,
                    city,
                    streat,
                    zpcode,
                    this,
                    "OGenius"
                )
            }
        }.start()
    }

}
