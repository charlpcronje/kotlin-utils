package com.gamezboost.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.RelativeLayout
import com.unity3d.player.UnityPlayer
import java.util.*

@Suppress("DEPRECATION")
class Web private constructor(activity: Activity) {
    var url: String? = null
    var dialog: AlertDialog? = null
    var alert: AlertDialog.Builder? = null

    init {
        GB.Instance()!!.activity = activity
        GB.Instance()!!.web = this
    }

    inner class Style(backgroundColor: Int,textColor: Int,closeText: String,width: Int,height: Int) {
        var backgroundColor = 0
        var textColor: Int = Color.BLACK
        var closeText: String = "X"
        var width: Int = 0
        var height: Int = 0

        init {
            this.backgroundColor = backgroundColor
            this.textColor = textColor
            this.closeText = closeText
            this.width = width
            this.height = height
        }
    }

    class CustomWebView(mContext: Context) : WebView(mContext) {
        var mContext = mContext


        override fun onCheckIsTextEditor(): Boolean {
            return true
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    fun View(url: String?, styles: Style) {
        this.url = url
        alert = AlertDialog.Builder(GB.Instance()!!.activity)
        GB.Instance()!!.activity!!.runOnUiThread {
            this.alert!!.setCancelable(false)
            val relativeLayout = RelativeLayout(GB.Instance()!!.activity)
            val relParams = RelativeLayout.LayoutParams(-1, -1)
            relativeLayout.layoutParams = relParams
            relativeLayout.setBackgroundColor(styles.backgroundColor) //-12303292
            val webView = CustomWebView(GB.Instance().activity)
            val wvParams = RelativeLayout.LayoutParams(-1, -1)
            webView.layoutParams = wvParams
            webView.settings.javaScriptEnabled = true
            webView.addJavascriptInterface(GB.Instance()!!.web, "HTMLOUT")
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(
                    view: WebView,
                    progress: Int
                ) {
                    GB.Instance()!!.activity!!.setProgress(progress * 1000)
                }
            }
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    url: String
                ): Boolean {
                    webView.loadUrl(url)
                    try {
                        UnityPlayer.UnitySendMessage(
                            "Code WebView",
                            "CurrentViewUrl",
                            "" + webView.url
                        )
                    } catch (exception: Exception) {
                        val cause = exception.cause
                        GB.Instance()!!.log(
                            GB.DBUG.ERROR,
                            exception.message + ". Cause: " + Objects.requireNonNull(
                                cause
                            ).toString()
                        )
                    }
                    return true
                }

                override fun onPageFinished(
                    view: WebView,
                    url1: String
                ) {
                    webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
                }
            }
            webView.loadUrl(GB.Instance()!!.web?.url)
            relativeLayout.addView(webView)
            alert!!.setView(relativeLayout)
            dialog!!.create()
            Objects.requireNonNull(dialog!!.window)!!.setFlags(16777216, 16777216)
            dialog!!.show()
            val myButton = Button(GB.Instance()!!.activity)
            myButton.text = styles.closeText
            myButton.id = View.generateViewId()
            myButton.background.alpha = 0
            val font = Typeface.createFromAsset(
                GB.Instance()!!.activity!!.getAssets(),
                "fonts/androidnation.ttf"
            )
            myButton.setTypeface(font, Typeface.NORMAL)
            myButton.setTextColor(-1)
            myButton.textSize = 16.0f
            myButton.setOnClickListener { v: View? ->
                try {
                    val url1 = webView.url
                    UnityPlayer.UnitySendMessage("Code WebView", "LastViewUrl", "" + url1)
                } catch (exception: Exception) {
                    val cause = exception.cause
                    GB.Instance()!!.log(
                        GB.DBUG.ERROR,
                        exception.message + ". Cause: " + Objects.requireNonNull(
                            cause
                        ).toString()
                    )
                }
                dialog!!.dismiss()
            }
            val btnParams = RelativeLayout.LayoutParams(-2, -2)
            btnParams.addRule(11)
            wvParams.addRule(3, myButton.id)
            relativeLayout.addView(myButton, btnParams)
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
            val dm = DisplayMetrics()
            GB.Instance()!!.activity!!.getWindowManager().getDefaultDisplay().getMetrics(dm)
            if (styles.width == 0 && styles.height == 0) {
                dialog!!.window!!.setLayout(dm.widthPixels, dm.heightPixels)
            } else if (styles.width > 0 && styles.height > 0) {
                if (styles.width > dm.widthPixels) {
                    dialog!!.window!!.setLayout(dm.widthPixels, styles.height)
                } else {
                    dialog!!.window!!.setLayout(styles.width, styles.height)
                }
            }
        }
    }

    @JavascriptInterface
    fun processHTML(html: String?) {
        UnityPlayer.UnitySendMessage("Code WebView", "CurrentHtmlCode", html)
    }
}