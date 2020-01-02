package com.orange.tpms

import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import com.orange.jzchi.jzframework.JzFragement
import com.orange.jzchi.jzframework.tool.LanguageUtil

abstract class RootFragement(var laid:Int) : JzFragement(laid) {


    val LOCALE_ENGLISH="en"
    val LOCALE_CHINESE="zh"
    val LOCALE_TAIWAIN="tw"
    val LOCALE_ITALIANO="it"
    val LOCALE_DE="de"
    val C=0
    val F=1
    val Psi=0
    val Bar=1
    val Kpa=2
    val Auto=0
    val Dec=1
    val hex=2
    var run=false
    /* ************** Key定义 *************** */
    private val KEY_UP = 19
    private val KEY_LEFT = 21
    private val KEY_RIGHT = 22
    private val KEY_DOWN = 20
    private val KEY_TWO = 8
    private val KEY_THREE = 9
    private val KEY_FOUR = 132
    private val KEY_FIVE = 11
    private val KEY_SIX = 12
    private val KEY_SEVEN = -1//(还没有)
    private val KEY_EIGHT = 14
    private val KEY_NINE = 15
    private val KEY_ZERO = 115
    private val KEY_A = 10
    private val KEY_B = 13
    private val KEY_C = 16
    private val KEY_D = 62
    private val KEY_E = 7
    private val KEY_F = 56
    private val KEY_ENT = 66
    private val KEYCODE_CAPS_LOCK = 115
    private val KEY_SCAN = 280
    private val KEY_TRIGGER = 131
    private val KEY_HOME = -1//(还没有)

    open fun onLeft(){}
    open fun onRight(){}
    open fun onTop(){}
    open fun onDown(){}
    /**
     * 将按键事件分发给Fragment
     */
    override fun dispatchKeyEvent(event: KeyEvent) {

        val keyCode = event.keyCode
        Log.e("keycode",""+keyCode)
        if (event.action == KeyEvent.ACTION_UP) {
            if (keyCode == KEY_SCAN) {
                //扫码
                onKeyScan()
            } else if (keyCode == KEY_TRIGGER) {
                //烧录读取传感器
                onKeyTrigger()
            } else if (keyCode == KEY_HOME) {
                //返回首页,清除栈顶
                act.supportFragmentManager.popBackStack(null,1)
            }else if (keyCode == KEY_UP){
                onTop()
            }else if(keyCode == KEY_LEFT){
                onLeft()
            }else if(keyCode == KEY_RIGHT){
                onRight()
            }else if(keyCode == KEY_DOWN){
                onDown()
            }else if(keyCode == KEY_ENT){
                act.HideKeyBoard()
                enter()
            }
        }
    }
    open fun enter(){

    }
    open fun ScanContent(a:String){}
    /**
     * 读传感器
     */
    open fun onKeyTrigger() {}

    /**
     * 扫码按键
     */
    open fun onKeyScan() {}
    /**
     * 返回首頁
     */
    open fun GoMenu(){
        act.supportFragmentManager.popBackStack(null,1)
    }
    open fun SetPro(key:String,value:Boolean){
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        profilePreferences.edit().putBoolean(key,value).commit()
    }
    open fun SetPro(key:String,value:String){
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        profilePreferences.edit().putString(key,value).commit()
    }
    open fun SetPro(key:String,value:Int){
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        profilePreferences.edit().putInt(key,value).commit()
    }
    open fun GetPro(key:String,value:String):String{
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        return profilePreferences.getString(key,value)
    }
    open fun GetPro(key:String,value:Boolean):Boolean{
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        return profilePreferences.getBoolean(key,value)
    }
    open fun GetPro(key:String,value:Int):Int{
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        return profilePreferences.getInt(key,value)
    }
    open fun SetLan(value:String){
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        profilePreferences.edit().putString("Lan",value).commit()
        Laninit()
    }
    open fun Laninit(){
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        when(profilePreferences.getString("Lan",LOCALE_ENGLISH)){
            LOCALE_ENGLISH->{
                LanguageUtil.updateLocale(activity, LanguageUtil.LOCALE_ENGLISH);}
            LOCALE_CHINESE->{LanguageUtil.updateLocale(activity, LanguageUtil.LOCALE_CHINESE);}
            LOCALE_TAIWAIN->{LanguageUtil.updateLocale(activity, LanguageUtil.LOCALE_TAIWAIN);}
            LOCALE_ITALIANO->{LanguageUtil.updateLocale(activity, LanguageUtil.LOCALE_ITALIANO);}
            LOCALE_DE->{LanguageUtil.updateLocale(activity, LanguageUtil.LOCALE_DE);}
        }
    }
    open fun SetPla(value:String){
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        profilePreferences.edit().putString("Area",value).commit()
    }
    open fun SetTem(wh:Int){
        SetPro("Tem",wh);
    }
    open fun SetPr(wh:Int){
        SetPro("Pr",wh);
    }
    open fun SetNs(wh:Int){
        SetPro("Ns",wh);
    }
    open fun GetTem(){
        GetPro("Tem",C)
    }
    open fun GetPr(){
        GetPro("Pr",Kpa)
    }
    open fun GetNs(){
        GetPro("Ns",hex)
    }
    open fun SetSleep(sl:Long){
        val profilePreferences =act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        profilePreferences.edit().putLong("Sleep",sl).commit()
    }

    abstract override fun viewInit()

}
