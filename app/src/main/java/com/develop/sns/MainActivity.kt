package com.develop.sns

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.develop.sns.databinding.ActivityMainBinding
import com.develop.sns.home.HomeActivity
import com.develop.sns.login.LoginActivity
import com.develop.sns.networkhandler.AppUrlManager
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import org.json.JSONObject
import java.util.*


class MainActivity : SubModuleActivity() {
    private val TAG = MainActivity::class.java.simpleName
    private val context: MainActivity = this@MainActivity

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override var preferenceHelper: PreferenceHelper? = null
    private var versionCode = 0
    override var languageId = 0
    private var notificationType = 0
    private var dataObject: String? = null
    var advertiseList: ArrayList<String>? = null
    private val i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        getIntentValue()
        initClassReference()
        getAndroidDeviceId()
        getSystemConfig()
        handleUiElement()

    }

    private fun getIntentValue() {
        try {
            val intent = intent
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initClassReference() {
        try {
            preferenceHelper = PreferenceHelper(context)
            languageId = preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
            advertiseList = ArrayList()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getAndroidDeviceId() {
        try {
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            preferenceHelper!!.saveValueToSharedPrefs(AppConstant.KEY_DEVICE_ID, deviceId)
            preferenceHelper!!.saveValueToSharedPrefs(AppConstant.KEY_DDI, deviceId)
            val languageId = preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
            AppConstant.LANGUAGE_ID = languageId
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            INTENT_LOGIN -> if (resultCode == Activity.RESULT_OK) {
                checkForToken()
            } else {
                finish()
            }
        }
    }

    private fun getSystemConfig() {
        try {
            if (AppUtils.isConnectedToInternet(context)) {
                showProgressBar()
                val url: String = AppUrlManager.getAPIUrl().toString() + "getSystemConfig"
                val mainActivityViewModel = MainActivityViewModel()
                mainActivityViewModel.getSystemConfig(
                    url,
                    AppConstant.REST_CALL_POST,
                    null
                )!!.observe(this) { jsonObject ->
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
        Log.i("Version Main Response", "" + obj.toString());
        try {
            if (obj.has("data") && !obj.isNull("data")) {
                val dataObject = obj.getJSONObject("data")

                if (dataObject.has("systemLanguages") && !dataObject.isNull("systemLanguages")) {
                    val systemLanguagesArr = dataObject.getJSONArray("systemLanguages")
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_SYSTEM_LANGUAGES,
                        systemLanguagesArr.toString()
                    );
                }

                if (dataObject.has("minUnits") && !dataObject.isNull("minUnits")) {
                    val minUnitsArr = dataObject.getJSONArray("minUnits")
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_MIN_UNITS,
                        minUnitsArr.toString()
                    );
                }

                if (dataObject.has("maxUnits") && !dataObject.isNull("maxUnits")) {
                    val maxUnitsArr = dataObject.getJSONArray("maxUnits")
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_MAX_UNITS,
                        maxUnitsArr.toString()
                    );
                }

                if (dataObject.has("isPaymentActive") && !dataObject.isNull("isPaymentActive")) {
                    preferenceHelper!!.saveBooleanValueToSharedPrefs(
                        AppConstant.KEY_IS_PAYMENT_ACTIVE,
                        dataObject.getBoolean("isPaymentActive")
                    );
                }

                if (dataObject.has("isCODActive") && !dataObject.isNull("isCODActive")) {
                    preferenceHelper!!.saveBooleanValueToSharedPrefs(
                        AppConstant.KEY_IS_COD_ACTIVE,
                        dataObject.getBoolean("isCODActive")
                    );
                }

                if (dataObject.has("packageCost") && !dataObject.isNull("packageCost")) {
                    preferenceHelper!!.saveIntValueToSharedPrefs(
                        AppConstant.KEY_PACKAGE_COST,
                        dataObject.getInt("packageCost")
                    );
                }

                if (dataObject.has("deliveryCost") && !dataObject.isNull("deliveryCost")) {
                    preferenceHelper!!.saveIntValueToSharedPrefs(
                        AppConstant.KEY_DELIVERY_COST,
                        dataObject.getInt("deliveryCost")
                    );
                }

                if (dataObject.has("minimumFreeDelivery") && !dataObject.isNull("minimumFreeDelivery")) {
                    preferenceHelper!!.saveIntValueToSharedPrefs(
                        AppConstant.KEY_MIN_FREE_DELIVERY,
                        dataObject.getInt("minimumFreeDelivery")
                    );
                }

                if (dataObject.has("minIOSVersion") && !dataObject.isNull("minIOSVersion")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_MIN_IOS,
                        dataObject.getString("minIOSVersion")
                    );
                }

                if (dataObject.has("minAndroidVersion") && !dataObject.isNull("minAndroidVersion")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_MIN_ANDROID,
                        dataObject.getString("minAndroidVersion")
                    );
                }

                if (dataObject.has("updatedAt") && !dataObject.isNull("updatedAt")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_UPDATED_AT,
                        dataObject.getString("updatedAt")
                    );
                }

                if (dataObject.has("updatedAtTZ") && !dataObject.isNull("updatedAtTZ")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_UPDATED_AT_TZ,
                        dataObject.getString("updatedAtTZ")
                    );
                }
            }
            checkForToken()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun launchPlayStore() {
        val appPackageName = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        } finally {
            finish()
        }
    }

    private fun checkForToken() {
        try {
            val token: String = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
            if (token != null && token.trim { it <= ' ' }.length > 0) {
                launchHomeActivity()
            } else {
                launchLoginActivity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
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
            binding.progressBar.setVisibility(View.VISIBLE)
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
            binding.progressBar.setVisibility(View.GONE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //UserDetails
    private fun getUserDetails(token: String) {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseUserDetails(obj: JSONObject?) {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            dismissProgressBar()
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

    companion object {
        private const val INTENT_LANGUAGE = 1001
        private const val INTENT_LOGIN = 1002
        private const val INTENT_CHANGE_PASSWORD = 1003
    }
}