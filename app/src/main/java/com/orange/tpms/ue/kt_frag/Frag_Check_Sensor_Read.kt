package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.Callback.DaiSetUp
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.VibMediaUtil
import kotlinx.android.synthetic.main.fragment_frag__check__sensor__read.view.*
import java.lang.Thread.sleep

class Frag_Check_Sensor_Read : RootFragement() {
    internal var ObdHex = "00"
    var lf="00"
    lateinit var vibMediaUtil: VibMediaUtil
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview = inflater.inflate(R.layout.fragment_frag__check__sensor__read, container, false)
        vibMediaUtil = VibMediaUtil(activity)
        rootview.bt_tigger.setOnClickListener {
            trigger()
        }
        rootview.bt_menue.setOnClickListener {
            act.supportFragmentManager!!.popBackStack(null, 1)
        }
        ObdHex=(activity as KtActivity).itemDAO.GetHex(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        lf=(activity as KtActivity).itemDAO.GetLf(PublicBean.SelectMake,PublicBean.SelectModel,PublicBean.SelectYear)
        while(ObdHex.length<2){ObdHex="0"+ObdHex}
        Log.e("Hex",ObdHex)
        rootview.tv_content.setText("${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}")
        return rootview
    }

    override fun onKeyTrigger() {
        trigger()
    }

    private fun trigger() {
        if (run) {
            return
        }
        run = true
        vibMediaUtil.playVibrate()
        rootview.press.visibility=View.GONE
        act.ShowDaiLog(R.layout.data_loading,false,true, DaiSetUp {  })
        Thread {
            val a = OgCommand.GetId(ObdHex, lf)
            handler.post {
                run = false
                if(!act.NowFrage.equals("Frag_Check_Sensor_Read")){return@post}
                act.DaiLogDismiss()
                if (a.success) {
                    vibMediaUtil.playBeep()
                    rootview.tv_id.setText(a.id)
                    rootview.tv_kpa.setText("${a.kpa}")
                    rootview.tv_c.setText("${a.c}")
                    rootview.tv_bat.setText("${a.bat}")
                    rootview.tv_vol.setText("${a.vol}")
                } else {
                    act.ShowDaiLog(R.layout.data_loading_false,true,false, DaiSetUp {
                        Thread{
                            sleep(3000)
                            act.DaiLogDismiss()
                        }.start()
                    })
                }
            }
        }.start()
    }


}
