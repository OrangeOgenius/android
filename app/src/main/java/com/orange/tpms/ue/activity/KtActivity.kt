package com.orange.tpms.ue.activity

import android.annotation.SuppressLint
import android.content.Context
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
import com.orange.tpms.Callback.Scan_C
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.mmySql.ItemDAO
import com.orange.tpms.ue.frag.Frag_base
import com.orange.tpms.ue.kt_frag.kt_splash
import com.orange.tpms.utils.Command
import kotlinx.android.synthetic.main.fragment_add_favorite.view.*
import java.util.ArrayList

class KtActivity : BleActivity(), Scan_C{
    override fun GetScan(a: String?) {
        if(Fraging != null){ (Fraging as RootFragement).ScanContent(a!!)}
    }

    lateinit var itemDAO: ItemDAO
    lateinit var back: ImageView
    lateinit var logout:ImageView
    override fun ChangePageListener(tag:String,frag: Fragment){
        Log.e("switch",tag)
        Log.e("switch","count:"+supportFragmentManager.backStackEntryCount)
if(supportFragmentManager.backStackEntryCount!=0){back.visibility=View.VISIBLE}else{back.visibility=View.GONE}
        Command.NowTag=tag
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
        Laninit()
        ShowTitleBar(false)
        ChangePage(kt_splash(),R.id.frage,"kt_splash",false)
//        Thread{Command.ReOpen()}.start()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onResume(){
        super.onResume()
        SetNaVaGation(true)
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
}
