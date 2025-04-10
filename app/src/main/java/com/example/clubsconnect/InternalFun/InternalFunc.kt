package com.example.clubsconnect.InternalFun

import android.content.Context
import android.provider.Settings.Global.putString

internal fun saveUserToPref(context: Context,uid: String, name: String, type: String){
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    prefs.edit().apply {
        putString("uid", uid)
        putString("name", name)
        putString("type", type)
        apply()
    }
}

internal fun getUserInfoFromPrefs(context: Context): Triple<String, String, String> {
    val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val uid = prefs.getString("uid", "") ?: ""
    val name = prefs.getString("name", "") ?: ""
    val type = prefs.getString("type", "") ?: ""
    return Triple(uid, name, type)
}