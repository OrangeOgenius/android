package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import kotlinx.android.synthetic.main.fragment_frag__web_view.view.*


class Frag_WebView : RootFragement() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
rootview=inflater.inflate(R.layout.fragment_frag__web_view, container, false)
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
        return rootview
    }


}
