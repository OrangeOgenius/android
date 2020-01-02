package com.orange.tpms.ue.kt_frag


import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.RootFragement
import kotlinx.android.synthetic.main.frag_wifi.view.*

import com.orange.tpms.R
import com.orange.tpms.helper.WifiConnectHelper
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.ue.receiver.WifiConnectReceiver
import com.orange.tpms.utils.OggUtils
import com.orange.tpms.utils.WifiUtils
import kotlinx.android.synthetic.main.data_loading.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Wifi : RootFragement(R.layout.frag_wifi) {
    var cango=false
    private val Permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
    private val permissionRequestCode = 1001
    lateinit var wifiHelper: WifiConnectHelper
    private var isStartConnected: Boolean = false
    lateinit var spWifiName: Spinner
    lateinit var etWifiPassword: EditText //密码
    private var wifiName: String? = null//wifi name
    private var hasList = false//是否已经拿到列表
    var WifiList= ArrayList<String>()
    lateinit var arrayAdapter:ArrayAdapter<String>
    override fun viewInit() {
        cango=false
        spWifiName=rootview.findViewById(R.id.sp_wifi_name)
        etWifiPassword=rootview.findViewById(R.id.et_wifi_password)
        arrayAdapter = ArrayAdapter<String>(act, R.layout.spinner, WifiList)
        rootview.sp_wifi_name.adapter=arrayAdapter

        if (!isGPSOpen()) {
            //跳转到手机原生设置页面,打开定位功能
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, permissionRequestCode)
        } else {
            initHelper()
        }
        checkPermissions()
        rootview.bt_cancel.setOnClickListener {
            JzActivity.getControlInstance().goBack()
        }
        rootview.bt_select.setOnClickListener {
            if (!TextUtils.isEmpty(rootview.sp_wifi_name.selectedItem.toString()) && !OggUtils.isEmpty(etWifiPassword)) {
                val connetedWifi = WifiUtils.getInstance(activity).connectedSSID
                if(connetedWifi==rootview.sp_wifi_name.selectedItem.toString()){
                    JzActivity.getControlInstance().changeFrag(Sign_in(), R.id.frage,"Sign_in",false)
                }else{
                    cango=true
                    wifiHelper.connectWifi(activity,rootview.sp_wifi_name.selectedItem.toString() , etWifiPassword.getText().toString())
                    isStartConnected = true
                }
            } else {
                JzActivity.getControlInstance().toast(R.string.app_content_empty)
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionRequestCode ->
                if (grantResults.isNotEmpty()) {
                    for (i in grantResults.indices) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            onPermissionGranted(permissions[i])
                        }
                    }
                }
        }
    }

    private fun checkPermissions() {
        val permissionDeniedList = ArrayList<String>()
        for (permission in Permissions) {
            val permissionCheck = ContextCompat.checkSelfPermission(act, permission)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission)
            } else {
                permissionDeniedList.add(permission)
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            val deniedPermissions = permissionDeniedList.toTypedArray()
            ActivityCompat.requestPermissions(act, deniedPermissions, permissionRequestCode)
        }
    }
    private fun onPermissionGranted(permission: String) {
        Log.d("權限",permission)
    }
    private fun isGPSOpen(): Boolean {
        val isOpen: Boolean
        val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isOpen
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == permissionRequestCode && isGPSOpen()) {
            //打开定位就执行
            initHelper()
        } else {
            //关闭定位功能无法正常运行
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, permissionRequestCode)
            JzActivity.getControlInstance().toast("請打開定位")
        }
    }
    private fun initHelper() {
        wifiHelper = WifiConnectHelper()
        //开始连接
        wifiHelper.setOnPreRequestListener {
            JzActivity.getControlInstance().showDiaLog(R.layout.data_loading,false,true, object : SetupDialog {
                override fun setup(rootview: Dialog) {
                    rootview.pass.visibility=View.VISIBLE
                    rootview.pass.text=resources.getString(R.string.app_wifi_connecting)
                }

                override fun keyevent(event: KeyEvent): Boolean {
                    return false
                }

                override fun dismess() {

                }

            })
        }
        //连接完成
        wifiHelper.setOnFinishRequestListener { JzActivity.getControlInstance().closeDiaLog() }
        //连接成功
        wifiHelper.setOnConnecteSuccessListener {
            JzActivity.getControlInstance().toast(R.string.app_wifi_connected)
            if(cango&&JzActivity.getControlInstance().getNowPageTag()!="Sign_in"){JzActivity.getControlInstance().changeFrag(Sign_in(), R.id.frage,"Sign_in",false)}
        }
        //连接失败
        wifiHelper.setOnConnecteFailedListener {
            JzActivity.getControlInstance().closeDiaLog()
            if (isStartConnected) {
                JzActivity.getControlInstance().toast(R.string.app_connected_failed)
                isStartConnected = false
            }
        }
        //wifi扫描失败
        wifiHelper.setOnScanFailedListener { wifiList ->
//            Log.d("wifi",""+wifiList)
//            JzActivity.getControlInstance().toast(R.string.app_wiFi_scan_failed)
//            spWifiName.setVisibility(View.GONE)
            spWifiName.setVisibility(View.VISIBLE)
            for (scanResult in wifiList) {
                var name = scanResult.SSID
                if (!TextUtils.isEmpty(name)) {
                    if (name.contains("\"")) {
                        name = name.replace("\"", "")
                    }
                }
                if(!WifiList.contains(name)){WifiList.add(name)
                    arrayAdapter.notifyDataSetChanged()
                }
            }
        }
        //wifi扫描成功
        wifiHelper.setOnScanSuccessListener { wifiList ->
            spWifiName.setVisibility(View.VISIBLE)
            for (scanResult in wifiList) {
                var name = scanResult.SSID
                if (!TextUtils.isEmpty(name)) {
                    if (name.contains("\"")) {
                        name = name.replace("\"", "")
                    }
                }
                if(!WifiList.contains(name)){WifiList.add(name)
                    arrayAdapter.notifyDataSetChanged()
                }
            }
        }
        //wifi开关状态改变
        wifiHelper.setOnWifiStateListener { state ->
            if (state == WifiConnectReceiver.WIFI_STATE.WIFI_STATE_ENABLED.toInt()) {
                wifiHelper.startScan(activity)
            }
        }
        //初始化注册广播
        wifiHelper.initViewFinish(activity)
        wifiHelper.switchWifi(activity, true)
//        if (WifiConnectHelper.isNetworkConnected(activity)) {
//            JzActivity.getControlInstance().changeFrag(Sign_in(), R.id.frage,"Sign_in",false)
//        } else {
//            //注册广播
//            wifiHelper.initViewFinish(activity)
//            //打开wifi
//            wifiHelper.switchWifi(activity, true)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiHelper.onDestroyView(activity)
    }
}
