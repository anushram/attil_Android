package com.illagu.attil

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.illagu.attil.databinding.ActivityMainBinding
import com.illagu.attil.home.HomeActivity
import com.illagu.attil.login.LoginActivity
import com.illagu.attil.login.LoginViewModel
import com.illagu.attil.utils.AppConstant
import com.illagu.attil.utils.AppUtils
import com.illagu.attil.utils.CommonClass
import com.google.gson.JsonObject
import org.json.JSONObject
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.*


class MainActivity : SubModuleActivity() {
    private val context: MainActivity = this@MainActivity

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getIntentValue()
        getAndroidDeviceId()
        handleUiElement()
        getSystemConfig()

    }

    private fun getIntentValue() {
        try {
            val intent = intent
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getAndroidDeviceId() {
        try {
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            preferenceHelper!!.saveValueToSharedPrefs(AppConstant.KEY_DEVICE_ID, deviceId)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSystemConfig() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
              //  showProgressBar()
                val mainActivityViewModel = MainActivityViewModel(application)
                mainActivityViewModel.getSystemConfig()!!.observe(this) { jsonObject ->
                    parseSystemConfigData(jsonObject)
                }
            } else {
                CommonClass.showToastMessage(
                    context,
                    binding.rootView,
                    resources.getString(R.string.no_internet),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    private fun parseSystemConfigData(obj: JSONObject) {
        Log.i("SystemConfig", "" + obj.toString())
        try {
            if (obj != null) {
                if (obj.has("code") && obj.getInt("code") == 200) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataObject = obj.getJSONObject("data")

                        if (dataObject.has("systemLanguages") && !dataObject.isNull("systemLanguages")) {
                            val systemLanguagesArr = dataObject.getJSONArray("systemLanguages")
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_SYSTEM_LANGUAGES,
                                systemLanguagesArr.toString()
                            )
                        }

                        if (dataObject.has("minUnits") && !dataObject.isNull("minUnits")) {
                            val minUnitsArr = dataObject.getJSONArray("minUnits")
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_MIN_UNITS,
                                minUnitsArr.toString()
                            )
                        }

                        if (dataObject.has("maxUnits") && !dataObject.isNull("maxUnits")) {
                            val maxUnitsArr = dataObject.getJSONArray("maxUnits")
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_MAX_UNITS,
                                maxUnitsArr.toString()
                            )
                        }

                        if (dataObject.has("isPaymentActive") && !dataObject.isNull("isPaymentActive")) {
                            preferenceHelper!!.saveBooleanValueToSharedPrefs(
                                AppConstant.KEY_IS_PAYMENT_ACTIVE,
                                dataObject.getBoolean("isPaymentActive")
                            )
                        }

                        if (dataObject.has("isCODActive") && !dataObject.isNull("isCODActive")) {
                            preferenceHelper!!.saveBooleanValueToSharedPrefs(
                                AppConstant.KEY_IS_COD_ACTIVE,
                                dataObject.getBoolean("isCODActive")
                            )
                        }

                        if (dataObject.has("packageCost") && !dataObject.isNull("packageCost")) {
                            preferenceHelper!!.saveIntValueToSharedPrefs(
                                AppConstant.KEY_PACKAGE_COST,
                                dataObject.getInt("packageCost")
                            )
                        }

                        if (dataObject.has("deliveryCost") && !dataObject.isNull("deliveryCost")) {
                            preferenceHelper!!.saveIntValueToSharedPrefs(
                                AppConstant.KEY_DELIVERY_COST,
                                dataObject.getInt("deliveryCost")
                            )
                        }

                        if (dataObject.has("minimumFreeDelivery") && !dataObject.isNull("minimumFreeDelivery")) {
                            preferenceHelper!!.saveIntValueToSharedPrefs(
                                AppConstant.KEY_MIN_FREE_DELIVERY,
                                dataObject.getInt("minimumFreeDelivery")
                            )
                        }

                        if (dataObject.has("minIOSVersion") && !dataObject.isNull("minIOSVersion")) {
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_MIN_IOS,
                                dataObject.getString("minIOSVersion")
                            )
                        }

                        if (dataObject.has("minAndroidVersion") && !dataObject.isNull("minAndroidVersion")) {
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_MIN_ANDROID,
                                dataObject.getString("minAndroidVersion")
                            )
                        }

                        if (dataObject.has("updatedAt") && !dataObject.isNull("updatedAt")) {
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_UPDATED_AT,
                                dataObject.getString("updatedAt")
                            )
                        }

                        if (dataObject.has("updatedAtTZ") && !dataObject.isNull("updatedAtTZ")) {
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_UPDATED_AT_TZ,
                                dataObject.getString("updatedAtTZ")
                            )
                        }
                    }
                    checkForToken()
                } else {
                  //  dismissProgressBar()
                    CommonClass.handleErrorResponse(context, obj, binding.rootView)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkForToken() {
        try {
            val token: String = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
            if (token.isNotEmpty()) {
                if (preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_NAME) != null
                    && preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_NAME)!!
                        .trim().isNotEmpty()
                    && preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_PWD) != null
                    && preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_PWD)!!.trim()
                        .isNotEmpty()
                ) {
                    if (AppUtils.isConnectedToInternet(context)) {
                        val requestObject = JsonObject()
                        requestObject.addProperty(
                            "phoneNumber",
                            preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_NAME)!!
                        )
                        requestObject.addProperty(
                            "password",
                            preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_USER_PWD)!!
                        )
                        requestObject.addProperty("preferredLanguage", language)
                        //Log.e("requestObj", requestObject.toString())
                     //   showProgressBar()
                        val loginViewModel = LoginViewModel(application)
                        loginViewModel.makeLogin(requestObject)
                            .observe(this, { jsonObject ->
                                //Log.e("jsonObject", jsonObject.toString() + "")
                                if (jsonObject != null) {
                                   // dismissProgressBar()
                                    parseSignInResponse(jsonObject)
                                }
                            })
                    }
                } else {
                    val mainActivityModel = MainActivityViewModel(application)
                    mainActivityModel.getProductList(token)?.observeForever {
                       // dismissProgressBar()
                        parseProductList(it)
                    }
                }
            } else {
                launchLoginActivity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseSignInResponse(obj: JSONObject) {
        try {
            //Log.e("LoginResponse", obj.toString())
            if (obj != null) {
                if (obj.has("code") && obj.getInt("code") == 200) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataObject = obj.getJSONObject("data")
                        if (dataObject.has("access_token") && !dataObject.isNull("access_token")) {
                            preferenceHelper!!.saveValueToSharedPrefs(
                                AppConstant.KEY_TOKEN,
                                dataObject.getString("access_token")
                            )
                        }
                    }
                    val token: String =
                        preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
                    val mainActivityModel = MainActivityViewModel(application)
                    mainActivityModel.getProductList(token)?.observeForever {
                       // dismissProgressBar()
                        parseProductList(it)
                    }
                } else {
                   // dismissProgressBar()
                    CommonClass.handleErrorResponse(context, obj, binding.rootView)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseProductList(jsonObject: JSONObject?) {
        try {
            //Log.e("ProducrList", jsonObject.toString())
            if (jsonObject != null) {
                if (jsonObject.has("code") && jsonObject.getInt("code") == 200) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_PRODUCTS_OBJ,
                        jsonObject.toString()
                    )
                    launchHomeActivity()
                } else {
                   // dismissProgressBar()
                    CommonClass.handleErrorResponse(context, jsonObject, binding.rootView)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                checkForToken()
            } else {
                finish()
            }
        }

    private fun launchLoginActivity() {
        try {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            resultLauncher.launch(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun launchHomeActivity() {
        try {
            val intent = Intent(this@MainActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun showProgressBar() {
        try {
            binding.progressBar.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismissProgressBar() {
        try {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.progressBar.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        try {
            super.onStart()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        try {
            super.onStop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object
}