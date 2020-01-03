package com.orange.tpms

import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jianzhi.jzblehelper.BleHelper
import com.jianzhi.jzblehelper.callback.BleCallBack
import com.jianzhi.jzblehelper.models.BleBinary
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.JzFragement
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.adapter.BleAdapter
import com.orange.tpms.utils.RxCommand

class BleManager(var context: Context) : BleCallBack {
    var RxChannel = "00008D81-0000-1000-8000-00805F9B34FB"
    var TxChannel = "00008D82-0000-1000-8000-00805F9B34FB"
    var BleHelper: BleHelper = BleHelper(context, this)
    lateinit var changepage: JzFragement
    lateinit var changetag: String
    var handler = Handler()
    var devices = ArrayList<BluetoothDevice>()
    var adapter: BleAdapter = BleAdapter(devices)
    override fun needGPS() {


    }

    override fun onConnectFalse() {
        handler.post {
            JzActivity.getControlInstance().closeDiaLog()
            JzActivity.getControlInstance().toast(context.resources.getString(R.string.app_connected_failed))
            JzActivity.getControlInstance().goMenu()
        }
        Log.d("JzBleMessage", "藍牙斷線")
    }

    override fun onConnectSuccess() {
        handler.post {
            JzActivity.getControlInstance().closeDiaLog()
            JzActivity.getControlInstance().changeFrag(changepage, R.id.frage, changetag, true)
        }
        Log.d("JzBleMessage", "藍牙連線")
    }

    override fun onConnecting() {
        Log.d("JzBleMessage", "藍牙正在連線中")
    }

    override fun requestPermission(permission: ArrayList<String>) {

    }

    override fun rx(a: BleBinary) {
        BleHelper.RxData += a.readHEX()
//        RxCommand.RX(BleHelper.RxData, BleHelper)
        Log.d("JzBleMessage", "收到藍牙消息${a.readHEX()}")
    }

    override fun scanBack(device: BluetoothDevice) {
        Log.d("JzBleMessage", "掃描到裝置:名稱${device.name}/地址:${device.address}")
        if (!devices.contains(device) && device.name != null) {
            devices.add(device)
            adapter.notifyDataSetChanged()
        }
    }

    override fun tx(b: BleBinary) {
        Log.d("JzBleMessage", "傳送藍牙消息${b.readHEX()}")
    }

    fun scan(frag: JzFragement, tag: String) {
        changetag = tag
        changepage = frag
        if (BleHelper.isConnect()) {
            JzActivity.getControlInstance().changeFrag(changepage, R.id.frage, changetag, true)
            return
        }
        BleHelper.startScan()
        JzActivity.getControlInstance().showDiaLog(R.layout.activity_scan_ble, false, false, object : SetupDialog {
            override fun dismess() {
                BleHelper.stopScan()
            }

            override fun keyevent(event: KeyEvent): Boolean {
                return true
            }

            override fun setup(rootview: Dialog) {
                rootview.findViewById<RecyclerView>(R.id.re).layoutManager =
                    LinearLayoutManager(JzActivity.getControlInstance().getRootActivity())
                rootview.findViewById<RecyclerView>(R.id.re).adapter = adapter
                rootview.findViewById<Button>(R.id.close).setOnClickListener {
                    BleHelper.stopScan()
                    BleHelper.disconnect()
                    JzActivity.getControlInstance().closeDiaLog()
                }
            }
        })
    }
}