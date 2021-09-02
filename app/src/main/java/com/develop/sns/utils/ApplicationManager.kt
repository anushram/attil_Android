package com.develop.sns.utils

import android.R.attr
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.text.TextUtils
import androidx.multidex.MultiDex
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.VolleyLog
import com.android.volley.toolbox.Volley
import java.util.*


class ApplicationManager : Application() {
    private val context: Context = this@ApplicationManager
    var state = 0
    private var preferenceHelper: PreferenceHelper? = null
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onTerminate() {
        super.onTerminate()
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
            val languageId = preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
            if (languageId == 0) {
                val defaultLanguageCode = Locale.getDefault().language
                if (defaultLanguageCode == "ta") {
                    preferenceHelper!!.saveIntValueToSharedPrefs(
                        AppConstant.KEY_LANGUAGE_ID,
                        AppConstant.LANGUAGE_TYPE_TAMIL
                    )
                } else {
                    preferenceHelper!!.saveIntValueToSharedPrefs(
                        AppConstant.KEY_LANGUAGE_ID,
                        AppConstant.LANGUAGE_TYPE_ENGLISH
                    )
                }
            } else {
                setLangRecreate(
                    preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_LANGUAGE).toString()
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

    //For service call volley
    fun <T> addToRequestQueue(req: Request<T>, tag: String?) {
        try {
            req.tag = if (TextUtils.isEmpty(tag)) TAG else attr.tag
            VolleyLog.d("Adding request to queue: %s", req.url)
            getRequestQueue().add(req)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    fun <T> addToRequestQueue(req: Request<T>) {
        try {
            req.setTag(TAG)
            getRequestQueue().add(req)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important to
     * specify a TAG so that the pending/ongoing requests can be cancelled.
     *
     * @param tag
     */
    fun cancelPendingRequests(tag: Any?) {
        try {
            if (mRequestQueue != null) {
                mRequestQueue!!.cancelAll(tag)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun getRequestQueue(): RequestQueue {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(applicationContext)
        }
        return mRequestQueue!!
    }

    companion object {

        val TAG = ApplicationManager::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        private var mContext: Context? = null

        @SuppressLint("StaticFieldLeak")
        @get:Synchronized
        lateinit var instance: ApplicationManager
        private var mRequestQueue: RequestQueue? = null
    }
}