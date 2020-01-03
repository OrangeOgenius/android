package com.orange.tpms.ue.kt_frag


import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import kotlinx.android.synthetic.main.fragment_frag__check__sensor__information.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Check_Sensor_Information : RootFragement(R.layout.fragment_frag__check__sensor__information) {
    override fun viewInit() {
        rootview.b1.isSelected = true
        rootview.b1.setOnClickListener {
            JzActivity.getControlInstance()
                .changeFrag(Frag_Check_Sensor_Read(), R.id.frage, "Frag_Check_Sensor_Read", true)
        }
        rootview.b2.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Check_Location(), R.id.frage, "Frag_Check_Location", true)
        }
    }

    var focus = 0

    override fun onDown() {
        ChangeFocus(1)
    }

    override fun onTop() {
        ChangeFocus(-1)
    }

    fun ChangeFocus(a: Int) {
        if (focus + a >= 0 && focus + a < 2) {
            focus += a;
        }
        rootview.b1.isSelected = false
        rootview.b2.isSelected = false
        if (focus == 0) {
            rootview.b1.isSelected = true
        } else {
            rootview.b2.isSelected = true
        }
    }

    override fun enter() {
        if (focus == 0) {
            rootview.b1.performClick()
        } else {
            rootview.b2.performClick()
        }
    }
}
