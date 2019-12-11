package com.orange.tpms.FireBase;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("MyFirebaseService","收到");
        Log.i("MyFirebaseService","title "+remoteMessage.getData().get("data_title").toString());
        Log.i("MyFirebaseService","body "+remoteMessage.getData().get("data_content").toString());
        SharedPreferences data=this.getApplicationContext().getSharedPreferences("Setting", Context.MODE_PRIVATE);
        data.edit().putString("Firebasetitle",remoteMessage.getData().get("data_title").toString()).putString("Firebasebody",remoteMessage.getData().get("data_content").toString()).putString("Firebasetag","").commit();
//        if (remoteMessage.getNotification() != null) {
////            Log.i("MyFirebaseService","name "+remoteMessage.getNotification().);
//
//            Log.i("MyFirebaseService","title "+remoteMessage.getNotification().getTitle());
//            Log.i("MyFirebaseService","body "+remoteMessage.getNotification().getBody());
//            Log.i("MyFirebaseService","tag "+remoteMessage.getNotification().getTag());
//            SharedPreferences data=this.getApplicationContext().getSharedPreferences("Setting", Context.MODE_PRIVATE);
//            data.edit().putString("Firebasetitle",remoteMessage.getNotification().getTitle()).putString("Firebasebody",remoteMessage.getNotification().getBody()).putString("Firebasetag",remoteMessage.getNotification().getTag()).commit();
////            if(TalkingActivity.Companion.getShowAdvice()){new NotificationManager().AddAdvice(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),this.getApplication());
////                SharedPreferences profilePreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE);
////                profilePreferences.edit().putBoolean("message", true).commit();
////            }
////            EventBus.getDefault().post(new Updatemessage());
//        }else{
//
//        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("MyFirebaseService","close");
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i("MyFirebaseService","token "+s);
    }

}