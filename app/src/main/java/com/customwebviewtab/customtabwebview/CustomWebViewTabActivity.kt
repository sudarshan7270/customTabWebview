package com.customwebviewtab.customtabwebview

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.util.*

class CustomWebViewTabActivity : AppCompatActivity() {

    private lateinit var currentWebView: WebView
    private val webViewStack = Stack<WebView>()
    private var isDownloadDialogVisible: Boolean = false
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var currentUrl: String = ""
    private lateinit var progressBar: ProgressBar


    companion object {
        private const val TAG = "CustomWebViewTabActivity"
        private val PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        private const val PERMISSIONS_REQUEST_CODE = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_web_view_tab)
         progressBar = findViewById<ProgressBar>(R.id.progressBar)


        val url = intent.getStringExtra("urlToOpenInWebview") ?: ""
        currentUrl = url

        currentWebView = findViewById(R.id.webView)
        setupWebView(currentWebView)
        loadUrl(url)

        findViewById<View>(R.id.ivHomeBack).setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupWebView(webView: WebView) {
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = true
            allowContentAccess = true
            setSupportMultipleWindows(true)
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            useWideViewPort = true
            loadWithOverviewMode = true
            setGeolocationEnabled(true)
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false
            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = View.VISIBLE // Show progress bar

            }
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE // Hide progress bar
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                val newWebView = WebView(this@CustomWebViewTabActivity)
                setupWebView(newWebView)

                webViewStack.push(currentWebView)
                currentWebView = newWebView

                val container = FrameLayout(this@CustomWebViewTabActivity)
                container.addView(newWebView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)

                (view?.parent as? FrameLayout)?.addView(container)
                (resultMsg?.obj as? WebView.WebViewTransport)?.webView = newWebView
                resultMsg?.sendToTarget()
                return true
            }

            override fun onCloseWindow(window: WebView?) {
                closeCurrentTab()
            }

            override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasPermissions(*PERMISSIONS)) {
                    ActivityCompat.requestPermissions(this@CustomWebViewTabActivity, PERMISSIONS, PERMISSIONS_REQUEST_CODE)
                } else {
                    openFileSelection(filePathCallback)
                }
                return true
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }

        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            if (isDownloadDialogVisible) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        downloadDialog(url, userAgent, contentDisposition, mimetype, contentLength)
                    } else {
                        ActivityCompat.requestPermissions(this@CustomWebViewTabActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
                    }
                } else {
                    downloadDialog(url, userAgent, contentDisposition, mimetype, contentLength)
                }
            } else {
                isDownloadDialogVisible = true
            }
        }
    }

    private fun closeCurrentTab() {
        if (webViewStack.isNotEmpty()) {
            val parentViewGroup = currentWebView.parent as? ViewGroup
            parentViewGroup?.removeView(currentWebView)
            currentWebView.destroy()
            currentWebView = webViewStack.pop()
            findViewById<FrameLayout>(R.id.weviewcontainer).addView(currentWebView)
        } else {
            finish()
        }
    }

    private fun loadUrl(url: String) {
        currentWebView.loadUrl(url)
        currentUrl = url
    }

    private fun openFileSelection(filePathCallback: ValueCallback<Array<Uri>>?) {
        mFilePathCallback = filePathCallback
        // Add logic to open file chooser for selecting files (if needed)
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun downloadDialog(url: String, userAgent: String, contentDisposition: String, mimetype: String, contentLength: Long) {
        // Implement download dialog functionality here (e.g., handle file download)
    }


}
