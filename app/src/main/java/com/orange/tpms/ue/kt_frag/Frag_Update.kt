package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.Callback.Update_C
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.FileDowload
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.PackageUtils
import kotlinx.android.synthetic.main.fragment_frag__update.view.*
import java.io.File

/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Update : RootFragement(R.layout.fragment_frag__update), Update_C {
    override fun viewInit() {
        val tmpInfo = act.getPackageManager().getApplicationInfo("com.orange.homescreem", -1)
        val size = File(tmpInfo.sourceDir).length()
        rootview.size.text = "$size"
        rootview.iv_check.isSelected = GetPro("AutoUpdate", true)
        rootview.iv_check.setOnClickListener {
            if (GetPro("AutoUpdate", true)) {
                SetPro("AutoUpdate", false)
            } else {
                SetPro("AutoUpdate", true)
            }
            rootview.iv_check.isSelected = GetPro("AutoUpdate", true)
        }
        rootview.check.setOnClickListener {
            if (run) {
                return@setOnClickListener
            }
            run = true
            JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog, false, false, object : SetupDialog {
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                    return false
                }

                override fun setup(rootview: Dialog) {

                }
            })
            Thread {
                FileDowload.ChechUpdate(act, this)
            }.start()
        }
        if (PublicBean.Update) {
            JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog, false, false, object : SetupDialog {
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                    return false
                }

                override fun setup(rootview: Dialog) {

                }
            })
            Thread {
                FileDowload.ChechUpdate(act, this)
            }.start()
        }
    }

    override fun Updateing(progress: Int) {
        handler.post {
            try {
                JzActivity.getControlInstance().showDiaLog(R.layout.update_dialog, false, false, object : SetupDialog {
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

    fun CheckApk() {
        val version = PackageUtils.getVersionCode(act)
        Log.e("Version_APP", GetPro("apk", "" + PackageUtils.getVersionCode(act)).replace(".apk", ""))
        Log.e("Version_APP", "" + version)
        if (GetPro("apk", "$version").replace(".apk", "") != "$version" || KtActivity.beta) {
            handler.post {
                try {
                    SetPro("Firebasetitle", "nodata")
                    val intent = Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setDataAndType(
                        Uri.fromFile(File("/sdcard/update/update.apk")),
                        "application/vnd.android.package-archive"
                    );//image/*
                    startActivity(intent);//此处可能会产生异常（比如说你的MIME类型是打开视频，但是你手机里面没装视频播放器，就会报错）
                    android.os.Process.killProcess(android.os.Process.myPid());
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            SetPro("Firebasetitle", "nodata")
        }
    }

    override fun Finish(a: Boolean) {
        run = false
        handler.post {
            if (a) {
                var internetversion = GetPro("mcu", "no").replace(".x2", "")
                var localversion = GetPro("Version", "no")
                Log.e("version_internet", internetversion)
                Log.e("version_local", localversion)
                if (internetversion != "no" && internetversion != localversion) {
                    Thread {
                        OgCommand.reboot()
                        handler.post {
                            JzActivity.getControlInstance().closeDiaLog()
                            val intent = Intent(act.applicationContext, KtActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            android.os.Process.killProcess(android.os.Process.myPid())
                        }
                    }.start()
                } else {
                    CheckApk()
                    JzActivity.getControlInstance().closeDiaLog()
                    JzActivity.getControlInstance().toast(resources.getString(R.string.update_success))
                }
            } else {
                JzActivity.getControlInstance().closeDiaLog()
                JzActivity.getControlInstance().toast(resources.getString(R.string.updatefault))
            }
        }
    }
}
