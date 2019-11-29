package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.Callback.DaiSetUp
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.Callback.Hanshake_C
import com.orange.tpms.Callback.Update_C
import com.orange.tpms.Callback.Version_C
import com.orange.tpms.R
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.OgCommand
import com.orango.electronic.orangetxusb.SettingPagr.Set_Languages
import java.lang.Thread.sleep


/**
 * A simple [Fragment] subclass.
 *
 */
class kt_splash : RootFragement(),Hanshake_C, Update_C, Version_C {
    override fun version(a: String, result: Boolean) {
        if(result){
            SetPro("Version",a)
            Log.e("版本號",a)
            ListenFinish()
        }else{
            OgCommand.HandShake(this)
        }
    }

    override fun Updateing(progress: Int) {
        handler.post {
            try{
                act.ShowDaiLog(R.layout.update_dialog,false,false, DaiSetUp {
                    it.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_updating)+"$progress%"
                })
        }catch (e: Exception){e.printStackTrace()}  }
    }

    override fun Finish(a: Boolean) {
        if(a){
            handler.post {
                act.DaiLogDismiss()
                val intent2 = context!!.getPackageManager().getLaunchIntentForPackage(context!!.getPackageName())
                context!!.startActivity(intent2)
            }
        }else{
            OgCommand.HandShake(this)
            OgCommand.GetHard()
        }

    }

    override fun result(position: Int) {
        when(position){
            1->{
                handler.post { act.ShowDaiLog(R.layout.update_dialog,false,false, DaiSetUp {  })  }
                OgCommand.WriteBootloader(act,132,GetPro("mcu","no").replace(".x2",""),this)}
            -1->{
                OgCommand.HandShake(this)
                }
            2->{
                OgCommand.GetVerion(this)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.frag_splash, container, false)
        super.onCreateView(inflater, container, savedInstanceState)
        Thread{ OgCommand.HandShake(this)}.start()
        return rootview
    }
    fun ListenFinish(){
        Thread{
            sleep(1000)
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
