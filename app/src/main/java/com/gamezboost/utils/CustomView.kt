package com.gamezboost.utils

import android.content.Context
import android.webkit.WebView

class CustomView(context: Context) : WebView(context) {
    var mContext: Context
    override fun onCheckIsTextEditor(): Boolean {
        return true
    }
    init {
        mContext = context
    }
}