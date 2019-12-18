package com.orange.tpms.Brocast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

class ScreenReceiver : BroadcastReceiver() {
    var handler= Handler()
    var awake=true
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_SCREEN_OFF) {
            Log.e("熄屏廣播", "關閉")
            awake=false
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            Thread {
                while (!awake) {
                    Log.e("關機監聽","${past}")
                    if (getDatePoor(past) > 600 ) {
                        handler.post {
                            Log.e("關機監聽","準備關機")
                            val intent = Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
                            intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
                            //当中false换成true,会弹出是否关机的确认窗体
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                    Thread.sleep(1000)
                }
            }.start()
        } else if (intent.action == Intent.ACTION_SCREEN_ON) {
            Log.e("熄屏廣播", "打開")
            awake=true
        }
    }
    fun getDatePoor(endDate: Date): Double {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
        val now = sdf.parse(sdf.format(Date()))
        val diff = now.time - endDate.time
        val sec = diff / 1000
        return sec.toDouble()
    }
}