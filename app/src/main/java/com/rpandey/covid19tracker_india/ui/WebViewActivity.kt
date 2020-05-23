package com.rpandey.covid19tracker_india.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.rpandey.covid19tracker_india.R
import kotlinx.android.synthetic.main.activity_web_view.*

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        readIntent()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun readIntent() {
        val title = intent.getStringExtra(KEY_TITLE)
        title?.let { setPageTitle(it) }

        val url = intent.getStringExtra(KEY_URL)
        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progress_container.visibility = View.GONE
                if (title == null) {
                    setPageTitle(webview.title)
                }

            }
        }
        webview.webChromeClient = WebChromeClient()
        webview.settings.javaScriptEnabled = true
        webview.loadUrl(url)
    }

    private fun setPageTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
            return
        }

        super.onBackPressed()
    }

    companion object {
        private const val KEY_URL = "KEY_URL"
        private const val KEY_TITLE = "KEY_TITLE"

        fun getIntent(context: Context, url: String, title: String? = null): Intent {
            return Intent(context, WebViewActivity::class.java).apply {
                putExtra(KEY_URL, url)
                putExtra(KEY_TITLE, title)
            }
        }
    }
}