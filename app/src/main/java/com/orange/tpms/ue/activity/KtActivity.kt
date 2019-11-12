package com.orange.tpms.ue.activity

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.RelativeLayout
import com.de.rocket.Rocket
import com.orange.blelibrary.blelibrary.BleActivity
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.mmySql.ItemDAO
import com.orange.tpms.ue.frag.Frag_base
import com.orange.tpms.ue.kt_frag.kt_splash

class KtActivity : BleActivity() {
    lateinit var itemDAO: ItemDAO
    override fun ChangePageListener(tag:String,frag: Fragment){

    }
    lateinit var titlebar:RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kt)
        titlebar=findViewById(R.id.toolbar)
        ChangePage(kt_splash(),R.id.frage,"kt_splash",false)
        ShowTitleBar(false)
    }
fun ShowTitleBar(boolean: Boolean){
    titlebar.visibility=if(boolean) View.VISIBLE else View.GONE
}
    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {//只处理按下的动画,抬起的动作忽略
            Log.v("yhd-", "event:$event")
            //按键事件向Fragment分发
            (Fraging as RootFragement).dispatchKeyEvent(event)
            //页面在顶层才会分发

        }
        return superDispatchKeyEvent(event)
    }
}