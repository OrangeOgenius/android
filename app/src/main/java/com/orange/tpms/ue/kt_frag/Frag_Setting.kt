package com.orange.tpms.ue.kt_frag


import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.orange.blelibrary.blelibrary.Callback.DaiSetUp
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.utils.WifiUtils
import kotlinx.android.synthetic.main.fragment_frag__setting.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Setting : RootFragement() {
    val adapter=BluetoothAdapter.getDefaultAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__setting, container, false)
        val connetedWifi = WifiUtils.getInstance(activity).connectedSSID
        rootview.tv_conneted_wifi.text=connetedWifi
rootview.bt_enginer.setOnClickListener {
    act.ChangePage(Frag_Enginer(),R.id.frage,"Frag_Enginer",true)
}
        rootview.bt_update.setOnClickListener {
            act.ChangePage(Frag_Update(),R.id.frage,"Frag_Update",true)
        }
        rootview.bt_favorite.setOnClickListener{
            act.ChangePage(Frag_SettingFavorite(),R.id.frage,"Frag_SettingFavorite",true)
        }
        rootview.bt_wifi.setOnClickListener {
            act.ChangePage(Frag_Setting_Wifi(),R.id.frage,"Frag_Setting_Wifi",true)
        }
        rootview.bt_lan.setOnClickListener {
            act.ChangePage(Frag_Setting_Lan(),R.id.frage,"Frag_Setting_Lan",true)
        }
        rootview.bt_unit.setOnClickListener {
            act.ChangePage(Frag_Setting_Unit(),R.id.frage,"Frag_Setting_Unit",true)
        }
        rootview.bt_auto_lock.setOnClickListener {
            act.ChangePage(Frag_Auto_Lock(),R.id.frage,"Frag_Auto_Lock",true)
        }
        rootview.bt_sounds.setOnClickListener {
            act.ChangePage(Frag_Sounds(),R.id.frage,"Frag_Sounds",true)
        }
rootview.bt_information.setOnClickListener {
    act.ChangePage(Frag_Information(),R.id.frage,"Frag_Information",true)
}
        rootview.bt_policy.setOnClickListener {
            act.ChangePage(Frag_Policy(),R.id.frage,"Frag_Policy",true)
        }
        rootview.bt_ble.setOnClickListener {
            act.ShowDaiLog(R.layout.bledialog,true,true, DaiSetUp {
                it.findViewById<TextView>(R.id.no).setOnClickListener {
                    adapter.disable()
                    rootview.bleconnect.text=resources.getString(R.string.app_blue_bud_close)
                    act.DaiLogDismiss()
                }
                it.findViewById<TextView>(R.id.yes).setOnClickListener {
                    adapter.enable()
                    rootview.bleconnect.text=resources.getString(R.string.app_blue_bud_open)
                    act.DaiLogDismiss()
                }
            })

        }
rootview.bt_reset.setOnClickListener {

    act.ShowDaiLog(R.layout.reset,true,true, DaiSetUp {
        it.findViewById<TextView>(R.id.no).setOnClickListener {
            act.DaiLogDismiss()
        }
        it.findViewById<TextView>(R.id.yes).setOnClickListener {
            act.DaiLogDismiss()
            act.getSharedPreferences("Setting", Context.MODE_PRIVATE).edit().clear().commit()
            act.getSharedPreferences("Favorite", Context.MODE_PRIVATE).edit().clear().commit()
            act.finish()
            val intent2 = context!!.getPackageManager().getLaunchIntentForPackage(context!!.getPackageName())
            context!!.startActivity(intent2)
        }
    })

}
        BleUpdate()
        act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        return rootview
    }

fun BleUpdate(){
    val originalBluetooth = adapter != null && adapter.isEnabled()
    if(originalBluetooth){
        rootview.bleconnect.text=resources.getString(R.string.app_blue_bud_open)
    }else{
        rootview.bleconnect.text=resources.getString(R.string.app_blue_bud_close)
    }
}
}
