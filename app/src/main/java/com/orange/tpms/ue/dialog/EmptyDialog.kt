package com.orange.tpms.ue.dialog

import android.app.Dialog
import android.view.KeyEvent
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog

class EmptyDialog(var id:Int) {
    fun show(){
        JzActivity.getControlInstance().showDiaLog(id,false,true,  object : SetupDialog {
            override fun dismess() {

            }

            override fun keyevent(event: KeyEvent): Boolean {
                return false
            }

            override fun setup(rootview: Dialog) {
            }
        })
    }

}