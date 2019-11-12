package com.orange.tpms.ue.kt_frag


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.helper.WifiConnectHelper
import com.orange.tpms.ue.frag.Frag_login
import com.orange.tpms.ue.receiver.WifiConnectReceiver
import com.orange.tpms.utils.OggUtils
import com.orange.tpms.widget.LoadingWidget
import kotlinx.android.synthetic.main.frag_wifi.view.*
import org.angmarch.views.NiceSpinner
import java.util.ArrayList


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Wifi : RootFragement() {
    private val Permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
    private val permissionRequestCode = 1001
    lateinit var wifiHelper: WifiConnectHelper
    lateinit var lwLoading: LoadingWidget
    private var isStartConnected: Boolean = false
    lateinit var spWifiName: NiceSpinner
    lateinit var etWifiPassword: EditText //密码
    private var wifiName: String? = null//wifi name
    private var hasList = false//是否已经拿到列表
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview=inflater.inflate(R.layout.frag_wifi, container, false)
        lwLoading=rootview.findViewById(R.id.ldw_loading)
        spWifiName=rootview.findViewById(R.id.sp_wifi_name)
        etWifiPassword=rootview.findViewById(R.id.et_wifi_password)
        spWifiName.setOnSpinnerItemSelectedListener { parent, view, position, id ->
            wifiName = parent.getItemAtPosition(position) as String
        }
        if (!isGPSOpen()) {
            //跳转到手机原生设置页面,打开定位功能
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, permissionRequestCode)
        } else {
            initHelper()
        }
        checkPermissions()
        rootview.bt_cancel.setOnClickListener {
            act.GoBack()
        }
        rootview.bt_select.setOnClickListener {
            if (!TextUtils.isEmpty(wifiName) && !OggUtils.isEmpty(etWifiPassword)) {
                wifiHelper.connectWifi(activity, wifiName, etWifiPassword.getText().toString())
                isStartConnected = true
            } else {
                act.Toast(R.string.app_content_empty)
            }
        }
        return rootview
        
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
            act.Toast("請打開定位")
        }
    }
    private fun initHelper() {
        wifiHelper = WifiConnectHelper()
        //开始连接
        wifiHelper.setOnPreRequestListener {
            lwLoading.show(
                R.mipmap.img_wifi_connection,
                resources.getString(R.string.app_wifi_connecting),
                true
            )
        }
        //连接完成
        wifiHelper.setOnFinishRequestListener { lwLoading.hide() }
        //连接成功
        wifiHelper.setOnConnecteSuccessListener {
            act.Toast(R.string.app_wifi_connected)
            act.ChangePage(Sign_in(), R.id.frage,"Sign_in",true)
        }
        //连接失败
        wifiHelper.setOnConnecteFailedListener {
            if (isStartConnected) {
                act.Toast(R.string.app_connected_failed)
                isStartConnected = false
            }
        }
        //wifi扫描失败
        wifiHelper.setOnScanFailedListener { wifiList ->
            act.Toast(R.string.app_wiFi_scan_failed)
            spWifiName.setVisibility(View.GONE)
        }
        //wifi扫描成功
        wifiHelper.setOnScanSuccessListener { wifiList ->
            if (hasList) {
                return@setOnScanSuccessListener
            }
            spWifiName.setVisibility(View.VISIBLE)
            val ssidList = ArrayList<String>()
            for (scanResult in wifiList) {
                var name = scanResult.SSID
                if (!TextUtils.isEmpty(name)) {
                    if (name.contains("\"")) {
                        name = name.replace("\"", "")
                    }
                }
                ssidList.add(name)
            }
            if (ssidList.size > 0) {
                hasList = true
                wifiName = ssidList[0]
                spWifiName.attachDataSource(ssidList)
            }
        }
        //wifi开关状态改变
        wifiHelper.setOnWifiStateListener { state ->
            if (state == WifiConnectReceiver.WIFI_STATE.WIFI_STATE_ENABLED.toInt()) {
                wifiHelper.startScan(activity)
            }
        }
        //初始化注册广播
        if (WifiConnectHelper.isNetworkConnected(activity)) {
            act.ChangePage(Sign_in(), R.id.frage,"Sign_in",true)
        } else {
            //注册广播
            wifiHelper.initViewFinish(activity)
            //打开wifi
            wifiHelper.switchWifi(activity, true)
        }
    }
}