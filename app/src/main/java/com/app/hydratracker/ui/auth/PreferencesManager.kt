package com.app.hydratracker.ui.auth

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
     val prefs: SharedPreferences = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)


    fun saveUserDataToPreferences(username:String,email:String,birth:String) {
        val editor = prefs.edit()
        editor.putString("username", username)
        editor.putString("email", email)
        editor.putString("hearts", birth)
        editor.apply()
    }
}
