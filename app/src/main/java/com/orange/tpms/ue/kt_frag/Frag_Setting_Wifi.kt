package com.orange.tpms.ue.kt_frag


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.ScanResult
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.de.rocket.ue.layout.PercentRelativeLayout
import com.orange.blelibrary.blelibrary.Callback.DaiSetUp
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import com.orange.tpms.adapter.WifiAdapter
import com.orange.tpms.helper.WifiConnectHelper
import com.orange.tpms.ue.receiver.WifiConnectReceiver
import com.orange.tpms.widget.EditDialogWidget
import kotlinx.android.synthetic.main.data_loading.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class Frag_Setting_Wifi : RootFragement() {
    private val Permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    private val permissionRequestCode = 1001
    lateinit var prlConnectedWifi: PercentRelativeLayout//ConnectedWifi
    lateinit var tvConnectedWifi: TextView//ConnectedWifi
    lateinit var rvWifi: RecyclerView//Wifi列表
    lateinit var ivWifiCheck: ImageView//Wifi开关
    lateinit var edwPassword: EditDialogWidget//Loading
    lateinit var ivAskCheck: ImageView//Ask开关
    lateinit var wifiAdapter: WifiAdapter//适配器
    lateinit var linearLayoutManager: LinearLayoutManager//列表表格布局
    lateinit var wifiConnectHelper: WifiConnectHelper//Helper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      rootview=inflater.inflate(R.layout.fragment_frag__setting__wifi, container, false)
        prlConnectedWifi=rootview.findViewById(R.id.prl_connected)
        tvConnectedWifi=rootview.findViewById(R.id.tv_connected)
        rvWifi=rootview.findViewById(R.id.rv_wifi)
        ivWifiCheck=rootview.findViewById(R.id.iv_check)
        ivAskCheck=rootview.findViewById(R.id.iv_check_auto)
        edwPassword=rootview.findViewById(R.id.edw_password)
        initView()
        if (!isGPSOpen()) {
            //跳转到手机原生设置页面,打开定位功能
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, permissionRequestCode)
        } else {
            initHelper()
        }
        checkPermissions()
        super.onCreateView(inflater, container, savedInstanceState)
        return rootview
    }

    /**
     * 初始化页面
     */
    private fun initView() {
        //开关wifi
        ivWifiCheck.setOnClickListener { view ->
            ivWifiCheck.setSelected(!ivWifiCheck.isSelected())
            wifiConnectHelper.switchWifi(activity, ivWifiCheck.isSelected())
        }
        //开关Ask
        ivAskCheck.setOnClickListener { view -> ivAskCheck.setSelected(!ivAskCheck.isSelected()) }
        //配置RecyclerView,每行是哪个元素
            linearLayoutManager = LinearLayoutManager(activity)
        rvWifi.setLayoutManager(linearLayoutManager)
        wifiAdapter = WifiAdapter(activity)
        rvWifi.setAdapter(wifiAdapter)
        wifiAdapter.setOnItemClickListener { pos, scanResult ->
            edwPassword.setObject(scanResult)
            if (WifiConnectHelper.hasPassword(activity, scanResult)) {
                edwPassword.show()
            } else {
                wifiConnectHelper.connectWifi(activity, scanResult.SSID)
            }
        }
        //输入密码框
        edwPassword.setDoneListener { content ->
            edwPassword.hide()
            if (!TextUtils.isEmpty(content)) {
                val scanResult = edwPassword.getObject() as ScanResult
                if (scanResult != null) {
                    wifiConnectHelper.connectWifi(activity, scanResult.SSID, content)
                }
            }
        }
    }
    private fun isGPSOpen(): Boolean {
        val isOpen: Boolean
        val locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return isOpen
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

    override fun onPause() {
        super.onPause()
        wifiConnectHelper.onDestroyView(activity)
    }
    private fun onPermissionGranted(permission: String) {
        Log.d("權限",permission)
    }
    /**
     * 初始化Helper
     */
    private fun initHelper() {
        wifiConnectHelper = WifiConnectHelper()
        //开始请求
        wifiConnectHelper.setOnPreRequestListener {
            act.ShowDaiLog(R.layout.data_loading,false,true, DaiSetUp {
                it.pass.visibility=View.VISIBLE
                it.pass.text=resources.getString(R.string.app_wifi_connecting)
            })
        }
        //结束请求
        wifiConnectHelper.setOnFinishRequestListener { act.DaiLogDismiss() }
        //wifi连接成功
        wifiConnectHelper.setOnConnecteFailedListener {

        }
        //wifi连接失败
        wifiConnectHelper.setOnConnecteSuccessListener {
            //读取当前连接的wifi
            wifiConnectHelper.getConnectedSSID(activity)
        }
        //wifi扫描失败
        wifiConnectHelper.setOnScanFailedListener { wifiList ->
            wifiAdapter.setItems(wifiList)
            wifiAdapter.notifyDataSetChanged()
        }
        //wifi扫描成功
        wifiConnectHelper.setOnScanSuccessListener { wifiList ->
            wifiAdapter.setItems(wifiList)
            wifiAdapter.notifyDataSetChanged()
            //读取当前连接的wifi
            wifiConnectHelper.getConnectedSSID(activity)
        }
        //wifi开关状态改变
        wifiConnectHelper.setOnWifiStateListener { state ->
            if (state == WifiConnectReceiver.WIFI_STATE.WIFI_STATE_DISABLED.toInt()) {
                ivWifiCheck.setSelected(false)
                prlConnectedWifi.setVisibility(View.GONE)
                //清空列表
                wifiAdapter.clean()
                wifiAdapter.notifyDataSetChanged()
            } else if (state == WifiConnectReceiver.WIFI_STATE.WIFI_STATE_ENABLED.toInt()) {
                ivWifiCheck.setSelected(true)
                //开始扫描
                wifiConnectHelper.startScan(activity)
            }
        }
        //当前连接的wifi回调
        wifiConnectHelper.setConnectedSSIDListener { ssid ->
            prlConnectedWifi.setVisibility(View.VISIBLE)
            tvConnectedWifi.setText(ssid)
        }
        //初始化注册广播
        wifiConnectHelper.initViewFinish(activity)
    }
}
