package com.orange.blelibrary.blelibrary;

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.orange.blelibrary.R
import com.orange.blelibrary.blelibrary.Callback.DaiSetUp
import com.orange.blelibrary.blelibrary.EventBus.ConnectBle
import com.orange.blelibrary.blelibrary.Server.BleServiceControl
import com.orange.blelibrary.blelibrary.tool.LanguageUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.schedule

open class BleActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener {
    override fun onBackStackChanged() {
        Fraging=supportFragmentManager.fragments[supportFragmentManager.fragments.size - 1]
        NowFrage=Fraging!!.tag!!
        ChangePageListener(NowFrage,Fraging!!)
        nowtime++
    }
    var RXDATA=""
    var Fraging:Fragment ? = null
    lateinit var Translation:Fragment
    var ConnectDelay=10
    var bleServiceControl = BleServiceControl()
    var id=0
    var SleepTime:Long=60*1000
    var NowFrage=""
    var tag=""
    var timer=Timer()
    var lastime=0
    var nowtime=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        timer.schedule(0,SleepTime){
//            try{
//                handler.post {    if(lastime!=nowtime){getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);}else{
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                }
//                    if(nowtime>10){nowtime=0}
//                    lastime=nowtime }
//            }catch (e:java.lang.Exception){e.printStackTrace()}
//        }
    }
    fun init(){
        EventBus.getDefault().register(this)
        supportFragmentManager.addOnBackStackChangedListener(this)
        bleServiceControl.act=this
    }
    public override fun onDestroy() {
        if(!bleServiceControl.first){
            unbindService(bleServiceControl.mServiceConnection)
        }
        timer.cancel()
        super.onDestroy()
    }
    fun GoBack( a:String) {
        try {
            supportFragmentManager!!.popBackStack(a,1)
            Log.d("frag", "${supportFragmentManager.backStackEntryCount}");
        }catch (e:java.lang.Exception){
            Log.e("error",e.message)
        }
    }
    fun GoBack() {
        try {
            supportFragmentManager!!.popBackStack()
            Log.d("frag", "${supportFragmentManager.backStackEntryCount}");
        }catch (e:java.lang.Exception){
            Log.e("error",e.message)
        }
    }

    fun GoBack(a: Int) {
        supportFragmentManager!!.popBackStack(a, 1)
        Log.d("frag", "${supportFragmentManager.backStackEntryCount}");
    }
     var handler= Handler()

    open fun LoadingUI(a:String,pass:Int){

    }
    open fun LoadingSuccessUI(){

    }
    fun HideKeyBoard() {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.getWindow().getDecorView().getWindowToken(), 0)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun Event(a: ConnectBle) {
        LoadingUI(resources.getString(R.string.paired_with_your_device),0)
        bleServiceControl.connect(a.reback)
        Thread{
            var fal=0
            while(true){
                if(bleServiceControl.isconnect||fal==ConnectDelay){break}
                Thread.sleep(1000)
                fal++
            }
            handler.post {LoadingSuccessUI()
                if(bleServiceControl.isconnect){
                    val transaction = supportFragmentManager!!.beginTransaction()
                    transaction.replace(id, Translation,tag)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)//設定動畫
                        .addToBackStack(NowFrage)
                        .commit()
                }
            }
        }.start()
    }
    open fun ConnectSituation(boolean: Boolean){
        if (boolean) {
            Log.d("連線","連線ok")
        } else {
            LoadingSuccessUI()
            Log.d("連線","Bluetooth is disconnected")
            handler.post {
                Toast.makeText(this,"Bluetooth is disconnected",Toast.LENGTH_SHORT).show()
            }
        }
    }
    open fun RX(a:String){
        try {
            Log.w("BLEDATA", "RX:$a")
        } catch (e: Exception) {
            Log.w("error", e.message)
        }
    }
    open fun TX(a:String){
        try {
            Log.w("BLEDATA", "TX:$a")
        } catch (e: Exception) {
            Log.w("error", e.message)
        }
    }
    open fun GoScanner(Translation:Fragment,DelayTime:Int,id:Int,tag:String){
        ConnectDelay=DelayTime
        this.Translation=Translation
        this.id=id
        this.tag=tag;
        if(bleServiceControl.isconnect){
            val transaction = supportFragmentManager!!.beginTransaction()
            transaction.replace(id, Translation,tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)//設定動畫
                .addToBackStack(NowFrage)
                .commit()
        }else{
            startActivity(Intent(this, ScanBle::class.java))
        }
    }
    open fun ChangePage(Translation:Fragment,id:Int,tag:String,goback:Boolean){
        DaiLogDismiss()
        mDialog=null
        if(goback){
            val transaction = supportFragmentManager!!.beginTransaction()
            transaction.replace(id, Translation,tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)//設定動畫
                .addToBackStack(NowFrage)
                .commit()
        }else{
            Fraging=Translation
            NowFrage=tag
            Log.d("switch",tag)
            ChangePageListener(tag,Translation)
            val transaction = supportFragmentManager!!.beginTransaction()
            transaction.replace(id, Translation,tag)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)//設定動畫
                .commit()}
    }
    open fun ChangePageListener(tag:String,frag:Fragment){

    }
    open fun Toast(a:String){
        handler.post { Toast.makeText(this,a,Toast.LENGTH_SHORT).show() }
    }
    open fun Toast(id:Int){
        handler.post { Toast.makeText(this,getString(id),Toast.LENGTH_SHORT).show() }
    }
   public var mDialog: Dialog? = null
    fun ShowDaiLog(Layout:Int,touchcancel:Boolean,style:Int,caller: DaiSetUp){
        try {
            if (mDialog == null) {
                mDialog = Dialog(this, style)
                mDialog!!.setContentView(Layout)
                mDialog!!.getWindow()!!.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
                mDialog!!.setCancelable(true)
                mDialog!!.setCanceledOnTouchOutside(touchcancel)
                mDialog!!.create()
                mDialog!!.show()
                if(touchcancel){getAllChildViews(mDialog!!.getWindow().getDecorView())}
            } else {
                if (!mDialog!!.isShowing()) {
                    Log.e("Dailog","有"+1)
                    mDialog = Dialog(this,style)
                    mDialog!!.setContentView(Layout)
                    mDialog!!.getWindow()!!.setLayout(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT)
                    mDialog!!.setCancelable(true)
                    mDialog!!.setCanceledOnTouchOutside(touchcancel)
                    mDialog!!.create()
                    mDialog!!.show()
                    if(touchcancel){getAllChildViews(mDialog!!.getWindow().getDecorView())}
                }else{
                    if(diaid!=Layout){
                        Log.e("Dailog","有"+2)
                        mDialog = Dialog(this,style)
                        mDialog!!.setContentView(Layout)
                        mDialog!!.getWindow()!!.setLayout(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT)
                        mDialog!!.setCancelable(true)
                        mDialog!!.setCanceledOnTouchOutside(touchcancel)
                        mDialog!!.create()
                        mDialog!!.show()
                        if(touchcancel){getAllChildViews(mDialog!!.getWindow().getDecorView())}
                    }
                }
            }
            diaid=Layout;
            caller.setup(mDialog)
        } catch (e: Exception) {
            Thread.sleep(1000)
            e.printStackTrace()
        }
    }
    var diaid=0;
    fun ShowDaiLog(Layout:Int,touchcancel:Boolean,swip:Boolean,caller: DaiSetUp){
        try {
            if (mDialog == null) {
                Log.e("Dailog","有"+0)
                mDialog = Dialog(this, if(swip) R.style.SwipTheme else R.style.MyDialog)
                mDialog!!.setContentView(Layout)
                mDialog!!.getWindow()!!.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
                mDialog!!.setCancelable(true)
                mDialog!!.setCanceledOnTouchOutside(touchcancel)
                mDialog!!.create()
                mDialog!!.show()
                if(touchcancel){getAllChildViews(mDialog!!.getWindow().getDecorView())}
            } else {
                if (!mDialog!!.isShowing()) {
                    Log.e("Dailog","有"+1)
                    mDialog = Dialog(this,if(swip) R.style.SwipTheme else R.style.MyDialog)
                    mDialog!!.setContentView(Layout)
                    mDialog!!.getWindow()!!.setLayout(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT)
                    mDialog!!.setCancelable(true)
                    mDialog!!.setCanceledOnTouchOutside(touchcancel)
                    mDialog!!.create()
                    mDialog!!.show()
                    if(touchcancel){getAllChildViews(mDialog!!.getWindow().getDecorView())}
                }else{
                    if(diaid!=Layout){
                        Log.e("Dailog","有"+2)
                        mDialog = Dialog(this,if(swip) R.style.SwipTheme else R.style.MyDialog)
                        mDialog!!.setContentView(Layout)
                        mDialog!!.getWindow()!!.setLayout(
                            WindowManager.LayoutParams.WRAP_CONTENT,
                            WindowManager.LayoutParams.WRAP_CONTENT)
                        mDialog!!.setCancelable(true)
                        mDialog!!.setCanceledOnTouchOutside(touchcancel)
                        mDialog!!.create()
                        mDialog!!.show()
                        if(touchcancel){getAllChildViews(mDialog!!.getWindow().getDecorView())}
                    }
                }
            }
            diaid=Layout;
            caller.setup(mDialog)
        } catch (e: Exception) {
            Log.e("錯誤",e.message)
//            Thread.sleep(1000)
            e.printStackTrace()
        }
    }

    private fun getAllChildViews(view: View): List<View> {
        val allchildren = ArrayList<View>()
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val viewchild = view.getChildAt(i)
                allchildren.add(viewchild)
                Log.d("ChildView","$viewchild")
                allchildren.addAll(getAllChildViews(viewchild))
                if("$viewchild".contains("RelativeLayout")){  viewchild.setOnClickListener { mDialog!!.dismiss() }
                    return allchildren}
            }
        }
        return allchildren
    }
    fun DaiLogDismiss() {
        try {
            diaid=-1
            if(mDialog!=null){mDialog!!.dismiss()}
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    var CanGoBack=true
    override fun onBackPressed() {
        supportFragmentManager.popBackStack()
    }
    open fun SetNaVaGation(hide:Boolean){
        var intent=Intent("hbyapi.intent.action_hide_navigationbar")
        intent.putExtra("hide",hide)
        sendBroadcast(intent)
        var intent2=Intent("hbyapi.intent.action_lock_panelbar")
        intent2.putExtra("state",hide)
        sendBroadcast(intent2)
    }
    val LOCALE_ENGLISH="en"
    val LOCALE_CHINESE="zh"
    val LOCALE_TAIWAIN="tw"
    val LOCALE_ITALIANO="it"
    val LOCALE_DE="de"
    open fun Laninit(){
        val profilePreferences =getSharedPreferences("Setting", Context.MODE_PRIVATE)
        when(profilePreferences.getString("Lan",LOCALE_ENGLISH)){
            LOCALE_ENGLISH->{
                LanguageUtil.updateLocale(this, LanguageUtil.LOCALE_ENGLISH);}
            LOCALE_CHINESE->{
                LanguageUtil.updateLocale(this, LanguageUtil.LOCALE_CHINESE);}
            LOCALE_TAIWAIN->{
                LanguageUtil.updateLocale(this, LanguageUtil.LOCALE_TAIWAIN);}
            LOCALE_ITALIANO->{
                LanguageUtil.updateLocale(this, LanguageUtil.LOCALE_ITALIANO);}
            LOCALE_DE->{
                LanguageUtil.updateLocale(this, LanguageUtil.LOCALE_DE);}
        }
    }
    open fun GoMenu(){
        supportFragmentManager.popBackStack(null,1)
    }
}
