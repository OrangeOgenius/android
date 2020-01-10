package com.orange.tpms.ue.kt_frag


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.orange.jzchi.jzframework.JzActivity
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





}
