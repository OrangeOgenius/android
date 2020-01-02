package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.JzFragement
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.Callback.Hanshake_C
import com.orange.tpms.Callback.Update_C
import com.orange.tpms.Callback.Version_C
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.OgCommand
import com.orango.electronic.orangetxusb.SettingPagr.Set_Languages
import java.lang.Thread.sleep


/**
 * A simple [Fragment] subclass.
 *
 */
class kt_splash : JzFragement(R.layout.frag_splash), Hanshake_C, Update_C, Version_C {
    override fun viewInit() {
        Thread { OgCommand.HandShake(this) }.start()
    }

    fun ListenFinish() {
        Thread {
            sleep(1000)
            handler.post {
                JzActivity.getControlInstance().setHome(Frag_Manager(), "Frag_Manager")
            }
        }.start()
    }

    override fun version(a: String, result: Boolean) {
        if (result) {
            JzActivity.getControlInstance().setPro("Version", a)
            Log.e("版本號", a)
            ListenFinish()
        } else {
            OgCommand.HandShake(this)
        }
    }

    override fun Updateing(progress: Int) {
        handler.post {
            try {
                JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog, false, false, object : SetupDialog {
                    var temppass = ""
                    override fun dismess() {

                    }

                    override fun keyevent(event: KeyEvent): Boolean {

                        return false
                    }

                    override fun setup(rootview: Dialog) {
                        rootview.findViewById<TextView>(R.id.tit).text =
                            resources.getString(R.string.app_updating) + "$progress%"
                    }

                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun Finish(a: Boolean) {
        if (a) {
            handler.post {
                JzActivity.getControlInstance().closeDiaLog()
                val intent2 = context!!.getPackageManager().getLaunchIntentForPackage(context!!.getPackageName())
                context!!.startActivity(intent2)
            }
        } else {
            OgCommand.HandShake(this)
            OgCommand.GetHard()
        }
    }

    var fal = 0;
    override fun result(position: Int) {
        when (position) {
            1 -> {
                handler.post {
                    JzActivity.getControlInstance()
                        .showDiaLog(R.layout.update_dialog, false, false, object : SetupDialog {
                            override fun dismess() {

                            }

                            var temppass = ""
                            override fun keyevent(event: KeyEvent): Boolean {
                                if (event.action == KeyEvent.ACTION_UP) {
                                    Log.e("keycode", "" + event.keyCode)
                                    if (event.keyCode == 19 || event.keyCode == 20 || event.keyCode == 21 || event.keyCode == 22 || event.keyCode == 66) {
                                        if (event.keyCode == 19) {
                                            temppass = ""
                                        }
                                        temppass += "${event.keyCode}"
                                        if (temppass == "1920212266") {
                                            val intent = Intent(Settings.ACTION_SETTINGS);
                                            JzActivity.getControlInstance().getRootActivity().startActivity(intent);
                                        }
                                        if (temppass.length > 20) {
                                            temppass = ""
                                        }
                                        Log.e("keycode", "" + temppass)
                                    }
                                }
                                return false
                            }

                            override fun setup(rootview: Dialog) {

                            }
                        })
                }
                var mcu = JzActivity.getControlInstance().getPro("mcu", "no").replace(".x2", "")
                if (fal > 5) {
                    mcu = "no"
                    Log.e("update", "update重臨開始")
                }
                OgCommand.WriteBootloader(act, 132, mcu, this)
                fal++;
            }
            -1 -> {
                OgCommand.HandShake(this)
            }
            2 -> {
                OgCommand.GetVerion(this)
            }
        }
    }
}
