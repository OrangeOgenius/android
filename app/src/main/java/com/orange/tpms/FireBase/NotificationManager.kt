package com.orange.FireBase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.orange.tpms.R
import com.orange.tpms.ue.activity.KtActivity

class NotificationManager{
var CHANNEL_ID="Orange"
    val NOTIFICATION_ID_1 = 3
    fun AddAdvice(title:String,content:String,context:Context){
        var  LargeBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon);
        val pi = PendingIntent.getActivity(
            context,
            100,
            Intent(context, KtActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val myBuilder = NotificationCompat.Builder(context,CHANNEL_ID)
        myBuilder.setContentTitle(title)
            .setContentText(content)
            //設定狀態列中的小圖片，尺寸一般建議在24×24，這個圖片同樣也是在下拉狀態列中所顯示
            .setSmallIcon(R.mipmap.icon)
            .setLargeIcon(LargeBitmap)
            //設定預設聲音和震動
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE)
            .setAutoCancel(true)//點選後取消
            .setWhen(System.currentTimeMillis())//設定通知時間
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setContentIntent(pi)
        createNotificationChannel(context).notify(NOTIFICATION_ID_1, myBuilder.build())
    }
    private fun createNotificationChannel(context:Context):NotificationManager {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "USB TPMS"
            val descriptionText = "channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            return notificationManager
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager
    }
}
