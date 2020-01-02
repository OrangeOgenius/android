package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import kotlinx.android.synthetic.main.fragment_frag__setting.*
import kotlinx.android.synthetic.main.fragment_frag__setting.view.*
import kotlinx.android.synthetic.main.fragment_frag__setting.view.bleconnect

import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.db.share.SettingShare
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.AssetsUtils
import com.orange.tpms.utils.WifiUtils
import java.io.File


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Setting : RootFragement(R.layout.fragment_frag__setting) {
    override fun viewInit() {
        btn.add(rootview.bt_favorite)
        btn.add(rootview.bt_wifi)
        btn.add(rootview.bt_ble)
        btn.add(rootview.bt_unit)
        btn.add(rootview.bt_auto_lock)
        btn.add(rootview.bt_lan)
        btn.add(rootview.bt_sounds)
        btn.add(rootview.bt_information)
        btn.add(rootview.bt_reset)
        btn.add(rootview.bt_update)
        btn.add(rootview.bt_policy)
        btn.add(rootview.bt_enginer)
        Ttn.add(rootview.tv_my_favourite)
        Ttn.add(rootview.tv_wifi)
        Ttn.add(rootview.tv_blue_bud)
        Ttn.add(rootview.tv_unit)
        Ttn.add(rootview.tv_auto_lock)
        Ttn.add(rootview.tv_language)
        Ttn.add(rootview.tv_sounds)
        Ttn.add(rootview.tv_information)
        Ttn.add(rootview.tv_system_reset)
        Ttn.add(rootview.tv_system_update)
        Ttn.add(rootview.tv_privacy_policy)
        Ttn.add(rootview.tv_enginer)

        val connetedWifi = WifiUtils.getInstance(activity).connectedSSID
        rootview.tv_conneted_wifi.text = connetedWifi
        rootview.bt_enginer.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Enginer(), R.id.frage, "Frag_Enginer", true)
        }
        rootview.bt_update.setOnClickListener {
            PublicBean.Update = false
            JzActivity.getControlInstance().changeFrag(Frag_Update(), R.id.frage, "Frag_Update", true)
        }
        rootview.bt_favorite.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_SettingFavorite(), R.id.frage, "Frag_SettingFavorite", true)
        }
        rootview.bt_wifi.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Setting_Wifi(), R.id.frage, "Frag_Setting_Wifi", true)
        }
        rootview.bt_lan.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Setting_Lan(), R.id.frage, "Frag_Setting_Lan", true)
        }
        rootview.bt_unit.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Setting_Unit(), R.id.frage, "Frag_Setting_Unit", true)
        }
        rootview.bt_auto_lock.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Auto_Lock(), R.id.frage, "Frag_Auto_Lock", true)
        }
        rootview.bt_sounds.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Sounds(), R.id.frage, "Frag_Sounds", true)
        }
        rootview.bt_information.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Information(), R.id.frage, "Frag_Information", true)
        }
        rootview.bt_policy.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Policy(), R.id.frage, "Frag_Policy", true)
        }
        rootview.bt_ble.setOnClickListener {
            JzActivity.getControlInstance().showDiaLog(R.layout.bledialog, true, false, object : SetupDialog {
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                    return true
                }

                override fun setup(rootview: Dialog) {
                    rootview.findViewById<TextView>(R.id.no).setOnClickListener {
                        adapter.disable()
                        this@Frag_Setting.rootview.bleconnect.text = resources.getString(R.string.app_blue_bud_close)
                        JzActivity.getControlInstance().closeDiaLog()
                    }
                    rootview.findViewById<TextView>(R.id.yes).setOnClickListener {
                        adapter.enable()
                        this@Frag_Setting.rootview.bleconnect.text = resources.getString(R.string.app_blue_bud_open)
                        JzActivity.getControlInstance().closeDiaLog()
                    }
                }

            })

        }
        rootview.bt_reset.setOnClickListener {
            JzActivity.getControlInstance().showDiaLog(R.layout.reset, true, false, object :SetupDialog {
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                    return true
                }

                override fun setup(rootview: Dialog) {
                    rootview.findViewById<TextView>(R.id.no).setOnClickListener {
                        JzActivity.getControlInstance().closeDiaLog()
                    }
                    rootview.findViewById<TextView>(R.id.yes).setOnClickListener {
                        JzActivity.getControlInstance().closeDiaLog()
                        JzActivity.getControlInstance().showDiaLog(R.layout.data_loading, false, false, object :SetupDialog {
                            override fun dismess() {

                            }

                            override fun keyevent(event: KeyEvent): Boolean {
                               return false
                            }

                            override fun setup(rootview: Dialog) {
                            }
                        })
                        val information = SettingShare.getSystemInformation(act)
                        if (information.version == "101") {
                            JzActivity.getControlInstance().closeDiaLog()
                            act.getSharedPreferences("Setting", Context.MODE_PRIVATE).edit().clear().commit()
                            act.getSharedPreferences("Favorite", Context.MODE_PRIVATE).edit().clear().commit()
                            val intent = Intent(act.applicationContext, KtActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            android.os.Process.killProcess(android.os.Process.myPid())
                        } else {
                            Thread {
                                AssetsUtils.copyFilesFassets(act, "original.apk", "/sdcard/update/reset.apk")
                                handler.post {
                                    act.getSharedPreferences("Setting", Context.MODE_PRIVATE).edit().clear().commit()
                                    act.getSharedPreferences("Favorite", Context.MODE_PRIVATE).edit().clear().commit()
                                    val intent = Intent(Intent.ACTION_VIEW);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setDataAndType(
                                        Uri.fromFile(File("/sdcard/update/reset.apk")),
                                        "application/vnd.android.package-archive"
                                    )//image/*
                                    startActivity(intent);//此处可能会产生异常（比如说你的MIME类型是打开视频，但是你手机里面没装视频播放器，就会报错）
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }.start()
                        }
//            act.finish()
                    }
                }

            })
        }
        BleUpdate()
        act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    val adapter = BluetoothAdapter.getDefaultAdapter()
    var btn = ArrayList<View>()
    var Ttn = ArrayList<TextView>()


    override fun enter() {
        for (i in 0 until Ttn.size) {
            if (Ttn[i].isFocused) {
                btn[i].performClick()
            }
        }
    }

    fun BleUpdate() {
        val originalBluetooth = adapter != null && adapter.isEnabled()
        if (originalBluetooth) {
            rootview.bleconnect.text = resources.getString(R.string.app_blue_bud_open)
        } else {
            rootview.bleconnect.text = resources.getString(R.string.app_blue_bud_close)
        }
    }
}
