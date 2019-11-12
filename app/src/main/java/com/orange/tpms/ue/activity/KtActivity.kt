package com.orange.tpms.ue.activity

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
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
    lateinit var back: ImageView
    lateinit var logout:ImageView
    override fun ChangePageListener(tag:String,frag: Fragment){
        Log.e("switch",tag)
        Log.e("switch","count:"+supportFragmentManager.backStackEntryCount)
if(tag!="Frag_home"){back.visibility=View.VISIBLE}else{back.visibility=View.GONE}
    }
    lateinit var titlebar:RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kt)
        back=findViewById(R.id.back)
        logout=findViewById(R.id.logout)
        titlebar=findViewById(R.id.toolbar)
        back.setOnClickListener {
            GoBack()
        }
        logout.setOnClickListener {

        }
        ChangePage(kt_splash(),R.id.frage,"kt_splash",false)
        ShowTitleBar(false)
        splash()

//Log.e("pd","${Integer.toHexString(11)}")
    }
fun ShowTitleBar(boolean: Boolean){
    titlebar.visibility=if(boolean) View.VISIBLE else View.GONE
}
    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {//只处理按下的动画,抬起的动作忽略
            Log.v("yhd-", "event:$event")
            //按键事件向Fragment分发
            if(Fraging != null){ (Fraging as RootFragement).dispatchKeyEvent(event)}
            //页面在顶层才会分发
        }
        return superDispatchKeyEvent(event)
    }
    fun splash(){
        if (HardwareApp.getInstance().isEnableHareware) {
            HardwareApp.getInstance().initWithCb(this, object : HardwareApp.InitCb {
                override fun onStart() {

                }

                override  fun pingReceive(ret: Int) {

                    if (ret == 1) {


                    } else {

                    }
                }
            })
        } else {

        }
    }
}
