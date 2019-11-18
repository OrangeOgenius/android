package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.HttpCommand.SensorRecord
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.mmySql.ItemDAO
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.activity_frag_home.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_home : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.activity_frag_home, container, false)
        Laninit()
        SleepInit()
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        PublicBean.admin=profilePreferences.getString("admin","")
        PublicBean.password=profilePreferences.getString("password","")
        rootview.bt_check_sensor.setOnClickListener {
            PublicBean.position=PublicBean.檢查傳感器
            act.ChangePage(Frag_CheckSensor(),R.id.frage,"Frag_CheckSensor",true)
        }
        rootview.bt_program_sensor.setOnClickListener {
            PublicBean.position=PublicBean.燒錄傳感器
            act.ChangePage(Frag_Program_Sensor(),R.id.frage,"Frag_Program_Sensor",true)
        }
        rootview.bt_sensor_idcopy.setOnClickListener {
            PublicBean.position=PublicBean.複製傳感器
            act.ChangePage(Frag_Id_Copy(),R.id.frage,"Frag_Id_Copy",true)
        }
        rootview.bt_setting.setOnClickListener {
            PublicBean.position=PublicBean.設定
            act.ChangePage(Frag_Setting(),R.id.frage,"Frag_Setting",true)
        }
        rootview.btn_relearn.setOnClickListener {
            PublicBean.position=PublicBean.學碼步驟
            act.ChangePage(Frag_Relearm(),R.id.frage,"Frag_Relearm",true)
        }
        rootview.bt_pad_copy.setOnClickListener {
            PublicBean.position=PublicBean.PAD_COPY
            act.ChangePage(Frag_Pad_IdCopy(),R.id.frage,"Frag_Pad_IdCopy",true)
        }
        rootview.bt_pad_program.setOnClickListener {
            PublicBean.position=PublicBean.PAD_PROGRAM
            act.ChangePage(Frag_Pad_IdCopy(),R.id.frage,"Frag_Pad_IdCopy",true)

        }
        (activity as KtActivity).itemDAO = ItemDAO(activity!!);
         val mmyname=GetPro("mmyname","nodata")
         SensorRecord.DB_Version = if (mmyname.length > 19) mmyname.substring(16) else mmyname
        return rootview
    }


}
