package com.orange.tpms.ue.frag;

import android.util.Log;
import android.view.KeyEvent;

import com.de.rocket.ue.frag.RoFragment;

/**
 * Fragment的基类
 * Created by haide.yin() on 2019/9/30 11:01.
 */
public abstract class Frag_base extends RoFragment {

    /* ************** Key定义 *************** */
    private static int KEY_ONE = 131;
    private static int KEY_TWO = 8;
    private static int KEY_THREE = 9;
    private static int KEY_FOUR = 132;
    private static int KEY_FIVE = 11;
    private static int KEY_SIX = 12;
    private static int KEY_SEVEN = -1;//(还没有)
    private static int KEY_EIGHT = 14;
    private static int KEY_NINE = 15;
    private static int KEY_ZERO = 115;
    private static int KEY_A = 10;
    private static int KEY_B = 13;
    private static int KEY_C = 16;
    private static int KEY_D = 62;
    private static int KEY_E = 7;
    private static int KEY_F = 56;
    private static int KEY_ENT = 66;
    private static int KEYCODE_CAPS_LOCK = 115;
    private static int KEY_SCAN = 280;
    private static int KEY_TRIGGER = 131;
    private static int KEY_HOME = -1;//(还没有)

    /**
     * 将按键事件分发给Fragment
     */
    public void dispatchKeyEvent(KeyEvent event) {
        final int keyCode = event.getKeyCode();
        activity.runOnUiThread(() -> toast( "KeyCode--->"+keyCode));
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            if(keyCode == KEY_SCAN){
                //扫码
                onKeyScan();
            }else if(keyCode == KEY_TRIGGER){
                //烧录读取传感器
                onKeyTrigger();
            }else if(keyCode == KEY_HOME){
                //返回首页,清除栈顶
                toFrag(Frag_home.class,true,true,null,true);
            }
        }
    }

    /**
     * 读传感器
     */
    public void onKeyTrigger(){}

    /**
     * 扫码按键
     */
    public void onKeyScan(){}
}
