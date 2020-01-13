package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.widget.ArrayAdapter
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.ue.dialog.SensorWay
import kotlinx.android.synthetic.main.fragment_frag__auto__lock.view.*
import java.util.*


class Frag_Auto_Lock : RootFragement(R.layout.fragment_frag__auto__lock) {
    override fun viewInit() {
        Auto_LockList.clear()
        Auto_LockList.add(resources.getString(R.string.array_autolock_1))
        Auto_LockList.add(resources.getString(R.string.array_autolock_3))
        Auto_LockList.add(resources.getString(R.string.array_autolock_5))
        Auto_LockList.add(resources.getString(R.string.array_autolock_ten))
        Auto_LockList.add(resources.getString(R.string.array_autolock_thirty))
        Auto_LockList.add(resources.getString(R.string.array_autolock_never))
        val lanAdapter = ArrayAdapter<String>(activity!!, R.layout.spinner, Auto_LockList)
        rootview.spinner.adapter=lanAdapter
        Log.d("sleep",""+ JzActivity.getControlInstance().getPro("sleep",60))
        when(JzActivity.getControlInstance().getPro("sleep",60)){
            60->{rootview.spinner.setSelection(0)}
            180->{rootview.spinner.setSelection(1)}
            300->{rootview.spinner.setSelection(2)}
            600->{rootview.spinner.setSelection(3)}
            1800->{rootview.spinner.setSelection(4)}
            3600->{rootview.spinner.setSelection(5)}
        }
        rootview.bt1.setOnClickListener {
            rootview.spinner.setSelection(5)
            SetUp()
        }
        rootview.setup.setOnClickListener {
            SetUp()
            JzActivity.getControlInstance().goMenu()
        }

    }

    var Auto_LockList= ArrayList<String>()



fun SetUp(){
    when(rootview.spinner.selectedItemPosition){
        0->{JzActivity.getControlInstance().setPro("sleep",60)}
        1->{JzActivity.getControlInstance().setPro("sleep",180)}
        2->{JzActivity.getControlInstance().setPro("sleep",300)}
        3->{JzActivity.getControlInstance().setPro("sleep",600)}
        4->{JzActivity.getControlInstance().setPro("sleep",1800)}
        5->{JzActivity.getControlInstance().setPro("sleep",3600)}
    }
    setScreenSleepTime(JzActivity.getControlInstance().getPro("sleep",60)*1000,JzActivity.getControlInstance().getRootActivity())
}
    fun setScreenSleepTime(millisecond: Int, context: Context) {
        try {
            Settings.System.putInt(
                context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT,
                millisecond
            )
        } catch (localException: Exception) {
            localException.printStackTrace()
        }
    }
}
