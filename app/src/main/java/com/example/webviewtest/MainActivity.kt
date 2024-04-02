package com.example.webviewtest

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    val JWT_STORAGE = "jwt_storage"
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        val myWebView: WebView = findViewById(R.id.webview)
        myWebView.settings.javaScriptEnabled = true
        myWebView.webViewClient = WebViewClient()
        myWebView.webChromeClient = WebChromeClient()
        myWebView.loadUrl("http://192.168.14.180:8080")
        myWebView.addJavascriptInterface(WebAppInterface(this), "Android")

//        myWebView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
//            val request = DownloadManager.Request(Uri.parse(url))
//            request.setMimeType(mimetype)
//            val cookies = CookieManager.getInstance().getCookie(url)
//            request.addRequestHeader("cookie", cookies)
//            request.addRequestHeader("User-Agent", userAgent)
//            request.setDescription("Downloading file...")
//            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype))
//            request.allowScanningByMediaScanner()
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            request.setDestinationInExternalPublicDir(
//                Environment.DIRECTORY_DOWNLOADS,
//                URLUtil.guessFileName(url, contentDisposition, mimetype)
//            )
//
//            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
//            dm.enqueue(request)
//            Toast.makeText(applicationContext, "Downloading File", Toast.LENGTH_LONG).show()
//        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val myWebView: WebView = findViewById(R.id.webview)
            if(myWebView.canGoBack()){
                myWebView.goBack()
            }
            else {
                finish()
            }
        }
    }

    class WebAppInterface(private val mContext : Context) {
        // 웹에서 안드로이드 toastmessage 띄우기!!?
        @JavascriptInterface
        fun saveJWT(token:String){
            (mContext as MainActivity).writeJwtSharedPreference("jwt",token)
            val toast = Toast.makeText(mContext,"$token SAVE",Toast.LENGTH_SHORT)
            toast.show()
        }

        @JavascriptInterface
        fun loadJWT(){
            val token = (mContext as MainActivity).readJwtSharedPreference("jwt")
            val toast = Toast.makeText(mContext,token,Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    fun writeJwtSharedPreference(key:String,value:String) {
        val sp = getSharedPreferences(JWT_STORAGE,Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun readJwtSharedPreference(key:String):String{
        val sp = getSharedPreferences(JWT_STORAGE,Context.MODE_PRIVATE)
        return sp.getString(key,"") ?: ""
    }

}