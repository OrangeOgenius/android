package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.RootFragement

import com.orange.tpms.R
import com.orange.tpms.helper.ReadSensorHelper
import com.orange.tpms.ue.frag.Frag_home
import com.orange.tpms.utils.Command
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.LoadingWidget
import kotlinx.android.synthetic.main.fragment_frag__check__sensor__read.view.*

class Frag_Check_Sensor_Read : RootFragement() {
    internal var ObdHex = "00"
    lateinit var vibMediaUtil: VibMediaUtil
    lateinit var lwLoading: LoadingWidget
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
        return rootview
    }

    override fun onKeyTrigger() {
        trigger()
    }

    var run = false
    private fun trigger() {
        if (run) {
            return
        }
        run = true
        vibMediaUtil.playVibrate()
        lwLoading.show(resources.getString(R.string.app_data_reading))
        Thread {
            val a = Command.GetId(ObdHex, "00")
            handler.post {
                run = false
                lwLoading.hide()
                if (a.success) {
                    vibMediaUtil.playBeep()
                    rootview.tv_id.setText(a.id)
                    rootview.tv_kpa.setText("${a.kpa}")
                    rootview.tv_c.setText("${a.c}")
                    rootview.tv_bat.setText("${a.bat}")
                    rootview.tv_vol.setText("${a.vol}")
                } else {
                    lwLoading.show(
                        R.mipmap.img_update_failed,
                        resources.getString(R.string.app_read_failed),
                        false
                    )
                }
            }
        }.start()
    }


}