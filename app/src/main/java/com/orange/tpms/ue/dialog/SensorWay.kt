package com.orange.tpms.ue.dialog

import android.app.Dialog
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.R
import com.orange.tpms.ue.kt_frag.Frag_Idcopy_Detail
import com.orange.tpms.ue.kt_frag.Frag_Idcopy_New
import com.orange.tpms.ue.kt_frag.Frag_Idcopy_original
import com.orange.tpms.ue.kt_frag.Frag_Program_Detail

open class SensorWay : SetupDialog {
    override fun dismess() {

    }

    var temppass = ""
    override fun keyevent(event: KeyEvent): Boolean {
        Log.e("Dia", "$event")
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            JzActivity.getControlInstance().closeDiaLog()
        }
        if ((event.keyCode == KeyEvent.KEYCODE_ENTER || event.keyCode == 19 || event.keyCode == 20 || event.keyCode == 21 || event.keyCode == 22) && event.action == KeyEvent.ACTION_UP) {
            if (event.keyCode == 19 || event.keyCode == 20 || event.keyCode == 21 || event.keyCode == 22 || event.keyCode == 66) {
                if (event.keyCode == 19) {
                    temppass = ""
                }
                temppass = temppass + "${event.keyCode}"
                if (temppass == "1920212266") {
                    val intent = Intent(Settings.ACTION_SETTINGS);
                    JzActivity.getControlInstance().getRootActivity().startActivity(intent)
                }
            }
            val dia = JzActivity.getControlInstance().getRootActivity().mDialog
            if (dia!!.findViewById<RelativeLayout>(R.id.scan).background == JzActivity.getControlInstance().getRootActivity().resources.getDrawable(
                    R.color.color_orange
                )
            ) {
                focus = 0
            }
            if (dia.findViewById<RelativeLayout>(R.id.trigger).background == JzActivity.getControlInstance().getRootActivity().resources.getDrawable(
                    R.color.color_orange
                )
            ) {
                focus = 1
            }
            if (dia.findViewById<RelativeLayout>(R.id.keyin).background == JzActivity.getControlInstance().getRootActivity().resources.getDrawable(
                    R.color.color_orange
                )
            ) {
                focus = 2
            }
            when (event.keyCode) {
                19 -> {
                    SensorWayChange(-1)
                }
                20 -> {
                    SensorWayChange(1)
                }
                KeyEvent.KEYCODE_ENTER -> {
                    if (focus == 2) {
                        when (JzActivity.getControlInstance().getNowPageTag()) {
                            "Frag_Program_Detail" -> {
                                (JzActivity.getControlInstance().getNowPage() as Frag_Program_Detail).updateEditable(
                                    true
                                )
                                JzActivity.getControlInstance().closeDiaLog()
                            }
                            "Frag_Idcopy_original" -> {
                                (JzActivity.getControlInstance().getNowPage() as Frag_Idcopy_original).updateEditable(
                                    true
                                )
                                JzActivity.getControlInstance().closeDiaLog()
                            }
                            "Frag_Idcopy_New" -> {
                                (JzActivity.getControlInstance().getNowPage() as Frag_Idcopy_New).updateEditable(true)
                                JzActivity.getControlInstance().closeDiaLog()
                            }
                            "Frag_Idcopy_Detail" -> {
                                (JzActivity.getControlInstance().getNowPage() as Frag_Idcopy_Detail).updateEditable(true)
                                JzActivity.getControlInstance().closeDiaLog()
                            }
                        }
                    } else {
                        JzActivity.getControlInstance().closeDiaLog()
                    }
                }
            }
        }
        return true
    }

    override fun setup(rootview: Dialog) {
//        (act as KtActivity).focus=0
        rootview.findViewById<RelativeLayout>(R.id.scan).setOnTouchListener { v, event ->
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                v.background =
                    JzActivity.getControlInstance().getRootActivity().resources.getDrawable(R.color.color_orange)
            } else {
                v.background = null;
            }
            if (event.action == MotionEvent.ACTION_UP) {
                JzActivity.getControlInstance().closeDiaLog()
            }
            true
        }
        rootview.findViewById<RelativeLayout>(R.id.trigger).setOnTouchListener { v, event ->
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                v.background = JzActivity.getControlInstance().getRootActivity().getDrawable(R.color.color_orange)
            } else {
                v.background = null;
            }
            if (event.action == MotionEvent.ACTION_UP) {
                JzActivity.getControlInstance().closeDiaLog()
            }
            true
        }
        rootview.findViewById<RelativeLayout>(R.id.keyin).setOnTouchListener { v, event ->
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                v.background = JzActivity.getControlInstance().getRootActivity().getDrawable(R.color.color_orange)
            } else {
                v.background = null;
            }
            if (event.action == MotionEvent.ACTION_UP) {
                JzActivity.getControlInstance().closeDiaLog()
            }
            true
        }
    }

    var focus = 0;
    fun SensorWayChange(a: Int) {
        if (focus + a in 0..2) {
            focus += a
            val dia = JzActivity.getControlInstance().getRootActivity().mDialog
            dia!!.findViewById<RelativeLayout>(R.id.scan).background = null;
            dia.findViewById<RelativeLayout>(R.id.trigger).background = null;
            dia.findViewById<RelativeLayout>(R.id.keyin).background = null;
            when (focus) {
                0 -> {
                    dia.findViewById<RelativeLayout>(R.id.scan).background =
                        JzActivity.getControlInstance().getRootActivity().resources.getDrawable(R.color.color_orange)
                }
                1 -> {
                    dia.findViewById<RelativeLayout>(R.id.trigger).background =
                        JzActivity.getControlInstance().getRootActivity().resources.getDrawable(R.color.color_orange)
                }
                2 -> {
                    dia.findViewById<RelativeLayout>(R.id.keyin).background =
                        JzActivity.getControlInstance().getRootActivity().getDrawable(R.color.color_orange)
                }
            }
        }
    }
}