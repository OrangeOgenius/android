package com.orange.tpms.ue.kt_frag


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import kotlinx.android.synthetic.main.fragment_frag__web_view.view.*
import java.lang.reflect.Method


class Frag_WebView : RootFragement(R.layout.fragment_frag__web_view) {
    @SuppressLint("SetJavaScriptEnabled")
    override fun viewInit() {
        try{
            val a=GetPro("Lan",LOCALE_ENGLISH)
            var uti="https://simple-sensor.com"
            when(a){
                LOCALE_TAIWAIN->{ uti="https://simple-sensor.com"}
                LOCALE_CHINESE->{ uti="https://simple-sensor.com"}
                LOCALE_DE->{ uti="https://orange-rdks.de"}
                LOCALE_ENGLISH->{ uti="https://simple-sensor.com"}
                LOCALE_ITALIANO->{
                    uti="https://orange-like.it"
                }
            }

            rootview.webview.loadUrl(uti)
            rootview.webview.settings.javaScriptEnabled = true;
            rootview.webview.settings.domStorageEnabled = true;
            rootview.webview.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }

            }
        }catch (e:Exception){e.printStackTrace()}
    }



    private fun hookWebView() {
        var factoryClass: Class<*>? = null
        try {
            factoryClass = Class.forName("android.webkit.WebViewFactory")
            var getProviderClassMethod: Method? = null
            var sProviderInstance: Any? = null

            if (Build.VERSION.SDK_INT == 23) {
                getProviderClassMethod = factoryClass!!.getDeclaredMethod("getProviderClass")
                getProviderClassMethod!!.isAccessible = true
                val providerClass = getProviderClassMethod.invoke(factoryClass) as Class<*>
                val delegateClass = Class.forName("android.webkit.WebViewDelegate")
                val constructor = providerClass.getConstructor(delegateClass)
                if (constructor != null) {
                    constructor.isAccessible = true
                    val constructor2 = delegateClass.getDeclaredConstructor()
                    constructor2.isAccessible = true
                    sProviderInstance = constructor.newInstance(constructor2.newInstance())
                }
            } else if (Build.VERSION.SDK_INT == 22) {
                getProviderClassMethod = factoryClass!!.getDeclaredMethod("getFactoryClass")
                getProviderClassMethod!!.isAccessible = true
                val providerClass = getProviderClassMethod.invoke(factoryClass) as Class<*>
                val delegateClass = Class.forName("android.webkit.WebViewDelegate")
                val constructor = providerClass.getConstructor(delegateClass)
                if (constructor != null) {
                    constructor.isAccessible = true
                    val constructor2 = delegateClass.getDeclaredConstructor()
                    constructor2.isAccessible = true
                    sProviderInstance = constructor.newInstance(constructor2.newInstance())
                }
            } else if (Build.VERSION.SDK_INT == 21) {// Android 21无WebView安全限制
                getProviderClassMethod = factoryClass!!.getDeclaredMethod("getFactoryClass")
                getProviderClassMethod!!.isAccessible = true
                val providerClass = getProviderClassMethod.invoke(factoryClass) as Class<*>
                sProviderInstance = providerClass.newInstance()
            }
            if (sProviderInstance != null) {
                val field = factoryClass!!.getDeclaredField("sProviderInstance")
                field.isAccessible = true
                field.set("sProviderInstance", sProviderInstance)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}
