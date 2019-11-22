package com.orange.tpms.Brocast;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent intent)
    {

 Log.v("TAG", "开机自动服务自动启动....."); //启动应用，参数为需要自动启动的应用的包名

Intent intent2 = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
context.startActivity(intent2);

}

}