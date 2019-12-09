package com.gamezboost.utils

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log

class GB(_activity: Activity? = null) {
    var web: Web? = null
    var activity: Activity? = _activity
    enum class DBUG {
        DEBUG, ERROR, INFO, VERBOSE
    }

    fun log(type: DBUG, message: String) {
        when (type) {
            DBUG.DEBUG -> Log.d(type.toString(), message)
            DBUG.ERROR -> Log.e(type.toString(), message)
            DBUG.VERBOSE -> Log.v(type.toString(), message)
            DBUG.INFO -> Log.i(type.toString(), message)
        }
    }

    companion object {
        private val Instance: GB? = GB()
        fun Instance(): GB? {
            return Instance
        }
    }
}