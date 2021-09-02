package com.develop.sns

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.develop.sns.utils.PreferenceHelper
import java.util.*


open class ModuleActivity : AppCompatActivity() {
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpScreen()
        getSharedPreferenceValue()
    }

    private fun setUpScreen() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window: Window = getWindow()
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                window.statusBarColor =
                    ContextCompat.getColor(this@ModuleActivity, R.color.accent)
                window.navigationBarColor =
                    ContextCompat.getColor(this@ModuleActivity, R.color.black)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    val sharedPreferenceValue: Unit
//        get() {
//            val preferenceHelper = PreferenceHelper(this@ModuleActivity)
//            val config: Configuration = getBaseContext().getResources().getConfiguration()
//            val lang: String? = preferenceHelper.getValueFromSharedPrefs("LANG")
//            if ("" != lang && config.locale.language != lang) {
//                val locale = Locale(lang)
//                Locale.setDefault(locale)
//                config.locale = locale
//                getBaseContext().getResources().updateConfiguration(
//                    config,
//                    getBaseContext().getResources().getDisplayMetrics()
//                )
//            }
//        }


    open fun getSharedPreferenceValue() {
        val preferenceHelper = PreferenceHelper(this@ModuleActivity)
        val config = baseContext.resources.configuration
        val lang = preferenceHelper.getValueFromSharedPrefs("LANG")
        if ("" != lang && config.locale.language != lang) {
            val locale = Locale(lang)
            Locale.setDefault(locale)
            config.locale = locale
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }
    }

    protected override fun onDestroy() {
        super.onDestroy()
    }
}