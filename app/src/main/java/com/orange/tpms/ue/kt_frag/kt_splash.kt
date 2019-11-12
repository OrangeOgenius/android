package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.BleActivity
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.ue.activity.KtActivity
import com.orango.electronic.orangetxusb.SettingPagr.Set_Languages
import java.lang.Thread.sleep


/**
 * A simple [Fragment] subclass.
 *
 */
class kt_splash : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.frag_splash, container, false)
        super.onCreateView(inflater, container, savedInstanceState)
        ListenFinish()
        return rootview
    }
    fun ListenFinish(){
        Thread{
            sleep(2000)
            handler.post {
                (activity as KtActivity).ShowTitleBar(true)
//            act.ChangePage(Set_Languages(), R.id.frage, "Set_Languages", false)
                val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
                if (profilePreferences.getString("admin", "nodata").equals("nodata")) {
                    Set_Languages.place = 0
                    act.ChangePage(Set_Languages(), R.id.frage, "Set_Languages", false)
                }else{
                    act.ChangePage(Frag_home(), R.id.frage, "Frag_home", false)
                }
            }
        }.start()

    }



}
