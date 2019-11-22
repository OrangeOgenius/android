package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.lib.hardware.HardwareApp
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.widget.LoadingWidget
import kotlinx.android.synthetic.main.fragment_frag__scan.view.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Scan : RootFragement() {
    private var dataReceiver: HardwareApp.DataReceiver? = null
    internal var lwLoading: LoadingWidget? = null//Loading
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.fragment_frag__scan, container, false)
        lwLoading=rootview.findViewById(R.id.ldw_loading)

        when(PublicBean.ScanType){
            PublicBean.掃描Mmy->{
                rootview.iv_scan_tips.setImageResource(R.mipmap.iv_scan_mmy)
                rootview.tv_info_tips.setText(R.string.app_scan_tips)
            }
            PublicBean.檢查傳感器->{
                rootview.iv_scan_tips.setImageResource(R.mipmap.iv_scan_sensorid)
                rootview.tv_info_tips.setText(R.string.app_scan_and_packing)
            }
        }
        init()
        return rootview
    }

    override fun onKeyScan() {
        if(run){return}
        run=true
        HardwareApp.getInstance().scan()
        lwLoading!!.hide()
        lwLoading!!.show(resources.getString(R.string.app_scaning))
        Thread{
            Thread.sleep(5000)
            handler.post { lwLoading!!.hide() }
            run=false
        }.start()
    }
fun init(){
    HardwareApp.getInstance().switchScan(true)
    dataReceiver = object : HardwareApp.DataReceiver {
        override fun scanReceive() {

        }
        override fun scanMsgReceive(content: String) {
            lwLoading!!.hide()
                Log.v("yhd-", "backToLastFrag:$content")
                GoOk(content)
        }

        override fun uart2MsgReceive(content: String) {

        }
    }
    HardwareApp.getInstance().addDataReceiver(dataReceiver)
}
    fun GoOk(content:String){
        if(!TextUtils.isEmpty(content)){
            if(PublicBean.ScanType==PublicBean.掃描Mmy){
                if (!content.contains("**")) {
                    act.Toast(R.string.app_invalid_mmy_qrcode)
                } else {
                    Log.d("Scan","")
                    val dataArray = content.split("\\*\\*".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if(dataArray.size==2){
                        (activity as KtActivity).itemDAO.GoOk(dataArray.get(0),act)
                    }
                }
            }else if(PublicBean.ScanType==PublicBean.掃描Sensor){

            }else{
                act.Toast(R.string.app_content_empty)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            HardwareApp.getInstance().switchScan(false)
            HardwareApp.getInstance().removeDataReceiver(dataReceiver)
        }catch (e:Exception){e.printStackTrace()}
    }
}
