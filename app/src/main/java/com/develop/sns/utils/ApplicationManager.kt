package com.develop.sns.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.multidex.MultiDex
import java.util.*


class ApplicationManager : Application() {
    private val context: Context = this@ApplicationManager
    var state = 0
    private lateinit var preferenceHelper: PreferenceHelper
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        mContext = this
        preferenceHelper = PreferenceHelper(context)
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        setUpPhoneLanguage()
    }

    fun setUpPhoneLanguage() {
        try {
            val languageId = preferenceHelper.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
            //Log.e("AppMgr", languageId.toString())
            if (languageId == 0) {
                val defaultLanguageCode = Locale.getDefault().language
                //Log.e("AppMgr LngCode", defaultLanguageCode.toString())
                if (defaultLanguageCode == "ta") {
                    preferenceHelper.saveIntValueToSharedPrefs(
                        AppConstant.KEY_LANGUAGE_ID,
                        AppConstant.LANGUAGE_TYPE_TAMIL
                    )
                    preferenceHelper.saveValueToSharedPrefs(AppConstant.KEY_LANGUAGE, "ta")
                } else {
                    preferenceHelper.saveIntValueToSharedPrefs(
                        AppConstant.KEY_LANGUAGE_ID,
                        AppConstant.LANGUAGE_TYPE_ENGLISH
                    )
                    preferenceHelper.saveValueToSharedPrefs(AppConstant.KEY_LANGUAGE, "en")
                }
            } else {
                setLangRecreate(
                    preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_LANGUAGE)!!
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setLangRecreate(langVal: String) {
        try {
            val config = baseContext.resources.configuration
            val locale = Locale(langVal)
            Locale.setDefault(locale)
            config.locale = locale
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {

        val TAG = ApplicationManager::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        @SuppressLint("StaticFieldLeak")
        @get:Synchronized
        lateinit var instance: ApplicationManager
    }
}