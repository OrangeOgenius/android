package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.util.Log
import android.view.KeyEvent
import android.view.View
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.VibMediaUtil
import kotlinx.android.synthetic.main.fragment_frag__check__sensor__read.view.*

class Frag_Check_Sensor_Read : RootFragement(R.layout.fragment_frag__check__sensor__read) {
    internal var ObdHex = "00"
    var lf = "00"
    lateinit var vibMediaUtil: VibMediaUtil
    override fun viewInit() {
        vibMediaUtil = VibMediaUtil(activity)
        rootview.bt_tigger.setOnClickListener {
            trigger()
        }
        rootview.bt_menue.setOnClickListener {
            JzActivity.getControlInstance().goMenu()
        }
        ObdHex = (activity as KtActivity).itemDAO.GetHex(
            PublicBean.SelectMake,
            PublicBean.SelectModel,
            PublicBean.SelectYear
        )
        lf =
            (activity as KtActivity).itemDAO.GetLf(PublicBean.SelectMake, PublicBean.SelectModel, PublicBean.SelectYear)
        while (ObdHex.length < 2) {
            ObdHex = "0" + ObdHex
        }
        Log.e("Hex", ObdHex)
        rootview.tv_content.setText("${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}")
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
        rootview.press.visibility = View.GONE
        JzActivity.getControlInstance().showDiaLog(R.layout.data_loading, false, true, object : SetupDialog {
            override fun keyevent(event: KeyEvent): Boolean {
                return false
            }

            override fun setup(rootview: Dialog) {
            }

            override fun dismess() {
            }
        })
        Thread {
            val a = OgCommand.GetId(ObdHex, lf)
            handler.post {
                run = false
                if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Check_Sensor_Read")) {
                    return@post
                }
                JzActivity.getControlInstance().closeDiaLog()
                if (a.success) {
                    vibMediaUtil.playBeep()
                    rootview.tv_id.setText(a.id)
                    rootview.tv_kpa.setText("${a.kpa}")
                    rootview.tv_c.setText("${if (a.有無胎溫) a.c else "NA"}")
                    rootview.tv_bat.setText("${if (a.有無電池) if (a.bat == "1") "ok" else "low" else "NA"} ")
                    rootview.tv_vol.setText("${if (a.有無電壓) a.vol else "NA"}")
                } else {
                    JzActivity.getControlInstance()
                        .showDiaLog(R.layout.data_loading_false, true, false, object : SetupDialog {
                            override fun dismess() {

                            }

                            override fun keyevent(event: KeyEvent): Boolean {
                                JzActivity.getControlInstance().closeDiaLog()
                                return false
                            }

                            override fun setup(rootview: Dialog) {

                            }

                        })
                }
            }
        }.start()
    }


}
