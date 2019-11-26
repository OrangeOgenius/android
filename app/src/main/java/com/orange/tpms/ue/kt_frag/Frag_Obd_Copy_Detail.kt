package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.blelibrary.blelibrary.tool.FormatConvert
import com.orange.tpms.R
import com.orange.tpms.adapter.obdadapter
import com.orange.tpms.bean.ObdBeans
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.fragment_frag__obd__copy__detail.view.*
import kotlinx.android.synthetic.main.normal_dialog.*

class Frag_Obd_Copy_Detail : RootFragement() {
    lateinit var adapter:obdadapter
    lateinit var beans: ObdBeans
    lateinit var srec:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__obd__copy__detail, container, false)
        rootview.tv_content.text="${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
        srec=(activity!! as KtActivity).itemDAO.getPart(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        Log.e("srec",srec)
        beans=ObdBeans()
        beans.rowcount=if((activity!! as KtActivity).itemDAO.IsFiveTire(srec)) 6 else 5
        adapter=obdadapter(beans)
        rootview.rv_id_copy_neww.layoutManager=LinearLayoutManager(act)
        rootview.rv_id_copy_neww.adapter=adapter
        Downs19()
        return rootview
    }
    fun Downs19(){
        act.ShowDaiLog(R.layout.normal_dialog, false, true)
        act.mDialog!!.tit.text=act.resources.getString(R.string.Programming)
        handler.post { (activity!! as KtActivity).back.isEnabled=false }
        Thread{
            if(!(activity!! as KtActivity).ObdCommand.HandShake()){(activity!! as KtActivity).ObdCommand.Reboot()}
                (activity!! as KtActivity).ObdCommand.AskVersion()
                if ((activity!! as KtActivity).ObdCommand.AppVersion == FormatConvert.bytesToHex(
                        (activity!! as KtActivity).ObdCommand.donloads19.replace(
                            ".srec",
                            ""
                        ).toByteArray()
                    )
                ) {
                    if((activity!! as KtActivity).ObdCommand.GoApp()){
                        handler.post {
                            SetId()
                            (activity!! as KtActivity).back.isEnabled=true
                        }
                        return@Thread
                    }
                } else {
                    if (!(activity!! as KtActivity).ObdCommand.WriteVersion()||!(activity!! as KtActivity).ObdCommand.GoBootloader()) {
                        handler.post {
                            act.Toast("燒錄失敗")
                            act.ShowDaiLog(R.layout.program_false,false,false);
                            act.mDialog!!.findViewById<TextView>(R.id.ok).setOnClickListener {act.DaiLogDismiss()  }
                            act.mDialog!!.findViewById<TextView>(R.id.yes).setOnClickListener {Downs19()  }
                            (activity!! as KtActivity).back.isEnabled=true }
                        return@Thread
                    }
                }
                Thread.sleep(2000)
                val Pro=(activity!! as KtActivity).ObdCommand.WriteFlash(activity!! ,srec,296,(activity!! as KtActivity))
                handler.post {
                    (activity!! as KtActivity).back.isEnabled=true
                    (activity!! as KtActivity).LoadingSuccessUI()
                    if(Pro){
                        act.Toast("燒錄成功")
                        SetId()
                    }else{
                        act.Toast("燒錄失敗")
                        act.ShowDaiLog(R.layout.program_false,false,false);
                        act.mDialog!!.findViewById<TextView>(R.id.ok).setOnClickListener { act.DaiLogDismiss() }
                        act.mDialog!!.findViewById<TextView>(R.id.yes).setOnClickListener { Downs19() }
                    }
                }
            }.start()}
    fun SetId() {
        act.ShowDaiLog(R.layout.normal_dialog, false, true)
        act.mDialog!!.tit.text=act.resources.getString(R.string.app_data_reading)
        Thread {
            val a = (activity!! as KtActivity).ObdCommand.GetId(if(beans.rowcount==6) "05" else "04");
            handler.post {
                if (a.success) {
                    Log.e("ID",a.LF)
                    Log.e("ID",a.RF)
                    Log.e("ID",a.LR)
                    Log.e("ID",a.RR)
                    Log.e("ID",a.SP)
//                    rootview.program.setOnClickListener {    act.ChangePage(Key_ID(),R.id.frage,"Key_ID",true)}
                }else{
//                    act.ChangePage(MakeFragement(),R.id.frage,"MakeFragement",false)
                    act.Toast("車種選擇錯誤")
//                    act.ShowDaiLog(R.layout.activity_re_program,true,false)
//                    act.bleServiceControl.disconnect()
                }
                act.LoadingSuccessUI()
            }
        }.start()
    }
    }