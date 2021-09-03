package com.develop.sns.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(var context: Context) {
    var gcmPreferences: SharedPreferences
    fun getValueFromSharedPrefs(KeyName: String?): String? {
        return gcmPreferences.getString(KeyName, "")
    }

    fun saveValueToSharedPrefs(KeyName: String?, value: String?): Boolean {
        val editor = gcmPreferences.edit()
        editor.putString(KeyName, value)
        editor.commit()
        return true
    }

    //@Sujit
    fun saveIntValueToSharedPrefs(KeyName: String?, value: Int): Boolean {
        val editor = gcmPreferences.edit()
        editor.putInt(KeyName, value)
        editor.commit()
        return true
    }

    fun getIntFromSharedPrefs(KeyName: String?): Int {
        return gcmPreferences.getInt(KeyName, 0)
    }

    fun saveFloatValueToSharedPrefs(KeyName: String?, value: Float): Boolean {
        val editor = gcmPreferences.edit()
        editor.putFloat(KeyName, value)
        editor.commit()
        return true
    }

    fun getFloatFromSharedPrefs(KeyName: String?): Float {
        return gcmPreferences.getFloat(KeyName, 0F)
    }

    fun getBoolFromSharedPrefs(KeyName: String?): Boolean {
        return gcmPreferences.getBoolean(KeyName, false)
    }

    fun savePreferenceIndex(keyName: String?, index: Int) {
        val editor = gcmPreferences.edit()
        editor.putInt(keyName, index)
        editor.commit()
    }

    fun getPreferenceIndex(keyName: String?): Int {
        return gcmPreferences.getInt(keyName, 0)
    }

    fun saveBooleanValueToSharedPrefs(KeyName: String?, value: Boolean) {
        val editor = gcmPreferences.edit()
        editor.putBoolean(KeyName, value)
        editor.commit()
    }

    fun removeValue(keyName: String?) {
        val editor = gcmPreferences.edit()
        editor.remove(keyName)
        editor.apply()
    }

    fun clear() {
        val editor = gcmPreferences.edit()
        editor.clear()
        editor.commit()
    }

    init {
        gcmPreferences = context.getSharedPreferences("Attil", Context.MODE_PRIVATE)
    }
}