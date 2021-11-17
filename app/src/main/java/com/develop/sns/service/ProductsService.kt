package com.develop.sns.service

import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.develop.sns.MainActivityViewModel
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.PreferenceHelper
import org.json.JSONObject


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class ProductsService : JobService() {
    private val context: Context = this@ProductsService
    private lateinit var preferenceHelper: PreferenceHelper
    lateinit var token: String

    override fun onStartJob(params: JobParameters): Boolean {
        Log.i(TAG, "onStartJob")
        preferenceHelper = PreferenceHelper(this)
        token = preferenceHelper.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
        val mainActivityModel = MainActivityViewModel()
        mainActivityModel.getProductList(token)?.observeForever {
            parseProductList(it)
        }
        return true
    }

    override fun onStopJob(params: JobParameters): Boolean {
        Log.i(TAG, "onStopJob:")

        return false
    }

    private fun parseProductList(jsonObject: JSONObject?) {
        try {
            val scheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            scheduler.cancel(1)
            Log.e("ProducrList", jsonObject.toString())
            preferenceHelper.saveValueToSharedPrefs(
                AppConstant.KEY_PRODUCTS_OBJ,
                jsonObject.toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "ProductsService"
    }
}