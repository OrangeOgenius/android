package com.orange.tpms.ue.kt_frag


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.orange.blelibrary.blelibrary.Callback.DaiSetUp
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.Callback.Update_C
import com.orange.tpms.R
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.FileDowload
import com.orange.tpms.utils.PackageUtils
import kotlinx.android.synthetic.main.fragment_frag__update.view.*
import java.io.File

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Update : RootFragement(), Update_C {
    override fun Updateing(progress: Int) {
        handler.post {  try{
            act.ShowDaiLog(R.layout.update_dialog,false,false, DaiSetUp {
                it.findViewById<TextView>(R.id.tit).text=resources.getString(R.string.app_updating)+"$progress%"
            })
        }catch (e: Exception){e.printStackTrace()}  }
    }
    fun CheckApk(){
        val version= PackageUtils.getVersionCode(act)
        Log.e("Version_APP",GetPro("apk", ""+ PackageUtils.getVersionCode(act)).replace(".apk",""))
        Log.e("Version_APP",""+version)
        if(GetPro("apk", "$version").replace(".apk","")!="$version"){
            handler.post {
                try{
                    val intent =  Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(File("/sdcard/update/update.apk")), "application/vnd.android.package-archive");//image/*
                    startActivity(intent);//此处可能会产生异常（比如说你的MIME类型是打开视频，但是你手机里面没装视频播放器，就会报错）
                }catch (e:Exception){e.printStackTrace()}
            }
        }
    }
    override fun Finish(a: Boolean) {
        run=false
        handler.post {
            if(a){
                    var internetversion= GetPro("mcu","no").replace(".x2","")
                    var localversion=GetPro("Version","no")
                    Log.e("version_internet",internetversion)
                    Log.e("version_local",localversion)
                    if(internetversion!="no"&&internetversion!=localversion){
                        Thread{
                            OgCommand.reboot()
                            handler.post {
                                act.DaiLogDismiss()
                                val intent2 = context!!.getPackageManager().getLaunchIntentForPackage(context!!.getPackageName())
                                context!!.startActivity(intent2)
                            }
                        }.start()
                    }else{
                        CheckApk()
                        act.DaiLogDismiss()
                        act.Toast(resources.getString(R.string.update_success))}
            }else{
                act.DaiLogDismiss()
                act.Toast(resources.getString(R.string.updatefault))
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__update, container, false)
        rootview.iv_check.isSelected=GetPro("AutoUpdate",true)
        rootview.iv_check.setOnClickListener {
            if(GetPro("AutoUpdate",true)){SetPro("AutoUpdate",false)}else{SetPro("AutoUpdate",true)}
            rootview.iv_check.isSelected=GetPro("AutoUpdate",true)
            }
        rootview.check.setOnClickListener{
            if(run){
                return@setOnClickListener
            }
            run=true
            act.ShowDaiLog(R.layout.update_dialog,false,false, DaiSetUp {  })
            Thread{
                FileDowload.ChechUpdate(act,this)
            }.start()
        }
        return rootview
    }


}
