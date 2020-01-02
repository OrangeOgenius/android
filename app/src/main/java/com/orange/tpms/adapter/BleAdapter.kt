package com.orange.tpms.adapter

import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.JzAdapter
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.R
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.selectble.view.*

class BleAdapter(var device:ArrayList<BluetoothDevice>):JzAdapter(R.layout.selectble){
    override fun sizeInit(): Int {
       return device.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       if(device[position].name==null){
           holder.mView.textView.text="UNKNOWN"
       }else{holder.mView.textView.text=device[position].name}
        holder.mView.textView2.text=device[position].address
        holder.mView.setOnClickListener {
            (  JzActivity.getControlInstance().getRootActivity() as KtActivity).BleManager.BleHelper.disconnect()
            (  JzActivity.getControlInstance().getRootActivity() as KtActivity).BleManager.BleHelper.connect(device[position].address,10)
            JzActivity.getControlInstance().closeDiaLog()
            JzActivity.getControlInstance().showDiaLog(R.layout.data_loading,false,false,object :SetupDialog{
                override fun dismess() {

                }

                override fun keyevent(event: KeyEvent): Boolean {
                 return false
                }

                override fun setup(rootview: Dialog) {
                  val tit=rootview.findViewById<TextView>(R.id.pass)
                    tit.visibility= View.VISIBLE
                    tit.text=JzActivity.getControlInstance().getRootActivity().resources.getString(R.string.app_connecting)
                }
            })
        }
    }

}