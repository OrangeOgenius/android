package com.orange.tpms.HomeScream

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.orange.blelibrary.blelibrary.BleActivity
import com.orange.testlauncher.Frage.HomeScreem
import com.orange.tpms.HomeScream.Beans.AppBeans
import com.orange.tpms.HomeScream.util.FileUtil
import com.orange.tpms.HomeScream.util.HttpDownloader
import com.orange.tpms.R
import java.io.File

class MainActivity : BleActivity(), FileUtil.FilePrograss {
    override fun fail(msg: String) {

    }

    override fun start(total: Int) {

    }

    override fun finish(total: Int) {
        handler.post {
            val intent =  Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(File("/sdcard/update/update.apk")), "application/vnd.android.package-archive");//image/*
            startActivity(intent);//此处可能会产生异常（比如说你的MIME类型是打开视频，但是你手机里面没装视频播放器，就会报错）
        }
    }

    override fun progress(total: Int, progress: Int) {
Log.e("下載中",""+progress+"/"+total)
    }
var donload= HttpDownloader()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
ChangePage(HomeScreem(),R.id.frage,"HomeScreem",false)
        val file =  File("/sdcard/update/");
        if (!file.exists()) {
       while(!file.mkdirs()){}
        }
//
//        Thread{
//            donload.download("https://bento2.orange-electronic.com/Orange%20Cloud/Drive/OG/APP%20Software/1.078.apk","","update.apk",this)
//        }.start()
        loadApps()
    }
    var appbeans: AppBeans = AppBeans()
    fun loadApps() {
        appbeans.clear()
        val mainIntent =  Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        for(i in packageManager.queryIntentActivities(mainIntent, 0)){
            if(i.activityInfo.packageName.contains("orange")){
                appbeans.app.add(i)
                Log.e("orange",i.activityInfo.packageName)
            }
        }
        Log.e("size",""+appbeans.app.size)
    }
}
//19945118