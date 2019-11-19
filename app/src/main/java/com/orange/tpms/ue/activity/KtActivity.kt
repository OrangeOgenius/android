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
import android.widget.TextView
import com.de.rocket.Rocket
import com.orange.blelibrary.blelibrary.BleActivity
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.Callback.Scan_C
import com.orange.tpms.HttpCommand.Fuction
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.mmySql.ItemDAO
import com.orange.tpms.ue.frag.Frag_base
import com.orange.tpms.ue.kt_frag.kt_splash
import com.orange.tpms.utils.Command
import com.orange.tpms.utils.Command.StringHexToByte
import com.orange.tpms.utils.RxCommand
import kotlinx.android.synthetic.main.fragment_add_favorite.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule

class KtActivity : BleActivity(), Scan_C{
    override fun GetScan(a: String?) {
        if(Fraging != null){ (Fraging as RootFragement).ScanContent(a!!)}
    }
    var BleCommand=com.orange.tpms.utils.BleCommand()
    lateinit var xml:ArrayList<String>
    lateinit var itemDAO: ItemDAO
    lateinit var back: ImageView
    lateinit var logout:ImageView
    var uploadtimer= Timer()
    var Fragnumber=0
    override fun ChangePageListener(tag:String,frag: Fragment){
        Log.e("switch",tag)
        Log.e("switch","count:"+supportFragmentManager.backStackEntryCount)
if(supportFragmentManager.backStackEntryCount!=0){
    back.setImageResource(R.mipmap.back)
    back.visibility=View.VISIBLE
    back.setOnClickListener { GoBack() }
}else{back.visibility=View.GONE}
        Command.NowTag="${Fragnumber++}"
        if(Fragnumber>100){Fragnumber=0}
        when(tag){
            "Frag_home"->{tit.text=resources.getString(R.string.app_o_genius)}
            "Frag_CheckSensor"->{tit.text=resources.getString(R.string.app_home_check_sensor)}
            "Frag_Check_Sensor_Read"->{tit.text=resources.getString(R.string.app_sensor_info_read)}
            "Frag_Check_Location"->{tit.text=resources.getString(R.string.app_home_check_sensor)}
            "Frag_Program_Sensor"->{tit.text=resources.getString(R.string.app_program)}
            "Frag_Id_Copy"->{tit.text=resources.getString(R.string.app_home_id_copy)}
            "Frag_Relearm"->{tit.text=resources.getString(R.string.Relearn_Procedure)}
        }
    }
    lateinit var titlebar:RelativeLayout
    lateinit var tit:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        xml=ArrayList()
        setContentView(R.layout.activity_kt)
        tit=findViewById(R.id.textView12)
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
        BleCommand.act=this
//        ShowDaiLog(R.layout.dataloading,false,false)
//        Thread{Command.ReOpen()}.start()

        uploadtimer.schedule(0,1000*60*5){
            GetXml()
            DataUpload()
        }
    }

    override fun LoadingUI(a: String, pass: Int) {
ShowDaiLog(R.layout.dataloading,false,false)
    }
    override fun LoadingSuccessUI(){
        DaiLogDismiss()
    }
    override fun ConnectSituation(b:Boolean){
        super.ConnectSituation(b)
        if(!b){
            when(PublicBean.position){
                PublicBean.PAD_PROGRAM->{GoMenu()}
                PublicBean.PAD_COPY->{GoMenu()}
            }
        }else{
            BleCommand.act=this
            when(PublicBean.position){
                PublicBean.PAD_PROGRAM->{Thread{BleCommand.Setserial(this)}.start()}
                PublicBean.PAD_COPY->{Thread{BleCommand.Setserial(this)}.start()}
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onResume(){
        super.onResume()
        SetNaVaGation(true)
    }
    override fun RX(a:String){
        Log.w("BLEDATA", "RX:$a")
        RxCommand.RX(StringHexToByte(a), this)
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
    fun GetXml(){
        xml.clear()
        val profilePreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE)
        val a= profilePreferences.getInt("xml_count",0)
        for(i in 0 until a){
            var tmpdata=profilePreferences.getString("xml_$i","nodata")
            if (!tmpdata.equals("nodata")){   xml.add(tmpdata)}
        }
    }
    fun SetXml(){
        val profilePreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE)
        profilePreferences.edit().putInt("xml_count",xml.size).commit()
        for(i in 0 until xml.size){
            profilePreferences.edit().putString("xml_$i",xml[i]).commit()
        }
    }
    fun DataUpload(){
        for(i in 0 until xml.size){
            if(Fuction._req(Fuction.wsdl,xml[i],Fuction.timeout).status!=-1){
                xml.removeAt(i)
            }
        }
        SetXml()
    }
}
