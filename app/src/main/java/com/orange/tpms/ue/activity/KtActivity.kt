package com.orange.tpms.ue.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.RelativeLayout
import com.orange.blelibrary.blelibrary.BleActivity
import com.orange.tpms.R
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.mmySql.ItemDAO
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
}
